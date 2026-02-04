package gg.hytaleheroes.herobase.charm.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class InventoryChangeSystem extends EntityTickingSystem<EntityStore> {
    private final ComponentType<EntityStore, Player> componentType = Player.getComponentType();
    private final ComponentType<EntityStore, PlayerRef> refComponentType = PlayerRef.getComponentType();
    private final Query<EntityStore> query = Query.and(componentType, refComponentType);

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }

    @Override
    public boolean isParallel(int archetypeChunkSize, int taskCount) {
        return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Player playerComponent = archetypeChunk.getComponent(index, this.componentType);

        assert playerComponent != null;

        Inventory inventory = playerComponent.getInventory();
        if (inventory.consumeIsDirty()) {
            PlayerRef playerRefComponent = archetypeChunk.getComponent(index, this.refComponentType);

            assert playerRefComponent != null;

            inventory.getCombinedEverything().forEach((slot, stack) -> {


            });
        }

        //playerComponent.getWindowManager().updateWindows();
    }
}