package block_party.world.structure;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.entities.MoeSpawner;
import block_party.entities.goals.HideUntil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class MoeStructureCohortCoordinator {
    private static final int MAX_PROTOTYPE_COHORT_SIZE = 128;
    private static final int DISPATCH_INTERVAL_TICKS = 10;
    private static final int DISPATCH_BATCH_SIZE = 12;
    private static final int MAX_ACTIVE_MOVERS = 48;
    private static final int CLOSE_RANGE_TIMEOUT_TICKS = 20 * 8;
    private static final double ARRIVAL_DISTANCE_SQR = 0.9D;
    private static final double ASSISTED_CLIMB_HORIZONTAL_SQR = 7.5D;
    private static final double CLOSE_ENOUGH_TO_NUDGE_SQR = 9.0D;
    private static final double CLOSE_ENOUGH_TO_SETTLE_SQR = 16.0D;
    private static final double MOVE_SPEED = 1.05D;
    private static final Map<UUID, Long> NEXT_DISPATCH_TICK = new HashMap<>();
    private static final Map<Long, Long> MOVEMENT_START_TICK = new HashMap<>();

    private MoeStructureCohortCoordinator() {
    }

    public static boolean isDesertWellSource(BlockState state) {
        return state.is(Blocks.SANDSTONE)
                || state.is(Blocks.SANDSTONE_SLAB)
                || state.is(Blocks.SANDSTONE_STAIRS)
                || state.is(Blocks.SANDSTONE_WALL)
                || state.is(Blocks.SMOOTH_SANDSTONE)
                || state.is(Blocks.SMOOTH_SANDSTONE_SLAB)
                || state.is(Blocks.SMOOTH_SANDSTONE_STAIRS)
                || state.is(Blocks.CUT_SANDSTONE)
                || state.is(Blocks.CUT_SANDSTONE_SLAB)
                || state.is(Blocks.CHISELED_SANDSTONE);
    }

    public static void spawnDesertWellPrototypeCohort(ServerLevel level, BlockPos anchor, UUID owner, Moe first) {
        if (first == null || first.structureAssignment().assigned()) {
            return;
        }
        UUID cohort = UUID.randomUUID();
        MoeStructureTemplate template = MoeStructureTemplates.desertWell(level);
        int count = Math.min(MAX_PROTOTYPE_COHORT_SIZE, template.partCount());
        assign(first, cohort, 0, anchor, true);
        for (int index = 1; index < count; index++) {
            MoeStructureTemplate.Part part = template.part(index);
            BlockPos spawn = anchor.offset(level.random.nextInt(9) - 4, 1, level.random.nextInt(9) - 4);
            int partIndex = index;
            MoeSpawner.spawn(level, spawn, part.state(), owner, new net.minecraft.nbt.CompoundTag(), moe ->
                    assign(moe, cohort, partIndex, anchor, false));
        }
        BlockParty.LOGGER.info("Created desert_well Moe cohort {} with {}/{} assigned parts from {} at anchor {}",
                cohort, count, template.partCount(), template.source(), anchor);
    }

    public static void onThreatened(Moe threatened) {
        if (!(threatened.level() instanceof ServerLevel level)) {
            return;
        }
        MoeStructureAssignment assignment = threatened.structureAssignment();
        if (!assignment.assigned() || assignment.state() == MoeStructureAssignment.State.HIDDEN) {
            return;
        }
        List<Moe> members = loadedCohortMembers(level.getServer(), assignment.cohortId());
        for (Moe member : members) {
            MoeStructureAssignment memberAssignment = member.structureAssignment();
            if (memberAssignment.state() == MoeStructureAssignment.State.IDLE) {
                member.setStructureAssignment(memberAssignment.withState(MoeStructureAssignment.State.ASSEMBLING), true);
            }
        }
        NEXT_DISPATCH_TICK.put(assignment.cohortId(), level.getGameTime());
        BlockParty.LOGGER.info("Threatened desert_well Moe cohort {} from member {} part {}; assembling {} loaded members",
                assignment.cohortId(), threatened.getDatabaseID(), assignment.partIndex(), members.size());
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        for (ServerLevel level : server.getAllLevels()) {
            tickLevel(server, level);
        }
    }

    private static void tickLevel(MinecraftServer server, ServerLevel level) {
        List<Moe> assigned = new ArrayList<>();
        for (Moe moe : level.getEntities(EntityTypeTest.forClass(Moe.class), moe ->
                moe.isAlive() && !moe.isRemoved() && moe.structureAssignment().assigned())) {
            assigned.add(moe);
        }
        Map<UUID, List<Moe>> byCohort = assigned.stream()
                .collect(Collectors.groupingBy(moe -> moe.structureAssignment().cohortId()));
        byCohort.forEach((cohort, members) -> tickCohort(server, level, cohort, members));
    }

    private static void tickCohort(MinecraftServer server, ServerLevel level, UUID cohort, List<Moe> levelMembers) {
        long now = level.getGameTime();
        if (now >= NEXT_DISPATCH_TICK.getOrDefault(cohort, 0L)) {
            int activeMovers = (int) levelMembers.stream()
                    .filter(moe -> moe.structureAssignment().state() == MoeStructureAssignment.State.MOVING)
                    .count();
            int dispatchSlots = Math.min(DISPATCH_BATCH_SIZE, Math.max(0, MAX_ACTIVE_MOVERS - activeMovers));
            List<Moe> dispatched = levelMembers.stream()
                    .filter(moe -> moe.structureAssignment().state() == MoeStructureAssignment.State.ASSEMBLING)
                    .sorted(Comparator.comparingInt(moe -> moe.structureAssignment().partIndex()))
                    .limit(dispatchSlots)
                    .toList();
            for (Moe moe : dispatched) {
                MoeStructureAssignment moving = moe.structureAssignment().withState(MoeStructureAssignment.State.MOVING);
                moe.setStructureAssignment(moving, true);
                MOVEMENT_START_TICK.put(moe.getDatabaseID(), now);
                BlockParty.LOGGER.info("Dispatching Moe {} cohort {} part {} to {} offset {}",
                        moe.getDatabaseID(), cohort, moving.partIndex(), moving.target().getPos(), moving.offset());
            }
            if (!dispatched.isEmpty()) {
                NEXT_DISPATCH_TICK.put(cohort, now + DISPATCH_INTERVAL_TICKS);
            }
        }

        for (Moe moe : levelMembers) {
            MoeStructureAssignment assignment = moe.structureAssignment();
            if (assignment.state() != MoeStructureAssignment.State.MOVING || assignment.target().getDim() != level.dimension()) {
                continue;
            }
            BlockPos targetPos = assignment.target().getPos();
            Vec3 target = Vec3.atBottomCenterOf(targetPos);
            double distanceSqr = moe.position().distanceToSqr(target);
            if (distanceSqr <= ARRIVAL_DISTANCE_SQR) {
                completeHide(level, cohort, moe, assignment, targetPos, false);
            } else if (canAssistedClimbSettle(level, moe, targetPos)) {
                completeHide(level, cohort, moe, assignment, targetPos, true);
            } else if (isCloseRangeTimedOut(level, moe, distanceSqr)) {
                completeHide(level, cohort, moe, assignment, targetPos, true);
            } else {
                moveTowardAssignedPart(moe, target);
            }
        }
    }

    private static boolean isCloseRangeTimedOut(ServerLevel level, Moe moe, double distanceSqr) {
        Long started = MOVEMENT_START_TICK.get(moe.getDatabaseID());
        return started != null
                && level.getGameTime() - started >= CLOSE_RANGE_TIMEOUT_TICKS
                && distanceSqr <= CLOSE_ENOUGH_TO_SETTLE_SQR;
    }

    private static boolean canAssistedClimbSettle(ServerLevel level, Moe moe, BlockPos targetPos) {
        if (targetPos.getY() <= moe.blockPosition().getY() + 1) {
            return false;
        }
        Vec3 target = Vec3.atBottomCenterOf(targetPos);
        double dx = moe.getX() - target.x;
        double dz = moe.getZ() - target.z;
        return dx * dx + dz * dz <= ASSISTED_CLIMB_HORIZONTAL_SQR
                && hasNearbyStructureSupport(level, targetPos);
    }

    private static boolean hasNearbyStructureSupport(ServerLevel level, BlockPos targetPos) {
        if (!level.getBlockState(targetPos.below()).getCollisionShape(level, targetPos.below()).isEmpty()) {
            return true;
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                BlockPos sameLayer = targetPos.offset(dx, 0, dz);
                BlockPos layerBelow = targetPos.offset(dx, -1, dz);
                if (!level.getBlockState(sameLayer).getCollisionShape(level, sameLayer).isEmpty()
                        || !level.getBlockState(layerBelow).getCollisionShape(level, layerBelow).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void completeHide(ServerLevel level, UUID cohort, Moe moe, MoeStructureAssignment assignment, BlockPos targetPos, boolean settled) {
        moe.moveToBlock(targetPos);
        moe.setBlockState(assignment.blockState());
        moe.setStructureAssignment(assignment.withState(MoeStructureAssignment.State.HIDDEN), true);
        MOVEMENT_START_TICK.remove(moe.getDatabaseID());
        if (moe.hide(HideUntil.EXPOSED) != null) {
            BlockParty.LOGGER.info("Moe {} completed desert_well hide cohort {} part {} at {}{}",
                    moe.getDatabaseID(), cohort, assignment.partIndex(), targetPos, settled ? " after close-range settle" : "");
        }
    }

    private static void moveTowardAssignedPart(Moe moe, Vec3 target) {
        boolean pathing = moe.getNavigation().moveTo(target.x, target.y, target.z, MOVE_SPEED);
        if (!pathing || (moe.getNavigation().isDone() && moe.position().distanceToSqr(target) <= CLOSE_ENOUGH_TO_NUDGE_SQR)) {
            moe.getMoveControl().setWantedPosition(target.x, target.y, target.z, MOVE_SPEED);
            if (target.y > moe.getY() + 0.35D && moe.onGround()) {
                moe.getJumpControl().jump();
            }
        }
    }

    private static List<Moe> loadedCohortMembers(MinecraftServer server, UUID cohort) {
        List<Moe> members = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Moe moe : level.getEntities(EntityTypeTest.forClass(Moe.class), moe ->
                    moe.isAlive() && !moe.isRemoved() && moe.structureAssignment().assigned()
                            && cohort.equals(moe.structureAssignment().cohortId()))) {
                members.add(moe);
            }
        }
        return List.copyOf(members);
    }

    private static void assign(Moe moe, UUID cohort, int partIndex, BlockPos anchor, boolean persist) {
        MoeStructureTemplate.Part part = MoeStructureTemplates.desertWellPart((ServerLevel) moe.level(), partIndex);
        BlockPos target = anchor.offset(part.offset());
        MoeStructureAssignment assignment = MoeStructureAssignment.desertWell(
                cohort,
                partIndex,
                part.offset(),
                new DimBlockPos(moe.level().dimension(), target));
        moe.setBlockState(part.state());
        moe.setStructureAssignment(assignment, persist);
        BlockParty.LOGGER.info("Assigned Moe {} to desert_well cohort {} part {} offset {} target {}",
                moe.getDatabaseID(), cohort, partIndex, part.offset(), target);
    }
}
