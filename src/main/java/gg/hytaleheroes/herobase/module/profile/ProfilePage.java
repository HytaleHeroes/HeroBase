package gg.hytaleheroes.herobase.module.profile;

import com.ecotale.api.EcotaleAPI;
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
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.pvp.PvpModule;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ProfilePage extends InteractiveCustomUIPage<ProfilePage.GuiData> {
    private boolean didRun = false;
    private final String playerName;

    private String newStatus;

    public ProfilePage(@Nonnull PlayerRef playerRef, String playerName, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, GuiData.CODEC);
        this.playerName = playerName;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/Profile/Profile.ui");

        uiCommandBuilder.set("#NameLabel.Text", playerName);
        uiCommandBuilder.set("#TitleLabel.Text", "Profile of " + playerName);


        CompletableFuture.runAsync(() -> {
            try {
                var pid = PvpModule.get().leaderboards().getId(playerName);
                var bal = EcotaleAPI.getBalance(pid);
                var profile = ProfileModule.get().playerProfiles().getProfile(PvpModule.get().leaderboards().getId(playerName));

                if (profile != null) {
                    this.newStatus = profile.status();

                    UICommandBuilder b = new UICommandBuilder();
                    b.set("#StatusLabel.Text", profile.status());
                    b.set("#StatusLabelInput.Value", profile.status());

                    b.set("#BalanceLabel.Text", String.format("Balance: %s", EcotaleAPI.format(bal)));

                    this.sendUpdate(b);
                }
            } catch (SQLException _) {
            }


        });

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
                    var p = PlayerProfilePictureAsset.load(playerName);
                    PlayerProfilePictureAsset.send(playerRef.getPacketHandler(), p);

                    if (playerRef.getWorldUuid() == null)
                        return;

                    if (playerRef.isValid()) {
                        UICommandBuilder commandBuilder = new UICommandBuilder();
                        commandBuilder.set("#ProfileImage.AssetPath", "UI/Custom/Pages/Profile/Profile2.png");
                        sendUpdate(commandBuilder);
                    }
                }
            });

            didRun = true;
        }

        uiCommandBuilder.set("#NameLabel.Text", playerName);

        CompletableFuture.runAsync(() -> {
            try {
                UICommandBuilder uiCommandBuilder1 = new UICommandBuilder();

                var pid = PvpModule.get().leaderboards().getId(playerName);
                var modes = PvpModule.get().leaderboards().getPlayedModes(PvpModule.get().leaderboards().getId(playerName));
                PvpModule.get().getConfig().pvpConfigEntries.forEach(x -> modes.add(x.mode));

                int i = 0;
                for (String mode : modes) {
                    var stats = PvpModule.get().leaderboards().getPlayerModeStat(pid, mode);
                    uiCommandBuilder1.append("#Entries", "Pages/Profile/Card.ui");

                    String sel = "#Entries[" + i + "] ";

                    if (stats != null) {
                        uiCommandBuilder1.set(sel + "#Kills.Text", "" + stats.kills());
                        uiCommandBuilder1.set(sel + "#Deaths.Text", "" + stats.deaths());
                    }

                    var named = PvpModule.get().getConfig().getByMode(mode);
                    uiCommandBuilder1.set(sel + "#Mode.Text", named == null ? mode : named.name);

                    i++;
                }

                sendUpdate(uiCommandBuilder1);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull GuiData data) {
        super.handleDataEvent(ref, store, data);

        if (data.message != null) {
            newStatus = data.message;
        }

        if (newStatus != null && data.action != null) {
            switch (data.action) {
                case "update_status" -> {
                    if (playerRef.getUsername().equals(playerName)) {
                        try {
                            ProfileModule.get().playerProfiles().setStatus(playerRef.getUuid(), Objects.requireNonNullElse(newStatus, ""));
                        } catch (SQLException _) {
                        } finally {
                            UICommandBuilder commandBuilder = new UICommandBuilder();
                            commandBuilder.set("#StatusLabelInput.Value", newStatus);
                            sendUpdate(commandBuilder);
                        }
                    }
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
