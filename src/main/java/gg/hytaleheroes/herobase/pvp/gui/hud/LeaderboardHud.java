package gg.hytaleheroes.herobase.pvp.gui.hud;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import gg.hytaleheroes.herobase.HeroBase;
import gg.hytaleheroes.herobase.pvp.config.PvpConfigEntry;
import gg.hytaleheroes.herobase.pvp.leaderboard.LeaderboardEntry;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.time.Duration;

public class LeaderboardHud extends CustomUIHud {
    public static String ID = "pvp_leaderboard";

    private final String mode;
    private PvpConfigEntry configEntry = null;

    public LeaderboardHud(@Nonnull PlayerRef playerRef, String world, String mode) {
        super(playerRef);
        this.mode = mode;

        var map = HeroBase.get().getPvpConfig().get().pvpConfigEntryMap;
        if (map == null)
            return;

        var configEntry = HeroBase.get().getPvpConfig().get().pvpConfigEntryMap.get(world);
        if (configEntry == null)
            return;

        this.configEntry = configEntry;
    }

    @Override
    protected void build(@Nonnull UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Pages/Leaderboard/LeaderboardHud.ui");

        try {
            var window = configEntry == null ? 4 : configEntry.leaderboardTimeWindowHours;
            var top = HeroBase.get().getLeaderboards().topKillsInWindow(mode, 10, Duration.ofHours(window));

            boolean hadMe = false;

            for (int i = 0; i < 10; i++) {
                uiCommandBuilder.append("#List", "Pages/Leaderboard/LeaderboardEntry.ui");

                var selector = "#List[" + i + "] ";

                var size = i < 3 ? 19 : 16;
                var col = i == 0 ? "#EFBF04" : i == 1 ? "#C0C0C0" : i == 2 ? "#B87333" : "#9a9a9a";
                uiCommandBuilder.set(selector + "#Number.Style.FontSize", size);
                uiCommandBuilder.set(selector + "#Name.Style.FontSize", size);
                uiCommandBuilder.set(selector + "#Score.Style.FontSize", size);

                uiCommandBuilder.set(selector + "#Number.Style.TextColor", col);
                uiCommandBuilder.set(selector + "#Name.Style.TextColor", col);
                uiCommandBuilder.set(selector + "#Score.Style.TextColor", col);

                if (i >= top.size()) {
                    uiCommandBuilder.set(selector + "#Number.Text", String.valueOf(i + 1));
                    uiCommandBuilder.set(selector + "#Name.Text", "---");
                    uiCommandBuilder.set(selector + "#Score.Text", "0");
                } else {
                    LeaderboardEntry entry = top.get(i);
                    uiCommandBuilder.set(selector + "#Number.Text", String.valueOf(i + 1));
                    uiCommandBuilder.set(selector + "#Name.Text", HeroBase.get().getLeaderboards().getUsername(entry.playerId()));
                    uiCommandBuilder.set(selector + "#Score.Text", String.valueOf(entry.score()));

                    hadMe |= entry.playerId().equals(getPlayerRef().getUuid());
                }

                if (!hadMe && i == 9) {
                    uiCommandBuilder.set(selector + "#Number.Text", "-");
                    uiCommandBuilder.set(selector + "#Name.Text", this.getPlayerRef().getUsername());

                    var entry = HeroBase.get().getLeaderboards().getEntry(getPlayerRef().getUuid(), mode);
                    if (entry == null)
                        uiCommandBuilder.set(selector + "#Score.Text", "0");
                    else
                        uiCommandBuilder.set(selector + "#Score.Text", String.valueOf(HeroBase.get().getLeaderboards().getEntry(getPlayerRef().getUuid(), mode).score()));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
