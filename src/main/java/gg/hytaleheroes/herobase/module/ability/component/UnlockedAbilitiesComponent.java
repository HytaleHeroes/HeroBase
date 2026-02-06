package gg.hytaleheroes.herobase.module.ability.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.ability.Ability;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.List;
import java.util.Set;

public class UnlockedAbilitiesComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, UnlockedAbilitiesComponent> TYPE;
    private Set<String> internal = new ObjectArraySet<>();

    public static ComponentType<EntityStore, UnlockedAbilitiesComponent> getComponentType() {
        return TYPE;
    }

    public static void setup(ComponentRegistryProxy<EntityStore> entityStoreRegistry) {
        UnlockedAbilitiesComponent.TYPE = entityStoreRegistry.registerComponent(UnlockedAbilitiesComponent.class, "UnlockedAbilities", UnlockedAbilitiesComponent.CODEC);
    }

    public static final BuilderCodec<UnlockedAbilitiesComponent> CODEC = BuilderCodec.builder(UnlockedAbilitiesComponent.class, UnlockedAbilitiesComponent::new)
            .append(new KeyedCodec<>("Values", BuilderCodec.STRING_ARRAY), (data, value) -> data.internal = new ObjectArraySet<>(List.of(value)), (data) -> data.internal.toArray(new String[0]))
            .add()
            .build();


    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        UnlockedAbilitiesComponent data = new UnlockedAbilitiesComponent();
        data.internal.addAll(internal);
        return data;
    }

    public Set<String> all() {
        return internal;
    }

    public boolean add(String id) {
        return internal.add(id);
    }

    public void add(Ability id) {
        internal.add(id.getId());
    }

    public boolean contains(String id) {
        return internal.contains(id);
    }

    public boolean remove(String id) {
        return internal.remove(id);
    }
}
