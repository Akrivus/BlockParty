package moe.blocks.mod.entity.ai.automata;

import moe.blocks.mod.entity.partial.NPCEntity;

import java.util.List;

public class BlankState<E extends NPCEntity> extends State<E> {
    @Override
    public void apply(List<IStateGoal> goals, E entity) {

    }

    @Override
    public void reset(E entity) {

    }
}
