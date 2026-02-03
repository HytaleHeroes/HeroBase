package gg.hytaleheroes.herobase.system;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.gui.hud.LeaderboardHud;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LeaderboardUpdateSystem extends DelayedEntitySystem<EntityStore> {
    public LeaderboardUpdateSystem(float intervalSec) {
        super(intervalSec);
    }

    @Override
    public void tick(float v, int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        var player = archetypeChunk.getComponent(i, Player.getComponentType());
        if (player == null)
            return;

        var playerRef = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
        if (playerRef == null)
            return;

        MultipleHUD.getInstance().hideCustomHud(player, LeaderboardHud.ID);
        MultipleHUD.getInstance().setCustomHud(player, playerRef, LeaderboardHud.ID, new LeaderboardHud(playerRef));
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), PlayerRef.getComponentType());
    }
}
