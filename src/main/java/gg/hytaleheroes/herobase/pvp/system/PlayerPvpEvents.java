package gg.hytaleheroes.herobase.pvp.system;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.HeroBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.logging.Level;

public class PlayerPvpEvents extends DeathSystems.OnDeathSystem {
    @Nullable
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getInspectDamageGroup();
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent deathComponent, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        var conf = HeroBase.get().getPvpConfig().get().pvpConfigEntryMap.get(commandBuffer.getExternalData().getWorld().getName());
        if (conf == null) return;

        var playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        var damage = deathComponent.getDeathInfo();
        if (damage != null && playerRef != null && damage.getAmount() > 0.0F) {
            Damage.Source damageSource = damage.getSource();

            if (damageSource instanceof Damage.EntitySource entitySource) {
                Ref<EntityStore> sourceRef = entitySource.getRef();
                if (sourceRef.isValid()) {
                    PlayerRef sourcePlayerRef = commandBuffer.getComponent(sourceRef, PlayerRef.getComponentType());
                    if (sourcePlayerRef != null && sourcePlayerRef.isValid()) {
                        try {
                            HeroBase.get().getLeaderboards().recordKill(sourcePlayerRef.getUuid(), playerRef.getUuid(), conf.mode);
                        } catch (SQLException e) {
                            HytaleLogger.forEnclosingClass().at(Level.SEVERE).log("Error while recording kill", e);
                        }
                    }
                }
            }
        }
    }
}