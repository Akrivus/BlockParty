package block_party.entities.chores;

import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.entities.environment.MoeEnvironmentalRules;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.Optional;

public final class PlaceBlockChores {
    private PlaceBlockChores() {
    }

    public static Optional<ItemEntity> nearestDrop(Moe moe, Chore chore, Config config, double radius) {
        if (!(moe.level() instanceof ServerLevel level) || !chore.active() || config == null) {
            return Optional.empty();
        }
        BlockPos origin = chore.origin().getPos();
        AABB bounds = new AABB(origin).inflate(radius, 4.0D, radius);
        return level.getEntitiesOfClass(ItemEntity.class, bounds, item -> item.isAlive() && item.getItem().is(config.item())).stream()
                .filter(drop -> canReachDrop(moe, drop))
                .min(Comparator.comparingDouble(item -> item.distanceToSqr(moe)));
    }

    public static Optional<BlockPos> nearestSpot(ServerLevel level, BlockPos origin, Config config) {
        if (level == null || origin == null || config == null) {
            return Optional.empty();
        }
        double radius = config.placeRadius();
        int blockRadius = (int) Math.ceil(radius);
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos candidate : BlockPos.betweenClosed(origin.offset(-blockRadius, -1, -blockRadius), origin.offset(blockRadius, 2, blockRadius))) {
            BlockPos immutable = candidate.immutable();
            double distance = immutable.distSqr(origin);
            if (distance > radius * radius || distance >= bestDistance || !config.canPlace(level, immutable)) {
                continue;
            }
            best = immutable;
            bestDistance = distance;
        }
        return Optional.ofNullable(best);
    }

    public static void syncHand(Moe moe, Config config) {
        if (config != null && count(moe.getInventory(), config.item()) > 0) {
            moe.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(config.item()));
        } else if (config == null || moe.getItemBySlot(EquipmentSlot.MAINHAND).is(config.item())) {
            moe.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    public static int count(Container inventory, Item item) {
        int count = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); ++slot) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static boolean removeOne(Container inventory, Item item) {
        for (int slot = 0; slot < inventory.getContainerSize(); ++slot) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(item)) {
                stack.shrink(1);
                inventory.setItem(slot, stack.isEmpty() ? ItemStack.EMPTY : stack);
                return true;
            }
        }
        return false;
    }

    private static boolean canReachDrop(Moe moe, ItemEntity drop) {
        if (drop == null || !drop.isAlive()) {
            return false;
        }
        if (moe.distanceToSqr(drop) <= 1.75D * 1.75D) {
            return true;
        }
        if (!MoeEnvironmentalRules.canStandAt(moe.level(), drop.blockPosition())
                && !MoeEnvironmentalRules.canStandAt(moe.level(), drop.blockPosition().below())) {
            return false;
        }
        Path path = moe.getNavigation().createPath(drop.blockPosition(), 1);
        return path != null && path.canReach();
    }

    public enum Config {
        OAK_SAPLING("oak_sapling", Items.OAK_SAPLING, Blocks.OAK_SAPLING.defaultBlockState(), 8.0D, 5, 6, 16);

        private final String key;
        private final Item item;
        private final BlockState placeState;
        private final double placeRadius;
        private final int minSpacing;
        private final int verticalClearance;
        private final int maxCarry;

        Config(String key, Item item, BlockState placeState, double placeRadius, int minSpacing, int verticalClearance, int maxCarry) {
            this.key = key;
            this.item = item;
            this.placeState = placeState;
            this.placeRadius = placeRadius;
            this.minSpacing = minSpacing;
            this.verticalClearance = verticalClearance;
            this.maxCarry = maxCarry;
        }

        public String key() {
            return this.key;
        }

        public Item item() {
            return this.item;
        }

        public BlockState placeState() {
            return this.placeState;
        }

        public double placeRadius() {
            return this.placeRadius;
        }

        public int maxCarry() {
            return this.maxCarry;
        }

        public boolean canPlace(ServerLevel level, BlockPos pos) {
            return level.isEmptyBlock(pos)
                    && !level.isEmptyBlock(pos.below())
                    && this.placeState.canSurvive(level, pos)
                    && MoeEnvironmentalRules.canStandAt(level, pos)
                    && this.hasGrowthRoom(level, pos)
                    && this.hasPlantSpacing(level, pos);
        }

        private boolean hasGrowthRoom(ServerLevel level, BlockPos pos) {
            for (int y = 1; y <= this.verticalClearance; ++y) {
                if (!level.isEmptyBlock(pos.above(y))) {
                    return false;
                }
            }
            return true;
        }

        private boolean hasPlantSpacing(ServerLevel level, BlockPos pos) {
            int radius = this.minSpacing;
            for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-radius, -1, -radius), pos.offset(radius, 1, radius))) {
                BlockPos immutable = nearby.immutable();
                if (!immutable.equals(pos)
                        && Math.abs(immutable.getX() - pos.getX()) + Math.abs(immutable.getZ() - pos.getZ()) < radius
                        && level.getBlockState(immutable).is(this.placeState.getBlock())) {
                    return false;
                }
            }
            return true;
        }

        public static Config fromKey(String key) {
            for (Config config : values()) {
                if (config.key.equals(key)) {
                    return config;
                }
            }
            return null;
        }
    }

    public record Chore(String key, DimBlockPos origin, int ticks) {
        public static Chore none() {
            return new Chore("", new DimBlockPos(), 0);
        }

        public boolean active() {
            return this.ticks > 0 && this.key != null && !this.key.isBlank() && this.origin != null && !this.origin.isEmpty();
        }

        public Chore tick() {
            return new Chore(this.key, this.origin, this.ticks - 1);
        }

        public CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Key", this.key == null ? "" : this.key);
            tag.put("Origin", this.origin == null ? new DimBlockPos().write() : this.origin.write());
            tag.putInt("Ticks", this.ticks);
            return tag;
        }

        public static Chore read(CompoundTag tag) {
            return new Chore(
                    tag.getString("Key"),
                    tag.contains("Origin") ? new DimBlockPos(tag.getCompound("Origin")) : new DimBlockPos(),
                    tag.getInt("Ticks"));
        }
    }
}
