package gg.hytaleheroes.herobase.extra.profile;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ProfilePage extends CustomUIPage {
    private boolean didRun = false;

    public ProfilePage(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/Profile.ui");

        if (!didRun) {
            CompletableFuture.runAsync(() -> {
                if (playerRef.isValid()) {
                    var p = PlayerProfileAsset.load(playerRef.getUsername());
                    PlayerProfileAsset.send(playerRef.getPacketHandler(), p);
                    rebuild();
                }
            });
            didRun = true;
        }

        uiCommandBuilder.set("#NameLabel.Text", playerRef.getUsername());
    }
}
