package block_party.registry;

import block_party.BlockParty;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BlockParty.ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCK_PARTY =
            CREATIVE_TABS.register("block_party", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.block_party"))
                    .icon(() -> new ItemStack(CustomItems.ENTRIES.get("cupcake").get()))
                    .displayItems((parameters, output) -> populate(output))
                    .build());

    private CustomCreativeTabs() {
    }

    public static void populate(CreativeModeTab.Output output) {
        for (DeferredItem<? extends Item> item : CustomItems.ENTRIES.values()) {
            output.accept(new ItemStack(item.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    public static List<ItemStack> reviewStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        populate((stack, visibility) -> stacks.add(stack.copy()));
        return stacks;
    }

    public static void register(IEventBus modBus) {
        CREATIVE_TABS.register(modBus);
    }
}
