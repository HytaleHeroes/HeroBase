package gg.hytaleheroes.herobase.ability.gui.hud;

import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import gg.hytaleheroes.herobase.ability.Ability;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nonnull;

public class AbilityHud extends CustomUIHud {
    final Int2ObjectMap<String> slots;
    final int oldSlot;

    public AbilityHud(@Nonnull PlayerRef playerRef, Int2ObjectMap<String> slots, int oldSlot) {
        super(playerRef);

        this.slots = new Int2ObjectOpenHashMap<>(slots);
        this.oldSlot = oldSlot;
    }

    public int getOldSlot() {
        return oldSlot;
    }

    @Override
    protected void build(@Nonnull UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Pages/Ability/AbilityHud.ui");

        int index = 0;
        for (Integer i : slots.keySet().intStream().sorted().boxed().toList()) {
            var ability = Ability.getAssetMap().getAsset(slots.get(i));
            if (ability != null) {
                uiCommandBuilder.append("#Root #Bar", "Pages/Ability/AbilityButton.ui");
                uiCommandBuilder.set("#Root #Bar[" + index + "] #Name.Text", ability.getName());
                uiCommandBuilder.set("#Root #Bar[" + index + "] #Icon.AssetPath", ability.getIcon());

                index++;
            }
        }
    }

    public static void resetSlot(PlayerRef playerRef, Player player) {
        resetSlot(playerRef, player, 8);
    }

    public static void resetSlot(PlayerRef playerRef, Player player, int slot) {
        player.getInventory().setActiveHotbarSlot((byte) slot);
        SetActiveSlot setActiveSlotPacket = new SetActiveSlot(
                Inventory.HOTBAR_SECTION_ID,
                slot
        );
        playerRef.getPacketHandler().write(setActiveSlotPacket);
    }
}