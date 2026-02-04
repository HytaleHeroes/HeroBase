package gg.hytaleheroes.herobase.pvp.leaderboard;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import gg.hytaleheroes.herobase.HeroBase;
import gg.hytaleheroes.herobase.pvp.PvpModule;
import org.jspecify.annotations.NonNull;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Leaderboards {
    private final Cache<@NonNull String, @NonNull LeaderboardEntry> entryCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).maximumSize(500).build();

    private final Cache<@NonNull String, @NonNull List<LeaderboardEntry>> topCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).maximumSize(200).build();

    private final Cache<@NonNull UUID, @NonNull String> nameCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(1000).build();

    private final Cache<@NonNull String, @NonNull ModeStats> statsCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).maximumSize(500).build();

    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();
    private final Duration cleanupWindow;

    public Leaderboards() {
        // clean up kill entries older than 12 hour, every 30 mins
        this(Duration.ofHours(PvpModule.get().getConfig().keepKillsSavedHours), Duration.ofMinutes(30));
    }

    public Leaderboards(Duration window, Duration interval) {
        this.cleanupWindow = Objects.requireNonNull(window);
        Duration cleanupInterval = Objects.requireNonNull(interval);

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection()) {
            createTablesIfNotExist(c);
        } catch (Exception e) {
            HytaleLogger.forEnclosingClass().at(Level.SEVERE).log("Could not create tables: " + e.getMessage());
        }

        cleanupScheduler.scheduleAtFixedRate(() -> {
            try {
                cleanOldKills(cleanupWindow);
            } catch (Throwable t) {
                HytaleLogger.forEnclosingClass().at(Level.WARNING).log("Error during kills cleanup: " + t.getMessage());
            }
        }, cleanupInterval.toMillis(), cleanupInterval.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        cleanupScheduler.shutdownNow();
    }

    private void createTablesIfNotExist(Connection c) throws SQLException {
        try (Statement s = c.createStatement()) {
            if (HeroBase.get().getDatabaseManager().isSqlite()) {
                s.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS players (
                          player_id TEXT PRIMARY KEY,
                          username TEXT,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                        );
                        """);

                s.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS leaderboard (
                          leaderboard_id INTEGER PRIMARY KEY AUTOINCREMENT,
                          player_id TEXT NOT NULL,
                          mode TEXT NOT NULL,
                          score INTEGER NOT NULL,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          UNIQUE(player_id, mode)
                        );
                        """);

                s.executeUpdate("""
                        CREATE INDEX IF NOT EXISTS idx_leaderboard_mode_score ON leaderboard(mode, score DESC);
                        """);

                s.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS kills (
                          kill_id INTEGER PRIMARY KEY AUTOINCREMENT,
                          attacker_id TEXT NOT NULL,
                          victim_id TEXT NOT NULL,
                          mode TEXT NOT NULL,
                          ts DATETIME DEFAULT CURRENT_TIMESTAMP
                        );
                        """);

                s.executeUpdate("""
                        CREATE INDEX IF NOT EXISTS idx_kills_ts ON kills(ts);
                        """);

                s.executeUpdate("""
                        CREATE INDEX IF NOT EXISTS idx_kills_attacker_mode ON kills(attacker_id, mode);
                        """);

                s.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS stats_mode (
                          player_id TEXT NOT NULL,
                          mode TEXT NOT NULL,
                          kills INTEGER DEFAULT 0,
                          deaths INTEGER DEFAULT 0,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (player_id, mode)
                        );
                        """);

                s.executeUpdate("""
                        CREATE INDEX IF NOT EXISTS idx_stats_mode_player ON stats_mode(player_id);
                        """);

                s.executeUpdate("""
                        CREATE INDEX IF NOT EXISTS idx_stats_mode_kills ON stats_mode(mode, kills DESC);
                        """);

                s.executeUpdate("""
                        CREATE INDEX IF NOT EXISTS idx_stats_mode_kd_ratio ON stats_mode(mode, (CAST(kills AS REAL) / NULLIF(deaths, 0)) DESC);
                        """);
            } else {
                s.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS players (
                          player_id CHAR(36) PRIMARY KEY,
                          username VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        ) ENGINE=InnoDB;
                        """);

                s.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS leaderboard (
                          leaderboard_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          player_id CHAR(36) NOT NULL,
                          mode VARCHAR(100) NOT NULL,
                          score INT NOT NULL,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          UNIQUE KEY ux_player_mode (player_id, mode),
                          INDEX idx_mode_score (mode, score DESC)
                        ) ENGINE=InnoDB;
                        """);

                s.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS kills (
                          kill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          attacker_id CHAR(36) NOT NULL,
                          victim_id CHAR(36) NOT NULL,
                          mode VARCHAR(100) NOT NULL,
                          ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        ) ENGINE=InnoDB;
                        """);

                s.executeUpdate("CREATE INDEX idx_kills_ts ON kills(ts);");
                s.executeUpdate("CREATE INDEX idx_kills_attacker_mode ON kills(attacker_id, mode);");

                s.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS stats_mode (
                          player_id CHAR(36) NOT NULL,
                          mode VARCHAR(100) NOT NULL,
                          kills BIGINT DEFAULT 0,
                          deaths BIGINT DEFAULT 0,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (player_id, mode),
                          INDEX idx_stats_mode_player (player_id),
                          INDEX idx_stats_mode_kills (mode, kills DESC)
                        ) ENGINE=InnoDB;
                        """);
            }
        }
    }

    public void updatePlayer(UUID playerId, String username) throws SQLException {
        nameCache.put(playerId, username);

        String sql = HeroBase.get().getDatabaseManager().isSqlite() ? "INSERT INTO players (player_id, username) VALUES (?, ?) ON CONFLICT(player_id) DO UPDATE SET username = excluded.username" : "INSERT INTO players (player_id, username) VALUES (?, ?) ON DUPLICATE KEY UPDATE username = VALUES(username)";

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerId.toString());
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }

    public String getUsername(UUID playerId) throws SQLException {
        String cached = nameCache.getIfPresent(playerId);
        if (cached != null) return cached;

        String sql = "SELECT username FROM players WHERE player_id = ?";

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerId.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("username");
                if (name != null) {
                    nameCache.put(playerId, name);
                    return name;
                }
            }
        }

        return "Unknown";
    }

    public LeaderboardEntry getEntry(UUID playerId, String mode) throws SQLException {
        String key = playerId + "|" + mode;
        LeaderboardEntry cached = entryCache.getIfPresent(key);
        if (cached != null) return cached;

        String sql = """
                SELECT leaderboard_id, player_id, mode, score, updated_at
                FROM leaderboard
                WHERE player_id = ? AND mode = ?
                """;

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, playerId.toString());
            ps.setString(2, mode);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            LeaderboardEntry e = new LeaderboardEntry(rs.getLong("leaderboard_id"), UUID.fromString(rs.getString("player_id")), rs.getString("mode"), rs.getInt("score"), rs.getTimestamp("updated_at").toInstant());

            entryCache.put(key, e);
            return e;
        }
    }


    public void recordKill(UUID attackerId, UUID victimId, String mode) throws SQLException {
        if (mode == null) mode = "unknown";

        String insertKillSql = "INSERT INTO kills (attacker_id, victim_id, mode, ts) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(insertKillSql)) {
                    ps.setString(1, attackerId.toString());
                    ps.setString(2, victimId.toString());
                    ps.setString(3, mode);
                    ps.executeUpdate();
                }

                upsertStatsMode(c, attackerId, mode, 1L, 0L);
                upsertStatsMode(c, victimId, mode, 0L, 1L);

                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }

        entryCache.invalidate(attackerId + "|" + mode);
        statsCache.invalidate(attackerId + "|" + mode);
        statsCache.invalidate(victimId + "|" + mode);

        String finalMode = mode;
        topCache.asMap().keySet().removeIf(k -> k.startsWith(finalMode + "|") || k.contains("|recent|") || k.contains("|stats|"));
    }

    private void upsertStatsMode(Connection c, UUID playerId, String mode, long incKills, long incDeaths) throws SQLException {
        boolean isSqlite = HeroBase.get().getDatabaseManager().isSqlite();
        String sql = isSqlite ? """
                INSERT INTO stats_mode (player_id, mode, kills, deaths)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(player_id, mode) DO UPDATE SET
                  kills = stats_mode.kills + excluded.kills,
                  deaths = stats_mode.deaths + excluded.deaths,
                  updated_at = CURRENT_TIMESTAMP
                """ : """
                INSERT INTO stats_mode (player_id, mode, kills, deaths)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                  kills = kills + VALUES(kills),
                  deaths = deaths + VALUES(deaths),
                  updated_at = CURRENT_TIMESTAMP
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerId.toString());
            ps.setString(2, mode);
            ps.setLong(3, incKills);
            ps.setLong(4, incDeaths);
            ps.executeUpdate();
        }
    }

    public void cleanOldKills(Duration window) throws SQLException {
        Instant cutoff = Instant.now().minus(window);
        String sql = "DELETE FROM kills WHERE ts < ?";

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            if (HeroBase.get().getDatabaseManager().isSqlite()) {
                ps.setString(1, Timestamp.from(cutoff).toString());
            } else {
                ps.setTimestamp(1, Timestamp.from(cutoff));
            }

            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                HytaleLogger.forEnclosingClass().at(Level.INFO).log("Deleted " + deleted + " old kills older than " + window);
            }
        }

        topCache.asMap().keySet().removeIf(k -> k.contains("|recent|") || k.contains("|stats|"));
    }

    public List<LeaderboardEntry> topKillsInWindow(String mode, int n, Duration window) throws SQLException {
        return topKillsInWindow(Collections.singletonList(mode), n, window);
    }

    public List<LeaderboardEntry> topKillsInWindow(Collection<String> modes, int n, Duration window) throws SQLException {
        if (modes == null || modes.isEmpty()) throw new IllegalArgumentException("modes must be non-empty");
        String modesKey = String.join(",", modes);
        String key = "modes|" + modesKey + "|" + n + "|recent|" + window.getSeconds();
        List<LeaderboardEntry> cached = topCache.getIfPresent(key);
        if (cached != null) return cached;

        Instant cutoff = Instant.now().minus(window);

        String sql = "SELECT attacker_id, COUNT(*) AS cnt, MAX(ts) AS last_ts FROM kills WHERE ts >= ? AND mode IN (" + String.join(",", Collections.nCopies(modes.size(), "?")) + ") GROUP BY attacker_id ORDER BY cnt DESC LIMIT ?";

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int idx = 1;

            if (HeroBase.get().getDatabaseManager().isSqlite()) {
                ps.setString(idx++, Timestamp.from(cutoff).toString());
            } else {
                ps.setTimestamp(idx++, Timestamp.from(cutoff));
            }

            for (String m : modes) ps.setString(idx++, m);
            ps.setInt(idx, n);

            ResultSet rs = ps.executeQuery();
            List<LeaderboardEntry> list = new ArrayList<>();
            while (rs.next()) {
                UUID pid = UUID.fromString(rs.getString("attacker_id"));
                int cnt = rs.getInt("cnt");
                Timestamp lastTs = rs.getTimestamp("last_ts");
                list.add(new LeaderboardEntry(0, pid, String.join(",", modes), cnt, lastTs != null ? lastTs.toInstant() : Instant.now()));
            }

            topCache.put(key, List.copyOf(list));
            return list;
        }
    }

    public Map<String, ModeStats> getPlayerModeStats(UUID playerId) throws SQLException {
        String sql = "SELECT mode, kills, deaths, updated_at FROM stats_mode WHERE player_id = ?";
        Map<String, ModeStats> res = new HashMap<>();
        try (Connection c = HeroBase.get().getDatabaseManager().openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String mode = rs.getString("mode");
                long kills = rs.getLong("kills");
                long deaths = rs.getLong("deaths");
                Instant updatedAt = rs.getTimestamp("updated_at").toInstant();
                res.put(mode, new ModeStats(mode, kills, deaths, updatedAt));
            }
        }
        return res;
    }

    public ModeStats getPlayerModeStat(UUID playerId, String mode) throws SQLException {
        String key = playerId + "|" + mode;
        ModeStats cached = statsCache.getIfPresent(key);
        if (cached != null) return cached;

        String sql = "SELECT kills, deaths, updated_at FROM stats_mode WHERE player_id = ? AND mode = ?";
        try (Connection c = HeroBase.get().getDatabaseManager().openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerId.toString());
            ps.setString(2, mode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long kills = rs.getLong("kills");
                long deaths = rs.getLong("deaths");
                Instant updatedAt = rs.getTimestamp("updated_at").toInstant();
                ModeStats stats = new ModeStats(mode, kills, deaths, updatedAt);
                statsCache.put(key, stats);
                return stats;
            }
        }

        return new ModeStats(mode, 0, 0, Instant.now());
    }

    public void updatePlayerStats(UUID playerId, String mode, long kills, long deaths) throws SQLException {
        String sql = HeroBase.get().getDatabaseManager().isSqlite() ? """
                INSERT INTO stats_mode (player_id, mode, kills, deaths)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(player_id, mode) DO UPDATE SET
                  kills = excluded.kills,
                  deaths = excluded.deaths,
                  updated_at = CURRENT_TIMESTAMP
                """ : """
                INSERT INTO stats_mode (player_id, mode, kills, deaths)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                  kills = VALUES(kills),
                  deaths = VALUES(deaths),
                  updated_at = CURRENT_TIMESTAMP
                """;

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerId.toString());
            ps.setString(2, mode);
            ps.setLong(3, kills);
            ps.setLong(4, deaths);
            ps.executeUpdate();
        }

        statsCache.invalidate(playerId + "|" + mode);
        topCache.asMap().keySet().removeIf(k -> k.contains("|stats|"));
    }

    public List<LeaderboardEntry> getTopPlayers(String mode, int n) throws SQLException {
        String key = "top|" + mode + "|" + n;

        List<LeaderboardEntry> cached = topCache.getIfPresent(key);
        if (cached != null) return cached;

        String sql = """
                SELECT leaderboard_id, player_id, mode, score, updated_at
                FROM leaderboard
                WHERE mode = ?
                ORDER BY score DESC
                LIMIT ?
                """;

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, mode);
            ps.setInt(2, n);

            ResultSet rs = ps.executeQuery();
            List<LeaderboardEntry> list = new ArrayList<>();

            while (rs.next()) {
                LeaderboardEntry e = new LeaderboardEntry(
                        rs.getLong("leaderboard_id"),
                        UUID.fromString(rs.getString("player_id")),
                        rs.getString("mode"),
                        rs.getInt("score"),
                        rs.getTimestamp("updated_at").toInstant()
                );
                list.add(e);
            }

            List<LeaderboardEntry> result = List.copyOf(list);
            topCache.put(key, result);
            return result;
        }
    }

    public record ModeStats(String mode, long kills, long deaths, Instant updatedAt) {
        public double getKDRatio() {
            return deaths == 0 ? (kills == 0 ? 0.0 : Double.POSITIVE_INFINITY) : (double) kills / deaths;
        }
    }
}