package gg.hytaleheroes.herobase.pvp.leaderboard;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.hytaleheroes.herobase.core.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseManager {
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final boolean sqlite;
    private final HikariDataSource dataSource; // may be Hikari

    public DatabaseManager(DatabaseConfig config) {
        this.jdbcUrl = config.url;
        this.username = config.username;
        this.password = config.password;
        this.sqlite = jdbcUrl.contains("sqlite");

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        if (username != null)
            cfg.setUsername(username);
        if (password != null)
            cfg.setPassword(password);

        cfg.setMaximumPoolSize(config.maxPoolSize);
        cfg.setMinimumIdle(config.minimumIdle);
        cfg.setConnectionTimeout(config.connectionTimeoutMs);
        cfg.setIdleTimeout(config.idleTimeoutMs);
        cfg.setMaxLifetime(config.maxLifetimeMs);

        this.dataSource = new HikariDataSource(cfg);
    }

    public Connection openConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        dataSource.close();
    }

    public boolean isSqlite() { return sqlite; }
}
