package gg.hytaleheroes.herobase;

import com.hypixel.hytale.server.core.event.events.ShutdownEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.Config;
import gg.hytaleheroes.herobase.command.BaseCommand;
import gg.hytaleheroes.herobase.config.ModConfig;
import gg.hytaleheroes.herobase.file.PlayerFirstJoinTrackerFile;
import gg.hytaleheroes.herobase.format.TinyMsg;

import javax.annotation.Nonnull;

public class HeroBase extends JavaPlugin {
    public static Config<ModConfig> CONFIG;
    public static PlayerFirstJoinTrackerFile TRACKER = new PlayerFirstJoinTrackerFile();

    public HeroBase(@Nonnull JavaPluginInit init) {
        super(init);
        CONFIG = this.withConfig("HeroBase", ModConfig.CODEC);
        CONFIG.load();
        TRACKER.syncLoad();
    }

    @Override
    protected void setup() {
        super.setup();

        this.getCommandRegistry().registerCommand(new BaseCommand());

        this.getEventRegistry().register(ShutdownEvent.class, (event) -> {
            TRACKER.syncSave();
        });

        this.getEventRegistry().register(PlayerConnectEvent.class, (event) -> {
            boolean isNewPlayer = TRACKER.add(event.getPlayerRef().getUuid());
            if (isNewPlayer) {
                for (String s : CONFIG.get().welcomeMessage) {
                    event.getPlayerRef().sendMessage(TinyMsg.parse(s.replace("%player%", event.getPlayerRef().getUsername())));
                }
            } else {
                for (String s : CONFIG.get().welcomeBackMessage) {
                    event.getPlayerRef().sendMessage(TinyMsg.parse(s.replace("%player%", event.getPlayerRef().getUsername())));
                }
            }

            var msg = isNewPlayer ? CONFIG.get().globalWelcomeMessage : CONFIG.get().globalWelcomeBackMessage;
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