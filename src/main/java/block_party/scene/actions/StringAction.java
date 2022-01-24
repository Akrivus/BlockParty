package block_party.scene.actions;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneAction;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StringAction implements ISceneAction {
    protected BiConsumer<BlockPartyNPC, String> setter;
    private String value;

    public StringAction(BiConsumer<BlockPartyNPC, String> setter) {
        this.setter = setter;
    }

    public StringAction() { }

    @Override
    public void apply(BlockPartyNPC npc) {
        this.setter.accept(npc, this.value);
    }

    @Override
    public void parse(JsonObject json) {
        this.value = GsonHelper.getAsString(json, "value");
    }

    @Override
    public boolean isComplete() {
        return true;
    }
}
