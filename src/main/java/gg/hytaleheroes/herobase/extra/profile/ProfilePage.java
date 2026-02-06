package gg.hytaleheroes.herobase.extra.profile;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ProfilePage extends InteractiveCustomUIPage<ProfilePage.GuiData> {
    private boolean didRun = false;
    private final String playerName;

    public ProfilePage(@Nonnull PlayerRef playerRef, String playerName, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, GuiData.CODEC);
        this.playerName = playerName;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/Profile/Profile.ui");

        uiCommandBuilder.set("#NameLabel.Text", playerName);
        uiCommandBuilder.set("#TitleLabel.Text", "Profile of " + playerName);

        if (playerRef.getUsername().equals(playerName)) {
            uiCommandBuilder.set("#StatusLabel.Visible", false);
            uiCommandBuilder.set("#InputGroup.Visible", true);
            uiEventBuilder.addEventBinding(
                    CustomUIEventBindingType.ValueChanged,
                    "#StatusLabelInput",
                    EventData.of("@Message", "#StatusLabelInput.Value"),
                    false
            );

            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#StatusLabelSave", EventData.of("Action", "update_status"), false);

        } else {
            uiCommandBuilder.set("#StatusLabel.Visible", true);
            uiCommandBuilder.set("#InputGroup.Visible", false);
        }

        if (!didRun) {
            CompletableFuture.runAsync(() -> {
                if (playerRef.isValid()) {
                    var p = PlayerProfileAsset.load(playerRef.getUsername());
                    PlayerProfileAsset.send(playerRef.getPacketHandler(), p);

                    if (playerRef.getWorldUuid() == null)
                        return;

                    var w = Universe.get().getWorld(playerRef.getWorldUuid());
                    if (w != null) {
                        CompletableFuture.runAsync(() -> {
                            if (playerRef.isValid()) {
                                UICommandBuilder commandBuilder = new UICommandBuilder();
                                commandBuilder.set("#ProfileImage.AssetPath", "UI/Custom/Pages/Profile/Profile.png");
                                sendUpdate(commandBuilder);
                            }
                        }, w);
                    }

                }
            });

            didRun = true;
        }

        uiCommandBuilder.set("#NameLabel.Text", playerRef.getUsername());
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull GuiData data) {
        super.handleDataEvent(ref, store, data);

        if (data.action != null) {
            switch (data.action) {
                case "update_status" -> {
                    UICommandBuilder commandBuilder = new UICommandBuilder();
                    commandBuilder.set("#StatusLabelInput.Value", data.message);
                    sendUpdate(commandBuilder);
                }
            }
        }
    }

    public static class GuiData {
        public static final BuilderCodec<GuiData> CODEC = BuilderCodec.builder(GuiData.class, GuiData::new)
                .append(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action).add()
                .append(new KeyedCodec<>("@Message", Codec.STRING), (d, s) -> d.message = s, d -> d.message).add()
                .build();

        private String action;
        private String message;
    }
}
