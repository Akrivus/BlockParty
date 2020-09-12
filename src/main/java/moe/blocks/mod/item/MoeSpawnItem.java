package moe.blocks.mod.item;

import moe.blocks.mod.entity.MoeEntity;
import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.partial.InteractiveEntity;
import moe.blocks.mod.init.MoeEntities;
import moe.blocks.mod.init.MoeItems;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class MoeSpawnItem extends Item {

    public MoeSpawnItem() {
        super(new Properties().group(MoeItems.Group.INSTANCE));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (world.isRemote()) { return ActionResultType.PASS; }
        PlayerEntity player = context.getPlayer();
        MoeEntity moe = MoeEntities.MOE.get().create(world);
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock().isIn(MoeTags.MOEABLES)) {
            if (state.hasTileEntity()) { moe.setExtraBlockData(world.getTileEntity(pos).getTileData()); }
            moe.setBlockData(state);
            pos = pos.offset(context.getFace());
            moe.setPositionAndRotation(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, player.rotationYaw, -player.rotationPitch);
            if (world.addEntity(moe)) {
                moe.onInitialSpawn((ServerWorld) world, world.getDifficultyForLocation(pos), SpawnReason.SPAWN_EGG, null, null);
                moe.setDere(Deres.values()[world.rand.nextInt(Deres.values().length)]);
                context.getItem().shrink(1);
                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.FAIL;
    }
}