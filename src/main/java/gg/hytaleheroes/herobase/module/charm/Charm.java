package gg.hytaleheroes.herobase.module.charm;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.JsonAsset;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.charm.type.CharmEffect;
import gg.hytaleheroes.herobase.module.charm.type.EcsCharmEffect;

import java.lang.reflect.Type;

public class Charm implements JsonAsset<String>, JsonAssetWithMap<String, DefaultAssetMap<String, Charm>> {
    private static AssetStore<String, Charm, DefaultAssetMap<String, Charm>> ASSET_STORE;

    public static final AssetBuilderCodec<String, Charm> CODEC = AssetBuilderCodec.builder(Charm.class, Charm::new, Codec.STRING, (a, b) -> a.id = b, (a) -> a.id, (a, b) -> a.extraData = b, (a) -> a.extraData)
            .appendInherited(new KeyedCodec<>("Name", Codec.STRING),
                    (config, x) -> config.name = x,
                    (config) -> config.name,
                    (config, parent) -> config.name = parent.name)
            .add()

            .appendInherited(new KeyedCodec<>("Effects", new ArrayCodec<>(CharmEffect.CODEC, CharmEffect[]::new)),
                    (config, x) -> config.effects = x,
                    (config) -> config.effects,
                    (config, parent) -> config.effects = parent.effects)
            .add()
            .build();

    private String id;
    private AssetExtraInfo.Data extraData;

    private String name;
    private CharmEffect[] effects;

    private int chance;

    @Override
    public String getId() {
        return id;
    }

    public int getChance() {
        return chance;
    }

    public Charm setChance(int chance) {
        this.chance = chance;
        return this;
    }

    public CharmEffect[] getEffects() {
        return effects;
    }

    public Charm setEffects(CharmEffect[] effects) {
        this.effects = effects;
        return this;
    }

    public String getName() {
        return name;
    }

    public Charm setName(String name) {
        this.name = name;
        return this;
    }

    public static AssetStore<String, Charm, DefaultAssetMap<String, Charm>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(Charm.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, Charm> getAssetMap() {
        return Charm.getAssetStore().getAssetMap();
    }

    public void run(Ref<EntityStore> ref, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, Type t, Object newComponent) {
        for (CharmEffect effect : this.getEffects()) {
            if (effect instanceof EcsCharmEffect ecsCharmEffect && ecsCharmEffect.type() == t) {
                ecsCharmEffect.apply(ref, store, commandBuffer, newComponent);
            }
        }
    }
}
