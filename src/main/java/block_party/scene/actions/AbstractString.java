package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.function.BiConsumer;

public class AbstractString extends Abstract1Shot {
    protected BiConsumer<BlockPartyNPC, String> setter;
    private String value;

    public AbstractString(BiConsumer<BlockPartyNPC, String> setter) {
        this.setter = setter;
    }

    public AbstractString() { }

    @Override
    public void apply(BlockPartyNPC npc) {
        this.setter.accept(npc, this.value);
    }

    @Override
    public void parse(JsonObject json) {
        this.value = GsonHelper.getAsString(json, "value");
    }
}
