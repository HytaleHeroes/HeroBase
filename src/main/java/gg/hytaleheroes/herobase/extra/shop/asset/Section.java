package gg.hytaleheroes.herobase.extra.shop.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

import java.util.ArrayList;
import java.util.List;

public final class Section {
    public String name;
    public List<Element> elements = new ArrayList<>();
    public boolean enabled = true;

    Section() {
    }

    Section(String name, List<Element> elements, boolean enabled) {
        this.name = name;
        this.elements = elements;
        this.enabled = enabled;
    }

    public static final BuilderCodec<Section> CODEC =
            BuilderCodec.builder(Section.class, Section::new)

                    .append(new KeyedCodec<>("Name", Codec.STRING),
                            (s, v) -> s.name = v,
                            s -> s.name).add()

                    .append(
                            new KeyedCodec<>("Elements",
                                    new ArrayCodec<>(Element.TYPE_CODEC, Item[]::new)),
                            (s, v) -> s.elements = List.of(v),
                            s -> s.elements.toArray(Item[]::new)
                    ).add()

                    .build();


}
