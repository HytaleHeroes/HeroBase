package gg.hytaleheroes.herobase.pvp.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;

import java.util.HashMap;
import java.util.Map;

public class PvpConfig {
    public static BuilderCodec<PvpConfig> CODEC = BuilderCodec.builder(PvpConfig.class, PvpConfig::new)
            .append(new KeyedCodec<>("PvpWorlds", new MapCodec<>(PvpConfigEntry.CODEC, HashMap::new)),
                    (config, x, extraInfo) -> config.pvpConfigEntryMap = x,
                    (config, extraInfo) -> config.pvpConfigEntryMap).add()

            .append(new KeyedCodec<>("KeepKillsSavedHours", Codec.INTEGER),
                    (config, x, extraInfo) -> config.keepKillsSavedHours = x,
                    (config, extraInfo) -> config.keepKillsSavedHours).add()

            .build();

    public Map<String, PvpConfigEntry> pvpConfigEntryMap = new HashMap<>();
    public int keepKillsSavedHours = 12;
}
