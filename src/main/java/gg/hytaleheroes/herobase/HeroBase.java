package gg.hytaleheroes.herobase;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.npc.NPCPlugin;
import gg.hytaleheroes.herobase.core.command.HeroBaseCommand;
import gg.hytaleheroes.herobase.core.config.DatabaseConfig;
import gg.hytaleheroes.herobase.core.config.ModConfig;
import gg.hytaleheroes.herobase.extra.action.BuilderActionSendMessage;
import gg.hytaleheroes.herobase.extra.navigator.NavigatorCommand;
import gg.hytaleheroes.herobase.extra.navigator.NavigatorConfig;
import gg.hytaleheroes.herobase.extra.shop.ShopCommand;
import gg.hytaleheroes.herobase.extra.shop.asset.ServerShop;
import gg.hytaleheroes.herobase.module.ability.AbilityModule;
import gg.hytaleheroes.herobase.module.charm.CharmModule;
import gg.hytaleheroes.herobase.module.profile.PlayerProfilePictureAsset;
import gg.hytaleheroes.herobase.module.profile.ProfileCommand;
import gg.hytaleheroes.herobase.module.profile.ProfileModule;
import gg.hytaleheroes.herobase.module.pvp.PvpModule;
import gg.hytaleheroes.herobase.module.pvp.command.LeaderboardCommand;
import gg.hytaleheroes.herobase.module.pvp.config.PvpConfig;
import gg.hytaleheroes.herobase.module.pvp.leaderboard.DatabaseManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HeroBase extends JavaPlugin {
    private static HeroBase INSTANCE;

    private DatabaseManager databaseManager;
    private final Config<ModConfig> config;
    private final Config<NavigatorConfig> navigatorConfig;
    private final Config<DatabaseConfig> dbConfig;

    Set<Module<?>> modules = ConcurrentHashMap.newKeySet();

    public HeroBase(@Nonnull JavaPluginInit init) {
        super(init);

        INSTANCE = this;

        this.config = this.withConfig("HeroBase", ModConfig.CODEC);
        this.navigatorConfig = this.withConfig("Navigator", NavigatorConfig.CODEC);
        this.dbConfig = this.withConfig("Database", DatabaseConfig.CODEC);

        this.modules.add(new PvpModule(this, this.withConfig("Pvp", PvpConfig.CODEC)));
        this.modules.add(new AbilityModule(this));
        this.modules.add(new CharmModule(this));
        this.modules.add(new ProfileModule(this));
    }

    public static HeroBase get() {
        return INSTANCE;
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    @Override
    protected void setup() {
        super.setup();

        INSTANCE = this;

        for (Config<?> config1 : List.of(config, navigatorConfig, dbConfig)) {
            config1.load().thenAccept(x -> config1.save()).join();
        }

        var dbCfg = this.dbConfig.get();

        this.databaseManager = new DatabaseManager(dbCfg);

        this.modules.forEach(x -> x.setup(this));

        this.getEventRegistry().register(PlayerDisconnectEvent.class, (event) -> {
            PlayerProfilePictureAsset.deleteCache(event.getPlayerRef().getUsername());
        });

        this.getCommandRegistry().registerCommand(new HeroBaseCommand());

        this.getCommandRegistry().registerCommand(new ProfileCommand());
        this.getCommandRegistry().registerCommand(new NavigatorCommand());

        this.getCommandRegistry().registerCommand(new ShopCommand());
        this.getAssetRegistry().register(HytaleAssetStore.builder(ServerShop.class, new DefaultAssetMap<>()).setPath("ServerShop").setCodec(ServerShop.CODEC).setKeyFunction(ServerShop::getId).build());

        this.getCommandRegistry().registerCommand(new LeaderboardCommand());

        NPCPlugin.get().registerCoreComponentType("SendMessage", BuilderActionSendMessage::new);
    }

    @Override
    protected void start() {
        super.start();

        this.modules.forEach(x -> x.start(this));
    }

    @Override
    protected void shutdown() {
        super.shutdown();

        this.modules.forEach(x -> x.shutdown(this));
    }

    public Config<ModConfig> getConfig() {
        return this.config;
    }

    public Config<NavigatorConfig> getNavigatorConfig() {
        return this.navigatorConfig;
    }

    public void reload() {
        this.config.load();
        this.dbConfig.load();
        this.navigatorConfig.load();
        this.modules.forEach(x -> x.reload(this));
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not on classpath", e);
        }
    }
}