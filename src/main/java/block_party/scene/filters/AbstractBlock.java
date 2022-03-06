package block_party.scene.filters;

import block_party.entities.BlockPartyNPC;
import block_party.registry.CustomTags;
import block_party.scene.ISceneFilter;
import block_party.utils.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class AbstractBlock implements ISceneFilter {
    protected Function<BlockPartyNPC, BlockState> getter;
    private boolean not = false;
    private Block block;
    private TagKey<Block> tag;

    public AbstractBlock(Function<BlockPartyNPC, BlockState> function) {
        this.getter = function;
    }

    public boolean verify(BlockPartyNPC npc) {
        BlockState state = this.getter.apply(npc);
        boolean pass = this.tag == null ? state.is(this.block) : state.is(this.tag);
        return this.not ? !pass : pass;
    }

    public void parse(JsonObject json) {
        this.not = GsonHelper.getAsBoolean(json, "not", false);
        String name = GsonHelper.getAsString(json, "name");
        if (name.startsWith("#")) {
            ResourceLocation loc = new ResourceLocation(name.substring(1));
            this.tag = CustomTags.bind(CustomTags.Type.BLOCK, loc);
        } else {
            this.block = JsonUtils.getAs(JsonUtils.BLOCK, name);
        }
    }
}
