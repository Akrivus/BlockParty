package moe.blocks.mod.data.conversation;

import moe.blocks.mod.entity.ai.automata.ReactiveState;
import moe.blocks.mod.entity.ai.goal.ReactiveGoal;
import moe.blocks.mod.entity.ai.goal.react.FlapArmsGoal;
import moe.blocks.mod.entity.partial.CharacterEntity;

public enum Reactions {
    NONE(new ReactiveState() {
        @Override
        public ReactiveGoal getGoal(CharacterEntity entity) {
            return null;
        }
    }),
    FLAP_ARMS(new ReactiveState() {
        @Override
        public ReactiveGoal getGoal(CharacterEntity entity) {
            return new FlapArmsGoal(entity);
        }
    });

    public final ReactiveState state;

    Reactions(ReactiveState state) {
        this.state = state;
    }
}
