package gg.hytaleheroes.herobase;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;

public interface Module<T> {
    void start(JavaPlugin plugin);
    void setup(JavaPlugin plugin);
    void shutdown(JavaPlugin plugin);
    void reload(JavaPlugin plugin);

    T getConfig();
}
