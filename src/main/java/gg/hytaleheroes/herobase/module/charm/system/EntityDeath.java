package gg.hytaleheroes.herobase.module.charm.system;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import gg.hytaleheroes.herobase.module.charm.ActiveCharmsComponent;
import gg.hytaleheroes.herobase.module.charm.Charm;
import gg.hytaleheroes.herobase.module.charm.type.api.CharmEffect;
import gg.hytaleheroes.herobase.module.charm.type.impl.MobLootCharmEffect;

import javax.annotation.Nonnull;

public class EntityDeath extends DeathSystems.OnDeathSystem {
    private final Query<EntityStore> query;

    public EntityDeath() {
        this.query = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, Query.not(Player.getComponentType()));
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }

    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        NPCEntity npcComponent = commandBuffer.getComponent(ref, NPCEntity.getComponentType());
        TransformComponent transformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
        Damage damageInfo = component.getDeathInfo();
        if (transformComponent != null && npcComponent != null && npcComponent.getRole() != null && damageInfo != null && damageInfo.getSource() instanceof Damage.EntitySource entitySource) {
            var sourceRef = entitySource.getRef();
            var player = commandBuffer.getComponent(sourceRef, Player.getComponentType());
            if (player == null) return;

            var comp = commandBuffer.getComponent(sourceRef, ActiveCharmsComponent.getComponentType());
            if (comp == null) return;

            comp.all().forEach((k,v) -> {
                var charm = Charm.getAssetMap().getAsset(k);
                if (charm != null ) {
                    for (CharmEffect effect : charm.getEffects()) {
                        if (effect instanceof MobLootCharmEffect mobLootCharmEffect)
                            mobLootCharmEffect.apply(ref, store, commandBuffer, npcComponent, transformComponent);
                    }
                }
            });
        }
    }
}
