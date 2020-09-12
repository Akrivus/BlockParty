package moe.blocks.mod.entity.ai.automata.state;

import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.partial.InteractiveEntity;
import moe.blocks.mod.entity.partial.NPCEntity;

import java.util.List;

public enum Emotions {
    ANGRY(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    BEGGING(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    CONFUSED(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    CRYING(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    MISCHIEVOUS(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    EMBARRASSED(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    HAPPY(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    NORMAL(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    PAINED(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    PSYCHOTIC(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    SCARED(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    SMITTEN(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    }),
    TIRED(new State<InteractiveEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractiveEntity entity) {

        }
    });

    public final State state;

    Emotions(State state) {
        this.state = state;
    }
}
