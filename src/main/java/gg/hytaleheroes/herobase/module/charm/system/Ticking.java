package gg.hytaleheroes.herobase.module.charm.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.charm.ActiveCharmsComponent;
import gg.hytaleheroes.herobase.module.charm.Charm;
import gg.hytaleheroes.herobase.module.charm.type.api.CharmEffect;
import gg.hytaleheroes.herobase.module.charm.type.api.TickingCharmEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Ticking extends DelayedEntitySystem<EntityStore> {
    long t = 0;

    public Ticking() {
        super(1);
    }

    @Override
    public void tick(float v, int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        ActiveCharmsComponent c = archetypeChunk.getComponent(i, ActiveCharmsComponent.getComponentType());
        if (c != null) c.all().forEach((x, val) -> {
            var charm = Charm.getAssetMap().getAsset(x);
            if (charm != null) {
                for (CharmEffect effect : charm.getEffects()) {
                    if (effect instanceof TickingCharmEffect tickingCharmEffect) {
                        int interval = tickingCharmEffect.getInterval();
                        if (interval == 0 || t % interval == 0)
                            tickingCharmEffect.tick(i, archetypeChunk, store, commandBuffer);
                    }
                }
            }
        });

        t++;
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return ActiveCharmsComponent.getComponentType();
    }
}
