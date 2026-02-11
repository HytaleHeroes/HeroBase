package gg.hytaleheroes.herobase.extra.shop.asset;

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
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.schema.metadata.ui.UIRebuildCaches;

import java.util.ArrayList;
import java.util.List;

public class ServerShop implements JsonAsset<String>, JsonAssetWithMap<String, DefaultAssetMap<String, ServerShop>> {
    private static AssetStore<String, ServerShop, DefaultAssetMap<String, ServerShop>> ASSET_STORE;

    public static final AssetBuilderCodec<String, ServerShop> CODEC = AssetBuilderCodec.builder(ServerShop.class, ServerShop::new, Codec.STRING, (a, b) -> a.id = b, (a) -> a.id, (a, b) -> a.extraData = b, (a) -> a.extraData)
            .appendInherited(new KeyedCodec<>("Name", Codec.STRING),
                    (config, x) -> config.name = x,
                    (config) -> config.name,
                    (config, parent) -> config.name = parent.name)
            .add()

            .appendInherited(new KeyedCodec<>("Icon", Codec.STRING),
                    (config, x) -> config.icon = x,
                    (config) -> config.icon,
                    (config, parent) -> config.icon = parent.icon)
            .metadata(new UIEditor(new UIEditor.Icon("Icons/ItemsGenerated/{assetId}.png", 64, 64)))
            .metadata(new UIRebuildCaches(UIRebuildCaches.ClientCache.MODEL_TEXTURES))
            .add()

            .appendInherited(
                    new KeyedCodec<>("Sections",
                            new ArrayCodec<>(Section.CODEC, Section[]::new)),
                    (c, v) -> c.sections = List.of(v),
                    c -> c.sections.toArray(Section[]::new),
                    (config, parent) -> config.sections = parent.sections)
            .add()

            .build();

    private String id;
    private String name;
    private String icon;

    public List<Section> sections = new ArrayList<>();

    private AssetExtraInfo.Data extraData;

    public ServerShop() {}

    public ServerShop(String id, String name, String icon, String interaction) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String getId() {
        return id;
    }

    public static AssetStore<String, ServerShop, DefaultAssetMap<String, ServerShop>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(ServerShop.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, ServerShop> getAssetMap() {
        return ServerShop.getAssetStore().getAssetMap();
    }

    static {
        Element.TYPE_CODEC.register("Item", Item.class, Item.CODEC);
        Element.TYPE_CODEC.register("Command", Command.class, Command.CODEC);
    }
}
