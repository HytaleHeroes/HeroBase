package gg.hytaleheroes.herobase.module.charm.type;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.lang.reflect.Type;

public interface EcsCharmEffect<T> extends CharmEffect {
    void apply(Ref<EntityStore> ref, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, T event);

    Type type();
}
