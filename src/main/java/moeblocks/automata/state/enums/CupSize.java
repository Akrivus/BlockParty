package moeblocks.automata.state.enums;


import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.WatchedGoalState;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.MoeEntity;
import moeblocks.init.MoeTags;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;

import java.util.List;
import java.util.function.BiConsumer;

public enum CupSize implements IStateEnum<MoeEntity> {
    A((moe, goals) -> {

    }),
    B((moe, goals) -> {

    }),
    C((moe, goals) -> {

    }),
    D((moe, goals) -> {

    });

    private final BiConsumer<MoeEntity, List<AbstractStateGoal>> generator;

    CupSize(BiConsumer<MoeEntity, List<AbstractStateGoal>> generator) {
        this.when(0, (npc) -> this.equals(npc.getCupSize()));
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
        CupSize.A.when(2, (moe) -> moe.is(Gender.MASCULINE));
        CupSize.A.when(2, (moe) -> moe.getAgeInYears() < 17);
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.ANVIL));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.BEACON));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.BLAST_FURNACE));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.BREWING_STAND));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.CAULDRON));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.CHIPPED_ANVIL));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.COMPOSTER));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.CONDUIT));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.CRAFTING_TABLE));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.DAMAGED_ANVIL));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.ENCHANTING_TABLE));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.FLETCHING_TABLE));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.FURNACE));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.LECTERN));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.SMITHING_TABLE));
        CupSize.B.when(1, (moe) -> moe.isBlock(Blocks.SMOKER));
        CupSize.C.when(1, (moe) -> moe.isBlock(Blocks.BASALT));
        CupSize.C.when(1, (moe) -> moe.isBlock(Blocks.HOPPER));
        CupSize.C.when(1, (moe) -> moe.isBlock(Blocks.NETHER_BRICKS));
        CupSize.C.when(1, (moe) -> moe.isBlock(Blocks.RED_NETHER_BRICKS));
        CupSize.D.when(1, (moe) -> moe.isBlock(BlockTags.SHULKER_BOXES));
        CupSize.D.when(1, (moe) -> moe.isBlock(MoeTags.Blocks.CHESTS));
    }
}