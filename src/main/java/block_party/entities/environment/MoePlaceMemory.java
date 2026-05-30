package block_party.entities.environment;

import block_party.blocks.GardenLanternBlock;
import block_party.entities.Moe;
import block_party.entities.movement.MoeAnchor;
import block_party.entities.movement.MoeAnchorResolver;
import block_party.entities.movement.MoeAnchorType;
import block_party.registry.CustomTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public final class MoePlaceMemory {
    public static final double PLACE_RADIUS = 14.0D;
    private static final double ANCHOR_EVIDENCE_RADIUS = 10.0D;
    private static final int FEATURE_RADIUS = 5;

    private MoePlaceMemory() {
    }

    public static Optional<Place> scan(Moe moe) {
        BlockPos origin = moe.blockPosition();
        Place best = Place.none();
        List<MoeAnchor> anchors = MoeAnchorResolver.activeAnchors(moe);
        int radius = (int) Math.ceil(PLACE_RADIUS);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z > PLACE_RADIUS * PLACE_RADIUS) {
                        continue;
                    }
                    BlockPos candidate = origin.offset(x, y, z);
                    if (!MoeEnvironmentalRules.canStandAt(moe.level(), candidate)) {
                        continue;
                    }
                    Place place = evaluate(moe, candidate, anchors);
                    if (place.score() > best.score()) {
                        best = place;
                    }
                }
            }
        }
        if (best.type() == PlaceType.NONE) {
            best = evaluate(moe, origin, anchors);
        }
        return best.type() == PlaceType.NONE ? Optional.empty() : Optional.of(best);
    }

    public static Place evaluate(Moe moe, BlockPos pos) {
        return evaluate(moe, pos, MoeAnchorResolver.activeAnchors(moe));
    }

    private static Place evaluate(Moe moe, BlockPos pos, List<MoeAnchor> anchors) {
        Level level = moe.level();
        MoeEnvironmentalRules.ShelterScore shelter = MoeEnvironmentalRules.shelterScore(level, pos);
        Features features = scanFeatures(level, pos, anchorEvidence(moe, pos, anchors));
        PlaceType type = typeFor(level, pos, shelter, features);
        int occupancy = occupancy(moe, pos);
        int capacity = capacity(type);
        double score = comfortScore(type, shelter, features) + anchorScore(features) - occupancyPenalty(occupancy, capacity);
        score -= Math.sqrt(moe.blockPosition().distSqr(pos)) * 0.05D;
        return new Place(type, pos.immutable(), score, occupancy, capacity, shelter, features);
    }

    public static boolean stillValid(Moe moe, Place place) {
        if (place == null || place.type() == PlaceType.NONE) {
            return false;
        }
        if (!MoeEnvironmentalRules.canStandAt(moe.level(), place.pos())) {
            return false;
        }
        Place current = evaluate(moe, place.pos());
        return current.type() == place.type() && !current.overcrowded();
    }

    public static boolean hasGardenLantern(Moe moe, Place place) {
        return gardenLanternCount(moe, place, null) > 0;
    }

    public static boolean hasLitGardenLantern(Moe moe, Place place) {
        return gardenLanternCount(moe, place, true) > 0;
    }

    public static boolean hasUnlitGardenLantern(Moe moe, Place place) {
        return gardenLanternCount(moe, place, false) > 0;
    }

    private static int gardenLanternCount(Moe moe, Place place, Boolean lit) {
        if (moe == null || place == null || place.type() == PlaceType.NONE) {
            return 0;
        }
        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(
                place.pos().offset(-FEATURE_RADIUS, -2, -FEATURE_RADIUS),
                place.pos().offset(FEATURE_RADIUS, 3, FEATURE_RADIUS))) {
            BlockState state = moe.level().getBlockState(pos);
            if (state.is(CustomTags.PLACE_GARDEN_LANTERNS)
                    && (lit == null || state.hasProperty(GardenLanternBlock.LIT)
                    && state.getValue(GardenLanternBlock.LIT) == lit)) {
                count++;
            }
        }
        return count;
    }

    private static PlaceType typeFor(Level level, BlockPos pos, MoeEnvironmentalRules.ShelterScore shelter, Features features) {
        if (features.anchorType() == MoeAnchorType.HOME) {
            return PlaceType.HOUSE;
        }
        if (features.anchorType() == MoeAnchorType.GARDEN) {
            return PlaceType.GARDEN;
        }
        if (features.anchorType() == MoeAnchorType.SHRINE) {
            return PlaceType.SHRINE;
        }
        if (features.anchorType() == MoeAnchorType.SAPLING) {
            return PlaceType.GROVE;
        }
        if (shelter.score() >= 75 || shelter.nearDoor() && shelter.score() >= 35) {
            return features.workshopBlocks() >= 1 ? PlaceType.WORKSHOP : PlaceType.HOUSE;
        }
        if (features.shrineBlocks() >= 1) {
            return PlaceType.SHRINE;
        }
        if (features.workshopBlocks() >= 2) {
            return PlaceType.WORKSHOP;
        }
        if (features.cropBlocks() + features.farmland() >= 5) {
            return PlaceType.FARM;
        }
        if (features.gardenBlocks() >= 4) {
            return PlaceType.GARDEN;
        }
        if (features.logs() >= 2 && features.leaves() >= 4) {
            return PlaceType.GROVE;
        }
        if (level.canSeeSky(pos.above()) && features.water() >= 5) {
            return PlaceType.WATERFRONT;
        }
        if (features.caveBlocks() >= 18) {
            return PlaceType.CAVE;
        }
        if (level.canSeeSky(pos.above()) && features.grass() >= 5) {
            return PlaceType.FIELD;
        }
        if (shelter.covered() || shelter.score() >= 45) {
            return PlaceType.SHELTER;
        }
        return PlaceType.NONE;
    }

    private static double comfortScore(PlaceType type, MoeEnvironmentalRules.ShelterScore shelter, Features features) {
        return switch (type) {
            case HOUSE -> 90.0D + shelter.score() * 0.35D;
            case WORKSHOP -> 72.0D + features.workshopBlocks() * 8.0D + shelter.score() * 0.2D;
            case SHRINE -> 78.0D + features.shrineBlocks() * 18.0D + shelter.blockLight();
            case FARM -> 66.0D + features.cropBlocks() * 3.5D + features.farmland() * 2.0D + shelter.blockLight() * 0.4D;
            case GARDEN -> 62.0D + features.gardenBlocks() * 4.0D + shelter.blockLight()
                    + (features.anchorType() == MoeAnchorType.GARDEN ? 28.0D : 0.0D);
            case GROVE -> 58.0D + features.logs() * 3.0D + features.leaves() * 1.5D;
            case WATERFRONT -> 56.0D + features.water() * 2.0D + features.grass();
            case CAVE -> 54.0D + features.caveBlocks() * 1.2D + shelter.solidSides() * 3.0D;
            case FIELD -> 50.0D + features.grass() * 2.0D + (shelter.blockLight() * 0.5D);
            case SHELTER -> 46.0D + shelter.score() * 0.45D;
            case NONE -> 0.0D;
        };
    }

    private static double anchorScore(Features features) {
        if (features.anchorType() == null) {
            return 0.0D;
        }
        double distance = Math.sqrt(features.anchorDistanceSqr());
        return Math.max(8.0D, 42.0D - distance * 2.0D + features.anchorPriority() * 0.12D);
    }

    private static double occupancyPenalty(int occupancy, int capacity) {
        if (occupancy <= capacity) {
            return occupancy * 3.0D;
        }
        return capacity * 3.0D + (occupancy - capacity) * 18.0D;
    }

    private static int capacity(PlaceType type) {
        return switch (type) {
            case HOUSE -> 3;
            case WORKSHOP -> 2;
            case SHRINE -> 4;
            case FARM -> 6;
            case GARDEN -> 6;
            case GROVE -> 5;
            case WATERFRONT -> 6;
            case CAVE -> 4;
            case FIELD -> 8;
            case SHELTER -> 2;
            case NONE -> 0;
        };
    }

    private static int occupancy(Moe moe, BlockPos pos) {
        return moe.level().getEntities(EntityTypeTest.forClass(Moe.class), new AABB(pos).inflate(5.0D), other ->
                other != moe && other.isAlive() && !other.isRemoved()).size();
    }

    private static Features scanFeatures(Level level, BlockPos origin, AnchorEvidence anchor) {
        int doors = 0;
        int workshopBlocks = 0;
        int gardenBlocks = 0;
        int logs = 0;
        int leaves = 0;
        int grass = 0;
        int water = 0;
        int caveBlocks = 0;
        int shrineBlocks = 0;
        int cropBlocks = 0;
        int farmland = 0;
        int radius = FEATURE_RADIUS;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -2; y <= 3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock() instanceof DoorBlock) {
                        doors++;
                    }
                    if (isWorkshopBlock(state)) {
                        workshopBlocks++;
                    }
                    if (isGardenBlock(state)) {
                        gardenBlocks++;
                    }
                    if (state.is(BlockTags.LOGS)) {
                        logs++;
                    }
                    if (state.is(BlockTags.LEAVES)) {
                        leaves++;
                    }
                    if (state.is(CustomTags.PLACE_GRASS_BLOCKS)) {
                        grass++;
                    }
                    if (state.getFluidState().is(FluidTags.WATER)) {
                        water++;
                    }
                    if (isCaveBlock(state)) {
                        caveBlocks++;
                    }
                    if (isShrineBlock(state)) {
                        shrineBlocks++;
                    }
                    if (isCropBlock(state)) {
                        cropBlocks++;
                    }
                    if (state.is(CustomTags.PLACE_FARMLAND)) {
                        farmland++;
                    }
                }
            }
        }
        return new Features(doors, workshopBlocks, gardenBlocks, logs, leaves, grass, water, caveBlocks, shrineBlocks, cropBlocks, farmland, anchor.type(), anchor.distanceSqr(), anchor.priority());
    }

    private static boolean isWorkshopBlock(BlockState state) {
        return state.is(CustomTags.PLACE_WORKSHOP_BLOCKS);
    }

    private static boolean isGardenBlock(BlockState state) {
        return state.is(CustomTags.PLACE_GARDEN_BLOCKS);
    }

    private static boolean isCaveBlock(BlockState state) {
        return state.is(CustomTags.PLACE_CAVE_BLOCKS);
    }

    private static boolean isShrineBlock(BlockState state) {
        return state.is(CustomTags.PLACE_SHRINE_BLOCKS);
    }

    private static boolean isCropBlock(BlockState state) {
        return state.is(CustomTags.PLACE_CROPS);
    }

    private static AnchorEvidence anchorEvidence(Moe moe, BlockPos pos, List<MoeAnchor> anchors) {
        MoeAnchor best = null;
        double bestDistance = Double.MAX_VALUE;
        double bestScore = Double.NEGATIVE_INFINITY;
        double radiusSqr = ANCHOR_EVIDENCE_RADIUS * ANCHOR_EVIDENCE_RADIUS;
        for (MoeAnchor anchor : anchors) {
            if (anchor.dimPos().getDim() != moe.level().dimension()) {
                continue;
            }
            double distance = anchor.dimPos().getPos().distSqr(pos);
            if (anchor.type() == MoeAnchorType.HOME && distance > 9.0D) {
                continue;
            }
            double score = anchor.priority() - Math.sqrt(distance) * 6.0D;
            if (distance <= radiusSqr && (best == null || score > bestScore || score == bestScore && distance < bestDistance)) {
                best = anchor;
                bestDistance = distance;
                bestScore = score;
            }
        }
        return best == null ? AnchorEvidence.none() : new AnchorEvidence(best.type(), bestDistance, best.priority());
    }

    public enum PlaceType {
        NONE,
        HOUSE,
        SHELTER,
        GARDEN,
        GROVE,
        FIELD,
        WORKSHOP,
        WATERFRONT,
        CAVE,
        SHRINE,
        FARM
    }

    private record AnchorEvidence(MoeAnchorType type, double distanceSqr, int priority) {
        static AnchorEvidence none() {
            return new AnchorEvidence(null, Double.MAX_VALUE, 0);
        }
    }

    public record Features(
            int doors,
            int workshopBlocks,
            int gardenBlocks,
            int logs,
            int leaves,
            int grass,
            int water,
            int caveBlocks,
            int shrineBlocks,
            int cropBlocks,
            int farmland,
            MoeAnchorType anchorType,
            double anchorDistanceSqr,
            int anchorPriority) {
    }

    public record Place(
            PlaceType type,
            BlockPos pos,
            double score,
            int occupancy,
            int capacity,
            MoeEnvironmentalRules.ShelterScore shelter,
            Features features) {
        public boolean overcrowded() {
            return this.capacity > 0 && this.occupancy > this.capacity;
        }

        public CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Type", this.type.name());
            tag.putInt("X", this.pos.getX());
            tag.putInt("Y", this.pos.getY());
            tag.putInt("Z", this.pos.getZ());
            tag.putDouble("Score", this.score);
            tag.putInt("Occupancy", this.occupancy);
            tag.putInt("Capacity", this.capacity);
            tag.putInt("ShelterScore", this.shelter.score());
            tag.putBoolean("Covered", this.shelter.covered());
            tag.putBoolean("NearDoor", this.shelter.nearDoor());
            tag.putInt("BlockLight", this.shelter.blockLight());
            tag.putInt("SolidSides", this.shelter.solidSides());
            tag.putInt("Doors", this.features.doors());
            tag.putInt("WorkshopBlocks", this.features.workshopBlocks());
            tag.putInt("GardenBlocks", this.features.gardenBlocks());
            tag.putInt("Logs", this.features.logs());
            tag.putInt("Leaves", this.features.leaves());
            tag.putInt("Grass", this.features.grass());
            tag.putInt("Water", this.features.water());
            tag.putInt("CaveBlocks", this.features.caveBlocks());
            tag.putInt("ShrineBlocks", this.features.shrineBlocks());
            tag.putInt("CropBlocks", this.features.cropBlocks());
            tag.putInt("Farmland", this.features.farmland());
            if (this.features.anchorType() != null) {
                tag.putString("AnchorType", this.features.anchorType().name());
                tag.putDouble("AnchorDistanceSqr", this.features.anchorDistanceSqr());
                tag.putInt("AnchorPriority", this.features.anchorPriority());
            }
            return tag;
        }

        public static Place read(CompoundTag tag) {
            PlaceType type = PlaceType.NONE;
            if (tag.contains("Type")) {
                try {
                    type = PlaceType.valueOf(tag.getString("Type"));
                } catch (IllegalArgumentException ignored) {
                    type = PlaceType.NONE;
                }
            }
            if (type == PlaceType.NONE) {
                return none();
            }
            return new Place(
                    type,
                    new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z")),
                    tag.getDouble("Score"),
                    tag.getInt("Occupancy"),
                    tag.getInt("Capacity"),
                    new MoeEnvironmentalRules.ShelterScore(
                            tag.getInt("ShelterScore"),
                            tag.getBoolean("Covered"),
                            tag.getBoolean("NearDoor"),
                            tag.getInt("BlockLight"),
                            tag.getInt("SolidSides")),
                    new Features(
                            tag.getInt("Doors"),
                            tag.getInt("WorkshopBlocks"),
                            tag.getInt("GardenBlocks"),
                            tag.getInt("Logs"),
                            tag.getInt("Leaves"),
                            tag.getInt("Grass"),
                            tag.getInt("Water"),
                            tag.getInt("CaveBlocks"),
                            tag.getInt("ShrineBlocks"),
                            tag.getInt("CropBlocks"),
                            tag.getInt("Farmland"),
                            readAnchorType(tag),
                            tag.contains("AnchorDistanceSqr") ? tag.getDouble("AnchorDistanceSqr") : Double.MAX_VALUE,
                            tag.getInt("AnchorPriority")));
        }

        public static Place none() {
            return new Place(PlaceType.NONE, BlockPos.ZERO, Double.NEGATIVE_INFINITY, 0, 0, new MoeEnvironmentalRules.ShelterScore(0, false, false, 0, 0), new Features(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, Double.MAX_VALUE, 0));
        }

        private static MoeAnchorType readAnchorType(CompoundTag tag) {
            if (!tag.contains("AnchorType")) {
                return null;
            }
            try {
                return MoeAnchorType.valueOf(tag.getString("AnchorType"));
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
    }
}
