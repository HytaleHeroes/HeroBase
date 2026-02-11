package gg.hytaleheroes.herobase.module.charm.type.api;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;

public interface CharmEffect {
    CodecMapCodec<CharmEffect> CODEC = new CodecMapCodec<>("Type");
}
