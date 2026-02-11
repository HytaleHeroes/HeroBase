package gg.hytaleheroes.herobase.module.charm.type.impl;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.validator.ArrayValidator;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.charm.type.api.TickingCharmEffect;

public class EntityEffectCharmEffect implements TickingCharmEffect {
    public static final BuilderCodec<EntityEffectCharmEffect> CODEC = BuilderCodec.builder(EntityEffectCharmEffect.class, EntityEffectCharmEffect::new)
            .appendInherited(new KeyedCodec<>("IntervalSeconds", Codec.INTEGER),
                    (config, x) -> config.intervalSeconds = x,
                    (config) -> config.intervalSeconds,
                    (config, parent) -> config.intervalSeconds = parent.intervalSeconds)
            .add()

            .appendInherited(new KeyedCodec<>("Chance", Codec.DOUBLE),
                    (config, x) -> config.chance = x,
                    (config) -> config.chance,
                    (config, parent) -> config.chance = parent.chance)
            .add()

            .appendInherited(new KeyedCodec<>("Effects", Codec.STRING_ARRAY),
                    (config, x) -> config.effects = x,
                    (config) -> config.effects,
                    (config, parent) -> config.effects = parent.effects)
            .addValidator(new ArrayValidator<>(EntityEffect.VALIDATOR_CACHE.getValidator()))
            .add()

            .build();

    String[] effects;
    int intervalSeconds = 10;
    double chance = 1;

    @Override
    public void tick(int i, ArchetypeChunk<EntityStore> archetypeChunk, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        double v = chance / 100.0;

        if (v < Math.random()) {
            return;
        }

        var ref = archetypeChunk.getReferenceTo(i);
        if (effects != null) for (String effectId : effects) {
            var effect = EntityEffect.getAssetMap().getAsset(effectId);
            if (effect != null) {
                var eff = archetypeChunk.getComponent(i, EffectControllerComponent.getComponentType());
                if (eff != null) eff.addEffect(ref, effect, store);
            }
        }
    }

    @Override
    public int getInterval() {
        return intervalSeconds;
    }
}
