package gg.hytaleheroes.herobase.extra.shop.asset;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

public interface Element {
    CodecMapCodec<Element> TYPE_CODEC = new CodecMapCodec<>("Type");
}
