package gg.hytaleheroes.herobase.extra.shop.asset;

import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

import java.util.List;

final class Command implements Element {
    public String name;
    public String tooltip;
    public String icon;
    int price;

    public boolean enabled = true;

    public List<Button> buttons = List.of(
            new Button("Buy", List.of("echo hello"), false)
    );

    Command() {
    }

    Command(String name, String tooltip, int price, String icon, boolean enabled) {
        this.name = name;
        this.tooltip = tooltip;
        this.icon = icon;
        this.enabled = enabled;
    }

    @Override
    public String getUIDocumentPath() {
        return "Pages/Navigator/Card.ui";
    }

    @Override
    public void build(String sel, UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder) {

    }

    @Override
    public void update(String sel, UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder) {

    }

    @Override
    public boolean run(int amount) {

        return true;
    }
}
