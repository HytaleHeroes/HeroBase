package gg.hytaleheroes.herobase.module.charm.type.impl;

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
import gg.hytaleheroes.herobase.module.charm.type.api.DamageCharmEffect;

import java.util.HashSet;
import java.util.Set;

public class DamageEntityEffectCharmEffect implements DamageCharmEffect {
    public static final BuilderCodec<DamageEntityEffectCharmEffect> CODEC = BuilderCodec.builder(DamageEntityEffectCharmEffect.class, DamageEntityEffectCharmEffect::new)
            .appendInherited(new KeyedCodec<>("DamageCause", Codec.STRING_ARRAY),
                    (config, x) -> config.damageCause = Set.of(x),
                    (config) -> config.damageCause.toArray(new String[0]),
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

            .appendInherited(new KeyedCodec<>("RunOnEnemyAttack", Codec.BOOLEAN),
                    (config, x) -> config.runOnEnemy = x,
                    (config) -> config.runOnEnemy,
                    (config, parent) -> config.runOnEnemy = parent.runOnEnemy)
            .documentation("Whether to run the entity effects when the player is hurt")
            .add()

            .appendInherited(new KeyedCodec<>("ApplyToEnemy", Codec.BOOLEAN),
                    (config, x) -> config.applyToEnemy = x,
                    (config) -> config.applyToEnemy,
                    (config, parent) -> config.applyToEnemy = parent.applyToEnemy)
            .documentation("Whether apply the effect on the attacker")
            .add()


            .build();

    Set<String> damageCause = new HashSet<>();
    String[] effects;
    double chance;
    boolean runOnEnemy;
    boolean applyToEnemy;

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

        for (String effectId : effects) {
            var entityEffect = EntityEffect.getAssetMap().getAsset(effectId);
            if (entityEffect != null) {
                EffectControllerComponent eff;
                if (unused.getSource() instanceof Damage.EntitySource entitySource && applyToEnemy) {
                    eff = commandBuffer.getComponent(entitySource.getRef(), EffectControllerComponent.getComponentType());
                    if (eff != null) eff.addEffect(entitySource.getRef(), entityEffect, store);
                }else {
                    eff = commandBuffer.getComponent(ref, EffectControllerComponent.getComponentType());
                    if (eff != null) eff.addEffect(ref, entityEffect, store);
                }
            }
        }
    }
}
