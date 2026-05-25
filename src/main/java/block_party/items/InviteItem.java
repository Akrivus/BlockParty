package block_party.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class InviteItem extends Item implements SortableItem {
    private static final String IS_CLOSED = "IsClosed";

    public InviteItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static boolean isClosed(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.contains(IS_CLOSED) && data.getUnsafe().getBoolean(IS_CLOSED);
    }

    public static void setClosed(ItemStack stack, boolean closed) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean(IS_CLOSED, closed);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            setClosed(stack, !isClosed(stack));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public int getSortOrder() {
        return 10;
    }
}
