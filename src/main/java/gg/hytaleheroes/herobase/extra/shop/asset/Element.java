package gg.hytaleheroes.herobase.extra.shop.asset;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

public interface Element {
    CodecMapCodec<Element> CODEC = new CodecMapCodec<>("Type");

    String getUIDocumentPath();
    void build(String sel, UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder);
    void update(String sel, UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder);
    boolean run(int amount);
}
