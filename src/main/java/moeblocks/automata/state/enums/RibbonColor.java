package moeblocks.automata.state.enums;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;

public enum RibbonColor implements IStateEnum<AbstractNPCEntity> {
    BLACK((npc, goals) -> {

    }),
    RED((npc, goals) -> {

    }),
    GREEN((npc, goals) -> {

    }),
    BROWN((npc, goals) -> {

    }),
    BLUE((npc, goals) -> {

    }),
    PURPLE((npc, goals) -> {

    }),
    CYAN((npc, goals) -> {

    }),
    LIGHT_GRAY((npc, goals) -> {

    }),
    GRAY((npc, goals) -> {

    }),
    PINK((npc, goals) -> {

    }),
    LIME((npc, goals) -> {

    }),
    YELLOW((npc, goals) -> {

    }),
    LIGHT_BLUE((npc, goals) -> {

    }),
    MAGENTA((npc, goals) -> {

    }),
    ORANGE((npc, goals) -> {

    }),
    WHITE((npc, goals) -> {

    }),
    NONE((npc, goals) -> {

    });

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    RibbonColor(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator) {
        this.generator = generator;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new GoalState(this, this.generator);
    }

    @Override
    public String toKey() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromKey(String key) {
        return MoonPhase.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return MoonPhase.values();
    }

    public static RibbonColor get(String key) {
        try { return RibbonColor.valueOf(key); } catch (IllegalArgumentException e) {
            return RibbonColor.NONE;
        }
    }

    public static void registerTriggers() {

    }
}
