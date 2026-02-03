package gg.hytaleheroes.herobase.leaderboard;

import java.time.Instant;
import java.util.UUID;

public record LeaderboardEntry(long id, UUID playerId, String mode, int score, Instant updatedAt) {
}
