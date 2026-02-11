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

    Item() {
    }

    Item(String name, String tooltip, double price, boolean enabled) {
        this.name = name;
        this.tooltip = tooltip;
        this.enabled = enabled;
        this.price = price;
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
