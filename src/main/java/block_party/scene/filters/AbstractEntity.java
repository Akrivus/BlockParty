package block_party.scene.filters;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneFilter;
import block_party.utils.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class AbstractEntity implements ISceneFilter {
    protected Function<BlockPartyNPC, Entity> getter;
    private boolean not = false;
    private EntityType type;
    private Tag<Block> tag;

    public AbstractEntity(Function<BlockPartyNPC, Entity> function) {
        this.getter = function;
    }

    public AbstractEntity() { }

    public boolean verify(BlockPartyNPC npc) {
        EntityType entity = this.getter.apply(npc).getType();
        boolean pass = this.tag == null ? entity.equals(this.type) : entity.is(this.tag);
        return this.not ? !pass : pass;
    }

    public void parse(JsonObject json) {
        this.not = GsonHelper.getAsBoolean(json, "not", false);
        String name = GsonHelper.getAsString(json, "name");
        if (name.startsWith("#")) {
            ResourceLocation loc = new ResourceLocation(name.substring(1));
            this.tag = BlockTags.createOptional(loc);
        } else {
            this.type = JsonUtils.getAs(JsonUtils.ENTITY_TYPE, name);
        }
    }
}
