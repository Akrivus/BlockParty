package mod.moeblocks.item;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.register.EntityTypesMoe;
import mod.moeblocks.register.ItemsMoe;
import mod.moeblocks.register.TagsMoe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SpawnEggItem extends Item {
    protected final SpawnTypes type;
    protected final Deres dere;

    public SpawnEggItem(SpawnTypes type, Deres dere) {
        super(new Properties().group(ItemsMoe.Group.INSTANCE));
        this.type = type;
        this.dere = dere;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        if (!world.isRemote()) {
            BlockState state = world.getBlockState(pos);
            TileEntity extra = state.getBlock().isIn(TagsMoe.MOEABLES) ? world.getTileEntity(pos) : null;
            pos = pos.offset(context.getFace());
            StateEntity entity = this.type.get(world);
            entity.setPositionAndRotation(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, -player.rotationYaw, -player.rotationPitch);
            if (entity instanceof MoeEntity) {
                MoeEntity moe = (MoeEntity) entity;
                moe.setBlockData(state.getBlock().isIn(TagsMoe.MOEABLES) ? state : Blocks.AIR.getDefaultState());
                moe.setExtraBlockData(extra != null ? extra.getTileData() : new CompoundNBT());
            }
            entity.setDere(this.dere);
            entity.getRelationships().get(player).addTrust(100);
            if (world.addEntity(entity)) {
                entity.onInitialSpawn((ServerWorld) world, world.getDifficultyForLocation(pos), SpawnReason.SPAWN_EGG, null, null);
                context.getItem().shrink(1);
            }
        }
        return ActionResultType.SUCCESS;
    }

    public enum SpawnTypes {
        MOE, SENPAI;

        public StateEntity get(World world) {
            switch (this) {
            case SENPAI:
                return EntityTypesMoe.SENPAI.get().create(world);
            default:
                return EntityTypesMoe.MOE.get().create(world);
            }
        }
    }
}
