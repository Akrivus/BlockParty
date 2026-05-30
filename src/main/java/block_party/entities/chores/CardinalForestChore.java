package block_party.entities.chores;

import block_party.BlockParty;
import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.entities.chores.PlaceBlockChores.Config;
import block_party.entities.movement.RoutineIntent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public final class CardinalForestChore implements MoeChore {
    public static final ResourceLocation ID = BlockParty.source("cardinal_forest");

    private static final double RADIUS = 16.0D;
    private static final int TICKS = 20 * 45;

    private final Config config;
    private final DimBlockPos origin;
    private final UUID playerUuid;
    private int ticks;

    private CardinalForestChore(Config config, DimBlockPos origin, int ticks, UUID playerUuid) {
        this.config = config == null ? Config.OAK_SAPLING : config;
        this.origin = origin == null ? new DimBlockPos() : origin;
        this.ticks = ticks;
        this.playerUuid = playerUuid;
    }

    public static CardinalForestChore oakSapling(ServerLevel level, BlockPos origin, UUID playerUuid) {
        return new CardinalForestChore(Config.OAK_SAPLING, new DimBlockPos(level.dimension(), origin.immutable()), TICKS, playerUuid);
    }

    public static CardinalForestChore read(CompoundTag tag) {
        return new CardinalForestChore(
                Config.fromKey(tag.getString("Key")),
                tag.contains("Origin") ? new DimBlockPos(tag.getCompound("Origin")) : new DimBlockPos(),
                tag.getInt("Ticks"),
                null);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public boolean active() {
        return this.ticks > 0 && this.config != null && !this.origin.isEmpty();
    }

    @Override
    public boolean canUse(Moe moe) {
        if (moe.shouldSkipGoalMovement() || moe.isFollowing() || !this.hasPlaceBlockChore(moe)) {
            return false;
        }
        boolean canRun = PlaceBlockChores.count(moe.getInventory(), this.config.item()) > 0 || this.nearestDrop(moe).isPresent();
        if (!canRun) {
            this.ticks = 0;
        }
        return canRun;
    }

    public boolean isCardinalForestVisitor(Moe moe) {
        return this.config == Config.OAK_SAPLING
                && this.hasPlaceBlockChore(moe)
                && moe.getVisibleBlockState().is(Blocks.OAK_LOG);
    }

    @Override
    public boolean tick(Moe moe) {
        if (!this.hasPlaceBlockChore(moe)) {
            this.ticks = 0;
            return false;
        }
        --this.ticks;
        if (!this.active()) {
            return false;
        }
        PlaceBlockChores.syncHand(moe, this.config);
        ItemEntity drop = this.nearestDrop(moe).orElse(null);
        if (drop != null && PlaceBlockChores.count(moe.getInventory(), this.config.item()) < this.config.maxCarry()) {
            return this.collectDrop(moe, drop);
        }
        if (PlaceBlockChores.count(moe.getInventory(), this.config.item()) > 0) {
            return this.placeBlock(moe);
        }
        this.ticks = 0;
        return false;
    }

    @Override
    public void stop(Moe moe) {
        PlaceBlockChores.syncHand(moe, this.config);
        if (moe.getRoutineIntent() == RoutineIntent.CHORE) {
            moe.setRoutineIntent(RoutineIntent.IDLE);
        }
    }

    @Override
    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Key", this.config.key());
        tag.put("Origin", this.origin.write());
        tag.putInt("Ticks", this.ticks);
        return tag;
    }

    @Override
    public void start(Moe moe) {
        moe.setRoutineIntent(RoutineIntent.CHORE);
        if (this.playerUuid != null) {
            moe.setDialogueTarget(this.playerUuid);
        }
    }

    private boolean hasPlaceBlockChore(Moe moe) {
        return this.active()
                && this.origin.getDim() == moe.level().dimension()
                && this.config != null;
    }

    private boolean collectDrop(Moe moe, ItemEntity drop) {
        if (drop == null || !drop.isAlive()) {
            return false;
        }
        moe.getLookControl().setLookAt(drop, 30.0F, 30.0F);
        if (moe.distanceToSqr(drop) > 1.75D * 1.75D) {
            moe.getNavigation().moveTo(drop, 0.9D);
            moe.getMoveControl().setWantedPosition(drop.getX(), drop.getY(), drop.getZ(), 0.9D);
            return true;
        }
        ItemStack stack = drop.getItem();
        if (!stack.is(this.config.item())) {
            return false;
        }
        ItemStack remainder = moe.getInventory().addItem(stack.copy());
        if (remainder.isEmpty()) {
            drop.discard();
        } else if (remainder.getCount() != stack.getCount()) {
            drop.setItem(remainder);
        } else {
            return false;
        }
        PlaceBlockChores.syncHand(moe, this.config);
        moe.setTemporaryAnimationKey("AWE", 30);
        return true;
    }

    private boolean placeBlock(Moe moe) {
        BlockPos plantPos = this.nearestSpot(moe).orElse(null);
        if (plantPos == null) {
            this.ticks = 0;
            return false;
        }
        moe.getLookControl().setLookAt(plantPos.getX() + 0.5D, plantPos.getY() + 0.5D, plantPos.getZ() + 0.5D, 30.0F, 30.0F);
        if (moe.blockPosition().distSqr(plantPos) > 2.25D * 2.25D) {
            Vec3 destination = Vec3.atBottomCenterOf(plantPos);
            moe.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, 0.8D);
            moe.getNavigation().moveTo(destination.x, destination.y, destination.z, 0.8D);
            return true;
        }
        ServerLevel level = (ServerLevel) moe.level();
        if (!PlaceBlockChores.removeOne(moe.getInventory(), this.config.item())) {
            this.ticks = 0;
            return false;
        }
        level.setBlock(plantPos, this.config.placeState(), 3);
        PlaceBlockChores.syncHand(moe, this.config);
        moe.addRelaxation(0.05F);
        moe.setTemporaryAnimationKey("AWE", 35);
        if (PlaceBlockChores.count(moe.getInventory(), this.config.item()) <= 0 && this.nearestDrop(moe).isEmpty()) {
            this.ticks = 0;
        }
        return true;
    }

    private Optional<ItemEntity> nearestDrop(Moe moe) {
        if (!this.hasPlaceBlockChore(moe)) {
            return Optional.empty();
        }
        return PlaceBlockChores.nearestDrop(moe, this.origin, this.config, RADIUS);
    }

    private Optional<BlockPos> nearestSpot(Moe moe) {
        if (!(moe.level() instanceof ServerLevel level)) {
            return Optional.empty();
        }
        return PlaceBlockChores.nearestSpot(level, this.origin.getPos(), this.config);
    }
}
