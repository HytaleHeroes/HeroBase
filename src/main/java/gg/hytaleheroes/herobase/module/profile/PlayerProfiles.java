package gg.hytaleheroes.herobase.module.profile;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import gg.hytaleheroes.herobase.HeroBase;
import org.jspecify.annotations.NonNull;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PlayerProfiles {
    private final Cache<@NonNull UUID, @NonNull ProfileData> profileCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public PlayerProfiles() {
        try (Connection c = HeroBase.get().getDatabaseManager().openConnection()) {
            createTablesIfNotExist(c);
        } catch (Exception e) {
            HytaleLogger.forEnclosingClass().at(Level.SEVERE).log("Could not create profile tables: " + e.getMessage());
        }
    }

    private void createTablesIfNotExist(Connection c) throws SQLException {
        try (Statement s = c.createStatement()) {
            if (HeroBase.get().getDatabaseManager().isSqlite()) {
                s.executeUpdate("CREATE TABLE IF NOT EXISTS player_profiles (" +
                        "player_id TEXT PRIMARY KEY, " +
                        "status_msg VARCHAR(140), " +
                        "wallpaper_id TEXT DEFAULT 'default', " +
                        "theme_color TEXT DEFAULT '#FFFFFF', " +
                        "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ");");
            } else {
                s.executeUpdate("CREATE TABLE IF NOT EXISTS player_profiles (" +
                        "player_id CHAR(36) PRIMARY KEY, " +
                        "status_msg VARCHAR(140), " +
                        "wallpaper_id VARCHAR(64) DEFAULT 'default', " +
                        "theme_color VARCHAR(16) DEFAULT '#FFFFFF', " +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB;");
            }
            s.getConnection().commit();
        }
    }

    public ProfileData getProfile(UUID playerId) {
        ProfileData cached = profileCache.getIfPresent(playerId);
        if (cached != null) return cached;

        try {
            createProfileIfNotExists(playerId);
        } catch (SQLException e) {
            HytaleLogger.forEnclosingClass().at(Level.SEVERE).log("Failed to create profile for {}: {}", playerId, e.getMessage());
        }

        String sql = "SELECT status_msg, wallpaper_id, theme_color, updated_at FROM player_profiles WHERE player_id = ?";

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, playerId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProfileData data = new ProfileData(
                            playerId,
                            rs.getString("status_msg"),
                            rs.getString("wallpaper_id"),
                            rs.getString("theme_color"),
                            rs.getTimestamp("updated_at").toInstant()
                    );
                    profileCache.put(playerId, data);
                    return data;
                }
            }
        } catch (SQLException e) {
            HytaleLogger.forEnclosingClass().at(Level.SEVERE).log("Failed to fetch profile for {}", playerId);
        }

        return new ProfileData(playerId, null, "default", "#FFFFFF", Instant.now());
    }


    private void createProfileIfNotExists(UUID playerId) throws SQLException {
        String sql = HeroBase.get().getDatabaseManager().isSqlite()
                ? "INSERT OR IGNORE INTO player_profiles (player_id) VALUES (?)"
                : "INSERT IGNORE INTO player_profiles (player_id) VALUES (?)";

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerId.toString());
            ps.executeUpdate();
        }
    }

    public void setStatus(UUID playerId, String status) throws SQLException {
        String safeStatus = (status != null && status.length() > 140) ? status.substring(0, 140) : status;
        upsertField(playerId, "status_msg", safeStatus);
    }

    public void setCosmetics(UUID playerId, String wallpaperId, String themeColor) throws SQLException {
        String sql = HeroBase.get().getDatabaseManager().isSqlite()
                ? "INSERT INTO player_profiles (player_id, wallpaper_id, theme_color) VALUES (?, ?, ?) " +
                "ON CONFLICT(player_id) DO UPDATE SET wallpaper_id = excluded.wallpaper_id, theme_color = excluded.theme_color, updated_at = CURRENT_TIMESTAMP"
                : "INSERT INTO player_profiles (player_id, wallpaper_id, theme_color) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE wallpaper_id = VALUES(wallpaper_id), theme_color = VALUES(theme_color)";

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerId.toString());
            ps.setString(2, wallpaperId);
            ps.setString(3, themeColor);
            ps.executeUpdate();
        }

        profileCache.invalidate(playerId);
    }

    private void upsertField(UUID playerId, String columnName, String value) throws SQLException {
        String sql = HeroBase.get().getDatabaseManager().isSqlite()
                ? "INSERT INTO player_profiles (player_id, " + columnName + ") VALUES (?, ?) " +
                "ON CONFLICT(player_id) DO UPDATE SET " + columnName + " = excluded." + columnName + ", updated_at = CURRENT_TIMESTAMP"
                : "INSERT INTO player_profiles (player_id, " + columnName + ") VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE " + columnName + " = VALUES(" + columnName + ")";

        try (Connection c = HeroBase.get().getDatabaseManager().openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerId.toString());
            ps.setString(2, value);
            ps.executeUpdate();
        }

        profileCache.invalidate(playerId);
    }

    public record ProfileData(UUID playerId, String status, String wallpaperId, String themeColor, Instant lastUpdate) {
        public String getStatusOr(String fallback) {
            return (status == null || status.isBlank()) ? fallback : status;
        }

        public String getThemeColor() {
            return themeColor == null ? "#FFFFFF" : themeColor;
        }
    }
}