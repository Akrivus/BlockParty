package block_party.scene.filters;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneFilter;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.function.Function;

public class StringFilter implements ISceneFilter {
    protected Function<BlockPartyNPC, String> function;
    private String name;

    public StringFilter(Function<BlockPartyNPC, String> function) {
        this.function = function;
    }

    public StringFilter() { }

    public boolean verify(BlockPartyNPC npc) {
        return this.name.equals(this.function.apply(npc));
    }

    public void parse(JsonObject json) {
        this.name = GsonHelper.getAsString(json, "name");
    }
}
