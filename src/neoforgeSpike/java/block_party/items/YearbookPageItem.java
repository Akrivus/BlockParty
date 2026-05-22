package block_party.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

public class YearbookPageItem extends Item {
    public YearbookPageItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return;
        }
        data.update(tag -> {
            if (tag.contains("NPC")) {
                var npc = tag.getCompound("NPC");
                String name = String.format("%s %s", npc.getString("FamilyName"), npc.getString("GivenName")).trim();
                if (!name.isBlank()) {
                    tooltip.add(Component.literal(name).withStyle(ChatFormatting.GRAY));
                }
            }
        });
    }
}
