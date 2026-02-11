package gg.hytaleheroes.herobase.module.charm.type.impl;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import gg.hytaleheroes.herobase.module.charm.type.api.EcsCharmEffect;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Collections;

public class MultijumpCharmEffect implements EcsCharmEffect<MovementStatesComponent> {
    public static final BuilderCodec<MultijumpCharmEffect> CODEC = BuilderCodec.builder(MultijumpCharmEffect.class, MultijumpCharmEffect::new)
            .appendInherited(new KeyedCodec<>("Jumps", Codec.INTEGER),
                    (config, x) -> config.jumps = x,
                    (config) -> config.jumps,
                    (config, parent) -> config.jumps = parent.jumps)
            .add()

            .appendInherited(new KeyedCodec<>("Velocity", Vector3d.CODEC),
                    (config, x) -> config.velocity = x,
                    (config) -> config.velocity,
                    (config, parent) -> config.velocity = parent.velocity)
            .add()

            .build();

    int jumps = 1;
    Vector3d velocity = new Vector3d(0, 1, 0);

    @Override
    public void apply(Ref<EntityStore> ref, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, MovementStatesComponent newComponent) {
        if (!newComponent.getMovementStates().onGround && newComponent.getMovementStates().crouching) {
            handle(ref, store, commandBuffer);
        } else if (newComponent.getMovementStates().onGround) {
            commandBuffer.tryRemoveComponent(ref, MultijumpCharmEffect.MultijumpCounter.TYPE);
        }
    }

    public void handle(Ref<EntityStore> ref, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        MultijumpCharmEffect.MultijumpCounter multijumpCounter = commandBuffer.getComponent(ref, MultijumpCharmEffect.MultijumpCounter.TYPE);
        if (multijumpCounter == null) {
            multijumpCounter = new MultijumpCharmEffect.MultijumpCounter(jumps);
        }

        if (multijumpCounter.value > 0 && (multijumpCounter.lastJump == null || Instant.now().toEpochMilli() - multijumpCounter.lastJump.toEpochMilli() >= 500)) {
            doJump(ref, store);
            multijumpCounter.value--;
            multijumpCounter.lastJump = Instant.now();
            commandBuffer.putComponent(ref, MultijumpCharmEffect.MultijumpCounter.TYPE, multijumpCounter);
        }
    }

    private void doJump(Ref<EntityStore> ref, Store<EntityStore> store) {
        TransformComponent transform = store.getComponent(ref, EntityModule.get().getTransformComponentType());
        if (transform != null) {
            Velocity velocity = store.getComponent(ref, Velocity.getComponentType());
            if (velocity != null) {
                Vector3d jumpVector = this.velocity.clone().rotateY(transform.getRotation().y);
                velocity.addInstruction(jumpVector, null, ChangeVelocityType.Add);
            }

            ///int index = SoundEvent.getAssetMap().getIndex("sf");
            ///SoundUtil.playSoundEvent3dToPlayer(ref, index, SoundCategory.UI, transform.getPosition(), store);

            Vector3d center = transform.getPosition();
            double radius = 0.75f;

            for (int i = 0; i < 8; ++i) {
                double angle = (Math.PI * 2.) * (double) i / 8f;
                double x = center.x + radius * Math.cos(angle);
                double z = center.z + radius * Math.sin(angle);
                double y = center.y + 0.1;

                Vector3d pos = new Vector3d(x, y, z);
                ParticleUtil.spawnParticleEffect("Block_Break_Snow", pos, Collections.singletonList(ref), store);
            }
        }
    }

    @Override
    public Type type() {
        return MovementStatesComponent.class;
    }

    public static class MultijumpCounter implements Component<EntityStore> {
        public static ComponentType<EntityStore, MultijumpCounter> TYPE;
        public Instant lastJump;

        public static void setup(ComponentRegistryProxy<EntityStore> entityStoreRegistry) {
            TYPE = entityStoreRegistry.registerComponent(MultijumpCounter.class, () -> null);
        }

        public int value;
        public Vector3d velocity;

        public MultijumpCounter() {
        }

        public MultijumpCounter(int v) {
            this.value = v;
        }

        @Nullable
        @Override
        public Component<EntityStore> clone() {
            return this;
        }
    }
}
