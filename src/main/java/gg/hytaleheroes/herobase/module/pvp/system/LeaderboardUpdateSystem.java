package gg.hytaleheroes.herobase.module.pvp.system;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.pvp.PvpModule;
import gg.hytaleheroes.herobase.module.pvp.gui.hud.LeaderboardHud;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LeaderboardUpdateSystem extends DelayedEntitySystem<EntityStore> {
    public LeaderboardUpdateSystem(float intervalSec) {
        super(intervalSec);
    }

    @Override
    public void tick(float v, int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        var conf = PvpModule.get().getConfig().getByWorldName(commandBuffer.getExternalData().getWorld().getName());
        if (conf == null || !conf.leaderboard)
            return;

        var player = archetypeChunk.getComponent(i, Player.getComponentType());
        if (player == null)
            return;

        var playerRef = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
        if (playerRef == null)
            return;


        MultipleHUD.getInstance().setCustomHud(player, playerRef, LeaderboardHud.ID, new LeaderboardHud(playerRef, commandBuffer.getExternalData().getWorld().getName(), conf.mode));
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), PlayerRef.getComponentType());
    }
}
