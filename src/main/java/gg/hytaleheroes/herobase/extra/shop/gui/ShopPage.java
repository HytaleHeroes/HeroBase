package gg.hytaleheroes.herobase.extra.shop.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.extra.shop.asset.Section;
import gg.hytaleheroes.herobase.extra.shop.asset.ServerShop;

import javax.annotation.Nonnull;

public class ShopPage extends InteractiveCustomUIPage<ShopPage.GuiData> {

    public ShopPage(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, GuiData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/Navigator/Navigator.ui");

        ServerShop.getAssetMap().getAssetMap().keySet().stream().sorted().forEachOrdered(x -> {
            ServerShop shop = ServerShop.getAssetMap().getAsset(x);
            if (shop != null) {
                for (int i = 0; i < shop.sections.size(); i++) {
                    var s = shop.sections.get(i);
                    this.addSection(i, s, uiCommandBuilder, uiEventBuilder);
                }
            }
        });
    }

    private void addSection(int sectionIndex, Section section, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder) {
        uiCommandBuilder.append("#Entries", "Pages/Navigator/Section.ui");

        var selector = "#Entries[" + sectionIndex + "] ";

        uiCommandBuilder.set(selector + "#SectionLabel.Text", section.name);

        for (int i = 0; i < section.elements.size(); i++) {
            var element = section.elements.get(i);

            uiCommandBuilder.append(selector + "#SectionCards", "Pages/Navigator/Card.ui");

            var selector2 = selector + "#SectionCards[" + i + "] ";
            element.build(selector2, uiCommandBuilder, uiEventBuilder);
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull GuiData data) {
        super.handleDataEvent(ref, store, data);

        if (data.action != null && !data.action.isBlank()) {
            var player = store.getComponent(ref, Player.getComponentType());

            assert player != null;

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
