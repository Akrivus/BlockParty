package moe.blocks.mod.entity.ai.automata.state;

import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.init.MoeSounds;
import net.minecraft.util.SoundEvent;

import java.util.List;

public enum Emotions {
    ANGRY(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_YELL.get(), 100),
    BEGGING(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_MOAN.get(), 101),
    CONFUSED(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_NO.get(), 102),
    CRYING(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_SOB.get(), 103),
    MISCHIEVOUS(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_LAUGH.get(), 104),
    EMBARRASSED(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_HURT.get(), 105),
    HAPPY(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_LAUGH.get(), 106),
    NORMAL(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_AMBIENT.get(), 107),
    PAINED(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_HURT.get(), 108),
    PSYCHOTIC(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_SCREAM.get(), 109),
    SCARED(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_SCREAM.get(), 110),
    SMITTEN(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_MOAN.get(), 111),
    TIRED(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {

        }
    }, MoeSounds.MOE_YAWN.get(), 112);

    public final State state;
    public final SoundEvent sound;
    public final int id;

    Emotions(State state, SoundEvent sound, int id) {
        this.state = state;
        this.sound = sound;
        this.id = id;
    }
}
