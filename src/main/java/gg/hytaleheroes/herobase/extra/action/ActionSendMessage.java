package gg.hytaleheroes.herobase.extra.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import gg.hytaleheroes.herobase.TinyMsg;

import javax.annotation.Nonnull;

public class ActionSendMessage extends ActionBase {
    protected final String message;

    public ActionSendMessage(@Nonnull BuilderActionSendMessage builder, @Nonnull BuilderSupport support) {
        super(builder);
        this.message = builder.getMessage(support);
    }

    public boolean canExecute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        return super.canExecute(ref, role, sensorInfo, dt, store) && role.getStateSupport().getInteractionIterationTarget() != null;
    }

    public boolean execute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        super.execute(ref, role, sensorInfo, dt, store);
        Ref<EntityStore> playerReference = role.getStateSupport().getInteractionIterationTarget();
        if (playerReference == null) {
            return false;
        } else {
            PlayerRef playerRefComponent = store.getComponent(playerReference, PlayerRef.getComponentType());
            if (playerRefComponent == null) {
                return false;
            } else {
                Player playerComponent = store.getComponent(playerReference, Player.getComponentType());
                if (playerComponent == null) {
                    return false;
                } else {
                    if (message != null) {
                        for (String s : message.split("\n")) {
                            playerComponent.sendMessage(TinyMsg.parse(s));
                        }
                    }

                    return true;
                }
            }
        }
    }
}