package gg.hytaleheroes.herobase.extra.profile;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class ProfileCommand extends AbstractPlayerCommand {
    public ProfileCommand() {
        super("profile", "herobase.commands.profile.desc");
        this.requirePermission("herobase.profile");
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        var player = store.getComponent(ref, Player.getComponentType());

        if (player != null) {
            player.getPageManager().openCustomPage(ref, store, new ProfilePage(playerRef, CustomPageLifetime.CanDismiss));
        }
    }
}

