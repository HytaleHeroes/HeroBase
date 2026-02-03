package gg.hytaleheroes.herobase.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.HeroBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.logging.Level;

public class PlayerPvpEvents extends DamageEventSystem {
    @Nullable
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getInspectDamageGroup();
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
        boolean isDead = archetypeChunk.getArchetype().contains(DeathComponent.getComponentType());
        var playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
        if (isDead && playerRef != null && !(damage.getAmount() <= 0.0F)) {
            Damage.Source damageSource = damage.getSource();
            if (damageSource instanceof Damage.EntitySource entitySource) {
                Ref<EntityStore> sourceRef = entitySource.getRef();
                if (sourceRef.isValid()) {
                    PlayerRef sourcePlayerRef = commandBuffer.getComponent(sourceRef, PlayerRef.getComponentType());
                    if (sourcePlayerRef != null && sourcePlayerRef.isValid()) {
                        try {
                            HeroBase.get().getLeaderboards().recordKill(sourcePlayerRef.getUuid(), playerRef.getUuid(), "ffa");
                        } catch (SQLException e) {
                            HytaleLogger.forEnclosingClass().at(Level.SEVERE).log("Error while recording kill", e);
                        }
                    }
                }
            }
        }
    }
}