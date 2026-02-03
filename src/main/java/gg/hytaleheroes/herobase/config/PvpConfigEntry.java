package gg.hytaleheroes.herobase.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class PvpConfigEntry {
    public static BuilderCodec<PvpConfigEntry> CODEC = BuilderCodec.builder(PvpConfigEntry.class, PvpConfigEntry::new)
            .append(new KeyedCodec<>("Command", Codec.STRING),
                    (config, x, extraInfo) -> config.command = x,
                    (config, extraInfo) -> config.command).add()

            .append(new KeyedCodec<>("Mode", Codec.STRING),
                    (config, x, extraInfo) -> config.mode = x,
                    (config, extraInfo) -> config.mode).add()

            .append(new KeyedCodec<>("PreventPvpAbove", Codec.INTEGER),
                    (config, x, extraInfo) -> config.preventPvpAbove = x,
                    (config, extraInfo) -> config.preventPvpAbove).add()

            .build();

    public String command = "arena";
    public String mode = "ffa";
    public int preventPvpAbove = 140;
}
