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

public class MoeTriggers {
    private static final Map<Class<? extends IStateEnum>, List<Trigger>> REGISTRY = new HashMap<>();
    private static final Predicate<AbstractNPCEntity> TRUE = (npc) -> true;

    public static void registerTriggers() {
        register(Animation.DEFAULT);
        register(BlockDataState.DEFAULT);
        register(BloodType.O);
        register(CupSize.A);
        register(Dere.NYANDERE);
        register(Emotion.NORMAL);
        register(Gender.FEMININE);
        register(HealthState.PERFECT);
        register(HeldItemState.DEFAULT);
        register(HungerState.SATISFIED);
        register(LoveState.ACQUAINTED);
        register(MoonPhase.FULL);
        register(PeriodOfTime.ATTACHED);
        register(StoryPhase.INTRODUCTION);
        register(StressState.RELAXED);
        register(TimeOfDay.MORNING);
    }

    public static List<Trigger> register(IStateEnum state) {
        return REGISTRY.getOrDefault(state.getClass(), new ArrayList<>());
    }

    public static <E extends AbstractNPCEntity> void register(IStateEnum state, Predicate<E> function) {
        List<Trigger> triggers = register(state);
        triggers.add(new Trigger(state, function));
        REGISTRY.put(state.getClass(), triggers);
    }

    public static Trigger find(IStateEnum state, AbstractNPCEntity npc) {
        for (Trigger trigger : register(state)) {
            if (trigger.fire(npc)) { return trigger; }
        }
        return new Trigger(state, TRUE);
    }

    static {
        CupSize.D.when((moe) -> moe.getBlockData().isIn(Blocks.BARREL));
    }
}
