package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.function.BiConsumer;

public class AbstractBoolean extends Abstract1Shot {
    protected BiConsumer<BlockPartyNPC, Boolean> setter;
    private boolean value;

    public AbstractBoolean(BiConsumer<BlockPartyNPC, Boolean> setter) {
        this.setter = setter;
    }

    public AbstractBoolean() { }

    @Override
    public void apply(BlockPartyNPC npc) {
        this.setter.accept(npc, this.value);
    }

    @Override
    public void parse(JsonObject json) {
        this.value = GsonHelper.getAsBoolean(json, "value");
    }
}
