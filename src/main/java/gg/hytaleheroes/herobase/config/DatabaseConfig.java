package gg.hytaleheroes.herobase.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class DatabaseConfig {
    public static BuilderCodec<DatabaseConfig> CODEC = BuilderCodec.builder(DatabaseConfig.class, DatabaseConfig::new)
            .append(new KeyedCodec<>("Url", Codec.STRING),
                    (config, x, extraInfo) -> config.url = x,
                    (config, extraInfo) -> config.url).add()

            .append(new KeyedCodec<>("Username", Codec.STRING),
                    (config, x, extraInfo) -> config.username = x,
                    (config, extraInfo) -> config.username).add()

            .append(new KeyedCodec<>("Password", Codec.STRING),
                    (config, x, extraInfo) -> config.password = x,
                    (config, extraInfo) -> config.password).add()

            .append(new KeyedCodec<>("MaxPoolSize", Codec.INTEGER),
                    (config, x, extraInfo) -> config.maxPoolSize = x,
                    (config, extraInfo) -> config.maxPoolSize).add()
            .append(new KeyedCodec<>("MinimumIdle", Codec.INTEGER),
                    (config, x, extraInfo) -> config.minimumIdle = x,
                    (config, extraInfo) -> config.minimumIdle).add()
            .append(new KeyedCodec<>("ConnectionTimeoutMs", Codec.LONG),
                    (config, x, extraInfo) -> config.connectionTimeoutMs = x,
                    (config, extraInfo) -> config.connectionTimeoutMs).add()
            .append(new KeyedCodec<>("IdleTimeoutMs", Codec.LONG),
                    (config, x, extraInfo) -> config.idleTimeoutMs = x,
                    (config, extraInfo) -> config.idleTimeoutMs).add()
            .append(new KeyedCodec<>("MaxLifetimeMs", Codec.LONG),
                    (config, x, extraInfo) -> config.maxLifetimeMs = x,
                    (config, extraInfo) -> config.maxLifetimeMs).add()

            .build();

    public String url = "jdbc:sqlite:./mods/HytaleHeroes_HeroBase/leaderboard.db";
    public String username = "root";
    public String password = "secret";

    public int maxPoolSize = 10;
    public int minimumIdle = 2;
    public long connectionTimeoutMs = 10_000L; // 10s
    public long idleTimeoutMs = 600_000L; // 10mins
    public long maxLifetimeMs = 1_800_000L; // 30mins
}
