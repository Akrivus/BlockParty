package block_party.scene.filters;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneFilter;
import block_party.utils.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class BlockFilter implements ISceneFilter {
    protected Function<BlockPartyNPC, BlockState> getter;
    private Block block;
    private Tag<Block> tag;

    public BlockFilter(Function<BlockPartyNPC, BlockState> function) {
        this.getter = function;
    }

    public boolean verify(BlockPartyNPC npc) {
        BlockState state = this.getter.apply(npc);
        if (this.tag != null) { return state.is(this.tag); }
        return state.is(this.block);
    }

    public void parse(JsonObject json) {
        String location = GsonHelper.getAsString(json, "name");
        if (location.startsWith("#")) {
            this.tag = BlockTags.createOptional(new ResourceLocation(location.substring(1)));
        } else {
            this.block = JsonUtils.getAs(JsonUtils.BLOCK, location);
        }
    }
}
