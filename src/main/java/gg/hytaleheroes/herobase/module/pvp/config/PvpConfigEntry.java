package gg.hytaleheroes.herobase.module.pvp.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class PvpConfigEntry {
    public static BuilderCodec<PvpConfigEntry> CODEC = BuilderCodec.builder(PvpConfigEntry.class, PvpConfigEntry::new)
            .append(new KeyedCodec<>("Name", Codec.STRING),
                    (config, x, extraInfo) -> config.name = x,
                    (config, extraInfo) -> config.name).add()

            .append(new KeyedCodec<>("World", Codec.STRING),
                    (config, x, extraInfo) -> config.world = x,
                    (config, extraInfo) -> config.world).add()

            .append(new KeyedCodec<>("Mode", Codec.STRING),
                    (config, x, extraInfo) -> config.mode = x,
                    (config, extraInfo) -> config.mode).add()

            .append(new KeyedCodec<>("PreventPvpAbove", Codec.INTEGER),
                    (config, x, extraInfo) -> config.preventPvpAbove = x,
                    (config, extraInfo) -> config.preventPvpAbove).add()

            .append(new KeyedCodec<>("Leaderboard", Codec.BOOLEAN),
                    (config, x, extraInfo) -> config.leaderboard = x,
                    (config, extraInfo) -> config.leaderboard).add()

            .append(new KeyedCodec<>("LeaderboardTimeWindowHours", Codec.INTEGER),
                    (config, x, extraInfo) -> config.leaderboardTimeWindowHours = x,
                    (config, extraInfo) -> config.leaderboardTimeWindowHours).add()

            .build();

    public String mode = "ffa_arena";
    public String name = "Arena";
    public String world = "arena";
    public boolean leaderboard = false;
    public int preventPvpAbove = 140;
    public int leaderboardTimeWindowHours = 12;
}
