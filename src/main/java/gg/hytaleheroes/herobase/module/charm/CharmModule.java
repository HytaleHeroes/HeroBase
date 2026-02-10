package gg.hytaleheroes.herobase.module.charm;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import gg.hytaleheroes.herobase.Module;
import gg.hytaleheroes.herobase.module.charm.handler.CharmInventoryChangeHandler;
import gg.hytaleheroes.herobase.module.charm.system.BlockBreak;
import gg.hytaleheroes.herobase.module.charm.system.DamageSystem;
import gg.hytaleheroes.herobase.module.charm.system.EntityDeath;
import gg.hytaleheroes.herobase.module.charm.system.MoveInputChange;
import gg.hytaleheroes.herobase.module.charm.type.*;

public class CharmModule implements Module<Void> {
    private static CharmModule INSTANCE;

    public CharmModule(JavaPlugin plugin) {
        INSTANCE = this;

        CharmEffect.CODEC.register("DamageEntityEffect", DamageEntityEffectCharmEffect.class, DamageEntityEffectCharmEffect.CODEC);
        CharmEffect.CODEC.register("DamageModifier", DamageModifierCharmEffect.class, DamageModifierCharmEffect.CODEC);
        CharmEffect.CODEC.register("BlockLoot", BlockLootCharmEffect.class, BlockLootCharmEffect.CODEC);
        CharmEffect.CODEC.register("EntityEffect", EntityEffectCharmEffect.class, EntityEffectCharmEffect.CODEC);
        CharmEffect.CODEC.register("MobLoot", MobLootCharmEffect.class, MobLootCharmEffect.CODEC);
        CharmEffect.CODEC.register("Multijump", MultijumpCharmEffect.class, MultijumpCharmEffect.CODEC);
    }

    public static CharmModule get() {
        return INSTANCE;
    }

    @Override
    public void start(JavaPlugin plugin) {
    }

    @Override
    public void setup(JavaPlugin plugin) {
        MultijumpCharmEffect.MultijumpCounter.setup(plugin.getEntityStoreRegistry());
        ActiveCharmsComponent.setup(plugin.getEntityStoreRegistry());

        plugin.getEntityStoreRegistry().registerSystem(new BlockBreak());
        plugin.getEntityStoreRegistry().registerSystem(new EntityDeath());
        plugin.getEntityStoreRegistry().registerSystem(new DamageSystem());
        plugin.getEntityStoreRegistry().registerSystem(new MoveInputChange());

        plugin.getAssetRegistry().register(HytaleAssetStore.builder(Charm.class, new DefaultAssetMap<>()).setPath("Charms").setCodec(Charm.CODEC).setKeyFunction(Charm::getId).build());

        plugin.getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, (x) -> {
            if (x.getEntity() instanceof Player player && player.getReference() != null) {
                var playerRef = player.getReference().getStore().getComponent(player.getReference(), PlayerRef.getComponentType());

                if (playerRef != null)
                    CharmInventoryChangeHandler.handle(playerRef, player.getInventory().getBackpack(), player.getReference().getStore(), player.getWorld());
            }
        });

        plugin.getEventRegistry().registerGlobal(CharmsChangedEvent.class, (event) -> {
            event.getComponent().all().forEach((k, v) -> {
                assert event.getWorld() != null;

                var ref = event.getPlayerRef().getReference();
                if (ref == null) return;

                var store = event.getWorld().getEntityStore().getStore();
                var stateMap = store.getComponent(ref, EntityStatMap.getComponentType());

                if (stateMap == null) return;

                for (int i : v) {
                    stateMap.removeModifier(DefaultEntityStatTypes.getHealth(), "Charm_Slot_" + i);
                }
            });
        });
    }

    @Override
    public void shutdown(JavaPlugin plugin) {

    }

    @Override
    public void reload(JavaPlugin plugin) {

    }

    @Override
    public Void getConfig() {
        return null;
    }
}
