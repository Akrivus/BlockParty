package moeblocks.automata.state.enums;


import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.Trigger;
import moeblocks.automata.state.WatchedGoalState;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.MoeEntity;
import moeblocks.init.MoeTags;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum CupSize implements IStateEnum<MoeEntity> {
    A((moe, goals) -> {

    }, 0.00F, 0.25F),
    B((moe, goals) -> {

    }, 0.25F, 0.50F),
    C((moe, goals) -> {

    }, 0.50F, 0.75F),
    D((moe, goals) -> {

    }, 0.75F, 1.00F);

    private final BiConsumer<MoeEntity, List<AbstractStateGoal>> generator;

    CupSize(BiConsumer<MoeEntity, List<AbstractStateGoal>> generator, float start, float end) {
        this.when(1, (moe) -> Trigger.isBetween(moe.getInventoryCapacity(), start, end));
        this.generator = generator;
    }

    @Override
    public IState getState(MoeEntity applicant) {
        return new WatchedGoalState(this, this.generator, MoeEntity.CUP_SIZE);
    }

    @Override
    public String toKey() {
        return this.name();
    }

    @Override
    public IStateEnum<MoeEntity> fromKey(String key) {
        return CupSize.get(key);
    }

    @Override
    public IStateEnum<MoeEntity>[] getKeys() {
        return CupSize.values();
    }

    public static CupSize get(String key) {
        try { return CupSize.valueOf(key); } catch (IllegalArgumentException e) {
            return CupSize.A;
        }
    }

    public static void registerTriggers() {
        CupSize.D.when(2, (moe) -> moe.isBlock(BlockTags.SHULKER_BOXES));
        CupSize.D.when(2, (moe) -> moe.isBlock(Blocks.BARREL));
        CupSize.D.when(2, (moe) -> moe.isBlock(MoeTags.Blocks.CHESTS));
        CupSize.A.when(5, (moe) -> moe.is(Gender.MASCULINE));
    }
}