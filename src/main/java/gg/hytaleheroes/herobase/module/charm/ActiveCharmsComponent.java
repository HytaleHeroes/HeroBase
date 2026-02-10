package gg.hytaleheroes.herobase.module.charm;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ActiveCharmsComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, ActiveCharmsComponent> TYPE;
    private Map<String, int[]> internal = new HashMap<>();

    public static ComponentType<EntityStore, ActiveCharmsComponent> getComponentType() {
        return TYPE;
    }

    public static void setup(ComponentRegistryProxy<EntityStore> entityStoreRegistry) {
        ActiveCharmsComponent.TYPE = entityStoreRegistry.registerComponent(ActiveCharmsComponent.class, "ActiveCharms", ActiveCharmsComponent.CODEC);
    }

    public static final BuilderCodec<ActiveCharmsComponent> CODEC = BuilderCodec.builder(ActiveCharmsComponent.class, ActiveCharmsComponent::new)
            .append(new KeyedCodec<>("Values", new MapCodec<>(MapCodec.INT_ARRAY, HashMap::new)), (data, value) -> data.internal = value, (data) -> data.internal)
            .add()
            .build();

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        ActiveCharmsComponent data = new ActiveCharmsComponent();
        internal.forEach((key, value) -> data.internal.put(key, Arrays.copyOf(value, value.length)));
        return data;
    }

    public Map<String, int[]> all() {
        return internal;
    }

    public int[] get(String id) {
        return internal.get(id);
    }

    public boolean contains(String id) {
        return internal.containsKey(id);
    }

    public boolean remove(String id) {
        return internal.remove(id) != null;
    }
}
