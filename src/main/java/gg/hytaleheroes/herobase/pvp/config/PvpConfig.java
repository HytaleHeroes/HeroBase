package gg.hytaleheroes.herobase.pvp.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

import java.util.List;

public class PvpConfig {
    public static BuilderCodec<PvpConfig> CODEC = BuilderCodec.builder(PvpConfig.class, PvpConfig::new)
            .append(new KeyedCodec<>("PvpWorlds", new ArrayCodec<>(PvpConfigEntry.CODEC, PvpConfigEntry[]::new)),
                    (config, x, extraInfo) -> config.pvpConfigEntries = List.of(x),
                    (config, extraInfo) -> config.pvpConfigEntries.toArray(new PvpConfigEntry[0])).add()

            .append(new KeyedCodec<>("KeepKillsSavedHours", Codec.INTEGER),
                    (config, x, extraInfo) -> config.keepKillsSavedHours = x,
                    (config, extraInfo) -> config.keepKillsSavedHours).add()

            .build();

    public List<PvpConfigEntry> pvpConfigEntries = List.of();
    public int keepKillsSavedHours = 24*3;


    public PvpConfigEntry getByName(String name) {
        for (PvpConfigEntry entry : pvpConfigEntries) {
            if (entry.world.equals(name)) {
                return entry;
            }
        }

        return null;
    }
}
