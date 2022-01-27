package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneAction;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.function.BiConsumer;

public class BooleanAction implements ISceneAction {
    protected BiConsumer<BlockPartyNPC, Boolean> setter;
    private boolean value;

    public BooleanAction(BiConsumer<BlockPartyNPC, Boolean> setter) {
        this.setter = setter;
    }

    public BooleanAction() { }

    @Override
    public void apply(BlockPartyNPC npc) {
        this.setter.accept(npc, this.value);
    }

    @Override
    public void parse(JsonObject json) {
        this.value = GsonHelper.getAsBoolean(json, "value");
    }

    @Override
    public boolean isComplete() {
        return true;
    }
}
