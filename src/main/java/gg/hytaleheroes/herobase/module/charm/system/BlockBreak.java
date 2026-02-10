package gg.hytaleheroes.herobase.module.charm.system;

import com.buuz135.simpleclaims.systems.events.BreakBlockEventSystem;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.charm.ActiveCharmsComponent;
import gg.hytaleheroes.herobase.module.charm.Charm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

public class BlockBreak extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    public BlockBreak() {
        super(BreakBlockEvent.class);
    }

    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(new SystemDependency<>(Order.AFTER, BreakBlockEventSystem.class));
    }

    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull BreakBlockEvent breakBlockEvent) {
        if (breakBlockEvent.isCancelled()) {
            return;
        }

        var comp = archetypeChunk.getComponent(i, ActiveCharmsComponent.getComponentType());
        if (comp != null) {
            comp.all().forEach((k,v) -> {
                var charm = Charm.getAssetMap().getAsset(k);
                if (charm != null) {
                    charm.run(archetypeChunk.getReferenceTo(i), store, commandBuffer, BreakBlockEvent.class, breakBlockEvent);
                }
            });
        }
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return ActiveCharmsComponent.getComponentType();
    }
}