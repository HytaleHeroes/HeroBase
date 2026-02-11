package gg.hytaleheroes.herobase.module.charm.type.impl;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.validator.ArrayValidator;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.range.FloatRange;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.charm.type.api.DamageCharmEffect;

import java.util.HashSet;
import java.util.Set;

public class DamageModifierCharmEffect implements DamageCharmEffect {
    public static final BuilderCodec<DamageModifierCharmEffect> CODEC = BuilderCodec.builder(DamageModifierCharmEffect.class, DamageModifierCharmEffect::new)
            .appendInherited(new KeyedCodec<>("DamageCause", Codec.STRING_ARRAY),
                    (config, x) -> config.damageCause = Set.of(x),
                    (config) -> config.damageCause.toArray(new String[0]),
                    (config, parent) -> config.damageCause = parent.damageCause)
            .addValidator(new ArrayValidator<>(DamageCause.VALIDATOR_CACHE.getValidator()))
            .add()

            .appendInherited(new KeyedCodec<>("Chance", Codec.DOUBLE),
                    (config, x) -> config.chance = x,
                    (config) -> config.chance,
                    (config, parent) -> config.chance = parent.chance)
            .add()

            .appendInherited(new KeyedCodec<>("Multiplier", FloatRange.CODEC),
                    (config, x) -> config.multiplier = x,
                    (config) -> config.multiplier,
                    (config, parent) -> config.multiplier = parent.multiplier)
            .add()

            .appendInherited(new KeyedCodec<>("RunOnEnemyAttack", Codec.BOOLEAN),
                    (config, x) -> config.runOnEnemy = x,
                    (config) -> config.runOnEnemy,
                    (config, parent) -> config.runOnEnemy = parent.runOnEnemy)
            .add()

            .build();

    Set<String> damageCause = new HashSet<>();
    double chance;
    FloatRange multiplier = new FloatRange(1.1f, 1.2f);
    boolean runOnEnemy;

    @Override
    public boolean runOnEnemy() {
        return runOnEnemy;
    }

    @Override
    public void apply(Ref<EntityStore> ref, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, Damage damage) {
        double v = chance / 100.0;

        if (v < Math.random()) {
            return;
        }

        var cause = DamageCause.getAssetMap().getAsset(damage.getDamageCauseIndex());

        if (damageCause == null || (cause != null && damageCause.contains(cause.getId())))
            damage.setAmount(damage.getAmount() * multiplier.getFloat(Math.random()));
    }
}
