package gg.hytaleheroes.herobase.ability.gui.hud;

import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import gg.hytaleheroes.herobase.ability.Ability;

import javax.annotation.Nonnull;
import java.util.List;

public class AbilityHud extends CustomUIHud {
    final List<Ability> abilities;
    final int oldSlot;

    public AbilityHud(@Nonnull PlayerRef playerRef, List<Ability> abilities, int oldSlot) {
        super(playerRef);
        this.abilities = abilities;
        this.oldSlot = oldSlot;
    }

    public int getOldSlot() {
        return oldSlot;
    }

    @Override
    protected void build(@Nonnull UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Pages/AbilityHud.ui");

        for (int i = 0; i < this.abilities.size(); i++) {
            var ability = this.abilities.get(i);
            uiCommandBuilder.append("#Root #Bar", "Pages/AbilityButton.ui");
            uiCommandBuilder.set("#Root #Bar["+i+"] #Name.Text", ability.getName());
            uiCommandBuilder.set("#Root #Bar["+i+"] #Icon.AssetPath", "UI/Custom/Pages/" + ability.getIcon());
        }
    }

    public static void resetSlot(PlayerRef playerRef, Player player) {
        resetSlot(playerRef, player, 8);
    }

    public static void resetSlot(PlayerRef playerRef, Player player, int slot) {
        player.getInventory().setActiveHotbarSlot((byte)slot);
        SetActiveSlot setActiveSlotPacket = new SetActiveSlot(
                Inventory.HOTBAR_SECTION_ID,
                slot
        );
        playerRef.getPacketHandler().write(setActiveSlotPacket);
    }
}