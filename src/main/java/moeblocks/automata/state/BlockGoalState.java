package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.state.enums.BlockDataState;
import moeblocks.automata.state.goal.AbstractStateGoal;
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

public class BlockGoalState<O extends BlockDataState> extends GoalState<O, MoeEntity> {
    public BlockGoalState(O filter, BiConsumer<MoeEntity, List<AbstractStateGoal>> generator) {
        super(filter, generator);
    }
    
    @Override
    public void apply(MoeEntity applicant) {
        this.setBuffs(applicant, applicant.getBlockData());
        super.apply(applicant);
    }
    
    @Override
    public void clear(MoeEntity applicant) {
        this.deBuff(applicant);
        super.clear(applicant);
    }
    
    private void deBuff(MoeEntity moe) {
        moe.getAttribute(Attributes.ARMOR).removeModifier(moe.getUniqueID());
    }
    
    private void setBuffs(MoeEntity moe, BlockState block) {
        moe.getAttribute(Attributes.ARMOR).applyNonPersistentModifier(this.getArmorModifier(moe, block));
        moe.setScale(block.isIn(MoeTags.FULLSIZED) ? 1.0F : this.getBlockVolume(moe));
        moe.setPathPriority(PathNodeType.DAMAGE_FIRE, moe.isImmuneToFire() ? 0.0F : -1.0F);
        moe.setPathPriority(PathNodeType.DANGER_FIRE, moe.isImmuneToFire() ? 0.0F : -1.0F);
        moe.setCanFly(block.isIn(MoeTags.WINGED));
    }
    
    private AttributeModifier getArmorModifier(MoeEntity applicant, BlockState block) {
        return new AttributeModifier(applicant.getUniqueID(), "Block-based armor modifier", block.getBlockHardness(applicant.world, applicant.getPosition()) * 6.0F, AttributeModifier.Operation.ADDITION);
    }
    
    private float getBlockVolume(MoeEntity applicant) {
        VoxelShape shape = applicant.getBlockData().getRenderShape(applicant.world, applicant.getPosition());
        float dX = (float) (shape.getEnd(Direction.Axis.X) - shape.getStart(Direction.Axis.X));
        float dY = (float) (shape.getEnd(Direction.Axis.Y) - shape.getStart(Direction.Axis.Y));
        float dZ = (float) (shape.getEnd(Direction.Axis.Z) - shape.getStart(Direction.Axis.Z));
        float volume = (float) (Math.cbrt(dX * dY * dZ));
        return Float.isFinite(volume) ? Math.min(Math.max(volume, 0.25F), 1.5F) : 1.0F;
    }
}
