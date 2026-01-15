package gg.hytaleheroes.herobase;

import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import gg.hytaleheroes.herobase.command.BaseCommand;
import gg.hytaleheroes.herobase.config.ModConfig;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HeroBase extends JavaPlugin {
    public static final ExecutorService PHYSICS = Executors.newSingleThreadExecutor();
    public static final ExecutorService COLLISION_GEN = Executors.newVirtualThreadPerTaskExecutor();

    public static HeroBase INSTANCE;
    public static Config<ModConfig> CONFIG;

    public HeroBase(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        this.withConfig("", ModConfig.CODEC);
    }

    @Override
    public void setup() {

        //this.getEntityStoreRegistry().registerSystem(new BreakBlockEventSystem());
        //this.getEntityStoreRegistry().registerSystem(new PlaceBlockEventSystem());
        //this.getEntityStoreRegistry().registerSystem(new InteractEventSystem());
        //this.getEntityStoreRegistry().registerSystem(new PickupInteractEventSystem());
        //this.getEntityStoreRegistry().registerSystem(new TitleTickingSystem());
        //this.getEntityStoreRegistry().registerSystem(new CustomDamageEventSystem());
        //this.getChunkStoreRegistry().registerSystem(new WorldMapUpdateTickingSystem());

        CommandRegistry commandRegistry = this.getCommandRegistry();
        commandRegistry.registerCommand(new BaseCommand());

    }

    @Override
    protected void start() {
        super.start();

    }
}