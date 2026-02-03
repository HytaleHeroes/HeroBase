package gg.hytaleheroes.herobase.handler;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.Ability;
import gg.hytaleheroes.herobase.HeroBase;
import gg.hytaleheroes.herobase.component.AbilityCooldownsComponent;
import gg.hytaleheroes.herobase.component.AbilityHotbarConfiguration;
import gg.hytaleheroes.herobase.gui.hud.AbilityHud;

import javax.annotation.Nonnull;

public class AbilitySlotHandler implements PlayerPacketFilter {

    @Override
    public boolean test(@Nonnull PlayerRef playerRef, @Nonnull Packet packet) {
        if (!(packet instanceof SyncInteractionChains syncPacket)) {
            return false;
        }

        var hud = HeroBase.get().getActiveHuds().get(playerRef.getUuid());

        if (hud != null)  {
            for (SyncInteractionChain chain : syncPacket.updates) {
                if (chain.interactionType == InteractionType.SwapFrom
                        && chain.data != null
                        && chain.initial) {

                    handleAbilityTrigger(playerRef, hud, chain.data.targetSlot);
                    return true;
                }
            }
        }

        return false;
    }

    private void handleAbilityTrigger(PlayerRef playerRef, AbilityHud hud, int targetSlot) {
        Ref<EntityStore> entityRef = playerRef.getReference();
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }

        Store<EntityStore> store = entityRef.getStore();
        World world = store.getExternalData().getWorld();

        world.execute(() -> {
            Player player = store.getComponent(entityRef, Player.getComponentType());
            if (player == null) {
                return;
            }

            var slotConfig = store.ensureAndGetComponent(entityRef, AbilityHotbarConfiguration.getComponentType());
            var abilityId = slotConfig.getSlots().get(targetSlot);
            if (abilityId != null) {
                var ability = Ability.getAssetMap().getAsset(abilityId);
                if (ability != null) {
                    var cooldowns = store.ensureAndGetComponent(entityRef, AbilityCooldownsComponent.getComponentType());
                    if (!cooldowns.hasCooldown(abilityId)) {
                        cast(entityRef, store, ability);
                        cooldowns.add(ability);
                    }
                }
            }

            AbilityHud.resetSlot(playerRef, player);
        });
    }

    private void cast(Ref<EntityStore> entityRef, Store<EntityStore> store, Ability ability) {
        ComponentType<EntityStore, InteractionManager> managerType = InteractionModule.get().getInteractionManagerComponent();

        var interaction = ability.getInteraction();

        InteractionManager manager = store.getComponent(entityRef, managerType);
        if (manager != null) {
            var root = RootInteraction.getAssetStore().getAssetMap().getAsset(interaction);
            if (root != null) {
                InteractionContext context = InteractionContext.forInteraction(
                        manager,
                        entityRef,
                        InteractionType.Ability3,
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
    }
}