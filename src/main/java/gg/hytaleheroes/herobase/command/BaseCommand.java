package gg.hytaleheroes.herobase.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class BaseCommand extends AbstractPlayerCommand { // checkout the other base command classes too
    public BaseCommand() {
        super("herobase", "herobase.commands.herobase.desc");
        this.requirePermission("herobase.herobase");
        this.setPermissionGroup(GameMode.Creative);
        this.addAliases("hb");
        this.addSubCommand(new ReloadCommand());
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        context.sender().sendMessage(Message.raw("HeroBase mod"));
    }
}

