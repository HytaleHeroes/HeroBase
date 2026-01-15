package gg.hytaleheroes.herobase.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class ModConfig {
    public static BuilderCodec<ModConfig> CODEC = BuilderCodec.builder(ModConfig.class, ModConfig::new)
            .append(new KeyedCodec<>("MyIntProperty", Codec.INTEGER),
                    (config, x, extraInfo) -> config.MyIntProperty = x,
                    (config, extraInfo) -> 16).add()
            .append(new KeyedCodec<>("MyBoolProperty", Codec.BOOLEAN),
                    (config, x, extraInfo) -> config.MyBoolProperty = x,
                    (config, extraInfo) -> true).add()
            .build();

    public int MyIntProperty = 16;
    public boolean MyBoolProperty = true;
}