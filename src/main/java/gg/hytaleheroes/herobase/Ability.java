package gg.hytaleheroes.herobase;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.JsonAsset;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.schema.metadata.ui.UIRebuildCaches;
import com.hypixel.hytale.server.core.asset.common.CommonAssetValidator;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;

import java.util.Objects;

public final class Ability implements JsonAsset<String>, JsonAssetWithMap<String, DefaultAssetMap<String, Ability>> {
    private static AssetStore<String, Ability, DefaultAssetMap<String, Ability>> ASSET_STORE;

    public static final AssetBuilderCodec<String, Ability> CODEC = AssetBuilderCodec.builder(Ability.class, Ability::new, Codec.STRING, (a, b) -> a.id = b, (a) -> a.id, (a, b) -> a.extraData = b, (a) -> a.extraData)
            .appendInherited(new KeyedCodec<>("Name", Codec.STRING),
                    (config, x) -> config.name = x,
                    (config) -> config.name,
                    (config, parent) -> config.name = parent.name)
            .add()

            .appendInherited(new KeyedCodec<>("Icon", Codec.STRING),
                    (config, x) -> config.name = x,
                    (config) -> config.name,
                    (config, parent) -> config.name = parent.name)
            .metadata(new UIEditor(new UIEditor.Icon("Icons/ItemsGenerated/{assetId}.png", 64, 64)))
            .metadata(new UIRebuildCaches(UIRebuildCaches.ClientCache.MODEL_TEXTURES))
            .add()

            .appendInherited(new KeyedCodec<>("Cooldown", Codec.INTEGER),
                    (config, x) -> config.cooldown = x,
                    (config) -> config.cooldown,
                    (config, parent) -> config.cooldown = parent.cooldown)
            .add()

            .appendInherited(new KeyedCodec<>("Interaction",  RootInteraction.CHILD_ASSET_CODEC),
                    (config, x) -> config.interaction = x,
                    (config) -> config.interaction,
                    (config, parent) -> config.interaction = parent.interaction)
            .addValidator(RootInteraction.VALIDATOR_CACHE.getValidator())
            .add()

            .build();


    private String id;
    private String name;
    private String icon;
    private Integer cooldown;
    private String interaction;
    private AssetExtraInfo.Data extraData;

    public Ability() {}

    public Ability(String id, String name, String icon, String interaction) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.interaction = interaction;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public Integer getCooldown() {
        return cooldown;
    }

    public String getInteraction() {
        return interaction;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Ability) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.icon, that.icon) &&
                Objects.equals(this.interaction, that.interaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, icon, interaction);
    }

    @Override
    public String toString() {
        return "Ability[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "icon=" + icon + ", " +
                "interaction=" + interaction + ']';
    }

    @Override
    public String getId() {
        return id;
    }

    public static AssetStore<String, Ability, DefaultAssetMap<String, Ability>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(Ability.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, Ability> getAssetMap() {
        return Ability.getAssetStore().getAssetMap();
    }
}
