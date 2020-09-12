package moe.blocks.mod.entity.ai.automata.state;

import moe.blocks.mod.entity.MoeEntity;
import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.partial.DieEntity;
import moe.blocks.mod.entity.partial.InteractiveEntity;
import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.block.material.MaterialColor;

import java.util.HashMap;
import java.util.List;

public enum Deres {
    HIMEDERE(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }, DieEntity.Face.ONE),
    KUUDERE(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }, DieEntity.Face.TWO),
    TSUNDERE(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }, DieEntity.Face.THREE),
    YANDERE(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }, DieEntity.Face.FOUR),
    DEREDERE(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }, DieEntity.Face.FIVE),
    DANDERE(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }, DieEntity.Face.SIX);

    public final State state;

    Deres(State state, DieEntity.Face face) {
        this.state = state;
        Registry.SET.put(face, this);
    }

    public static Deres get(DieEntity.Face face) {
        return Registry.SET.getOrDefault(face, HIMEDERE);
    }

    public static MaterialColor getAura(Deres dere) {
        switch (dere) {
        case HIMEDERE:
            return MaterialColor.PURPLE;
        case KUUDERE:
            return MaterialColor.BLUE;
        case TSUNDERE:
            return MaterialColor.ADOBE;
        case YANDERE:
            return MaterialColor.RED;
        case DEREDERE:
            return MaterialColor.GREEN;
        default:
            return MaterialColor.YELLOW;
        }
    }

    protected static class Registry {
        public static HashMap<DieEntity.Face, Deres> SET = new HashMap<>();
    }
}