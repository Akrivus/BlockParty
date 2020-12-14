package moeblocks.automata;

import moeblocks.automata.state.BlockStates;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.MoeEntity;
import moeblocks.entity.ai.goal.target.AbstractStateTarget;
import moeblocks.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class GoalState<O extends IStateEnum<E>,  E extends AbstractNPCEntity> implements IState<E> {
    public final O filter;
    public final BiConsumer<E, List<IStateGoal>> generator;
    private List<IStateGoal> goals = new ArrayList<>();

    public GoalState(O filter, BiConsumer<E, List<IStateGoal>> generator) {
        this.filter = filter;
        this.generator = generator;
    }

    @Override
    public boolean canApply(E applicant) {
        return !applicant.getState(this.filter.getClass()).equals(this.filter);
    }

    @Override
    public boolean canClear(E applicant) {
        return !this.canApply(applicant);
    }

    @Override
    public void apply(E applicant) {
        this.generator.accept(applicant, this.goals);
        this.goals.forEach(goal -> this.getSelector(applicant, goal).addGoal(goal.getPriority(), (Goal) goal));
    }

    @Override
    public void clear(E applicant) {
        this.goals.forEach(goal -> this.getSelector(applicant, goal).removeGoal((Goal) goal));
    }

    private GoalSelector getSelector(E applicant, IStateGoal goal) {
        if (goal instanceof AbstractStateTarget) { return applicant.targetSelector; }
        return applicant.goalSelector;
    }

    public static class BlockBased<O extends BlockStates> extends GoalState<O, MoeEntity> {
        public BlockBased(O filter, BiConsumer<MoeEntity, List<IStateGoal>> generator) {
            super(filter, generator);
        }

        @Override
        public void apply(MoeEntity applicant) {
            BlockState block = applicant.getBlockData();
            applicant.getAttribute(Attributes.ARMOR).applyNonPersistentModifier(this.getArmorModifier(applicant, block));
            applicant.setScale(block.isIn(MoeTags.FULLSIZED) ? 1.0F : this.getBlockVolume(applicant));
            applicant.setPathPriority(PathNodeType.DAMAGE_FIRE, applicant.isImmuneToFire() ? 0.0F : -1.0F);
            applicant.setPathPriority(PathNodeType.DANGER_FIRE, applicant.isImmuneToFire() ? 0.0F : -1.0F);
            applicant.setCanFly(block.isIn(MoeTags.WINGED));
            super.apply(applicant);
        }

        @Override
        public void clear(MoeEntity applicant) {
            applicant.getAttribute(Attributes.ARMOR).removeModifier(applicant.getUniqueID());
            super.clear(applicant);
        }

        public float getBlockVolume(MoeEntity applicant) {
            VoxelShape shape = applicant.getBlockData().getRenderShape(applicant.world, applicant.getPosition());
            float dX = (float) (shape.getEnd(Direction.Axis.X) - shape.getStart(Direction.Axis.X));
            float dY = (float) (shape.getEnd(Direction.Axis.Y) - shape.getStart(Direction.Axis.Y));
            float dZ = (float) (shape.getEnd(Direction.Axis.Z) - shape.getStart(Direction.Axis.Z));
            float volume = (float) (Math.cbrt(dX * dY * dZ));
            return Float.isFinite(volume) ? Math.min(Math.max(volume, 0.25F), 1.5F) : 1.0F;
        }

        public AttributeModifier getArmorModifier(MoeEntity applicant, BlockState block) {
            return new AttributeModifier(applicant.getUniqueID(), "Block-based armor modifier", block.getBlockHardness(applicant.world, applicant.getPosition()) * 6.0F, AttributeModifier.Operation.ADDITION);
        }
    }

    public static class ValueBased<O extends IStateEnum<E>,  E extends AbstractNPCEntity> extends GoalState<O, E> {
        public final Function<E, Number> valuator;
        public final float start;
        public final float end;

        public ValueBased(O filter, BiConsumer<E, List<IStateGoal>> generator, Function<E, Number> valuator, float start, float end) {
            super(filter, generator);
            this.valuator = valuator;
            this.start = start; this.end = end;
        }

        @Override
        public boolean canApply(E applicant) {
            float value = (float) this.valuator.apply(applicant);
            return this.start <= value && value <= this.end;
        }
    }
}
