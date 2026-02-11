package gg.hytaleheroes.herobase.extra.shop.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

import java.util.List;

final class Command implements Element {
    public String name;
    public String tooltip;
    public String icon;
    double price;
    public String command;

    public boolean enabled = true;

    Command() {
    }

    Command(String name, String tooltip, int price, String icon, boolean enabled) {
        this.name = name;
        this.tooltip = tooltip;
        this.icon = icon;
        this.enabled = enabled;
    }

    public static final BuilderCodec<Command> CODEC =
            BuilderCodec.builder(Command.class, Command::new)
                    .append(new KeyedCodec<>("Name", Codec.STRING),
                            (c, v) -> c.name = v,
                            c -> c.name).add()
                    .append(new KeyedCodec<>("Command", Codec.STRING),
                            (c, v) -> c.command = v,
                            c -> c.command).add()
                    .append(new KeyedCodec<>("Tooltip", Codec.STRING),
                            (c, v) -> c.tooltip = v,
                            c -> c.tooltip).add()
                    .append(new KeyedCodec<>("Price", Codec.DOUBLE),
                            (c, v) -> c.price = v,
                            c -> c.price).add()
                    .build();
}
