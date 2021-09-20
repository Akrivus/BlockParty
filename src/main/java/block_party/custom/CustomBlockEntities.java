package block_party.custom;

import block_party.BlockParty;
import block_party.blocks.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class CustomBlockEntities {
    public static final RegistryObject<BlockEntityType<GardenLanternBlockEntity>> GARDEN_LANTERN = BlockParty.BLOCK_ENTITIES.register("garden_lantern", () -> BlockEntityType.Builder.of(GardenLanternBlockEntity::new, CustomBlocks.GARDEN_LANTERN.get()).build(null));
    public static final RegistryObject<BlockEntityType<HangingScrollBlockEntity>> HANGING_SCROLL = BlockParty.BLOCK_ENTITIES.register("hanging_scroll", () -> BlockEntityType.Builder.of(HangingScrollBlockEntity::new, CustomBlocks.BLANK_HANGING_SCROLL.get(), CustomBlocks.MORNING_HANGING_SCROLL.get(), CustomBlocks.NOON_HANGING_SCROLL.get(), CustomBlocks.EVENING_HANGING_SCROLL.get(), CustomBlocks.NIGHT_HANGING_SCROLL.get(), CustomBlocks.MIDNIGHT_HANGING_SCROLL.get(), CustomBlocks.DAWN_HANGING_SCROLL.get()).build(null));
    public static final RegistryObject<BlockEntityType<PaperLanternBlockEntity>> PAPER_LANTERN = BlockParty.BLOCK_ENTITIES.register("paper_lantern", () -> BlockEntityType.Builder.of(PaperLanternBlockEntity::new, CustomBlocks.BLACK_PAPER_LANTERN.get(), CustomBlocks.RED_PAPER_LANTERN.get(), CustomBlocks.GREEN_PAPER_LANTERN.get(), CustomBlocks.BROWN_PAPER_LANTERN.get(), CustomBlocks.BLUE_PAPER_LANTERN.get(), CustomBlocks.PURPLE_PAPER_LANTERN.get(), CustomBlocks.CYAN_PAPER_LANTERN.get(), CustomBlocks.LIGHT_GRAY_PAPER_LANTERN.get(), CustomBlocks.GRAY_PAPER_LANTERN.get(), CustomBlocks.PINK_PAPER_LANTERN.get(), CustomBlocks.LIME_PAPER_LANTERN.get(), CustomBlocks.YELLOW_PAPER_LANTERN.get(), CustomBlocks.LIGHT_BLUE_PAPER_LANTERN.get(), CustomBlocks.MAGENTA_PAPER_LANTERN.get(), CustomBlocks.ORANGE_PAPER_LANTERN.get(), CustomBlocks.WHITE_PAPER_LANTERN.get()).build(null));
    public static final RegistryObject<BlockEntityType<SakuraSaplingBlockEntity>> SAKURA_SAPLING = BlockParty.BLOCK_ENTITIES.register("sakura_sapling", () -> BlockEntityType.Builder.of(SakuraSaplingBlockEntity::new, CustomBlocks.SAKURA_SAPLING.get(), CustomBlocks.WHITE_SAKURA_SAPLING.get()).build(null));
    public static final RegistryObject<BlockEntityType<ShimenawaBlockEntity>> SHIMENAWA = BlockParty.BLOCK_ENTITIES.register("shimenawa", () -> BlockEntityType.Builder.of(ShimenawaBlockEntity::new, CustomBlocks.SHIMENAWA.get()).build(null));
    public static final RegistryObject<BlockEntityType<ShrineTabletBlockEntity>> SHRINE_TABLET = BlockParty.BLOCK_ENTITIES.register("shrine_tablet", () -> BlockEntityType.Builder.of(ShrineTabletBlockEntity::new, CustomBlocks.SHRINE_TABLET.get()).build(null));
    public static final RegistryObject<BlockEntityType<WindChimesBlockEntity>> WIND_CHIME = BlockParty.BLOCK_ENTITIES.register("wind_chimes", () -> BlockEntityType.Builder.of(WindChimesBlockEntity::new, CustomBlocks.WIND_CHIMES.get()).build(null));

    public static void add(DeferredRegister<BlockEntityType<?>> registry, IEventBus bus) {
        registry.register(bus);
    }
}
