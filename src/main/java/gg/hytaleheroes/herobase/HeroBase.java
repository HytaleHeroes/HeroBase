package gg.hytaleheroes.herobase;

import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.modules.item.ItemModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.registry.AssetRegistry;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.Config;
import gg.hytaleheroes.herobase.command.BaseCommand;
import gg.hytaleheroes.herobase.config.ModConfig;
import gg.hytaleheroes.herobase.format.TinyMsg;
import gg.hytaleheroes.herobase.system.AbilityKeybindSystem;

import javax.annotation.Nonnull;
import java.io.IOException;

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

        this.config.get();

        this.getCommandRegistry().registerCommand(new BaseCommand());

        this.getEventRegistry().register(PlayerConnectEvent.class, (event) -> {
            boolean isNewPlayer;
            try {
                isNewPlayer = !Universe.get().getPlayerStorage().getPlayers().contains(event.getPlayerRef().getUuid());
            } catch (IOException _) {
                isNewPlayer = true;
            }

            if (isNewPlayer) {
                for (String s : config.get().welcomeMessage) {
                    event.getPlayerRef().sendMessage(TinyMsg.parse(s.replace("%player%", event.getPlayerRef().getUsername())));
                }
            } else {
                for (String s : config.get().welcomeBackMessage) {
                    event.getPlayerRef().sendMessage(TinyMsg.parse(s.replace("%player%", event.getPlayerRef().getUsername())));
                }
            }

            var msg = isNewPlayer ? config.get().globalWelcomeMessage : config.get().globalWelcomeBackMessage;
            if (msg != null && !msg.isBlank()) {
                var playerRefList = Universe.get().getPlayers();
                playerRefList.forEach(x -> {
                    if (x != null && event.getPlayerRef() != x)
                        x.sendMessage(TinyMsg.parse(msg.replace("%player%", event.getPlayerRef().getUsername())));
                });
            }
        });

    }

    @Override
    protected void start() {
        super.start();

        this.getEntityStoreRegistry().registerSystem(new AbilityKeybindSystem());

        SimpleBlockInteraction.CODEC
    }



    public Config<ModConfig> getConfig() {
        return config;
    }
}