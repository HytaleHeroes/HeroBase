package gg.hytaleheroes.herobase.pvp.leaderboard;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import gg.hytaleheroes.herobase.HeroBase;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.time.Duration;

public class LeaderboardHud extends CustomUIHud {
    public static String ID = "pvp_leaderboard";

    private final String mode;

    public LeaderboardHud(@Nonnull PlayerRef playerRef, String mode) {
        super(playerRef);
        this.mode = mode;
    }

    @Override
    protected void build(@Nonnull UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("LeaderboardHud.ui");

        try {
            var top = HeroBase.get().getLeaderboards().topKillsInWindow(mode, 10, Duration.ofHours(1));
            for (int i = 0; i < 10; i++) {
                uiCommandBuilder.append("#List", "LeaderboardEntry.ui");

                var size = i < 3 ? 19 : 16;
                var col = i == 0 ? "#EFBF04" : i == 1 ? "#C0C0C0" : i == 2 ? "#B87333" : "#9a9a9a";
                uiCommandBuilder.set("#List["+i+"] #Number.Style.FontSize", size);
                uiCommandBuilder.set("#List["+i+"] #Name.Style.FontSize", size);
                uiCommandBuilder.set("#List["+i+"] #Score.Style.FontSize", size);

                uiCommandBuilder.set("#List["+i+"] #Number.Style.TextColor", col);
                uiCommandBuilder.set("#List["+i+"] #Name.Style.TextColor", col);
                uiCommandBuilder.set("#List["+i+"] #Score.Style.TextColor", col);

                if (i >= top.size()) {
                    uiCommandBuilder.set("#List["+i+"] #Number.Text", String.valueOf(i+1));
                    uiCommandBuilder.set("#List["+i+"] #Name.Text", "---");
                    uiCommandBuilder.set("#List["+i+"] #Score.Text", "0");
                } else {
                    LeaderboardEntry entry = top.get(i);
                    uiCommandBuilder.set("#List["+i+"] #Number.Text", String.valueOf(i+1));
                    uiCommandBuilder.set("#List["+i+"] #Name.Text", HeroBase.get().getLeaderboards().getUsername(entry.playerId()));
                    uiCommandBuilder.set("#List["+i+"] #Score.Text", String.valueOf(entry.score()));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
