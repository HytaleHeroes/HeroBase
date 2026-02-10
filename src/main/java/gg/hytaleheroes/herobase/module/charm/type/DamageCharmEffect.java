package gg.hytaleheroes.herobase.module.charm.type;

import com.hypixel.hytale.server.core.modules.entity.damage.Damage;

import java.lang.reflect.Type;

public interface DamageCharmEffect extends EcsCharmEffect<Damage> {

    boolean runOnEnemy();

    @Override
    default Type type() {
        return Damage.class;
    }
}
