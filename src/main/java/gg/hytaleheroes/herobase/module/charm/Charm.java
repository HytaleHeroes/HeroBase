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
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public class Charm implements JsonAsset<String>, JsonAssetWithMap<String, DefaultAssetMap<String, Charm>> {
    private static AssetStore<String, Charm, DefaultAssetMap<String, Charm>> ASSET_STORE;

    public static final AssetBuilderCodec<String, Charm> CODEC = AssetBuilderCodec.builder(Charm.class, Charm::new, Codec.STRING, (a, b) -> a.id = b, (a) -> a.id, (a, b) -> a.extraData = b, (a) -> a.extraData)
            .appendInherited(new KeyedCodec<>("Name", Codec.STRING),
                    (config, x) -> config.name = x,
                    (config) -> config.name,
                    (config, parent) -> config.name = parent.name)
            .add()

            .appendInherited(new KeyedCodec<>("StatModifiers", new ArrayCodec<>(StaticModifier.CODEC, StaticModifier[]::new)),
                    (config, x) -> config.staticModifiers = x,
                    (config) -> config.staticModifiers,
                    (config, parent) -> config.staticModifiers = parent.staticModifiers)
            .add()
            .appendInherited(new KeyedCodec<>("Chance", Codec.INTEGER),
                    (config, x) -> config.cooldown = x,
                    (config) -> config.cooldown,
                    (config, parent) -> config.cooldown = parent.cooldown)
            .documentation("Chance from 0 to 100 in %")
            .add()

            .build();

    private String id;
    private AssetExtraInfo.Data extraData;

    private String name;
    private StaticModifier[] staticModifiers;
    private int cooldown;

    @Override
    public String getId() {
        return id;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Charm setCooldown(int cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public StaticModifier[] getStaticModifiers() {
        return staticModifiers;
    }

    public Charm setStaticModifiers(StaticModifier[] staticModifiers) {
        this.staticModifiers = staticModifiers;
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
}
