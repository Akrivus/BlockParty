package mod.moeblocks.item;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.init.MoeEntities;
import mod.moeblocks.init.MoeItems;
import mod.moeblocks.init.MoeTags;
import mod.moeblocks.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SpawnEggItem extends Item {
    protected final SpawnTypes type;
    protected final Deres dere;

    public SpawnEggItem(SpawnTypes type, Deres dere) {
        super(new Properties().group(MoeItems.Group.INSTANCE));
        this.type = type;
        this.dere = dere;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        if (!world.isRemote()) {
            BlockPos pos = context.getPos().offset(context.getFace());
            StateEntity entity = this.type.get(world);
            entity.setPositionAndRotation(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, -player.rotationYaw, -player.rotationPitch);
            if (entity instanceof MoeEntity) {
                BlockPos block = context.getPos();
                BlockState state = world.getBlockState(block);
                if (state.getBlock().isIn(MoeTags.BLOCKS)) {
                    MoeEntity moe = (MoeEntity) entity;
                    moe.setBlockData(state);
                    if (state.hasTileEntity()) {
                        moe.setExtraBlockData(world.getTileEntity(block).getTileData());
                    }
                } else {
                    return PlayerUtils.showResult(player, entity, "command.moeblocks.spawn_egg", ActionResultType.FAIL);
                }
            }
            if (world.addEntity(entity)) {
                entity.onInitialSpawn((ServerWorld) world, world.getDifficultyForLocation(pos), SpawnReason.SPAWN_EGG, null, null);
                entity.getRelationships().get(player).addTrust(100);
                entity.setDere(this.dere);
                context.getItem().shrink(1);
                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.PASS;
    }

    public enum SpawnTypes {
        MOE, SENPAI;

        public StateEntity get(World world) {
            switch (this) {
            case SENPAI:
                return MoeEntities.SENPAI.get().create(world);
            default:
                return MoeEntities.MOE.get().create(world);
            }
        }
    }
}
