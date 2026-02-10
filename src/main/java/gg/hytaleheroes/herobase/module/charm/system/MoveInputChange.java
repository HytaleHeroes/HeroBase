package gg.hytaleheroes.herobase.module.charm.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.charm.ActiveCharmsComponent;
import gg.hytaleheroes.herobase.module.charm.Charm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MoveInputChange extends DelayedEntitySystem<EntityStore> {
    public MoveInputChange() {
        super(1/10f);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    @Override
    public void tick(float v, int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        var ref = archetypeChunk.getReferenceTo(i);
        MovementStatesComponent movementStatesComponent = commandBuffer.getComponent(ref, MovementStatesComponent.getComponentType());
        if (movementStatesComponent != null) {
            handleMovementSet(i, archetypeChunk, ref, movementStatesComponent, store, commandBuffer);
        }
    }

    public void handleMovementSet(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Ref<EntityStore> ref, @Nonnull MovementStatesComponent newComponent, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        PlayerRef playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        ActiveCharmsComponent charms = commandBuffer.getComponent(ref, ActiveCharmsComponent.getComponentType());
        if (charms != null) {
            charms.all().forEach((k, v) -> {
                var charm = Charm.getAssetMap().getAsset(k);
                if (charm != null) {
                    charm.run(archetypeChunk.getReferenceTo(i), store, commandBuffer, MovementStatesComponent.class, newComponent);
                }
            });

        }

    }
}
