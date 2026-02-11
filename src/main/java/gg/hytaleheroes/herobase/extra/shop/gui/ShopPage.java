package gg.hytaleheroes.herobase.extra.shop.gui;

import com.ecotale.api.EcotaleAPI;
import com.google.crypto.tink.proto.EcPointFormat;
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
import gg.hytaleheroes.herobase.extra.shop.asset.Item;
import gg.hytaleheroes.herobase.extra.shop.asset.Section;
import gg.hytaleheroes.herobase.extra.shop.asset.ServerShop;

import javax.annotation.Nonnull;

public class ShopPage extends InteractiveCustomUIPage<ShopPage.GuiData> {

    public ShopPage(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, GuiData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/ServerShop/Shop.ui");

        //uiCommandBuilder.set("#MyThing.Visible", false);
        //uiCommandBuilder.set("#MyButton.Disabled", true);

        uiCommandBuilder.append("#OverlayContent", "Pages/ServerShop/ConfirmPage.ui");

        uiCommandBuilder.set("#PreviewOutputSlot.ItemId", "Ore_Adamantite");

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
        uiCommandBuilder.append("#Entries", "Pages/ServerShop/Section.ui");

        var selector = "#Entries[" + sectionIndex + "] ";

        uiCommandBuilder.set(selector + "#SectionLabel.Text", section.name);

        for (int i = 0; i < section.elements.size(); i++) {
            var element = section.elements.get(i);

            uiCommandBuilder.append(selector + "#SectionCards", "Pages/ServerShop/Card.ui");

            var selector2 = selector + "#SectionCards[" + i + "] ";

            if (element instanceof Item item) {
                if (!item.enabled) uiCommandBuilder.set(selector2 + "#Button.Disabled", true);

                uiCommandBuilder.set(selector2 + "#Button.TooltipText", item.tooltip);

                uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector2 + "#Button", EventData.of("Action", "SelectItem").append("Section", String.valueOf(sectionIndex)).append("Index", String.valueOf(i)), false);
            }
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull GuiData data) {
        super.handleDataEvent(ref, store, data);

        if (data.action != null && !data.action.isBlank()) {
            var player = store.getComponent(ref, Player.getComponentType());

            assert player != null;

            var bal = EcotaleAPI.getBalance(playerRef.getUuid());

            if (EcotaleAPI.withdraw(playerRef.getUuid(), 100.0, "Purchase: <quantity> <item>")) {
                // did success
            }

        }
    }

    public static class GuiData {
        public static final BuilderCodec<GuiData> CODEC = BuilderCodec.builder(GuiData.class, GuiData::new)
                .append(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action).add()
                .build();

        private String action;
    }
}
