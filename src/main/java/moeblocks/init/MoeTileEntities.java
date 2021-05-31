package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.block.entity.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeTileEntities {
    public static final DeferredRegister<TileEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MoeMod.ID);

    public static final RegistryObject<TileEntityType<GardenLanternTileEntity>> GARDEN_LANTERN = REGISTRY.register("garden_lantern", () -> TileEntityType.Builder.create(GardenLanternTileEntity::new, MoeBlocks.GARDEN_LANTERN.get()).build(null));
    public static final RegistryObject<TileEntityType<HangingScrollTileEntity>> HANGING_SCROLL = REGISTRY.register("hanging_scroll", () -> TileEntityType.Builder.create(HangingScrollTileEntity::new, MoeBlocks.BLANK_HANGING_SCROLL.get(), MoeBlocks.MORNING_HANGING_SCROLL.get(), MoeBlocks.NOON_HANGING_SCROLL.get(), MoeBlocks.EVENING_HANGING_SCROLL.get(), MoeBlocks.NIGHT_HANGING_SCROLL.get(), MoeBlocks.MIDNIGHT_HANGING_SCROLL.get(), MoeBlocks.DAWN_HANGING_SCROLL.get()).build(null));
    public static final RegistryObject<TileEntityType<LuckyCatTileEntity>> LUCKY_CAT = REGISTRY.register("lucky_cat", () -> TileEntityType.Builder.create(LuckyCatTileEntity::new, MoeBlocks.LUCKY_CAT.get()).build(null));
    public static final RegistryObject<TileEntityType<PaperLanternTileEntity>> PAPER_LANTERN = REGISTRY.register("paper_lantern", () -> TileEntityType.Builder.create(PaperLanternTileEntity::new, MoeBlocks.BLACK_PAPER_LANTERN.get(), MoeBlocks.RED_PAPER_LANTERN.get(), MoeBlocks.GREEN_PAPER_LANTERN.get(), MoeBlocks.BROWN_PAPER_LANTERN.get(), MoeBlocks.BLUE_PAPER_LANTERN.get(), MoeBlocks.PURPLE_PAPER_LANTERN.get(), MoeBlocks.CYAN_PAPER_LANTERN.get(), MoeBlocks.LIGHT_GRAY_PAPER_LANTERN.get(), MoeBlocks.GRAY_PAPER_LANTERN.get(), MoeBlocks.PINK_PAPER_LANTERN.get(), MoeBlocks.LIME_PAPER_LANTERN.get(), MoeBlocks.YELLOW_PAPER_LANTERN.get(), MoeBlocks.LIGHT_BLUE_PAPER_LANTERN.get(), MoeBlocks.MAGENTA_PAPER_LANTERN.get(), MoeBlocks.ORANGE_PAPER_LANTERN.get(), MoeBlocks.WHITE_PAPER_LANTERN.get()).build(null));
    public static final RegistryObject<TileEntityType<SakuraSaplingTileEntity>> SAKURA_SAPLING = REGISTRY.register("sakura_sapling", () -> TileEntityType.Builder.create(SakuraSaplingTileEntity::new, MoeBlocks.SAKURA_SAPLING.get(), MoeBlocks.WHITE_SAKURA_SAPLING.get()).build(null));
    public static final RegistryObject<TileEntityType<ShimenawaTileEntity>> SHIMENAWA = REGISTRY.register("shimenawa", () -> TileEntityType.Builder.create(ShimenawaTileEntity::new, MoeBlocks.SHIMENAWA.get()).build(null));
    public static final RegistryObject<TileEntityType<ToriiTabletTileEntity>> TORII_TABLET = REGISTRY.register("torii_tablet", () -> TileEntityType.Builder.create(ToriiTabletTileEntity::new, MoeBlocks.TORII_TABLET.get()).build(null));
    public static final RegistryObject<TileEntityType<WindChimesTileEntity>> WIND_CHIME = REGISTRY.register("wind_chimes", () -> TileEntityType.Builder.create(WindChimesTileEntity::new, MoeBlocks.WIND_CHIMES.get()).build(null));
    public static final RegistryObject<TileEntityType<WritingTableTileEntity>> WRITING_TABLE = REGISTRY.register("writing_table", () -> TileEntityType.Builder.create(WritingTableTileEntity::new, MoeBlocks.WRITING_TABLE.get()).build(null));
}
