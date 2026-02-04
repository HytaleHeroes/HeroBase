package gg.hytaleheroes.herobase.extra.action;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class BuilderActionSendMessage extends BuilderActionBase {
    String message;

    @Nonnull
    public String getShortDescription() {
        return "Sends a formatted message to the current player";
    }

    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Nonnull
    public Action build(@Nonnull BuilderSupport builderSupport) {
        return new ActionSendMessage(this, builderSupport);
    }

    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionSendMessage readConfig(@Nonnull JsonElement data) {
        this.getString(data, "Message", v -> this.message = v, "", StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "Formatted Message", null);

        this.requireInstructionType(EnumSet.of(InstructionType.Interaction));
        return this;
    }

    public String getMessage(@Nonnull BuilderSupport support) {
        return this.message;
    }
}