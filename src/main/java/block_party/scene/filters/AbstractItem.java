package block_party.scene.filters;

import block_party.entities.BlockPartyNPC;
import block_party.registry.CustomTags;
import block_party.scene.ISceneFilter;
import block_party.utils.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class AbstractItem implements ISceneFilter {
    protected Function<BlockPartyNPC, ItemStack> getter;
    private final AbstractInteger counter = new AbstractInteger((npc) -> this.getter.apply(npc).getCount());
    private boolean not = false;
    private Item item;
    private TagKey<Item> tag;

    public AbstractItem(Function<BlockPartyNPC, ItemStack> function) {
        this.getter = function;
    }

    public AbstractItem() { }

    public boolean verify(BlockPartyNPC npc) {
        ItemStack stack = this.getter.apply(npc);
        boolean pass = this.tag == null ? stack.is(this.item) : stack.is(this.tag);
        return this.not ? !pass : pass;
    }

    public void parse(JsonObject json) {
        this.counter.parse(json);
        this.not = GsonHelper.getAsBoolean(json, "not", false);
        String name = GsonHelper.getAsString(json, "name");
        if (name.startsWith("#")) {
            ResourceLocation loc = new ResourceLocation(name.substring(1));
            this.tag = CustomTags.bind(CustomTags.Type.ITEM, loc);
        } else {
            this.item = JsonUtils.getAs(JsonUtils.ITEM, name);
        }
    }
}
