package gg.hytaleheroes.herobase.module.charm.type;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.range.IntRange;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.interaction.BlockHarvestUtils;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class BlockLootCharmEffect implements EcsCharmEffect<BreakBlockEvent> {
    public static final BuilderCodec<BlockLootCharmEffect> CODEC = BuilderCodec.builder(BlockLootCharmEffect.class, BlockLootCharmEffect::new)
            .appendInherited(new KeyedCodec<>("BlockItemCategory", Codec.STRING),
                    (config, x) -> config.category = x,
                    (config) -> config.category,
                    (config, parent) -> config.category = parent.category)
            .metadata(new UIEditor(new UIEditor.Dropdown("ItemCategories")))
            .add()

            .appendInherited(new KeyedCodec<>("Chance", Codec.DOUBLE),
                    (config, x) -> config.chance = x,
                    (config) -> config.chance,
                    (config, parent) -> config.chance = parent.chance)
            .add()

            .appendInherited(new KeyedCodec<>("Multiplier", IntRange.CODEC),
                    (config, x) -> config.multiplier = x,
                    (config) -> config.multiplier,
                    (config, parent) -> config.multiplier = parent.multiplier)
            .add()

            .appendInherited(new KeyedCodec<>("MaxDrops", IntRange.CODEC),
                    (config, x) -> config.maxDrops = x,
                    (config) -> config.maxDrops,
                    (config, parent) -> config.maxDrops = parent.maxDrops)

            .add()
            .build();

    String category;
    double chance = 0;
    IntRange multiplier = new IntRange(2,2);
    IntRange maxDrops = new IntRange(2, 2);

    @Override
    public void apply(Ref<EntityStore> ref, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, BreakBlockEvent event) {
        var player = commandBuffer.getComponent(ref, Player.getComponentType());

        double v = chance / 100.0;

        if (v < Math.random()) {
            return;
        }

        List<ItemStack> defaultDrops = getBlockDrops(event.getBlockType());
        var match = defaultDrops.stream().filter(x -> Arrays.stream(x.getItem().getCategories()).anyMatch(y -> y.equals(category))).toList();

        if (!match.isEmpty()) {
            Vector3d spawnPos = event.getTargetBlock().toVector3d().add(0.5, 0.5, 0.5);
            Vector3f velocity = new Vector3f(0, 0.2f, 0);

            Holder<EntityStore>[] itemEntities = ItemComponent.generateItemDrops(store, match, spawnPos, velocity);

            var amount = maxDrops.getInt(Math.random());
            int c = 0;
            for (Holder<EntityStore> itemEntity : itemEntities) {

                commandBuffer.addEntity(itemEntity, AddReason.SPAWN);

                c++;
                if (c >= amount)
                    break;
            }
        }
    }

    @Override
    public Type type() {
        return BreakBlockEvent.class;
    }

    private List<ItemStack> getBlockDrops(BlockType blockType) {
        if (blockType.getGathering() == null || blockType.getGathering().getBreaking() == null) {
            return List.of();
        }

        return BlockHarvestUtils.getDrops(blockType, multiplier.getInt(Math.random()), blockType.getGathering().getBreaking().getItemId(), blockType.getGathering().getBreaking().getDropListId());
    }
}
