package moeblocks.automata;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.animation.AnimationState;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;

import java.util.function.Function;

public class Automaton<E extends AbstractNPCEntity, O extends IStateEnum> {
    protected final E applicant;
    protected final Function<E, O> generator;
    protected IState state;
    protected O key;
    private boolean isServerOnly = true;
    private boolean canUpdate = true;

    public Automaton(E applicant, Function<E, O> generator) {
        this.applicant = applicant;
        this.generator = generator;
    }

    public Automaton(E applicant, O initial) {
        this(applicant, (npc) -> initial);
    }
    
    public Automaton setCanRunOnClient() {
        this.isServerOnly = false;
        return this;
    }
    
    public Automaton setCanUpdate(boolean updatable) {
        this.canUpdate = updatable;
        return this;
    }
    
    public Automaton start() {
        this.key = this.generator.apply(this.applicant);
        this.state = this.key.getState(this.applicant);
        return this;
    }
    
    public boolean update() {
        if (this.isBlocked()) { return false; }
        if (this.state.canClear(this.applicant)) {
            return this.setNextState();
        } else if (this.canUpdate) {
            this.state.tick(this.applicant);
        }
        return this.canUpdate;
    }
    
    public boolean setNextState() {
        return this.setNextState(this.generator.apply(this.applicant));
    }
    
    public boolean setNextState(O key) {
        IState state = key.getState(this.applicant);
        if (state.canApply(this.applicant)) {
            this.state.clear(this.applicant);
            this.state = state;
            this.state.apply(this.applicant);
            this.key = key;
            return true;
        }
        return false;
    }
    
    private boolean isBlocked() {
        return this.applicant.isRemote() && this.isServerOnly;
    }
    
    public O getKey() {
        return this.key;
    }
    
    public void fromKey(String key) {
        this.key = (O) this.key.fromKey(key);
        this.state = this.key.getState(this.applicant);
        this.state.apply(this.applicant);
    }
    
    public void render(MatrixStack stack, float partialTickTime) {
        this.state.render(this.applicant, stack, partialTickTime);
    }
    
    public void setRotationAngles(IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {
        if (this.state instanceof AnimationState) {
            ((AnimationState) (this.state)).setRotationAngles(model, this.applicant, limbSwing, limbSwingAmount, ageInTicks);
        }
    }
}
