package gg.hytaleheroes.herobase.module.charm.handler;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.charm.ActiveCharmsComponent;
import gg.hytaleheroes.herobase.module.charm.Charm;
import gg.hytaleheroes.herobase.module.charm.CharmsChangedEvent;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import javax.annotation.Nonnull;
import java.util.Map;

public class CharmInventoryChangeHandler {
    private CharmInventoryChangeHandler() {}

    public static void handle(PlayerRef playerRefComponent, ItemContainer inventory, @Nonnull Store<EntityStore> store, World world) {
        Map<String, IntList> charms = new Object2ObjectArrayMap<>();
        inventory.forEach((slot, stack) -> {
            var charmForItem = Charm.getAssetMap().getAsset(stack.getItem().getId());
            if (charmForItem != null) {
                charms.computeIfAbsent(charmForItem.getId(), k -> new IntArrayList()).add(slot);
            }
        });

        var ref = playerRefComponent.getReference();
        if (ref != null) {
            var comp = new ActiveCharmsComponent();
            charms.forEach((k, v) -> comp.all().put(k, v.toIntArray()));
            world.execute(() -> {
                store.putComponent(ref, ActiveCharmsComponent.getComponentType(), comp);

                var dispatcher = HytaleServer.get().getEventBus().dispatchFor(CharmsChangedEvent.class, world.getName());
                if (dispatcher.hasListener() && playerRefComponent.getHolder() != null) {
                    dispatcher.dispatch(new CharmsChangedEvent(comp, playerRefComponent.getHolder(), playerRefComponent, world));
                }
            });
        }
    }
}