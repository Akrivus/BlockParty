package block_party.scene.filters;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneFilter;
import block_party.utils.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class BlockFilter implements ISceneFilter {
    protected Function<BlockPartyNPC, BlockState> function;
    private Block block;

    public BlockFilter(Function<BlockPartyNPC, BlockState> function) {
        this.function = function;
    }

    public boolean verify(BlockPartyNPC npc) {
        return this.function.apply(npc).is(this.block);
    }

    public void parse(JsonObject json) {
        this.block = JsonUtils.getAs(JsonUtils.BLOCK, GsonHelper.getAsString(json, "block"));
    }

    public static class WithTag implements ISceneFilter {
        private String name;

        public boolean verify(BlockPartyNPC npc) {
            return npc.getVisibleBlockState().is(BlockTags.createOptional(new ResourceLocation(this.name)));
        }

        public void parse(JsonObject json) {
            this.name = GsonHelper.getAsString(json, "name");
        }
    }
}
