package moe.blocks.mod.init;

import moe.blocks.mod.MoeMod;
import moe.blocks.mod.entity.util.Deres;
import moe.blocks.mod.item.SpawnEggItem;
import moe.blocks.mod.item.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MoeMod.ID);
    public static final RegistryObject<Item> BENTO_BOX = REGISTRY.register("bento_box", BentoBoxItem::new);
    public static final RegistryObject<Item> CELL_PHONE = REGISTRY.register("cell_phone", CellPhoneItem::new);
    public static final RegistryObject<Item> CUPCAKE = REGISTRY.register("cupcake", CupcakeItem::new);
    public static final RegistryObject<Item> DANDERE_MOE_SPAWN_EGG = REGISTRY.register("dandere_moe_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(moe.blocks.mod.item.SpawnEggItem.SpawnTypes.MOE, Deres.DANDERE));
    public static final RegistryObject<Item> DANDERE_SENPAI_SPAWN_EGG = REGISTRY.register("dandere_senpai_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(moe.blocks.mod.item.SpawnEggItem.SpawnTypes.SENPAI, Deres.DANDERE));
    public static final RegistryObject<Item> DEREDERE_MOE_SPAWN_EGG = REGISTRY.register("deredere_moe_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(moe.blocks.mod.item.SpawnEggItem.SpawnTypes.MOE, Deres.DEREDERE));
    public static final RegistryObject<Item> DEREDERE_SENPAI_SPAWN_EGG = REGISTRY.register("deredere_senpai_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(moe.blocks.mod.item.SpawnEggItem.SpawnTypes.SENPAI, Deres.DEREDERE));
    public static final RegistryObject<Item> HIMEDERE_MOE_SPAWN_EGG = REGISTRY.register("himedere_moe_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(moe.blocks.mod.item.SpawnEggItem.SpawnTypes.MOE, Deres.HIMEDERE));
    public static final RegistryObject<Item> HIMEDERE_SENPAI_SPAWN_EGG = REGISTRY.register("himedere_senpai_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(moe.blocks.mod.item.SpawnEggItem.SpawnTypes.SENPAI, Deres.HIMEDERE));
    public static final RegistryObject<Item> KNAIFU = REGISTRY.register("knaifu", KnaifuItem::new);
    public static final RegistryObject<Item> KUUDERE_MOE_SPAWN_EGG = REGISTRY.register("kuudere_moe_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(moe.blocks.mod.item.SpawnEggItem.SpawnTypes.MOE, Deres.KUUDERE));
    public static final RegistryObject<Item> KUUDERE_SENPAI_SPAWN_EGG = REGISTRY.register("kuudere_senpai_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(moe.blocks.mod.item.SpawnEggItem.SpawnTypes.SENPAI, Deres.KUUDERE));
    public static final RegistryObject<Item> MOE_DIE = REGISTRY.register("moe_die", MoeDieItem::new);
    public static final RegistryObject<Item> MUSIC_DISC_POMP_POMF = REGISTRY.register("music_disc_pomf_pomf", () -> new MusicDiscItem(14, () -> MoeSounds.MUSIC_DISC_POMF_POMF.get(), new Item.Properties().maxStackSize(1).group(MoeItems.Group.INSTANCE).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> ONIGIRI = REGISTRY.register("onigiri", OnigiriItem::new);
    public static final RegistryObject<Item> PINK_BOW = REGISTRY.register("pink_bow", PinkBowItem::new);
    public static final RegistryObject<Item> TSUNDERE_MOE_SPAWN_EGG = REGISTRY.register("tsundere_moe_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(moe.blocks.mod.item.SpawnEggItem.SpawnTypes.MOE, Deres.TSUNDERE));
    public static final RegistryObject<Item> TSUNDERE_SENPAI_SPAWN_EGG = REGISTRY.register("tsundere_senpai_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(moe.blocks.mod.item.SpawnEggItem.SpawnTypes.SENPAI, Deres.TSUNDERE));
    public static final RegistryObject<Item> YANDERE_MOE_SPAWN_EGG = REGISTRY.register("yandere_moe_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(moe.blocks.mod.item.SpawnEggItem.SpawnTypes.MOE, Deres.YANDERE));
    public static final RegistryObject<Item> YANDERE_SENPAI_SPAWN_EGG = REGISTRY.register("yandere_senpai_spawn_egg", () -> new moe.blocks.mod.item.SpawnEggItem(SpawnEggItem.SpawnTypes.SENPAI, Deres.YANDERE));
    public static final RegistryObject<Item> YEARBOOK = REGISTRY.register("yearbook", YearbookItem::new);


    public static class Group extends ItemGroup {
        public static final Group INSTANCE = new Group();

        public Group() {
            super(MoeMod.ID);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(MoeItems.MOE_DIE.get());
        }
    }
}