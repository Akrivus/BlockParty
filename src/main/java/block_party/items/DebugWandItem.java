package block_party.items;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.records.PlayerRelationship;
import block_party.entities.Moe;
import block_party.entities.data.HidingSpots;
import block_party.entities.environment.MoeEnvironmentalObservation;
import block_party.entities.environment.MoePlaceMemory;
import block_party.entities.movement.FollowSession;
import block_party.entities.movement.MoeAnchor;
import block_party.registry.CustomResources;
import block_party.registry.CustomTags;
import block_party.registry.resources.ScenesReloadListener;
import block_party.scene.SceneTrigger;
import block_party.world.structure.MoeStructureAssignment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

public class DebugWandItem extends Item {
    private static final double SUMMARY_RADIUS = 32.0D;
    private static final int MAX_SCENES_PER_GROUP = 8;

    public DebugWandItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            nearbySummary(serverPlayer);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return InteractionResult.PASS;
        }
        Player player = context.getPlayer();
        if (player instanceof ServerPlayer serverPlayer) {
            inspectBlock(serverPlayer, context.getClickedPos(), player.isShiftKeyDown());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (player.level().isClientSide()) {
            return InteractionResult.PASS;
        }
        if (!(player instanceof ServerPlayer serverPlayer) || !(entity instanceof Moe moe)) {
            return InteractionResult.FAIL;
        }
        inspect(serverPlayer, moe, player.isShiftKeyDown());
        return InteractionResult.SUCCESS;
    }

    private static void inspect(ServerPlayer player, Moe moe, boolean deep) {
        lines(player, moe, deep).forEach(player::sendSystemMessage);
    }

    private static List<Component> lines(ServerPlayer player, Moe moe, boolean deep) {
        Optional<MoeAnchor> anchor = moe.currentRoutineAnchor();
        Optional<MoePlaceMemory.Place> place = moe.rememberedPlace();
        Optional<MoeEnvironmentalObservation.Observation> observation = moe.latestEnvironmentalObservation();
        FollowSession follow = moe.getFollowSession();
        List<Component> lines = new ArrayList<>(List.of(
                line("Moe", "%s (%s)", moe.getDisplayName().getString(), moe.getGivenName()),
                line("Kind", "%s / %s", moe.getFamilyName(), blockId(moe.getActualBlockState())),
                line("IDs", "uuid=%s database=%d", moe.getUUID(), moe.getDatabaseID()),
                line("Owner/player", "owner=%s target=%s known=%s", moe.getPlayerUUID(), player.getDisplayName().getString(),
                        relationship(player, moe).isPresent()),
                line("Location", "dimension=%s current=%s home=%s", dimension(moe), pos(moe.blockPosition()), dimPos(moe.getHome())),
                line("Block", "%s visible=%s corporeal=%s",
                        blockId(moe.getActualBlockState()), blockId(moe.getVisibleBlockState()), moe.isCorporeal()),
                line("Profile", "dere=%s blood=%s zodiac=%s gender=%s age=%.1f scale=%.2f",
                        moe.getDere(), moe.getBloodType(), moe.getZodiac(), moe.getGender(), moe.getAge(), moe.getMoeScale()),
                line("State", "mode=%s goal=%s scene=%s", mode(moe), goal(moe), moe.sceneManager().getTriggerForTests()),
                line("Vitals", "health=%.1f/%.1f energy=%.1f saturation=%.1f exhaustion=%.2f",
                        moe.getHealth(), moe.getMaxHealth(), moe.getFoodLevel(), moe.getSaturation(), moe.getExhaustion()),
                line("Mood", "emotion=%s animation=%s loyalty=%.1f affection=%.1f stress=%.1f relaxation=%.1f slouch=%.1f",
                        moe.getEmotion(), moe.getAnimationKey(), moe.getLoyalty(), moe.getAffection(),
                        moe.getStress(), moe.getRelaxation(), moe.getSlouch()),
                line("Timers", "hungry=%d lonely=%d stress=%d sinceSleep=%d",
                        moe.getTimeUntilHungry(), moe.getTimeUntilLonely(), moe.getTimeUntilStress(), moe.getTimeSinceSleep()),
                line("Routine", "sitting=%s following=%s intent=%s effective=%s playerOnline=%s playerBusy=%s",
                        moe.isSitting(), moe.isFollowing(), moe.getRoutineIntent(), moe.getEffectiveRoutineIntent(),
                        moe.isPlayerOnline(), moe.isPlayerBusy()),
                line("Follow", "active=%s player=%s intent=%s ticks=%d dimensionTravel=%s",
                        follow.active(), follow.playerUuid(), follow.intent(), follow.ticksRemaining(), follow.canChangeDimension()),
                relationshipLine(player, moe),
                line("Anchor", anchor.map(DebugWandItem::anchor).orElse("none")),
                line("Place", place.map(DebugWandItem::place).orElse("none")),
                line("Observation", observation.map(DebugWandItem::observation).orElse("none")),
                line("Inventory", "%d slots, tileNbtKeys=%d", moe.getInventory().getContainerSize(), moe.getTileEntityData().size())
        ));
        if (deep) {
            lines.add(structureLine(moe.structureAssignment()));
            lines.addAll(sceneLines(SceneTrigger.RIGHT_CLICK, moe));
            lines.addAll(sceneLines(SceneTrigger.SHIFT_RIGHT_CLICK, moe));
            lines.addAll(spawnHideLines(moe));
        }
        return lines;
    }

    private static void inspectBlock(ServerPlayer player, BlockPos pos, boolean deep) {
        ServerLevel level = player.serverLevel();
        BlockState state = level.getBlockState(pos);
        player.sendSystemMessage(line("Block", "%s at %s dimension=%s", blockId(state), pos(pos), level.dimension().location()));
        player.sendSystemMessage(line("Moe spawn", "%s", state.is(CustomTags.SPAWNS_MOES) ? "yes" : "no: missing block_party:spawns_moes tag"));
        OptionalLong hidden = HidingSpots.get(level).find(pos);
        player.sendSystemMessage(line("Hiding spot", hidden.isPresent() ? "occupied by database #%d" : "empty", hidden.orElse(-1L)));
        if (deep) {
            player.sendSystemMessage(line("Can hide here", "%s", canHideAt(level, pos, state)));
            player.sendSystemMessage(line("Spawn target", "%s", spawnTarget(level, pos)));
        }
    }

    private static void nearbySummary(ServerPlayer player) {
        AABB area = player.getBoundingBox().inflate(SUMMARY_RADIUS);
        List<Moe> moes = player.serverLevel().getEntities(EntityTypeTest.forClass(Moe.class), area, moe ->
                moe.isAlive() && !moe.isRemoved());
        player.sendSystemMessage(line("Nearby Moes", "%d within %.0f blocks", moes.size(), SUMMARY_RADIUS));
        moes.stream()
                .sorted((left, right) -> Double.compare(left.distanceToSqr(player), right.distanceToSqr(player)))
                .limit(12)
                .forEach(moe -> player.sendSystemMessage(line("Moe",
                        "%s #%d %s mode=%s goal=%s",
                        moe.getGivenName(), moe.getDatabaseID(), pos(moe.blockPosition()), mode(moe), goal(moe))));
    }

    private static Component line(String label, String format, Object... args) {
        return line(label, String.format(Locale.ROOT, format, args));
    }

    private static Component line(String label, String value) {
        MutableComponent prefix = Component.literal("[Debug Wand] " + label + ": ");
        return prefix.append(Component.literal(value));
    }

    private static String anchor(MoeAnchor anchor) {
        return String.format(Locale.ROOT, "%s #%d at %s priority=%d owner=%s",
                anchor.type(), anchor.databaseId(), pos(anchor.dimPos().getPos()), anchor.priority(), anchor.playerUuid());
    }

    private static String place(MoePlaceMemory.Place place) {
        return String.format(Locale.ROOT, "%s at %s score=%.2f occupancy=%d/%d shelter=%d",
                place.type(), pos(place.pos()), place.score(), place.occupancy(), place.capacity(), place.shelter().score());
    }

    private static String observation(MoeEnvironmentalObservation.Observation observation) {
        return String.format(Locale.ROOT, "%s %s at %s score=%.2f signal=i%.2f/t%.2f/a%.2f",
                observation.kind(), blockId(observation.state()), pos(observation.pos()), observation.score(),
                observation.signal().interest(), observation.signal().tension(), observation.signal().affinity());
    }

    private static String blockId(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }

    private static String mode(Moe moe) {
        if (moe.getDialogue() != null || moe.getResponse() != null) {
            return "TALKING";
        }
        if (moe.isFollowing()) {
            return "FOLLOWING";
        }
        if (moe.isSitting()) {
            return "SITTING";
        }
        if (moe.currentRoutineAnchor().isPresent()) {
            return "ROUTINE";
        }
        return "WANDERING";
    }

    private static String goal(Moe moe) {
        return moe.chores().activeId()
                .map(id -> "chore:" + id)
                .orElseGet(() -> moe.currentRoutineAnchor()
                        .map(anchor -> "anchor:" + anchor.type() + "@" + pos(anchor.dimPos().getPos()))
                        .orElse("routine:" + moe.getEffectiveRoutineIntent()));
    }

    private static Optional<PlayerRelationship> relationship(ServerPlayer player, Moe moe) {
        return BlockPartyDB.get(player.level()).findPlayerRelationshipSafe(moe.getDatabaseID(), player.getUUID());
    }

    private static Component relationshipLine(ServerPlayer player, Moe moe) {
        Optional<PlayerRelationship> relationship = relationship(player, moe);
        if (relationship.isEmpty()) {
            return line("Relationship", "player=%s missing row contact=no yearbook=no phoneCallable=no",
                    player.getDisplayName().getString());
        }
        PlayerRelationship row = relationship.get();
        return line("Relationship",
                "player=%s trust=%.1f affection=%.1f loyalty=%.1f tension=%.1f interest=%.1f last=%d contact=%s yearbook=%s phoneCallable=%s",
                player.getDisplayName().getString(), row.trust(), row.affection(), row.loyalty(), row.stress(),
                interest(row), row.lastInteractionAt(), row.phoneContact(), row.yearbookSigned(), row.phoneContact() && !moe.isPlayerBusy());
    }

    private static float interest(PlayerRelationship relationship) {
        return Math.max(relationship.trust(), Math.max(relationship.affection(), relationship.loyalty()));
    }

    private static Component structureLine(MoeStructureAssignment assignment) {
        if (!assignment.assigned()) {
            return line("Spawn coordinator", "none");
        }
        return line("Spawn coordinator", "%s cohort=%s part=%d state=%s target=%s",
                assignment.structureId(), assignment.cohortId(), assignment.partIndex(), assignment.state(), dimPos(assignment.target()));
    }

    private static List<Component> sceneLines(SceneTrigger trigger, Moe moe) {
        List<ScenesReloadListener.SceneDebugResult> results = CustomResources.SCENES.debug(trigger, moe);
        List<String> available = results.stream()
                .filter(ScenesReloadListener.SceneDebugResult::available)
                .map(result -> result.id().toString())
                .limit(MAX_SCENES_PER_GROUP)
                .toList();
        List<String> blocked = results.stream()
                .filter(result -> !result.available())
                .map(DebugWandItem::blockedScene)
                .limit(MAX_SCENES_PER_GROUP)
                .toList();
        return List.of(
                line("Available scenes " + trigger, available.isEmpty() ? "none" : String.join(", ", available)),
                line("Blocked scenes " + trigger, blocked.isEmpty() ? "none" : String.join(", ", blocked))
        );
    }

    private static String blockedScene(ScenesReloadListener.SceneDebugResult result) {
        if (result.reasons().isEmpty()) {
            return result.id() + " filters=" + result.filterCount();
        }
        return result.id() + " missing: " + String.join("; ", result.reasons().stream().limit(3).toList());
    }

    private static List<Component> spawnHideLines(Moe moe) {
        if (!(moe.level() instanceof ServerLevel level)) {
            return List.of(line("Spawn/hide", "server state unavailable"));
        }
        BlockPos current = moe.blockPosition();
        return List.of(
                line("Origin block", blockId(moe.getActualBlockState())),
                line("Hiding block", "%s at %s", blockId(level.getBlockState(current)), pos(current)),
                line("Can hide here", "%s", canHideAt(level, current, level.getBlockState(current))),
                line("Saved home", "%s", dimPos(moe.getHome())),
                line("Respawn target", "%s", moe.hasHome() ? dimPos(moe.getHome()) : "none")
        );
    }

    private static String canHideAt(ServerLevel level, BlockPos pos, BlockState state) {
        if (!level.getBlockState(pos).isAir()) {
            return "no: occupied by " + blockId(state);
        }
        if (HidingSpots.get(level).find(pos).isPresent()) {
            return "no: hiding spot already registered";
        }
        return "yes";
    }

    private static String spawnTarget(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos target = pos.relative(direction);
            if (level.getBlockState(target).isAir()) {
                return target + " via " + direction.getName();
            }
        }
        return "none: no adjacent air block";
    }

    private static String pos(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private static String dimPos(DimBlockPos dimPos) {
        if (dimPos == null || dimPos.isEmpty()) {
            return "none";
        }
        return dimPos.getDim().location() + " " + pos(dimPos.getPos());
    }

    private static String dimension(Moe moe) {
        return moe.level().dimension().location().toString();
    }
}
