package moe.blocks.mod.entity.ai.automata.state;

import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.partial.DieEntity;
import moe.blocks.mod.entity.partial.InteractEntity;
import moe.blocks.mod.util.Trans;
import net.minecraft.block.material.MaterialColor;

import java.util.HashMap;
import java.util.List;

public enum Deres {
    HIMEDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.ONE),
    KUUDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.TWO),
    TSUNDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.THREE),
    YANDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.FOUR),
    DEREDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.FIVE),
    DANDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.SIX);

    public final State state;

    Deres(State state, DieEntity.Face face) {
        this.state = state;
        Registry.SET.put(face, this);
    }

    @Override
    public String toString() {
        return Trans.late(String.format("debug.moeblocks.deres.%s", this.name().toLowerCase()));
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
