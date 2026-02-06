package gg.hytaleheroes.herobase.module.ability;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import gg.hytaleheroes.herobase.Module;
import gg.hytaleheroes.herobase.module.ability.command.AbilityCommand;
import gg.hytaleheroes.herobase.module.ability.component.AbilityCooldownsComponent;
import gg.hytaleheroes.herobase.module.ability.component.AbilityHotbarConfiguration;
import gg.hytaleheroes.herobase.module.ability.component.UnlockedAbilitiesComponent;
import gg.hytaleheroes.herobase.module.ability.gui.hud.AbilityHud;
import gg.hytaleheroes.herobase.module.ability.system.AbilityKeybindSystem;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityModule implements Module<Void> {
    private static AbilityModule INSTANCE;

    private ConcurrentHashMap<UUID, AbilityHud> activeHuds;


    public AbilityModule(JavaPlugin plugin) {
        INSTANCE = this;
    }

    public static AbilityModule get() {
        return INSTANCE;
    }

    @Override
    public void start(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(new AbilityKeybindSystem());
    }

    @Override
    public void setup(JavaPlugin plugin) {
        this.activeHuds = new ConcurrentHashMap<>();

        UnlockedAbilitiesComponent.setup(plugin.getEntityStoreRegistry());
        AbilityCooldownsComponent.setup(plugin.getEntityStoreRegistry());
        AbilityHotbarConfiguration.setup(plugin.getEntityStoreRegistry());

        plugin.getAssetRegistry().register(HytaleAssetStore.builder(Ability.class, new DefaultAssetMap<>()).setPath("Abilities").setCodec(Ability.CODEC).setKeyFunction(Ability::getId).build());

        plugin.getCommandRegistry().registerCommand(new AbilityCommand());
    }

    @Override
    public void shutdown(JavaPlugin plugin) {
        if (this.activeHuds != null) {
            this.activeHuds.clear();
        }
    }

    @Override
    public void reload(JavaPlugin plugin) {

    }

    @Override
    public Void getConfig() {
        return null;
    }

    public ConcurrentHashMap<UUID, AbilityHud> getActiveHuds() {
        return this.activeHuds;
    }
}
