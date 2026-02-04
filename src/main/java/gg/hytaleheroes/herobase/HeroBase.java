package gg.hytaleheroes.herobase;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PacketFilter;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.npc.NPCPlugin;
import gg.hytaleheroes.herobase.command.HeroBaseCommand;
import gg.hytaleheroes.herobase.command.ability.AbilityCommand;
import gg.hytaleheroes.herobase.component.AbilityCooldownsComponent;
import gg.hytaleheroes.herobase.component.AbilityHotbarConfiguration;
import gg.hytaleheroes.herobase.component.UnlockedAbilitiesComponent;
import gg.hytaleheroes.herobase.config.DatabaseConfig;
import gg.hytaleheroes.herobase.config.ModConfig;
import gg.hytaleheroes.herobase.config.PvpConfig;
import gg.hytaleheroes.herobase.gui.hud.AbilityHud;
import gg.hytaleheroes.herobase.gui.hud.LeaderboardHud;
import gg.hytaleheroes.herobase.handler.AbilitySlotHandler;
import gg.hytaleheroes.herobase.handler.PlayerWelcomeHandler;
import gg.hytaleheroes.herobase.leaderboard.DatabaseManager;
import gg.hytaleheroes.herobase.leaderboard.Leaderboards;
import gg.hytaleheroes.herobase.npc.action.BuilderActionSendMessage;
import gg.hytaleheroes.herobase.system.AbilityKeybindSystem;
import gg.hytaleheroes.herobase.system.LeaderboardUpdateSystem;
import gg.hytaleheroes.herobase.system.PlayerPvpEvents;
import gg.hytaleheroes.herobase.system.PreventPvpDamageFilterSystem;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class HeroBase extends JavaPlugin {
    private final Config<ModConfig> config;
    private final Config<DatabaseConfig> dbConfig;
    private final Config<PvpConfig> pvpConfig;

    public static HeroBase INSTANCE;

    private PacketFilter inboundFilter;
    private ConcurrentHashMap<UUID, AbilityHud> activeHuds;
    private DatabaseManager databaseManager;
    private Leaderboards leaderboards;

    public HeroBase(@Nonnull JavaPluginInit init) {
        super(init);
        this.config = this.withConfig("HeroBase", ModConfig.CODEC);
        this.dbConfig = this.withConfig("Database", DatabaseConfig.CODEC);
        this.pvpConfig = this.withConfig("Pvp", PvpConfig.CODEC);
    }

    public static HeroBase get() {
        return INSTANCE;
    }

    public ConcurrentHashMap<UUID, AbilityHud> getActiveHuds() {
        return this.activeHuds;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    protected void setup() {
        super.setup();

        INSTANCE = this;

        this.config.save();
        this.dbConfig.save();
        this.pvpConfig.save();

        var dbCfg = this.dbConfig.get();

        this.databaseManager = new DatabaseManager(dbCfg);
        this.leaderboards = new Leaderboards();
        this.activeHuds = new ConcurrentHashMap<>();

        UnlockedAbilitiesComponent.setup(this.getEntityStoreRegistry());
        AbilityCooldownsComponent.setup(this.getEntityStoreRegistry());
        AbilityHotbarConfiguration.setup(this.getEntityStoreRegistry());

        this.getEventRegistry().register(PlayerConnectEvent.class, PlayerWelcomeHandler::onPlayerJoin);
        this.getAssetRegistry().register(HytaleAssetStore.builder(Ability.class, new DefaultAssetMap<>()).setPath("Abilities").setCodec(Ability.CODEC).setKeyFunction(Ability::getId).build());

        this.getCommandRegistry().registerCommand(new HeroBaseCommand());
        this.getCommandRegistry().registerCommand(new AbilityCommand());

        AbilitySlotHandler handler = new AbilitySlotHandler();
        this.inboundFilter = PacketAdapters.registerInbound(handler);

        this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, (event) -> {
            var conf = pvpConfig.get().pvpConfigEntryMap.get(event.getWorld().getName());
            if (conf != null) {
                var playerRef = event.getHolder().getComponent(PlayerRef.getComponentType());
                var player = event.getHolder().getComponent(Player.getComponentType());
                if (playerRef != null && player != null) {
                    MultipleHUD.getInstance().setCustomHud(player, playerRef, LeaderboardHud.ID, new LeaderboardHud(playerRef, conf.mode));
                }
            } else {
                var player = event.getHolder().getComponent(Player.getComponentType());
                MultipleHUD.getInstance().hideCustomHud(player, LeaderboardHud.ID);
            }
        });

        this.getEventRegistry().register(PlayerConnectEvent.class, (event) -> {
            try {
                this.leaderboards.updatePlayer(event.getPlayerRef().getUuid(), event.getPlayerRef().getUsername());
            } catch (SQLException e) {
                HytaleLogger.forEnclosingClass().at(Level.SEVERE).log("Could not save player information!");
            }
        });

        NPCPlugin.get().registerCoreComponentType("SendMessage", BuilderActionSendMessage::new);
    }

    @Override
    protected void start() {
        super.start();

        this.getEntityStoreRegistry().registerSystem(new AbilityKeybindSystem());
        this.getEntityStoreRegistry().registerSystem(new LeaderboardUpdateSystem(1));
        this.getEntityStoreRegistry().registerSystem(new PlayerPvpEvents());
        this.getEntityStoreRegistry().registerSystem(new PreventPvpDamageFilterSystem());
    }

    @Override
    protected void shutdown() {
        super.shutdown();

        if (this.inboundFilter != null) {
            PacketAdapters.deregisterInbound(this.inboundFilter);
        }

        if (this.activeHuds != null) {
            this.activeHuds.clear();
        }

        if (this.leaderboards != null) {
            this.leaderboards.shutdown();
        }
    }

    public Config<ModConfig> getConfig() {
        return config;
    }

    public Leaderboards getLeaderboards() {
        return leaderboards;
    }

    public Config<PvpConfig> getPvpConfig() {
        return pvpConfig;
    }

    public void reload() {
        config.load();
        dbConfig.load();
        pvpConfig.load();
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not on classpath", e);
        }
    }
}