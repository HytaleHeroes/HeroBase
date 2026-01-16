package gg.hytaleheroes.herobase.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.List;

public class ModConfig {
    public static String[] DefaultWelcomeMessage = List.of(
            "    <gradient:red:blue><b>Welcome to Hytale Heroes, %player%!</b></gradient>",
            " ",
            "    <blue><i><u><link:https://discord.gg/FjxYPVZF>CLICK HERE TO JOIN OUR COMMUNITY</link></u></i></blue>",
            "    Use /sc to claim land",
            "    Use /sethome to set a home"
    ).toArray(new String[0]);

    public static String[] DefaultWelcomeBackMessage = List.of(
            "    <gradient:red:blue><b>Welcome back to Hytale Heroes, %player%!</b></gradient>",
            " ",
            "    <blue><i><u><link:https://discord.gg/FjxYPVZF>DID YOU JOIN OUR COMMUNITY ALREADY?</link></u></i></blue>",
            "    Use /sc to claim land",
            "    Use /sethome to set a home"
    ).toArray(new String[0]);

    public static String DefaultGlobalWelcomeMessage = "<gradient:red:blue><b>Welcome, %player%!</b></gradient>";
    public static String DefaultGlobalWelcomeBackMessage = "<gradient:red:blue><b>Welcome back, %player%!</b></gradient>";

    public String[] welcomeMessage = DefaultWelcomeMessage;
    public String[] welcomeBackMessage;
    public String globalWelcomeMessage;
    public String globalWelcomeBackMessage;

    public static BuilderCodec<ModConfig> CODEC = BuilderCodec.builder(ModConfig.class, ModConfig::new)
            .append(new KeyedCodec<>("WelcomeMessage", Codec.STRING_ARRAY),
                    (config, x, extraInfo) -> config.welcomeMessage = x,
                    (config, extraInfo) -> DefaultWelcomeMessage).add()
            .append(new KeyedCodec<>("WelcomeBackMessage", Codec.STRING_ARRAY),
                    (config, x, extraInfo) -> config.welcomeBackMessage = x,
                    (config, extraInfo) -> DefaultWelcomeBackMessage).add()
            .append(new KeyedCodec<>("GlobalWelcomeMessage", Codec.STRING),
                    (config, x, extraInfo) -> config.globalWelcomeMessage = x,
                    (config, extraInfo) -> DefaultGlobalWelcomeMessage).add()
            .append(new KeyedCodec<>("GlobalWelcomeBackMessage", Codec.STRING),
                    (config, x, extraInfo) -> config.globalWelcomeBackMessage = x,
                    (config, extraInfo) -> DefaultGlobalWelcomeBackMessage).add()
            .build();
}
