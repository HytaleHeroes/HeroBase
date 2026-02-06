package gg.hytaleheroes.herobase.module.pvp.handler;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import gg.hytaleheroes.herobase.module.pvp.PvpModule;
import gg.hytaleheroes.herobase.module.pvp.gui.hud.LeaderboardHud;

import java.sql.SQLException;
import java.util.logging.Level;

public class LeaderboardHandler {
    public static void onAddToWorld(AddPlayerToWorldEvent event) {
        var conf = PvpModule.get().getConfig().getByWorldName(event.getWorld().getName());
        if (conf != null && conf.leaderboard) {
            var playerRef = event.getHolder().getComponent(PlayerRef.getComponentType());
            var player = event.getHolder().getComponent(Player.getComponentType());
            if (playerRef != null && player != null && player.getWorld() != null) {
                MultipleHUD.getInstance().setCustomHud(player, playerRef, LeaderboardHud.ID, new LeaderboardHud(playerRef, player.getWorld().getName(), conf.mode));
            }
        } else {
            var player = event.getHolder().getComponent(Player.getComponentType());
            MultipleHUD.getInstance().hideCustomHud(player, LeaderboardHud.ID);
        }
    }

    public static void onConnect(PlayerConnectEvent event) {
        try {
            PvpModule.get().leaderboards().updatePlayer(event.getPlayerRef().getUuid(), event.getPlayerRef().getUsername());
        } catch (SQLException e) {
            HytaleLogger.forEnclosingClass().at(Level.SEVERE).log("Could not save player information!");
        }
    }
}
