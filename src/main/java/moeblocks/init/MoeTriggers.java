package moeblocks.init;

import moeblocks.automata.IStateEnum;
import moeblocks.automata.Trigger;
import moeblocks.automata.state.enums.*;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.util.Trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MoeTriggers {
    private static final Map<Class<? extends IStateEnum>, List<Trigger>> REGISTRY = new HashMap<>();

    public static void registerTriggers() {
        register(0, Animation.DEFAULT, (npc) -> true);
        register(0, BlockDataState.DEFAULT, (moe) -> true);
        register(0, BloodType.O, (npc) -> true);
        register(0, CupSize.A, (moe) -> true);
        register(0, Dere.NYANDERE, (npc) -> true);
        register(0, Emotion.NORMAL, (npc) -> true);
        register(0, Gender.FEMININE, (npc) -> true);
        register(0, HealthState.PERFECT, (npc) -> true);
        register(0, HeldItemState.DEFAULT, (npc) -> true);
        register(0, HungerState.SATISFIED, (npc) -> true);
        register(0, LoveState.ACQUAINTED, (npc) -> true);
        register(0, Mood.WANDER, (npc) -> true);
        register(0, MoonPhase.FULL, (npc) -> true);
        register(0, PeriodOfTime.ATTACHED, (npc) -> true);
        register(0, RibbonColor.NONE, (npc) -> true);
        register(0, StoryPhase.INTRODUCTION, (npc) -> true);
        register(0, StressState.RELAXED, (npc) -> true);
        register(0, TimeOfDay.MORNING, (npc) -> true);
        Animation.registerTriggers();
        CupSize.registerTriggers();
        Emotion.registerTriggers();
        Mood.registerTriggers();
    }

    public static Map<Class<? extends IStateEnum>, List<Trigger>> all() {
        return REGISTRY;
    }

    public static <E extends AbstractNPCEntity> void register(int priority, IStateEnum state, Predicate<E> function) {
        List<Trigger> triggers = getTriggersFor(state);
        triggers.add(new Trigger(priority, state, function));
        REGISTRY.put(state.getClass(), triggers);
    }

    protected static List<Trigger> getTriggersFor(IStateEnum state) {
        return REGISTRY.getOrDefault(state.getClass(), new ArrayList<>());
    }
}
