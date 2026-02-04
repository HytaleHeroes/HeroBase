package gg.hytaleheroes.herobase.ability.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.ability.Ability;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Map;

public class AbilityCooldownsComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, AbilityCooldownsComponent> TYPE;
    private Object2IntOpenHashMap<String> internal = new Object2IntOpenHashMap<>();

    public static ComponentType<EntityStore, AbilityCooldownsComponent> getComponentType() {
        return TYPE;
    }

    public static void setup(ComponentRegistryProxy<EntityStore> entityStoreRegistry) {
        AbilityCooldownsComponent.TYPE = entityStoreRegistry.registerComponent(AbilityCooldownsComponent.class, "AbilityCooldowns", AbilityCooldownsComponent.CODEC);
    }

    public static final BuilderCodec<AbilityCooldownsComponent> CODEC = BuilderCodec.builder(AbilityCooldownsComponent.class, AbilityCooldownsComponent::new)
            .append(new KeyedCodec<>("Cooldowns", new MapCodec<>(MapCodec.INTEGER, Object2IntOpenHashMap::new)), (data, value) -> data.internal = new Object2IntOpenHashMap<>(value), (data) -> data.internal)
            .add()
            .build();

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        AbilityCooldownsComponent data = new AbilityCooldownsComponent();
        data.internal.putAll(internal);
        return data;
    }

    public Map<String, Integer> all() {
        return internal;
    }

    public Integer add(String id, Integer cooldown) {
        return internal.put(id, cooldown);
    }

    public void add(Ability ability) {
        internal.put(ability.getId(), ability.getCooldown());
    }

    public boolean hasCooldown(String id) {
        var val = internal.get(id);
        if (val == null) {
            return false;
        } else if (val <= 0) {
            internal.removeInt(id);
            return false;
        }

        return true;
    }

    public Integer remove(String id) {
        return internal.removeInt(id);
    }
}
