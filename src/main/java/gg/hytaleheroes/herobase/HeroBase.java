package gg.hytaleheroes.herobase;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.Config;
import gg.hytaleheroes.herobase.command.BaseCommand;
import gg.hytaleheroes.herobase.config.ModConfig;
import gg.hytaleheroes.herobase.format.TinyMsg;

import javax.annotation.Nonnull;
import java.io.IOException;

public class HeroBase extends JavaPlugin {
    private final Config<ModConfig> config;

    public HeroBase(@Nonnull JavaPluginInit init) {
        super(init);
        this.config = this.withConfig("HeroBase", ModConfig.CODEC);
    }

    @Override
    protected void setup() {
        super.setup();

        this.config.load().thenAccept(x -> config.save()).join();

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
}