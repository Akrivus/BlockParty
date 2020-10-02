package moe.blocks.mod.entity.ai.automata.state;

import moe.blocks.mod.entity.MoeEntity;
import moe.blocks.mod.entity.ai.automata.BlockBasedState;
import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.ai.goal.TryStuffItemGoal;
import moe.blocks.mod.entity.ai.goal.blocks.OpenChestGoal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import java.util.HashMap;
import java.util.List;

public enum BlockStates {
    DEFAULT(new BlockBasedState(MoeEntity.CupSizes.B) {
        @Override
        public void apply(List<IStateGoal> goals, MoeEntity entity) {

        }
    }),
    CHEST(new BlockBasedState(MoeEntity.CupSizes.DD) {
        @Override
        public void apply(List<IStateGoal> goals, MoeEntity entity) {
            goals.add(new OpenChestGoal(entity));
        }
    }),
    TRAPPED_CHEST(new BlockBasedState(MoeEntity.CupSizes.D) {
        @Override
        public void apply(List<IStateGoal> goals, MoeEntity entity) {
            goals.add(new OpenChestGoal(entity));
        }
    }),
    HOPPER(new BlockBasedState(MoeEntity.CupSizes.C) {
        @Override
        public void apply(List<IStateGoal> goals, MoeEntity entity) {
            goals.add(new TryStuffItemGoal<>(entity, stack -> true));
            goals.add(new OpenChestGoal(entity));
        }
    });

    public final State state;

    BlockStates(State state, Block... blocks) {
        this.state = state;
        for (Block block : blocks) { Registry.SET.put(block, this); }
    }

    public static BlockStates get(BlockState state) {
        return Registry.SET.getOrDefault(state.getBlock(), DEFAULT);
    }

    protected static class Registry {
        public static HashMap<Block, BlockStates> SET = new HashMap<>();
    }
}
