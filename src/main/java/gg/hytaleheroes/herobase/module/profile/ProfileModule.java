package gg.hytaleheroes.herobase.module.profile;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import gg.hytaleheroes.herobase.Module;

public class ProfileModule implements Module<Void> {
    private static ProfileModule INSTANCE;

    private PlayerProfiles playerProfiles;

    public ProfileModule(JavaPlugin plugin) {
        INSTANCE = this;
    }

    public static ProfileModule get() {
        return INSTANCE;
    }

    public PlayerProfiles playerProfiles() {
        return playerProfiles;
    }

    @Override
    public void start(JavaPlugin plugin) {

    }

    @Override
    public void setup(JavaPlugin plugin) {
        this.playerProfiles = new PlayerProfiles();

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
