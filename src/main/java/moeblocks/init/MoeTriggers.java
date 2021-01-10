package moeblocks.init;

import moeblocks.automata.IStateEnum;
import moeblocks.automata.Trigger;
import moeblocks.automata.state.enums.*;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.block.Blocks;

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
        register(0, MoonPhase.FULL, (npc) -> true);
        register(0, PeriodOfTime.ATTACHED, (npc) -> true);
        register(0, StoryPhase.INTRODUCTION, (npc) -> true);
        register(0, StressState.RELAXED, (npc) -> true);
        register(0, TimeOfDay.MORNING, (npc) -> true);
        CupSize.D.when(1, (moe) -> moe.isBlock(Blocks.BARREL));
    }

    public static List<Trigger> registry(IStateEnum state) {
        List<Trigger> triggers = REGISTRY.getOrDefault(state.getClass(), new ArrayList<>());
        triggers.sort((one, two) -> -Integer.compare(one.getPriority(), two.getPriority()));
        return triggers;
    }

    public static <E extends AbstractNPCEntity> void register(int priority, IStateEnum state, Predicate<E> function) {
        List<Trigger> triggers = registry(state);
        triggers.add(new Trigger(priority, state, function));
        REGISTRY.put(state.getClass(), triggers);
    }

    public static Trigger find(IStateEnum state, AbstractNPCEntity applicant) {
        List<Trigger> triggers = registry(state).stream().filter((trigger) -> trigger.fire(applicant)).collect(Collectors.toList());
        if (triggers.isEmpty()) { return new Trigger(0, applicant.getState(state), (npc) -> true); }
        return triggers.get(0);
    }
}
