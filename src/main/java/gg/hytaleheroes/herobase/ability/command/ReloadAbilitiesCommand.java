package gg.hytaleheroes.herobase.ability.command;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import gg.hytaleheroes.herobase.ability.Ability;
import gg.hytaleheroes.herobase.ability.component.AbilityHotbarConfiguration;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ReloadAbilitiesCommand extends AbstractAsyncCommand {
    public ReloadAbilitiesCommand() {
        super("reload", "herobase.commands.ability.reload.desc");
        this.addAliases("rl", "r");
        this.setPermissionGroup(GameMode.Creative);
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        if (commandContext.sender() instanceof Player player && player.getWorld() != null) {
            return CompletableFuture.runAsync(() -> {
                if (player.getReference() != null) {
                    var store = player.getWorld().getEntityStore().getStore();
                    var comp = store.ensureAndGetComponent(player.getReference(), AbilityHotbarConfiguration.getComponentType());

                    comp.getSlots().clear();

                    int i = 0;
                    for (Ability ability : Ability.getAssetMap().getAssetMap().values()) {
                        comp.getSlots().put(i, ability.getId());

                        i++;

                        if (i > 8) {
                            break;
                        }
                    }

                    store.replaceComponent(player.getReference(), AbilityHotbarConfiguration.getComponentType(), comp);
                }
            }, player.getWorld());
        }

        return CompletableFuture.completedFuture(null);
    }
}

