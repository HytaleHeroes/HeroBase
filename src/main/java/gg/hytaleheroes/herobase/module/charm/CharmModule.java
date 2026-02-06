package gg.hytaleheroes.herobase.module.charm;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import gg.hytaleheroes.herobase.Module;

public class CharmModule implements Module<Void> {
    private static CharmModule INSTANCE;

    public CharmModule(JavaPlugin plugin) {
        INSTANCE = this;
    }

    public static CharmModule get() {
        return INSTANCE;
    }

    @Override
    public void start(JavaPlugin plugin) {
        //plugin.getEntityStoreRegistry().registerSystem(new InventoryChangeSystem());
    }

    @Override
    public void setup(JavaPlugin plugin) {

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
