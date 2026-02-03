package gg.hytaleheroes.herobase.gui.hud;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.PatchStyle;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import gg.hytaleheroes.herobase.format.TinyMsg;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AbilityHud extends CustomUIHud {
    private Vector3d startDirection;
    private int selection = -1;

    private static final double CENTER_DRAG_SPEED = 0.05;
    private static final double HYSTERESIS_ANGLE = Math.toRadians(2.0);

    final Map<String, String> abilities;
    final List<String> strings;

    public AbilityHud(@Nonnull PlayerRef playerRef, Vector3d direction, Map<String, String> abilities) {
        super(playerRef);
        this.abilities = abilities;
        this.startDirection = new Vector3d(direction).normalize();
        this.strings = abilities.keySet().stream().toList();
    }

    public void updateDirection(Vector3d currentDirection) {
        int idx = getWheelSelection(
                this.startDirection,
                currentDirection,
                this.selection,
                5,
                0.05
        );

        if (idx == -1) {
            Vector3d diff = new Vector3d(currentDirection).subtract(this.startDirection);
            this.startDirection.add(diff.scale(CENTER_DRAG_SPEED));
            this.startDirection.normalize();
        }

        UICommandBuilder commandBuilder = new UICommandBuilder();

        if (idx != this.selection) {
            this.selection = idx;

            String key = "";
            if (idx >= 0) {
                key = this.strings.get(idx);
                commandBuilder.setObject("#Wheel.Background", new PatchStyle(Value.of("wheel" + selection + ".png")));
            } else {
                commandBuilder.setObject("#Wheel.Background", new PatchStyle(Value.of("wheel.png")));
            }

            commandBuilder.set("#AbilityLabel.TextSpans", TinyMsg.parse(key));

            this.update(false, commandBuilder);
        }
    }

    public String selectedInteraction() {
        if (this.selection == -1)
            return null;

        return this.abilities.get(this.strings.get(this.selection));
    }

    @Override
    protected void build(@Nonnull UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("AbilityHud.ui");
    }

    public static int getWheelSelection(
            Vector3d startDir,
            Vector3d currDir,
            int currentSelection,
            int slices,
            double deadzone
    ) {
        Vector3d right = startDir.cross(Vector3d.UP);
        if (right.squaredLength() < 0.001) right = new Vector3d(1, 0, 0);
        else right.normalize();

        Vector3d upLocal = right.cross(startDir).normalize();

        double dx = currDir.dot(right);
        double dy = currDir.dot(upLocal);

        double magSq = dx * dx + dy * dy;
        if (magSq < deadzone * deadzone) {
            return -1;
        }

        double angle = Math.atan2(dx, dy);
        if (angle < 0) angle += Math.PI * 2.0;

        double sliceSize = (Math.PI * 2.0) / slices;

        double rawSelection = (angle + (sliceSize / 2.0));
        if (rawSelection >= Math.PI * 2.0) rawSelection -= Math.PI * 2.0;

        int newSelection = (int) (rawSelection / sliceSize);

        if (currentSelection != -1 && newSelection != currentSelection) {
            double angleToCurrent = getAngleToCenterOfSlice(currentSelection, slices, angle);
            if (angleToCurrent < (sliceSize / 2.0) + HYSTERESIS_ANGLE) {
                return currentSelection;
            }
        }

        return newSelection;
    }

    private static double getAngleToCenterOfSlice(int sliceIdx, int totalSlices, double currentAngle) {
        double sliceSize = (Math.PI * 2.0) / totalSlices;
        double sliceCenterAngle = sliceIdx * sliceSize;

        double dist = Math.abs(currentAngle - sliceCenterAngle);
        if (dist > Math.PI) dist = (Math.PI * 2.0) - dist;
        return dist;
    }

    public void close(InteractionManager intactionManager) {


    }
}