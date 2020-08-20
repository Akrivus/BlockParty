package mod.moeblocks.register;

import mod.moeblocks.MoeMod;
import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.item.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemsMoe {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MoeMod.ID);
    public static final RegistryObject<Item> BENTO_BOX = REGISTRY.register("bento_box", BentoBoxItem::new);
    public static final RegistryObject<Item> CELL_PHONE = REGISTRY.register("cell_phone", CellPhoneItem::new);
    public static final RegistryObject<Item> DANDERE_MOE_SPAWN_EGG = REGISTRY.register("dandere_moe_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.MOE, Deres.DANDERE));
    public static final RegistryObject<Item> DANDERE_SENPAI_SPAWN_EGG = REGISTRY.register("dandere_senpai_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.SENPAI, Deres.DANDERE));
    public static final RegistryObject<Item> DEREDERE_MOE_SPAWN_EGG = REGISTRY.register("deredere_moe_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.MOE, Deres.DEREDERE));
    public static final RegistryObject<Item> DEREDERE_SENPAI_SPAWN_EGG = REGISTRY.register("deredere_senpai_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.SENPAI, Deres.DEREDERE));
    public static final RegistryObject<Item> HIMEDERE_MOE_SPAWN_EGG = REGISTRY.register("himedere_moe_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.MOE, Deres.HIMEDERE));
    public static final RegistryObject<Item> HIMEDERE_SENPAI_SPAWN_EGG = REGISTRY.register("himedere_senpai_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.SENPAI, Deres.HIMEDERE));
    public static final RegistryObject<Item> KNAIFU = REGISTRY.register("knaifu", KnaifuItem::new);
    public static final RegistryObject<Item> KUUDERE_MOE_SPAWN_EGG = REGISTRY.register("kuudere_moe_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.MOE, Deres.KUUDERE));
    public static final RegistryObject<Item> KUUDERE_SENPAI_SPAWN_EGG = REGISTRY.register("kuudere_senpai_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.SENPAI, Deres.KUUDERE));
    public static final RegistryObject<Item> MOE_DIE = REGISTRY.register("moe_die", MoeDieItem::new);
    public static final RegistryObject<Item> ONIGIRI = REGISTRY.register("onigiri", OnigiriItem::new);
    public static final RegistryObject<Item> PINK_BOW = REGISTRY.register("pink_bow", PinkBowItem::new);
    public static final RegistryObject<Item> TSUNDERE_MOE_SPAWN_EGG = REGISTRY.register("tsundere_moe_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.MOE, Deres.TSUNDERE));
    public static final RegistryObject<Item> TSUNDERE_SENPAI_SPAWN_EGG = REGISTRY.register("tsundere_senpai_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.SENPAI, Deres.TSUNDERE));
    public static final RegistryObject<Item> YANDERE_MOE_SPAWN_EGG = REGISTRY.register("yandere_moe_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.MOE, Deres.YANDERE));
    public static final RegistryObject<Item> YANDERE_SENPAI_SPAWN_EGG = REGISTRY.register("yandere_senpai_spawn_egg", () -> new SpawnEggItem(SpawnEggItem.SpawnTypes.SENPAI, Deres.YANDERE));
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
}
