package gg.hytaleheroes.herobase.module.profile;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.pvp.PvpModule;

import javax.annotation.Nonnull;
import java.sql.SQLException;

public class ProfileCommand extends AbstractPlayerCommand {
    OptionalArg<String> playerArg;

    public ProfileCommand() {
        super("profile", "herobase.commands.profile.desc");
        this.requirePermission("herobase.profile");

        playerArg = this.withOptionalArg("player", "Player name", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        var player = store.getComponent(ref, Player.getComponentType());

        var arg = playerArg.get(context);
        if (arg == null) arg = playerRef.getUsername();
        else {
            try {
                var id = PvpModule.get().leaderboards().getId(arg);
                if (id == null) {
                    playerRef.sendMessage(Message.raw("Could not find player profile for "));
                }
            } catch (SQLException e) {

            }



        }

        if (player != null) {
            player.getPageManager().openCustomPage(ref, store, new ProfilePage(playerRef, arg, CustomPageLifetime.CanDismiss));
        }
    }
}

