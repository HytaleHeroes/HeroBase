package gg.hytaleheroes.herobase.module.charm.type;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;

public class EntityEffectCharmEffect implements CharmEffect {
    public static final BuilderCodec<EntityEffectCharmEffect> CODEC = BuilderCodec.builder(EntityEffectCharmEffect.class, EntityEffectCharmEffect::new)
            .appendInherited(new KeyedCodec<>("Name", Codec.STRING),
                    (config, x) -> config.name = x,
                    (config) -> config.name,
                    (config, parent) -> config.name = parent.name)
            .add()

            .appendInherited(new KeyedCodec<>("IntervalSeconds", Codec.INTEGER),
                    (config, x) -> config.intervalSeconds = x,
                    (config) -> config.intervalSeconds,
                    (config, parent) -> config.intervalSeconds = parent.intervalSeconds)
            .add()

            .appendInherited(new KeyedCodec<>("Effects", new ArrayCodec<>(EntityEffect.CODEC, EntityEffect[]::new)),
                    (config, x) -> config.effects = x,
                    (config) -> config.effects,
                    (config, parent) -> config.effects = parent.effects)
            .add()

            .build();

    String name;
    EntityEffect[] effects;
    int intervalSeconds = 10;

}
