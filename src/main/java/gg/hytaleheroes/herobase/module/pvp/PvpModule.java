package gg.hytaleheroes.herobase.module.pvp;

import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PacketFilter;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.util.Config;
import gg.hytaleheroes.herobase.Module;
import gg.hytaleheroes.herobase.module.ability.handler.AbilitySlotHandler;
import gg.hytaleheroes.herobase.module.pvp.config.PvpConfig;
import gg.hytaleheroes.herobase.module.pvp.handler.LeaderboardHandler;
import gg.hytaleheroes.herobase.module.pvp.leaderboard.Leaderboards;
import gg.hytaleheroes.herobase.module.pvp.system.LeaderboardUpdateSystem;
import gg.hytaleheroes.herobase.module.pvp.system.PlayerPvpEvents;
import gg.hytaleheroes.herobase.module.pvp.system.PreventPvpDamageFilterSystem;

public class PvpModule implements Module<PvpConfig> {
    private static PvpModule INSTANCE;

    private PacketFilter inboundFilter;
    private Leaderboards leaderboards;
    private final Config<PvpConfig> pvpConfig;

    public PvpModule(JavaPlugin plugin, Config<PvpConfig> config) {
        INSTANCE = this;
        this.pvpConfig = config;
    }

    public static PvpModule get() {
        return INSTANCE;
    }

    @Override
    public void start(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(new LeaderboardUpdateSystem(2));
        plugin.getEntityStoreRegistry().registerSystem(new PlayerPvpEvents());
        plugin.getEntityStoreRegistry().registerSystem(new PreventPvpDamageFilterSystem());
    }

    @Override
    public void setup(JavaPlugin plugin) {
        this.pvpConfig.save();
        this.leaderboards = new Leaderboards();

        this.pvpConfig.load().thenAccept(x -> this.pvpConfig.save()).join();

        AbilitySlotHandler handler = new AbilitySlotHandler();
        this.inboundFilter = PacketAdapters.registerInbound(handler);

        plugin.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, LeaderboardHandler::onAddToWorld);
        plugin.getEventRegistry().register(PlayerConnectEvent.class, LeaderboardHandler::onConnect);
    }

    @Override
    public void shutdown(JavaPlugin plugin) {
        if (this.inboundFilter != null) {
            PacketAdapters.deregisterInbound(this.inboundFilter);
        }

        if (this.leaderboards != null) {
            this.leaderboards.shutdown();
        }
    }

    @Override
    public void reload(JavaPlugin plugin) {
        pvpConfig.load();
    }

    @Override
    public PvpConfig getConfig() {
        return pvpConfig.get();
    }

    public Leaderboards leaderboards() {
        return leaderboards;
    }
}
