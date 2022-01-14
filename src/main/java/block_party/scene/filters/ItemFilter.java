package block_party.scene.filters;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneFilter;
import block_party.utils.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class ItemFilter implements ISceneFilter {
    protected Function<BlockPartyNPC, ItemStack> function;
    private Item item;

    public ItemFilter(Function<BlockPartyNPC, ItemStack> function) {
        this.function = function;
    }

    public boolean verify(BlockPartyNPC npc) {
        return this.function.apply(npc).is(this.item);
    }

    public void parse(JsonObject json) {
        this.item = JsonUtils.getAs(JsonUtils.ITEM, GsonHelper.getAsString(json, "item"));
    }

    public static class WithTag implements ISceneFilter {
        protected Function<BlockPartyNPC, ItemStack> function;
        private String name;

        public boolean verify(BlockPartyNPC npc) {
            return this.function.apply(npc).is(ItemTags.createOptional(new ResourceLocation(this.name)));
        }

        public void parse(JsonObject json) {
            this.name = GsonHelper.getAsString(json, "name");
        }
    }
}
