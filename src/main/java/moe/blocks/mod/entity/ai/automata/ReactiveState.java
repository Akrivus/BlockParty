package moe.blocks.mod.entity.ai.automata;

import moe.blocks.mod.entity.ai.goal.ReactiveGoal;
import moe.blocks.mod.entity.partial.CharacterEntity;

import java.util.List;

public abstract class ReactiveState extends State<CharacterEntity> {
    @Override
    public void apply(List<IStateGoal> goals, CharacterEntity entity) {
        ReactiveGoal goal = this.getGoal(entity);
        if (goal != null) { goals.add(goal); }
    }

    public abstract ReactiveGoal getGoal(CharacterEntity entity);
}
