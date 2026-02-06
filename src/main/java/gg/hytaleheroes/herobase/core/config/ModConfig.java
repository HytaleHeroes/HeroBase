package gg.hytaleheroes.herobase.core.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.List;

public class ModConfig {
    private int secret = 420;

    public static BuilderCodec<ModConfig> CODEC = BuilderCodec.builder(ModConfig.class, ModConfig::new)
            .append(new KeyedCodec<>("SuperSecretSetting", Codec.INTEGER),
                    (config, x, extraInfo) -> config.secret = x,
                    (config, extraInfo) -> config.secret).add()

            .build();
}
