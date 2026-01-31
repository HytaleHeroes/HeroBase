package gg.hytaleheroes.herobase;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.npc.NPCPlugin;
import gg.hytaleheroes.herobase.command.BaseCommand;
import gg.hytaleheroes.herobase.config.ModConfig;
import gg.hytaleheroes.herobase.handler.PlayerWelcomeHandler;
import gg.hytaleheroes.herobase.npc.action.BuilderActionSendMessage;

import javax.annotation.Nonnull;

public class HeroBase extends JavaPlugin {
    private final Config<ModConfig> config;

    public static HeroBase INSTANCE;

    public HeroBase(@Nonnull JavaPluginInit init) {
        super(init);
        this.config = this.withConfig("HeroBase", ModConfig.CODEC);
    }

    @Override
    protected void setup() {
        super.setup();

        INSTANCE = this;

        this.config.load().thenAccept(x -> config.save()).join();

        this.getCommandRegistry().registerCommand(new BaseCommand());
        this.getEventRegistry().register(PlayerConnectEvent.class, PlayerWelcomeHandler::onPlayerJoin);

        NPCPlugin.get().registerCoreComponentType("SendMessage", BuilderActionSendMessage::new);
    }

    public Config<ModConfig> getConfig() {
        return config;
    }
}