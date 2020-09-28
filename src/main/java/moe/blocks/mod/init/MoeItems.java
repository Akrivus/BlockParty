package moe.blocks.mod.init;

import moe.blocks.mod.MoeMod;
import moe.blocks.mod.item.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MoeMod.ID);
    public static final RegistryObject<Item> BENTO_BOX = REGISTRY.register("bento_box", BentoBoxItem::new);
    public static final RegistryObject<Item> CELL_PHONE = REGISTRY.register("cell_phone", CellPhoneItem::new);
    public static final RegistryObject<Item> CLOSED_LETTER = REGISTRY.register("closed_letter", LetterItem::new);
    public static final RegistryObject<Item> CUPCAKE = REGISTRY.register("cupcake", CupcakeItem::new);
    public static final RegistryObject<Item> KNAIFU = REGISTRY.register("knaifu", KnaifuItem::new);
    public static final RegistryObject<Item> MOE_DIE = REGISTRY.register("moe_die", MoeDieItem::new);
    public static final RegistryObject<Item> MOE_SPAWN_EGG = REGISTRY.register("moe_spawn_egg", MoeSpawnItem::new);
    public static final RegistryObject<Item> MUSIC_DISC_POMP_POMF = REGISTRY.register("music_disc_pomf_pomf", () -> new MusicDiscItem(14, () -> MoeSounds.MUSIC_DISC_POMF_POMF.get(), new Item.Properties().maxStackSize(1).group(MoeItems.Group.INSTANCE).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> ONIGIRI = REGISTRY.register("onigiri", OnigiriItem::new);
    public static final RegistryObject<Item> OPENED_LETTER = REGISTRY.register("opened_letter", LetterItem::new);
    public static final RegistryObject<Item> PINK_BOW = REGISTRY.register("pink_bow", PinkBowItem::new);
    public static final RegistryObject<Item> YEARBOOK_PAGE = REGISTRY.register("yearbook_page", YearbookPageItem::new);
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
