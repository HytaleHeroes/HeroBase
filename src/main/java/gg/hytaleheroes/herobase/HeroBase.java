package gg.hytaleheroes.herobase;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PacketFilter;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.Config;
import gg.hytaleheroes.herobase.command.BaseCommand;
import gg.hytaleheroes.herobase.component.AbilityCooldownsComponent;
import gg.hytaleheroes.herobase.component.AbilityHotbarConfiguration;
import gg.hytaleheroes.herobase.component.UnlockedAbilitiesComponent;
import gg.hytaleheroes.herobase.config.ModConfig;
import gg.hytaleheroes.herobase.format.TinyMsg;
import gg.hytaleheroes.herobase.gui.hud.AbilityHud;
import gg.hytaleheroes.herobase.handler.AbilitySlotHandler;
import gg.hytaleheroes.herobase.system.AbilityKeybindSystem;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HeroBase extends JavaPlugin {
    private final Config<ModConfig> config;

    public static HeroBase INSTANCE;

    private PacketFilter inboundFilter;
    private ConcurrentHashMap<UUID, AbilityHud> activeHuds;

    public HeroBase(@Nonnull JavaPluginInit init) {
        super(init);
        this.config = this.withConfig("HeroBase", ModConfig.CODEC);
    }

    public static HeroBase get() {
        return INSTANCE;
    }

    public ConcurrentHashMap<UUID, AbilityHud> activeHuds() {
        return this.activeHuds;
    }

    @Override
    protected void setup() {
        super.setup();

        INSTANCE = this;

        this.activeHuds = new ConcurrentHashMap<>();

        UnlockedAbilitiesComponent.setup(this.getEntityStoreRegistry());
        AbilityCooldownsComponent.setup(this.getEntityStoreRegistry());
        AbilityHotbarConfiguration.setup(this.getEntityStoreRegistry());

        this.getAssetRegistry().register(HytaleAssetStore.builder(Ability.class, new DefaultAssetMap<>()).setPath("Abilities").setCodec(Ability.CODEC).setKeyFunction(Ability::getId).build());

        this.getCommandRegistry().registerCommand(new BaseCommand());

        AbilitySlotHandler handler = new AbilitySlotHandler();
        this.inboundFilter = PacketAdapters.registerInbound(handler);

        this.getEventRegistry().register(PlayerConnectEvent.class, (event) -> {
            var cfg = this.config.get();

            boolean isNewPlayer;
            try {
                isNewPlayer = !Universe.get().getPlayerStorage().getPlayers().contains(event.getPlayerRef().getUuid());
            } catch (IOException _) {
                isNewPlayer = true;
            }

            if (isNewPlayer) {
                for (String s : cfg.welcomeMessage) {
                    event.getPlayerRef().sendMessage(TinyMsg.parse(s.replace("%player%", event.getPlayerRef().getUsername())));
                }
            } else {
                for (String s : cfg.welcomeBackMessage) {
                    event.getPlayerRef().sendMessage(TinyMsg.parse(s.replace("%player%", event.getPlayerRef().getUsername())));
                }
            }

            var msg = isNewPlayer ? cfg.globalWelcomeMessage : cfg.globalWelcomeBackMessage;
            if (msg != null && !msg.isBlank()) {
                var playerRefList = Universe.get().getPlayers();
                playerRefList.forEach(x -> {
                    if (x != null && event.getPlayerRef() != x)
                        x.sendMessage(TinyMsg.parse(msg.replace("%player%", event.getPlayerRef().getUsername())));
                });
            }
        });

    }

    @Override
    protected void start() {
        super.start();

        this.getEntityStoreRegistry().registerSystem(new AbilityKeybindSystem());
    }

    @Override
    protected void shutdown() {
        super.shutdown();

        if (this.inboundFilter != null) {
            PacketAdapters.deregisterInbound(this.inboundFilter);
        }

        this.activeHuds.clear();
    }

    public Config<ModConfig> getConfig() {
        return config;
    }
}