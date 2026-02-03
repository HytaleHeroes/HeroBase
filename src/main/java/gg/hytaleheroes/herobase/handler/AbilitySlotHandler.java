package gg.hytaleheroes.herobase.handler;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.HeroBase;
import gg.hytaleheroes.herobase.gui.hud.AbilityHud;

import javax.annotation.Nonnull;

public class AbilitySlotHandler implements PlayerPacketFilter {
    private static final int ABILITY_SLOT = 8;  // Slot index 8 = Key "9"


    @Override
    public boolean test(@Nonnull PlayerRef playerRef, @Nonnull Packet packet) {
        if (!(packet instanceof SyncInteractionChains syncPacket)) {
            return false;
        }

        var hud = HeroBase.get().activeHuds().get(playerRef.getUuid());

        if (hud != null)  {
            for (SyncInteractionChain chain : syncPacket.updates) {
                if (chain.interactionType == InteractionType.SwapFrom
                        && chain.data != null
                        && chain.initial) {

                    handleAbilityTrigger(playerRef, hud);
                    return true;
                }
            }
        }

        return false;
    }

    private void handleAbilityTrigger(PlayerRef playerRef, AbilityHud hud) {
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

            AbilityHud.resetSlot(playerRef, player);

            ComponentType<EntityStore, InteractionManager> managerType = InteractionModule.get().getInteractionManagerComponent();

            var interaction = "Dash_Skill";

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
        });
    }
}