package gg.hytaleheroes.herobase.command;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import gg.hytaleheroes.herobase.HeroBase;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ReloadCommand extends AbstractAsyncCommand {
    public ReloadCommand() {
        super("reload", "herobase.commands.reload.desc");
        this.addAliases("rl", "r");
        this.setPermissionGroup(GameMode.Creative);
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        HeroBase.INSTANCE.reload();
        return CompletableFuture.completedFuture(null);
    }
}

