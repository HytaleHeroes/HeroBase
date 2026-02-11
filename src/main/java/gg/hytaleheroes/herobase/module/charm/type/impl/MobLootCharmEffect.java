package gg.hytaleheroes.herobase.module.charm.type.impl;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.item.ItemModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import gg.hytaleheroes.herobase.module.charm.type.api.CharmEffect;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class MobLootCharmEffect implements CharmEffect {
    public static final BuilderCodec<MobLootCharmEffect> CODEC = BuilderCodec.builder(MobLootCharmEffect.class, MobLootCharmEffect::new)
            .appendInherited(new KeyedCodec<>("MobType", Codec.STRING),
                    (config, x) -> config.mobType = x,
                    (config) -> config.mobType,
                    (config, parent) -> config.mobType = parent.mobType)
            .add()

            .appendInherited(new KeyedCodec<>("Chance", Codec.DOUBLE),
                    (config, x) -> config.chance = x,
                    (config) -> config.chance,
                    (config, parent) -> config.chance = parent.chance)
            .add()

            .build();

    String mobType;
    double chance = 0;

    public void apply(Ref<EntityStore> ref, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, NPCEntity npcComponent, TransformComponent transformComponent) {
        ObjectArrayList<ItemStack> itemsToDrop = new ObjectArrayList<>();
        String dropListId;
        ItemModule itemModule = ItemModule.get();
        if ((dropListId = npcComponent.getRole().getDropListId()) != null && itemModule.isEnabled()) {
            List<ItemStack> randomItemsToDrop = itemModule.getRandomItemDrops(dropListId);
            itemsToDrop.addAll(randomItemsToDrop);
        }

        if (!itemsToDrop.isEmpty()) {
            Vector3d position = transformComponent.getPosition();
            HeadRotation headRotationComponent = store.getComponent(ref, HeadRotation.getComponentType());

            assert headRotationComponent != null;
            Vector3f headRotation = headRotationComponent.getRotation();
            Vector3d dropPosition = position.clone().add(0.0, 1.0, 0.0);
            Holder<EntityStore>[] drops = ItemComponent.generateItemDrops(store, itemsToDrop, dropPosition, headRotation.clone());
            commandBuffer.addEntities(drops, AddReason.SPAWN);
        }
    }
}
