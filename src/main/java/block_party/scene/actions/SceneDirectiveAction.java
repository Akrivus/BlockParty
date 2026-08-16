package block_party.scene.actions;

import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.entities.social.MoeSocialContext;
import block_party.entities.environment.MoeEnvironmentalRules;
import block_party.scene.SceneAction;
import block_party.scene.SceneVariableScope;
import block_party.scene.SceneVariableStore;
import block_party.scene.SceneVariables;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record SceneDirectiveAction(
        Operation operation,
        String name,
        String id,
        SceneVariableScope scope,
        TargetSelector selector,
        String block,
        int searchRadius,
        int verticalRadius,
        double speed,
        double arrivalRadius,
        int timeoutTicks) implements SceneAction {

    @Override
    public void apply(Moe moe) {
        if (this.operation == Operation.CLEAR) {
            moe.sceneDirective().cancel(moe);
            return;
        }
        if (this.operation == Operation.ASSIGN_LOCATION) {
            DimBlockPos location = store(moe, this.scope).locations().get(this.name);
            if (location == null) {
                location = new DimBlockPos();
            }
            moe.sceneDirective().assignLocation(this.id, location, this.name, this.scope.serializedName(),
                    speed(this.speed), radius(this.arrivalRadius), timeout(this.timeoutTicks));
            return;
        }
        if (this.operation == Operation.ASSIGN_BLOCK) {
            ResolvedBlock resolved = resolveBlock(moe, this.block, this.searchRadius, this.verticalRadius);
            moe.sceneDirective().assignBlock(this.id,
                    resolved == null ? null : new DimBlockPos(moe.level().dimension(), resolved.standing()),
                    resolved == null ? null : new DimBlockPos(moe.level().dimension(), resolved.block()),
                    this.block, speed(this.speed), radius(this.arrivalRadius), timeout(this.timeoutTicks));
            return;
        }
        TargetSelector resolvedSelector = this.selector == null ? TargetSelector.OWNER : this.selector;
        moe.sceneDirective().assignEntity(this.id, resolvedSelector.resolve(moe), resolvedSelector.serializedName(),
                speed(this.speed), radius(this.arrivalRadius), timeout(this.timeoutTicks));
    }

    private static SceneVariableStore store(Moe moe, SceneVariableScope scope) {
        SceneVariables variables = SceneVariables.get(moe.level());
        return switch (scope == null ? SceneVariableScope.NPC : scope) {
            case NPC -> variables.npc(moe.getDatabaseID());
            case PLAYER -> variables.player(SceneActionPlayers.targetPlayerUuid(moe));
            case WORLD -> variables.world();
        };
    }

    private static double speed(double value) { return value <= 0.0D ? 1.0D : value; }
    private static double radius(double value) { return value <= 0.0D ? 2.0D : value; }
    private static int timeout(int value) { return value <= 0 ? 1200 : value; }

    private static ResolvedBlock resolveBlock(Moe moe, String selector, int horizontal, int vertical) {
        if (selector == null || selector.isBlank()) return null;
        int radius = Math.max(1, Math.min(32, horizontal));
        int yRadius = Math.max(0, Math.min(16, vertical));
        BlockPos origin = moe.blockPosition();
        ResolvedBlock best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos candidate : BlockPos.betweenClosed(origin.offset(-radius, -yRadius, -radius), origin.offset(radius, yRadius, radius))) {
            BlockPos blockPos = candidate.immutable();
            if (!matches(moe.level().getBlockState(blockPos), selector)) continue;
            for (BlockPos standing : BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))) {
                BlockPos feet = standing.immutable();
                if (feet.equals(blockPos) || !MoeEnvironmentalRules.canStandAt(moe.level(), feet)) continue;
                double distance = feet.distSqr(origin);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = new ResolvedBlock(blockPos, feet);
                }
            }
        }
        return best;
    }

    private static boolean matches(BlockState state, String selector) {
        if (selector.startsWith("#")) {
            return state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse(selector.substring(1))));
        }
        Block block = BuiltInRegistries.BLOCK.getValue(ResourceLocation.parse(selector));
        return block != null && state.is(block);
    }

    private record ResolvedBlock(BlockPos block, BlockPos standing) {}

    public enum Operation { ASSIGN_LOCATION, ASSIGN_TARGET, ASSIGN_BLOCK, CLEAR }

    public enum TargetSelector {
        OWNER("owner"), DIALOGUE_PLAYER("dialogue_player"), SOCIAL_TARGET("social_target"), NEAREST_MOE("nearest_moe");

        private final String serializedName;
        TargetSelector(String serializedName) { this.serializedName = serializedName; }
        public String serializedName() { return this.serializedName; }

        public static TargetSelector fromValue(String value) {
            String normalized = value == null ? "" : value.toLowerCase(Locale.ROOT);
            for (TargetSelector selector : values()) if (selector.serializedName.equals(normalized)) return selector;
            return OWNER;
        }

        UUID resolve(Moe moe) {
            return switch (this) {
                case OWNER -> moe.getPlayerUUID();
                case DIALOGUE_PLAYER -> SceneActionPlayers.targetPlayerUuid(moe);
                case SOCIAL_TARGET -> MoeSocialContext.find(moe, 16.0D).map(context -> context.target().getUUID()).orElse(null);
                case NEAREST_MOE -> MoeSocialContext.nearby(moe, 16.0D).stream().findFirst().map(Moe::getUUID).orElse(null);
            };
        }
    }
}
