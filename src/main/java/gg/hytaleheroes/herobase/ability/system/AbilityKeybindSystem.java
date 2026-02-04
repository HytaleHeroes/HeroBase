package gg.hytaleheroes.herobase.ability.system;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.packets.interface_.HudComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.ability.Ability;
import gg.hytaleheroes.herobase.HeroBase;
import gg.hytaleheroes.herobase.ability.component.AbilityHotbarConfiguration;
import gg.hytaleheroes.herobase.ability.component.UnlockedAbilitiesComponent;
import gg.hytaleheroes.herobase.ability.gui.hud.AbilityHud;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// TODO: this could be a RefChangeSystem too?
public class AbilityKeybindSystem extends EntityTickingSystem<EntityStore> {
    private static final String HUD_KEY = "HeroAbilities";

    private final List<Ability> abilities = List.of(
            new Ability("Dash_Skill", "Dash", "beam-acid-2.png", ("Dash_Skill")),
            new Ability("Staff_Cast_Summon_Charged", "Cast", "enchant-acid-2.png", ("Staff_Cast_Summon_Charged")),
            new Ability("Stoneskin_Cast", "Stoneskin", "explosion-magenta-2.png", ("Stoneskin_Cast")),
            new Ability("Skeleton_Burnt_Alchemist_Bomb_Throw", "Bomb Throw", "haste-sky-2.png", ("Skeleton_Burnt_Alchemist_Bomb_Throw")),
            new Ability("Bow_Bomb_Boomshot", "Boomshot", "horror-acid-2.png", ("Bow_Bomb_Boomshot"))
    );

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        MovementStatesComponent statesComponent = archetypeChunk.getComponent(index, MovementStatesComponent.getComponentType());
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        if (player == null || !player.hasPermission("hero.abilities"))
            return;

        PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
        HeadRotation headRotation = archetypeChunk.getComponent(index, HeadRotation.getComponentType());
        TransformComponent transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());

        var huds = HeroBase.get().getActiveHuds();

        if (transform != null && headRotation != null && statesComponent != null && playerRef != null && playerRef.isValid()) {
            MovementStates movementStates = statesComponent.getMovementStates();

            AbilityHud currentHud = huds.get(playerRef.getUuid());
            boolean hasHud = currentHud != null;

            if (movementStates.walking && !hasHud) {
                var comp = archetypeChunk.getComponent(index, AbilityHotbarConfiguration.getComponentType());
                if (comp != null) {
                    if (!comp.getSlots().isEmpty()) {
                        player.getHudManager().hideHudComponents(playerRef, HudComponent.Hotbar);

                        var hud = new AbilityHud(playerRef, comp.getSlots(), player.getInventory().getActiveHotbarSlot());
                        MultipleHUD.getInstance().setCustomHud(player, playerRef, HUD_KEY, hud);
                        huds.put(playerRef.getUuid(), hud);

                        AbilityHud.resetSlot(playerRef, player);
                    }
                }
            } else if (!movementStates.walking && hasHud) {
                player.getHudManager().showHudComponents(playerRef, HudComponent.Hotbar);
                MultipleHUD.getInstance().hideCustomHud(player, HUD_KEY);
                var old = huds.remove(playerRef.getUuid());
                if (old != null) {
                    AbilityHud.resetSlot(playerRef, player, old.getOldSlot());
                }
            }
        }
    }

    @Nullable
    public Query<EntityStore> getQuery() {
        return Query.and(MovementStatesComponent.getComponentType(), Player.getComponentType());
    }
}