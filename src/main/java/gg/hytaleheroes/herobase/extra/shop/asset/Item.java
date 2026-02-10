package gg.hytaleheroes.herobase.extra.shop.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

import java.util.List;

public final class Item implements Element {
    public String name;
    public String tooltip;
    public double price = 1;
    public boolean enabled = true;
    private String itemId;

    public List<Button> buttons = List.of(
            new Button("Buy", List.of("echo hello"), false)
    );

    Item() {
    }

    Item(String name, String tooltip, double price, boolean enabled) {
        this.name = name;
        this.tooltip = tooltip;
        this.enabled = enabled;
        this.price = price;
    }

    @Override
    public String getUIDocumentPath() {
        return "Pages/Navigator/Card.ui";
    }

    @Override
    public void build(String selector2, UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder) {

        //uiCommandBuilder.set(selector2 + "#Label.Text", this.name);
        //uiCommandBuilder.set(selector2 + "#Icon.AssetPath", this.icon);

        if (!this.enabled) uiCommandBuilder.set(selector2 + "#Button.Disabled", true);

        uiCommandBuilder.set(selector2 + "#Button.TooltipText", this.tooltip);

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector2 + "#Button", EventData.of("Action", "BuyItem").append("Price", String.valueOf(this.price)), false);
    }

    @Override
    public void update(String sel, UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder) {

    }

    @Override
    public boolean run(int amount) {

        return true;
    }


    public static final BuilderCodec<Item> CODEC =
            BuilderCodec.builder(Item.class, Item::new)
                    .append(new KeyedCodec<>("Name", Codec.STRING),
                            (c, v) -> c.name = v,
                            c -> c.name).add()
                    .append(new KeyedCodec<>("ItemId", Codec.STRING),
                            (c, v) -> c.itemId = v,
                            c -> c.itemId).add()
                    .append(new KeyedCodec<>("Tooltip", Codec.STRING),
                            (c, v) -> c.tooltip = v,
                            c -> c.tooltip).add()
                    .append(new KeyedCodec<>("Price", Codec.DOUBLE),
                            (c, v) -> c.price = v,
                            c -> c.price).add()
                    .build();
}
