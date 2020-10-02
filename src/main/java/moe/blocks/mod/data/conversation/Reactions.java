package moe.blocks.mod.data.conversation;

import moe.blocks.mod.entity.AbstractNPCEntity;
import moe.blocks.mod.entity.ai.automata.ReactiveState;
import moe.blocks.mod.entity.ai.goal.ReactiveGoal;
import moe.blocks.mod.entity.ai.goal.react.FlapArmsGoal;

public enum Reactions {
    NONE(new ReactiveState() {
        @Override
        public ReactiveGoal getGoal(AbstractNPCEntity entity) {
            return null;
        }
    }),
    FLAP_ARMS(new ReactiveState() {
        @Override
        public ReactiveGoal getGoal(AbstractNPCEntity entity) {
            return new FlapArmsGoal(entity);
        }
    });

    public final ReactiveState state;

    Reactions(ReactiveState state) {
        this.state = state;
    }
}
