package moeblocks.automata;

import moeblocks.automata.state.BlockDataState;
import moeblocks.entity.MoeEntity;
import moeblocks.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.List;
import java.util.function.BiConsumer;

public class BlockBasedGoalState<O extends BlockDataState> extends GoalState<O, MoeEntity> {
    public BlockBasedGoalState(O filter, BiConsumer<MoeEntity, List<IStateGoal>> generator) {
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
