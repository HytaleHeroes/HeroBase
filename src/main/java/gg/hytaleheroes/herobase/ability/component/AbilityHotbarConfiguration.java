package gg.hytaleheroes.herobase.ability.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.Int2ObjectMapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nullable;

public class AbilityHotbarConfiguration implements Component<EntityStore> {
    private static ComponentType<EntityStore, AbilityHotbarConfiguration> TYPE;

    public static ComponentType<EntityStore, AbilityHotbarConfiguration> getComponentType() {
        return TYPE;
    }

    public static void setup(ComponentRegistryProxy<EntityStore> entityStoreRegistry) {
        AbilityHotbarConfiguration.TYPE = entityStoreRegistry.registerComponent(AbilityHotbarConfiguration.class, "AbilityHotbar", AbilityHotbarConfiguration.CODEC);
    }

    Int2ObjectMap<String> slots = new Int2ObjectOpenHashMap<>();

    public static final BuilderCodec<AbilityHotbarConfiguration> CODEC = BuilderCodec.builder(AbilityHotbarConfiguration.class, AbilityHotbarConfiguration::new)
            .append(new KeyedCodec<>("Slots", new Int2ObjectMapCodec<>(Codec.STRING, Int2ObjectOpenHashMap::new)), (o, v) -> o.slots = v, (o) -> o.slots)
            .documentation("Slot configuration")
            .add()
            .build();

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        var conf = new AbilityHotbarConfiguration();
        conf.slots.putAll(this.slots);
        return conf;
    }

    public Int2ObjectMap<String> getSlots() {
        return slots;
    }

    public AbilityHotbarConfiguration setSlots(Int2ObjectMap<String> slots) {
        this.slots = slots;
        return this;
    }
}
