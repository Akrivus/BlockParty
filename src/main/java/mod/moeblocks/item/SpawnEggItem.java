package mod.moeblocks.item;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.register.BlocksMoe;
import mod.moeblocks.register.EntityTypesMoe;
import mod.moeblocks.register.ItemsMoe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnEggItem extends Item {
    protected final Deres dere;

    public SpawnEggItem(Deres dere) {
        super(new Properties().group(ItemsMoe.Group.INSTANCE));
        this.dere = dere;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        if (!world.isRemote()) {
            BlockState state = world.getBlockState(pos);
            TileEntity extra = state.getBlock().isIn(BlocksMoe.Tags.MOEABLES) ? world.getTileEntity(pos) : null;
            pos = pos.offset(context.getFace());
            MoeEntity moe = EntityTypesMoe.MOE.get().create(world);
            moe.setPositionAndRotation(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, -player.rotationYaw, -player.rotationPitch);
            moe.setBlockData(state.getBlock().isIn(BlocksMoe.Tags.MOEABLES) ? state : Blocks.AIR.getDefaultState());
            moe.setExtraBlockData(extra != null ? extra.getTileData() : new CompoundNBT());
            moe.setDere(this.dere);
            moe.getRelationships().get(player).addTrust(100);
            if (world.addEntity(moe)) {
                context.getItem().shrink(1);
            }
        }
        return ActionResultType.SUCCESS;
    }
}
