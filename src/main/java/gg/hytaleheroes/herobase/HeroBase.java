package gg.hytaleheroes.herobase;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.npc.NPCPlugin;
import gg.hytaleheroes.herobase.ability.AbilityModule;
import gg.hytaleheroes.herobase.charm.system.CharmModule;
import gg.hytaleheroes.herobase.core.command.HeroBaseCommand;
import gg.hytaleheroes.herobase.core.config.DatabaseConfig;
import gg.hytaleheroes.herobase.core.config.ModConfig;
import gg.hytaleheroes.herobase.extra.action.BuilderActionSendMessage;
import gg.hytaleheroes.herobase.extra.handler.PlayerWelcomeHandler;
import gg.hytaleheroes.herobase.extra.leaderboard.LeaderboardCommand;
import gg.hytaleheroes.herobase.extra.navigator.NavigatorCommand;
import gg.hytaleheroes.herobase.extra.profile.PlayerProfileAsset;
import gg.hytaleheroes.herobase.extra.profile.ProfileCommand;
import gg.hytaleheroes.herobase.pvp.PvpModule;
import gg.hytaleheroes.herobase.pvp.config.PvpConfig;
import gg.hytaleheroes.herobase.pvp.leaderboard.DatabaseManager;

import javax.annotation.Nonnull;

public class HeroBase extends JavaPlugin {
    private static HeroBase INSTANCE;

    private DatabaseManager databaseManager;
    private final Config<ModConfig> config;
    private final Config<DatabaseConfig> dbConfig;

    private final PvpModule pvpModule;
    private final AbilityModule abilityModule;
    private final CharmModule charmModule;

    public HeroBase(@Nonnull JavaPluginInit init) {
        super(init);
        this.config = this.withConfig("HeroBase", ModConfig.CODEC);
        this.dbConfig = this.withConfig("Database", DatabaseConfig.CODEC);

        this.pvpModule = new PvpModule(this, this.withConfig("Pvp", PvpConfig.CODEC));
        this.abilityModule = new AbilityModule(this);
        this.charmModule = new CharmModule(this);
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

        this.config.save();
        this.dbConfig.save();

        var dbCfg = this.dbConfig.get();

        this.databaseManager = new DatabaseManager(dbCfg);

        this.abilityModule.setup(this);
        this.pvpModule.setup(this);
        this.charmModule.setup(this);

        this.getEventRegistry().register(PlayerConnectEvent.class, PlayerWelcomeHandler::onPlayerJoin);
        this.getEventRegistry().register(PlayerDisconnectEvent.class, (event) -> {
            PlayerProfileAsset.deleteCache(event.getPlayerRef().getUsername());
        });

        this.getCommandRegistry().registerCommand(new HeroBaseCommand());

        this.getCommandRegistry().registerCommand(new ProfileCommand());
        this.getCommandRegistry().registerCommand(new NavigatorCommand());
        this.getCommandRegistry().registerCommand(new LeaderboardCommand());

        NPCPlugin.get().registerCoreComponentType("SendMessage", BuilderActionSendMessage::new);
    }

    @Override
    protected void start() {
        super.start();

        this.abilityModule.start(this);
        this.pvpModule.start(this);
        this.charmModule.start(this);
    }

    @Override
    protected void shutdown() {
        super.shutdown();

        this.pvpModule.shutdown(this);
        this.abilityModule.shutdown(this);
        this.charmModule.shutdown(this);
    }

    public Config<ModConfig> getConfig() {
        return this.config;
    }

    public void reload() {
        this.config.load();
        this.dbConfig.load();

        this.abilityModule.reload(this);
        this.pvpModule.reload(this);
        this.charmModule.reload(this);
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not on classpath", e);
        }
    }
}