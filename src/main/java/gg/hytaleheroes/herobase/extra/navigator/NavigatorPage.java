package gg.hytaleheroes.herobase.extra.navigator;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.List;

public class NavigatorPage extends InteractiveCustomUIPage<NavigatorPage.GuiData> {
    List<Section> sections = List.of(
            new Section("Navigation", List.of(
                    new Card("Hub", "hub", "UI/Custom/Pages/Navigator/Icons/target.png", false),
                    new Card("Home", "home", "UI/Custom/Pages/Navigator/Icons/home.png", false),
                    new Card("Teleport Request", "tpa", "UI/Custom/Pages/Navigator/Icons/map-marker.png", false),
                    new Card("Random Teleport", "rtp", "UI/Custom/Pages/Navigator/Icons/portal.png", false)
            )),

            new Section("Combat", List.of(
                    new Card("FFA Arena", "arena", "UI/Custom/Pages/Navigator/Icons/battle.png", true),
                    new Card("Ranked Match", "", "UI/Custom/Pages/Navigator/Icons/battleaxe.png", true),
                    new Card("Leaderboards", "leaderboard", "UI/Custom/Pages/Navigator/Icons/trophy.png", false)
            )),

            new Section("Player", List.of(
                    new Card("Abilities", "", "UI/Custom/Pages/Navigator/Icons/lightning.png", false),
                    new Card("Profile", "profile", "UI/Custom/Pages/Navigator/Icons/user.png", false),
                    new Card("Market", "market", "UI/Custom/Pages/Navigator/Icons/pouch.png", false),
                    new Card("Cosmetics", "", "UI/Custom/Pages/Navigator/Icons/armor.png", false),
                    new Card("Settings", "", "UI/Custom/Pages/Navigator/Icons/settings.png", false)
            )),

            new Section("Information", List.of(
                    new Card("Help", "help", "UI/Custom/Pages/Navigator/Icons/misc.png", false),
                    new Card("Discord", "discord", "UI/Custom/Pages/Navigator/Icons/chat.png", false),
                    new Card("TikTok", "", "UI/Custom/Pages/Navigator/Icons/hand-thumbs-up.png", false)
            ))
    );


    record Card(String name, String command, String icon, boolean red) {
    }

    record Section(String name, List<Card> cards) {
    }

    public NavigatorPage(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, GuiData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/Navigator/Navigator.ui");

        for (int i = 0; i < sections.size(); i++) {
            var s = sections.get(i);
            addSection(i, s, uiCommandBuilder, uiEventBuilder);
        }
    }

    private void addSection(int sectionIndex, Section section, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder) {
        uiCommandBuilder.append("#Entries", "Pages/Navigator/Section.ui");

        var selector = "#Entries[" + sectionIndex + "] ";

        uiCommandBuilder.set(selector + "#SectionLabel.Text", section.name());

        for (int i = 0; i < section.cards.size(); i++) {
            var card = section.cards.get(i);

            uiCommandBuilder.append(selector + "#SectionCards", "Pages/Navigator/Card.ui");

            var selector2 = selector + "#SectionCards[" + i + "] ";

            uiCommandBuilder.set(selector2 + "#Label.Text", card.name);
            uiCommandBuilder.set(selector2 + "#Icon.AssetPath", card.icon);

            if (card.command.isBlank()) uiCommandBuilder.set(selector2 + "#Button.Disabled", true);

            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector2 + "#Button", EventData.of("Action", card.command()), false);
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull GuiData data) {
        super.handleDataEvent(ref, store, data);

        if (data.action != null && !data.action.isBlank()) {
            var player = store.getComponent(ref, Player.getComponentType());

            assert player != null;

            player.getPageManager().setPage(ref, store, Page.None);
            CommandManager.get().handleCommand(playerRef, data.action);
        }
    }

    public static class GuiData {
        public static final BuilderCodec<GuiData> CODEC = BuilderCodec.builder(GuiData.class, GuiData::new)
                .append(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action).add()
                .build();

        private String action;
    }
}
