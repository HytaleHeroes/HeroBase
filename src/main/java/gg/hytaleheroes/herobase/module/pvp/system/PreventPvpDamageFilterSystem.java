package gg.hytaleheroes.herobase.module.pvp.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.pvp.PvpModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PreventPvpDamageFilterSystem extends DamageEventSystem {
    @Nonnull
    private static final Query<EntityStore> QUERY = Player.getComponentType();

    @Nullable
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    public Query<EntityStore> getQuery() {
        return QUERY;
    }

    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
        World world = store.getExternalData().getWorld();
        var conf = PvpModule.get().getConfig().getByName(world.getName());
        if (conf == null) return;

        Player playerComponent = archetypeChunk.getComponent(index, Player.getComponentType());
        TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());

        assert playerComponent != null;
        assert transformComponent != null;

        if (playerComponent.hasSpawnProtection()) {
            damage.setAmount(0);
            damage.setCancelled(true);
        } else {
            if (world.getWorldConfig().isPvpEnabled()) {
                if (transformComponent.getTransform().getPosition().getY() > conf.preventPvpAbove) {
                    damage.setAmount(0);
                    damage.setCancelled(true);
                }
            }
        }
    }
}