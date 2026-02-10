package gg.hytaleheroes.herobase.module.charm.type;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.validator.ArrayValidator;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class DamageEntityEffectCharmEffect implements DamageCharmEffect {
    public static final BuilderCodec<DamageEntityEffectCharmEffect> CODEC = BuilderCodec.builder(DamageEntityEffectCharmEffect.class, DamageEntityEffectCharmEffect::new)
            .appendInherited(new KeyedCodec<>("DamageCause", Codec.STRING_ARRAY),
                    (config, x) -> config.damageCause = x,
                    (config) -> config.damageCause,
                    (config, parent) -> config.damageCause = parent.damageCause)
            .addValidator(new ArrayValidator<>(DamageCause.VALIDATOR_CACHE.getValidator()))
            .add()

            .appendInherited(new KeyedCodec<>("Effects", Codec.STRING_ARRAY),
                    (config, x) -> config.effects = x,
                    (config) -> config.effects,
                    (config, parent) -> config.effects = parent.effects)
            .addValidator(new ArrayValidator<>(EntityEffect.VALIDATOR_CACHE.getValidator()))
            .add()

            .appendInherited(new KeyedCodec<>("Chance", Codec.DOUBLE),
                    (config, x) -> config.chance = x,
                    (config) -> config.chance,
                    (config, parent) -> config.chance = parent.chance)
            .add()

            .appendInherited(new KeyedCodec<>("RunOnEnemy", Codec.BOOLEAN),
                    (config, x) -> config.runOnEnemy = x,
                    (config) -> config.runOnEnemy,
                    (config, parent) -> config.runOnEnemy = parent.runOnEnemy)
            .add()

            .build();

    String[] damageCause;
    String[] effects;
    double chance;
    boolean runOnEnemy;

    @Override
    public boolean runOnEnemy() {
        return runOnEnemy;
    }

    @Override
    public void apply(Ref<EntityStore> ref, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, Damage unused) {
        double v = chance / 100.0;

        if (v < Math.random()) {
            return;
        }

        for (String effect : effects) {
            var e = EntityEffect.getAssetMap().getAsset(effect);
            if (e != null) {
                var eff = commandBuffer.getComponent(ref, EffectControllerComponent.getComponentType());
                if (eff != null) eff.addEffect(ref, e, store);
            }
        }
    }
}
