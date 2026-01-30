package gg.hytaleheroes.herobase.system;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.packets.interface_.HudComponent;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.gui.hud.AbilityHud;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// TODO: this could be a RefChangeSystem too?
public class AbilityKeybindSystem extends EntityTickingSystem<EntityStore> {
    private static final String HUD_KEY = "HeroAbilities";

    private Map<String, String> abilities = Map.of(
            "Dash", "Dash_Skill",
            "Cast", "Staff_Cast_Summon_Charged",
            "Stoneskin", "Stoneskin_Cast",
            "Bomb Throw", "Skeleton_Burnt_Alchemist_Bomb_Throw",
            "Boomshot", "Bow_Bomb_Boomshot"
    );

    private ConcurrentHashMap<UUID, AbilityHud> activeHuds = new ConcurrentHashMap<>();

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        MovementStatesComponent statesComponent = archetypeChunk.getComponent(index, MovementStatesComponent.getComponentType());
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
        HeadRotation headRotation = archetypeChunk.getComponent(index, HeadRotation.getComponentType());
        TransformComponent transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
        if (transform != null && headRotation != null && player != null && statesComponent != null && playerRef != null && playerRef.isValid()) {
            MovementStates movementStates = statesComponent.getMovementStates();



            AbilityHud currentHud = activeHuds.get(playerRef.getUuid());
            boolean hasHud = currentHud != null;

            if (movementStates.walking && !hasHud) {
                player.getHudManager().hideHudComponents(playerRef, HudComponent.Hotbar);
                var hud = new AbilityHud(playerRef, headRotation.getDirection().normalize(), abilities);
                MultipleHUD.getInstance().setCustomHud(player, playerRef, HUD_KEY, hud);
                activeHuds.put(playerRef.getUuid(), hud);
            } else if (!movementStates.walking && hasHud) {
                MultipleHUD.getInstance().hideCustomHud(player, HUD_KEY);
                var old = activeHuds.remove(playerRef.getUuid());
                if (old != null) {

                    player.getHudManager().showHudComponents(playerRef, HudComponent.Hotbar);
                    player.getHudManager().showHudComponents(playerRef, HudComponent.Speedometer);
                    player.getHudManager().showHudComponents(playerRef, HudComponent.PlayerList);

                    player.getHotbarManager().getCurrentHotbarIndex();

                    ComponentType<EntityStore, InteractionManager> managerType = InteractionModule.get().getInteractionManagerComponent();

                    InteractionManager manager = archetypeChunk.getComponent(index, managerType);
                    String interaction = old.selectedInteraction();
                    if (manager != null && interaction != null) {
                        var root = RootInteraction.getAssetStore().getAssetMap().getAsset(interaction);
                        if (root != null) {
                            InteractionContext context = InteractionContext.forInteraction(
                                    manager,
                                    player.getReference(),
                                    InteractionType.CollisionLeave,
                                    -1,
                                    store
                            );

                            InteractionChain chain = manager.initChain(
                                    InteractionType.CollisionLeave,
                                    context,
                                    root,
                                    true
                            );

                            manager.queueExecuteChain(chain);
                        }
                    }


                    old.close(manager);
                }
            } else if (hasHud) {
                currentHud.updateDirection(headRotation.getDirection().normalize());
            }
        }
    }

    @Nullable
    public Query<EntityStore> getQuery() {
        return Query.and(MovementStatesComponent.getComponentType(), Player.getComponentType());
    }
}