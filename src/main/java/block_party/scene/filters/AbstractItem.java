package block_party.scene.filters;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneFilter;
import block_party.utils.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class AbstractItem implements ISceneFilter {
    protected Function<BlockPartyNPC, ItemStack> getter;
    private final AbstractInteger counter = new AbstractInteger((npc) -> this.getter.apply(npc).getCount());
    private Item item;
    private Tag<Item> tag;

    public AbstractItem(Function<BlockPartyNPC, ItemStack> function) {
        this.getter = function;
    }

    public AbstractItem() { }

    public boolean verify(BlockPartyNPC npc) {
        ItemStack stack = this.getter.apply(npc);
        if (this.tag != null) { return stack.is(this.tag); }
        return stack.is(this.item);
    }

    public void parse(JsonObject json) {
        this.counter.parse(json);
        String location = GsonHelper.getAsString(json, "name");
        if (location.startsWith("#")) {
            this.tag = ItemTags.createOptional(new ResourceLocation(location.substring(1)));
        } else {
            this.item = JsonUtils.getAs(JsonUtils.ITEM, location);
        }
    }
}
