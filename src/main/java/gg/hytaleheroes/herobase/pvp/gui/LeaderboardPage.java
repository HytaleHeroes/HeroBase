package gg.hytaleheroes.herobase.pvp.gui;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.pvp.PvpModule;
import gg.hytaleheroes.herobase.pvp.config.PvpConfigEntry;

import javax.annotation.Nonnull;
import java.sql.SQLException;

public class LeaderboardPage extends CustomUIPage {

    public LeaderboardPage(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime);
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
                    var e = entries.size()-1 <= i ? null : entries.get(i);

                    uiCommandBuilder.append("#Cards[" + cardIndex + "] #Entries", "Pages/Leaderboard/LeaderboardEntry.ui");

                    uiCommandBuilder.set("#Cards[" + cardIndex + "] #Entries[" + i + "] #Number.Text", String.valueOf(i + 1));

                    var size = i < 3 ? 24 : 20;
                    var col = i == 0 ? "#EFBF04" : i == 1 ? "#C0C0C0" : i == 2 ? "#B87333" : "#9a9a9a";
                    uiCommandBuilder.set("#Cards[" + cardIndex + "] #Entries[" + i + "] " + "#Number.Style.FontSize", size);
                    uiCommandBuilder.set("#Cards[" + cardIndex + "] #Entries[" + i + "] " + "#Name.Style.FontSize", size);
                    uiCommandBuilder.set("#Cards[" + cardIndex + "] #Entries[" + i + "] " + "#Score.Style.FontSize", size);

                    uiCommandBuilder.set("#Cards[" + cardIndex + "] #Entries[" + i + "] " + "#Number.Style.TextColor", col);
                    uiCommandBuilder.set("#Cards[" + cardIndex + "] #Entries[" + i + "] " + "#Name.Style.TextColor", col);
                    uiCommandBuilder.set("#Cards[" + cardIndex + "] #Entries[" + i + "] " + "#Score.Style.TextColor", col);


                    if (e != null) {
                        uiCommandBuilder.set("#Cards[" + cardIndex + "] #Entries[" + i + "] #Name.Text", PvpModule.get().leaderboards().getUsername(e.playerId()));
                        uiCommandBuilder.set("#Cards[" + cardIndex + "] #Entries[" + i + "] #Score.Text", String.valueOf(e.score()));
                    } else {
                        uiCommandBuilder.set("#Cards[" + cardIndex + "] #Entries[" + i + "] #Name.Text", "---");
                        uiCommandBuilder.set("#Cards[" + cardIndex + "] #Entries[" + i + "] #Score.Text", "-");
                    }
                }

            } catch (SQLException e) {
                return;
            }

            cardIndex++;
        }

    }
}
