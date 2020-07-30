package mod.moeblocks.register;

import mod.moeblocks.MoeMod;
import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.item.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemsMoe {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MoeMod.ID);
    public static final RegistryObject<Item> BORING_CELL_PHONE = REGISTRY.register("boring_cell_phone", CellPhoneItem::new);
    public static final RegistryObject<Item> CUTE_CELL_PHONE = REGISTRY.register("cute_cell_phone", CellPhoneItem::new);
    public static final RegistryObject<Item> DANDERE_SPAWN_EGG = REGISTRY.register("dandere_spawn_egg", () -> new SpawnEggItem(Deres.DANDERE));
    public static final RegistryObject<Item> DEREDERE_SPAWN_EGG = REGISTRY.register("deredere_spawn_egg", () -> new SpawnEggItem(Deres.DEREDERE));
    public static final RegistryObject<Item> HIMEDERE_SPAWN_EGG = REGISTRY.register("himedere_spawn_egg", () -> new SpawnEggItem(Deres.HIMEDERE));
    public static final RegistryObject<Item> KNAIFU = REGISTRY.register("knaifu", KnaifuItem::new);
    public static final RegistryObject<Item> KUUDERE_SPAWN_EGG = REGISTRY.register("kuudere_spawn_egg", () -> new SpawnEggItem(Deres.KUUDERE));
    public static final RegistryObject<Item> MOE_DIE = REGISTRY.register("moe_die", MoeDieItem::new);
    public static final RegistryObject<Item> ONIGIRI = REGISTRY.register("onigiri", OnigiriItem::new);
    public static final RegistryObject<Item> PINK_BOW = REGISTRY.register("pink_bow", PinkBowItem::new);
    public static final RegistryObject<Item> TSUNDERE_SPAWN_EGG = REGISTRY.register("tsundere_spawn_egg", () -> new SpawnEggItem(Deres.TSUNDERE));
    public static final RegistryObject<Item> YANDERE_SPAWN_EGG = REGISTRY.register("yandere_spawn_egg", () -> new SpawnEggItem(Deres.YANDERE));
    public static final RegistryObject<Item> YEARBOOK = REGISTRY.register("yearbook", YearbookItem::new);


    public static class Group extends ItemGroup {
        public static final Group INSTANCE = new Group();

        public Group() {
            super(MoeMod.ID);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemsMoe.MOE_DIE.get());
        }
    }

    public static class Tags {
        public static final Tag<Item> ACCEPTABLE = new ItemTags.Wrapper(new ResourceLocation(MoeMod.ID, "acceptable"));
    }
}
