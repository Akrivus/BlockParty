package moeblocks.entity.ai.items;

import moeblocks.entity.MoeEntity;
import moeblocks.entity.ai.AbstractMoveToBlockGoal;
import moeblocks.init.MoeTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public class ReplaceCropsGoal extends AbstractMoveToBlockGoal<MoeEntity> {
    private static final Map<PlantType, BiPredicate<World, BlockPos>> blocksForPlant = new HashMap<>();
    
    static {
        blocksForPlant.put(PlantType.CAVE, (world, pos) -> world.getBlockState(pos).getBlock().equals(Blocks.MYCELIUM) || world.getLight(pos) < 12);
        blocksForPlant.put(PlantType.CROP, (world, pos) -> world.getBlockState(pos).getBlock().equals(Blocks.FARMLAND));
        blocksForPlant.put(PlantType.DESERT, (world, pos) -> world.getBlockState(pos).isIn(Tags.Blocks.SAND));
        blocksForPlant.put(PlantType.NETHER, (world, pos) -> world.getBlockState(pos).getBlock().equals(Blocks.SOUL_SOIL));
        blocksForPlant.put(PlantType.PLAINS, (world, pos) -> world.getBlockState(pos).isIn(BlockTags.VALID_SPAWN));
        blocksForPlant.put(PlantType.WATER, (world, pos) -> world.getFluidState(pos).getFluid().isIn(FluidTags.WATER));
    }
    
    protected IPlantable plant;
    protected ItemStack stack;
    
    public ReplaceCropsGoal(MoeEntity entity) {
        super(entity, 4, 8);
        this.timeUntilNextMove = 20;
    }
    
    @Override
    public int getPriority() {
        return 0x7;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() && this.plant != null;
    }
    
    @Override
    public void resetTask() {
        this.plant = null;
        this.stack = null;
        super.resetTask();
    }
    
    @Override
    public void onArrival() {
        if (this.world.setBlockState(this.pos.up(), this.plant.getPlant(this.world, this.pos))) {
            this.entity.swingArm(Hand.MAIN_HAND);
            this.stack.shrink(1);
        }
    }
    
    @Override
    public boolean canMoveTo(BlockPos pos, BlockState state) {
        if (!this.world.isAirBlock(pos.up())) { return false; }
        Inventory inventory = this.entity.getBrassiere();
        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            Item item = stack.getItem();
            if (item.isIn(MoeTags.SEEDS)) {
                this.plant = this.getPlant(item);
                this.stack = stack;
                BiPredicate<World, BlockPos> function = blocksForPlant.get(this.plant.getPlantType(this.world, pos));
                if (function != null && function.test(this.world, pos)) { return true; }
            }
        }
        return false;
    }
    
    public IPlantable getPlant(Item item) {
        Block block = Block.getBlockFromItem(item);
        if (block instanceof IPlantable) { return (IPlantable) block; }
        return (world, pos) -> Blocks.WHEAT.getDefaultState();
    }
}
