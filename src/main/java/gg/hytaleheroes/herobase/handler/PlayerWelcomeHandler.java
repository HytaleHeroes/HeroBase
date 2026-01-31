package gg.hytaleheroes.herobase.handler;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.universe.Universe;
import gg.hytaleheroes.herobase.HeroBase;
import gg.hytaleheroes.herobase.format.TinyMsg;

import java.io.IOException;

public class PlayerWelcomeHandler {
    private PlayerWelcomeHandler() {}

    public static void onPlayerJoin(PlayerConnectEvent event) {
        var config = HeroBase.INSTANCE.getConfig();

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

    }
}
