package gg.hytaleheroes.herobase.module.charm.system;

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
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import gg.hytaleheroes.herobase.module.charm.ActiveCharmsComponent;
import gg.hytaleheroes.herobase.module.charm.Charm;
import gg.hytaleheroes.herobase.module.charm.type.api.CharmEffect;
import gg.hytaleheroes.herobase.module.charm.type.api.DamageCharmEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DamageSystem extends DamageEventSystem {
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
        Player player = archetypeChunk.getComponent(i, Player.getComponentType());
        if (player == null) {
            var type = NPCEntity.getComponentType();
            if (type == null) return;

            var npc = archetypeChunk.getComponent(i, NPCEntity.getComponentType());
            if (npc == null) return;

            handleNpcDamaged(i, archetypeChunk, store, commandBuffer, damage, npc);
        } else {
            handlePlayerDamaged(i, archetypeChunk, store, commandBuffer, damage, player);
        }
    }

    void handleNpcDamaged(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage, NPCEntity entity) {
        if (damage.getSource() instanceof Damage.EntitySource entitySource) {
            var sourceRef = entitySource.getRef();
            var charmsComponent = store.getComponent(sourceRef, ActiveCharmsComponent.getComponentType());
            if (charmsComponent == null)
                return;


            charmsComponent.all().forEach((k,v) -> {
                var charm = Charm.getAssetMap().getAsset(k);
                if (charm != null) {
                    for (CharmEffect effect : charm.getEffects()) {
                        if (effect instanceof DamageCharmEffect damageCharmEffect && !damageCharmEffect.runOnEnemy()) {
                            damageCharmEffect.apply(sourceRef, store, commandBuffer, damage);
                        }
                    }
                }
            });
        }
    }

    void handlePlayerDamaged(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage, Player player) {
        ActiveCharmsComponent charmsComponent = archetypeChunk.getComponent(i, ActiveCharmsComponent.getComponentType());
        if (player == null || charmsComponent == null)
            return;

        charmsComponent.all().forEach((k,v) -> {
            var charm = Charm.getAssetMap().getAsset(k);
            if (charm != null) {
                for (CharmEffect effect : charm.getEffects()) {
                    if (effect instanceof DamageCharmEffect damageCharmEffect && damageCharmEffect.runOnEnemy()) {
                        damageCharmEffect.apply(archetypeChunk.getReferenceTo(i), store, commandBuffer, damage);
                    }
                }
            }
        });
    }

    @Nullable
    public Query<EntityStore> getQuery() {
        return Query.and(Query.or(NPCEntity.getComponentType(), Player.getComponentType()), TransformComponent.getComponentType());
    }

    @Nullable
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }
}
