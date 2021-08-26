package block_party.init;

import block_party.BlockParty;
import block_party.blocks.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockPartyBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, BlockParty.ID);

    public static final RegistryObject<BlockEntityType<GardenLanternBlockEntity>> GARDEN_LANTERN = REGISTRY.register("garden_lantern", () -> BlockEntityType.Builder.of(GardenLanternBlockEntity::new, BlockPartyBlocks.GARDEN_LANTERN.get()).build(null));
    public static final RegistryObject<BlockEntityType<HangingScrollBlockEntity>> HANGING_SCROLL = REGISTRY.register("hanging_scroll", () -> BlockEntityType.Builder.of(HangingScrollBlockEntity::new, BlockPartyBlocks.BLANK_HANGING_SCROLL.get(), BlockPartyBlocks.MORNING_HANGING_SCROLL.get(), BlockPartyBlocks.NOON_HANGING_SCROLL.get(), BlockPartyBlocks.EVENING_HANGING_SCROLL.get(), BlockPartyBlocks.NIGHT_HANGING_SCROLL.get(), BlockPartyBlocks.MIDNIGHT_HANGING_SCROLL.get(), BlockPartyBlocks.DAWN_HANGING_SCROLL.get()).build(null));
    public static final RegistryObject<BlockEntityType<LuckyCatBlockEntity>> LUCKY_CAT = REGISTRY.register("lucky_cat", () -> BlockEntityType.Builder.of(LuckyCatBlockEntity::new, BlockPartyBlocks.LUCKY_CAT.get()).build(null));
    public static final RegistryObject<BlockEntityType<PaperLanternBlockEntity>> PAPER_LANTERN = REGISTRY.register("paper_lantern", () -> BlockEntityType.Builder.of(PaperLanternBlockEntity::new, BlockPartyBlocks.BLACK_PAPER_LANTERN.get(), BlockPartyBlocks.RED_PAPER_LANTERN.get(), BlockPartyBlocks.GREEN_PAPER_LANTERN.get(), BlockPartyBlocks.BROWN_PAPER_LANTERN.get(), BlockPartyBlocks.BLUE_PAPER_LANTERN.get(), BlockPartyBlocks.PURPLE_PAPER_LANTERN.get(), BlockPartyBlocks.CYAN_PAPER_LANTERN.get(), BlockPartyBlocks.LIGHT_GRAY_PAPER_LANTERN.get(), BlockPartyBlocks.GRAY_PAPER_LANTERN.get(), BlockPartyBlocks.PINK_PAPER_LANTERN.get(), BlockPartyBlocks.LIME_PAPER_LANTERN.get(), BlockPartyBlocks.YELLOW_PAPER_LANTERN.get(), BlockPartyBlocks.LIGHT_BLUE_PAPER_LANTERN.get(), BlockPartyBlocks.MAGENTA_PAPER_LANTERN.get(), BlockPartyBlocks.ORANGE_PAPER_LANTERN.get(), BlockPartyBlocks.WHITE_PAPER_LANTERN.get()).build(null));
    public static final RegistryObject<BlockEntityType<SakuraSaplingBlockEntity>> SAKURA_SAPLING = REGISTRY.register("sakura_sapling", () -> BlockEntityType.Builder.of(SakuraSaplingBlockEntity::new, BlockPartyBlocks.SAKURA_SAPLING.get(), BlockPartyBlocks.WHITE_SAKURA_SAPLING.get()).build(null));
    public static final RegistryObject<BlockEntityType<ShimenawaBlockEntity>> SHIMENAWA = REGISTRY.register("shimenawa", () -> BlockEntityType.Builder.of(ShimenawaBlockEntity::new, BlockPartyBlocks.SHIMENAWA.get()).build(null));
    public static final RegistryObject<BlockEntityType<ToriiTabletBlockEntity>> TORII_TABLET = REGISTRY.register("torii_tablet", () -> BlockEntityType.Builder.of(ToriiTabletBlockEntity::new, BlockPartyBlocks.TORII_TABLET.get()).build(null));
    public static final RegistryObject<BlockEntityType<WindChimesBlockEntity>> WIND_CHIME = REGISTRY.register("wind_chimes", () -> BlockEntityType.Builder.of(WindChimesBlockEntity::new, BlockPartyBlocks.WIND_CHIMES.get()).build(null));
    public static final RegistryObject<BlockEntityType<WritingTableBlockEntity>> WRITING_TABLE = REGISTRY.register("writing_table", () -> BlockEntityType.Builder.of(WritingTableBlockEntity::new, BlockPartyBlocks.WRITING_TABLE.get()).build(null));
}
