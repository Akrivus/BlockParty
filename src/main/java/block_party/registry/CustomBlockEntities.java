package block_party.registry;

import block_party.BlockParty;
import block_party.blocks.entity.GardenLanternBlockEntity;
import block_party.blocks.entity.HangingScrollBlockEntity;
import block_party.blocks.entity.PaperLanternBlockEntity;
import block_party.blocks.entity.SakuraSaplingBlockEntity;
import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.blocks.entity.ShrineTabletBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BlockParty.ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GardenLanternBlockEntity>> GARDEN_LANTERN =
            BLOCK_ENTITIES.register("garden_lantern", () -> new BlockEntityType<>(
                    GardenLanternBlockEntity::new,
                    CustomBlocks.GARDEN_LANTERN.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HangingScrollBlockEntity>> HANGING_SCROLL =
            BLOCK_ENTITIES.register("hanging_scroll", () -> new BlockEntityType<>(
                    HangingScrollBlockEntity::new,
                    CustomBlocks.BLANK_HANGING_SCROLL.get(),
                    CustomBlocks.MORNING_HANGING_SCROLL.get(),
                    CustomBlocks.NOON_HANGING_SCROLL.get(),
                    CustomBlocks.EVENING_HANGING_SCROLL.get(),
                    CustomBlocks.NIGHT_HANGING_SCROLL.get(),
                    CustomBlocks.MIDNIGHT_HANGING_SCROLL.get(),
                    CustomBlocks.DAWN_HANGING_SCROLL.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PaperLanternBlockEntity>> PAPER_LANTERN =
            BLOCK_ENTITIES.register("paper_lantern", () -> new BlockEntityType<>(
                    PaperLanternBlockEntity::new,
                    CustomBlocks.PAPER_LANTERN.get(),
                    CustomBlocks.RED_PAPER_LANTERN.get(),
                    CustomBlocks.GREEN_PAPER_LANTERN.get(),
                    CustomBlocks.BROWN_PAPER_LANTERN.get(),
                    CustomBlocks.BLUE_PAPER_LANTERN.get(),
                    CustomBlocks.PURPLE_PAPER_LANTERN.get(),
                    CustomBlocks.CYAN_PAPER_LANTERN.get(),
                    CustomBlocks.LIGHT_GRAY_PAPER_LANTERN.get(),
                    CustomBlocks.GRAY_PAPER_LANTERN.get(),
                    CustomBlocks.PINK_PAPER_LANTERN.get(),
                    CustomBlocks.LIME_PAPER_LANTERN.get(),
                    CustomBlocks.YELLOW_PAPER_LANTERN.get(),
                    CustomBlocks.LIGHT_BLUE_PAPER_LANTERN.get(),
                    CustomBlocks.MAGENTA_PAPER_LANTERN.get(),
                    CustomBlocks.ORANGE_PAPER_LANTERN.get(),
                    CustomBlocks.WHITE_PAPER_LANTERN.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SakuraSaplingBlockEntity>> SAKURA_SAPLING =
            BLOCK_ENTITIES.register("sakura_sapling", () -> new BlockEntityType<>(
                    SakuraSaplingBlockEntity::new,
                    CustomBlocks.SAKURA_SAPLING.get(),
                    CustomBlocks.WHITE_SAKURA_SAPLING.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShimenawaBlockEntity>> SHIMENAWA =
            BLOCK_ENTITIES.register("shimenawa", () -> new BlockEntityType<>(
                    ShimenawaBlockEntity::new,
                    CustomBlocks.SHIMENAWA.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShrineTabletBlockEntity>> SHRINE_TABLET =
            BLOCK_ENTITIES.register("shrine_tablet", () -> new BlockEntityType<>(
                    ShrineTabletBlockEntity::new,
                    CustomBlocks.SHRINE_TABLET.get()));
    private CustomBlockEntities() {
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITIES.register(modBus);
    }
}
