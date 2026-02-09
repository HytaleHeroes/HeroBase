package gg.hytaleheroes.herobase.extra.navigator;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.HeroBase;

import javax.annotation.Nonnull;
import java.util.List;

public class NavigatorPage extends InteractiveCustomUIPage<NavigatorPage.GuiData> {
    static final class Card {
        public String name;
        public String tooltip;
        public String command;
        public String icon;

        Card() {}

        Card(String name, String tooltip, String command, String icon, boolean red) {
            this.name = name;
            this.tooltip = tooltip;
            this.command = command;
            this.icon = icon;
        }
    }

    static final class Section {
        public String name;
        public List<Card> cards;

        Section() {}

        Section(String name, List<Card> cards) {
            this.name = name;
            this.cards = cards;
        }
    }

    public NavigatorPage(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, GuiData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/Navigator/Navigator.ui");

        var conf = HeroBase.get().getNavigatorConfig().get();
        for (int i = 0; i < conf.sections.size(); i++) {
            var s = conf.sections.get(i);
            addSection(i, s, uiCommandBuilder, uiEventBuilder);
        }
    }

    private void addSection(int sectionIndex, Section section, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder) {
        uiCommandBuilder.append("#Entries", "Pages/Navigator/Section.ui");

        var selector = "#Entries[" + sectionIndex + "] ";

        uiCommandBuilder.set(selector + "#SectionLabel.Text", section.name);

        for (int i = 0; i < section.cards.size(); i++) {
            var card = section.cards.get(i);

            uiCommandBuilder.append(selector + "#SectionCards", "Pages/Navigator/Card.ui");

            var selector2 = selector + "#SectionCards[" + i + "] ";

            uiCommandBuilder.set(selector2 + "#Label.Text", card.name);
            uiCommandBuilder.set(selector2 + "#Icon.AssetPath", card.icon);

            if (card.command.isBlank()) uiCommandBuilder.set(selector2 + "#Button.Disabled", true);

            uiCommandBuilder.set(selector2 + "#Button.TooltipText", card.tooltip);

            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector2 + "#Button", EventData.of("Section", String.valueOf(sectionIndex)).append("Index", String.valueOf(i)), false);
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull GuiData data) {
        super.handleDataEvent(ref, store, data);

        if (data.action != null && !data.action.isBlank()) {
            var player = store.getComponent(ref, Player.getComponentType());

            assert player != null;

            var card = HeroBase.get().getNavigatorConfig().get().sections.get(data.section).cards.get(data.index);
            CommandManager.get().handleCommand(playerRef, card.command);
        }
    }

    public static class GuiData {
        public static final BuilderCodec<GuiData> CODEC = BuilderCodec.builder(GuiData.class, GuiData::new)
                .append(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action).add()
                .append(new KeyedCodec<>("Section", Codec.INTEGER), (d, s) -> d.section = s, d -> d.section).add()
                .append(new KeyedCodec<>("Index", Codec.INTEGER), (d, s) -> d.index = s, d -> d.index).add()
                .build();

        private String action;
        private int section;
        private int index;
    }
}
