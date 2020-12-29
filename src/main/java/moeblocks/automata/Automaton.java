package moeblocks.automata;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.animation.AnimationState;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;

public class Automaton<E extends AbstractNPCEntity, O extends IStateEnum> {
    protected final E applicant;
    protected final O natural;
    protected IState state;
    protected O key;
    private boolean hasNoDefault = true;
    private boolean isServerOnly = true;
    private boolean canUpdate = true;
    
    public Automaton(E applicant, O natural) {
        this.applicant = applicant;
        this.natural = natural;
    }
    
    public Automaton setCanRunOnClient() {
        this.isServerOnly = false;
        return this;
    }
    
    public Automaton setHasDefault() {
        this.hasNoDefault = false;
        return this;
    }
    
    public Automaton setCanUpdate(boolean updatable) {
        this.canUpdate = updatable;
        return this;
    }
    
    public Automaton start() {
        this.state = this.natural.getState(this.applicant);
        this.key = this.natural;
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
        return this.setNextState(this.getNatural());
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
    
    public O getNatural() {
        if (!this.hasNoDefault) { return this.natural; }
        for (IStateEnum key : this.natural.getKeys()) {
            if (key.getState(this.applicant).canApply(this.applicant)) { return (O) key; }
        }
        return this.natural;
    }
    
    private boolean isBlocked() {
        return this.applicant.isRemote() && this.isServerOnly;
    }
    
    public boolean isNatural() {
        return this.key.equals(this.natural);
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
