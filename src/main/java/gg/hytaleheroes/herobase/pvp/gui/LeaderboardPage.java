package gg.hytaleheroes.herobase.pvp.gui;

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
import gg.hytaleheroes.herobase.pvp.PvpModule;
import gg.hytaleheroes.herobase.pvp.config.PvpConfigEntry;
import gg.hytaleheroes.herobase.pvp.leaderboard.LeaderboardEntry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import javax.annotation.Nonnull;
import java.sql.SQLException;

public class LeaderboardPage extends InteractiveCustomUIPage<LeaderboardPage.GuiData> {
    Object2IntOpenHashMap<String> pageCounter = new Object2IntOpenHashMap<>();

    public LeaderboardPage(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, GuiData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/Leaderboard/Leaderboard.ui");

        int cardIndex = 0;
        for (PvpConfigEntry entry : PvpModule.get().getConfig().pvpConfigEntries) {
            uiCommandBuilder.append("#Cards", "Pages/Leaderboard/LeaderboardCard.ui");

            uiCommandBuilder.set("#Cards[" + cardIndex + "] #CardTitle.Text", entry.name);

            try {
                var entries = PvpModule.get().leaderboards().getTopPlayers(entry.mode, 10);

                for (int i = 0; i < 10; i++) {
                    var leaderboardEntry = entries.size() - 1 <= i ? null : entries.get(i);

                    uiCommandBuilder.append("#Cards[" + cardIndex + "] #Entries", "Pages/Leaderboard/LeaderboardEntry.ui");

                    var cardSel = "#Cards[" + cardIndex + "] ";

                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, cardSel + "#PreviousPage", EventData.of("Action", "previous_page").append("Mode", entry.mode), false);
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, cardSel + "#NextPage", EventData.of("Action", "next_page").append("Mode", entry.mode), false);

                    setLabels(uiCommandBuilder, cardSel, i, leaderboardEntry);
                }

            } catch (SQLException e) {
                return;
            }

            cardIndex++;
        }


    }

    private static void setLabels(UICommandBuilder uiCommandBuilder, String cardSel, int i, LeaderboardEntry leaderboardEntry) throws SQLException {
        uiCommandBuilder.set(cardSel + "#PreviousPage.Disabled", true);

        var selector = cardSel + "#Entries[" + i + "] ";
        uiCommandBuilder.set(selector + "#Number.Text", String.valueOf(i + 1));

        var size = i < 3 ? 24 : 20;
        var col = i == 0 ? "#EFBF04" : i == 1 ? "#C0C0C0" : i == 2 ? "#B87333" : "#9a9a9a";
        uiCommandBuilder.set(selector + "#Number.Style.FontSize", size);
        uiCommandBuilder.set(selector + "#Name.Style.FontSize", size);
        uiCommandBuilder.set(selector + "#Score.Style.FontSize", size);

        uiCommandBuilder.set(selector + "#Number.Style.TextColor", col);
        uiCommandBuilder.set(selector + "#Name.Style.TextColor", col);
        uiCommandBuilder.set(selector + "#Score.Style.TextColor", col);

        if (leaderboardEntry != null) {
            uiCommandBuilder.set(selector + "#Name.Text", PvpModule.get().leaderboards().getUsername(leaderboardEntry.playerId()));
            uiCommandBuilder.set(selector + "#Score.Text", String.valueOf(leaderboardEntry.score()));

            uiCommandBuilder.set(cardSel + "#PreviousPage.Disabled", false);
            uiCommandBuilder.set(cardSel + "#NextPage.Disabled", false);

        } else {
            uiCommandBuilder.set(selector + "#Name.Text", "---");
            uiCommandBuilder.set(selector + "#Score.Text", "-");

            uiCommandBuilder.set(cardSel + "#NextPage.Disabled", true);
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, GuiData data) {
        super.handleDataEvent(ref, store, data);

        switch (data.action) {
            case "previous_page" -> {
                if (pageCounter.getOrDefault(data.mode, 0) > 0) {
                    try {
                        reload(data.mode);
                        this.pageCounter.put(data.mode, this.pageCounter.getInt(data.mode) - 1);
                    } catch (SQLException _) {}
                }
            }
            case "next_page" -> {
                if (pageCounter.getOrDefault(data.mode, 0) < 10) {
                    try {
                        reload(data.mode);
                        this.pageCounter.put(data.mode, this.pageCounter.getInt(data.mode) + 1);
                    } catch (SQLException _) {}
                }
            }
        }
    }

    private void reload(String mode) throws SQLException {
        UICommandBuilder commandBuilder = new UICommandBuilder();

        int cardIndex = 0;
        for (PvpConfigEntry entry : PvpModule.get().getConfig().pvpConfigEntries) {
            if (entry.mode.equals(mode)) {
                break;
            } else cardIndex++;
        }

        var cardSel = "#Cards[" + cardIndex + "] ";
        var entries = PvpModule.get().leaderboards().getTopPlayersPaged(mode,  pageCounter.getInt(mode)*10, 10);

        for (int i = 0; i < 10; i++) {
            var leaderboardEntry = entries.size() - 1 <= i ? null : entries.get(i);
            setLabels(commandBuilder, cardSel, i, leaderboardEntry);
        }

        sendUpdate(commandBuilder);
    }

    public static class GuiData {
        public static final BuilderCodec<GuiData> CODEC = BuilderCodec.builder(GuiData.class, GuiData::new)
                .append(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action).add()
                .append(new KeyedCodec<>("Mode", Codec.STRING), (d, s) -> d.mode = s, d -> d.mode).add()
                .build();

        private String action;
        private String mode;
    }
}
