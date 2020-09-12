package moe.blocks.mod.entity.ai.automata;

import moe.blocks.mod.entity.MoeEntity;
import moe.blocks.mod.entity.partial.NPCEntity;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public abstract class BlockBasedState extends State<MoeEntity> {

    @Override
    public State start(MoeEntity entity) {
        BlockState block = entity.getBlockData();
        entity.getAttribute(Attributes.ARMOR).applyNonPersistentModifier(this.getArmorModifier(entity, block));
        entity.setScale(block.isIn(MoeTags.FULLSIZED_MOES) ? 1.0F : this.getBlockVolume(entity));
        entity.setPathPriority(PathNodeType.DAMAGE_FIRE, entity.isImmuneToFire() ? 0.0F : -1.0F);
        entity.setPathPriority(PathNodeType.DANGER_FIRE, entity.isImmuneToFire() ? 0.0F : -1.0F);
        entity.setCanFly(block.isIn(MoeTags.WINGED_MOES));
        return super.start(entity);
    }

    @Override
    public State clean(MoeEntity entity) {
        entity.getAttribute(Attributes.ARMOR).removeModifier(entity.getUniqueID());
        return super.clean(entity);
    }

    public float getBlockVolume(MoeEntity moe) {
        VoxelShape shape = moe.getBlockData().getRenderShape(moe.world, moe.getPosition());
        float dX = (float) (shape.getEnd(Direction.Axis.X) - shape.getStart(Direction.Axis.X));
        float dY = (float) (shape.getEnd(Direction.Axis.Y) - shape.getStart(Direction.Axis.Y));
        float dZ = (float) (shape.getEnd(Direction.Axis.Z) - shape.getStart(Direction.Axis.Z));
        float volume = (float) (Math.cbrt(dX * dY * dZ));
        return Float.isFinite(volume) ? Math.min(Math.max(volume, 0.25F), 1.5F) : 1.0F;
    }

    public AttributeModifier getArmorModifier(MoeEntity moe, BlockState block) {
        return new AttributeModifier(moe.getUniqueID(), "Block-based armor modifier", block.getBlockHardness(moe.world, moe.getPosition()) * 6.0F, AttributeModifier.Operation.ADDITION);
    }
}