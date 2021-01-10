package moeblocks.automata;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;

import java.util.function.Function;

public class Automaton<E extends AbstractNPCEntity> {
    protected final E applicant;
    protected final Function<E, IStateEnum<E>> generator;
    protected IState state;
    protected IStateEnum<E> key;
    private boolean isServerOnly = true;
    private boolean canUpdate = true;

    public Automaton(E applicant, Function<E, IStateEnum<E>> generator) {
        this.applicant = applicant;
        this.generator = generator;
    }

    public Automaton(E applicant, IStateEnum<E> initial) {
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
        this.fromKey(this.generator.apply(this.applicant));
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

    public boolean setNextState(IStateEnum<E> key) {
        return this.setNextState(key, false);
    }

    public boolean setNextState(IStateEnum<E> key, boolean forced) {
        if (this.state.canClear(this.applicant) || forced) {
            this.state.clear(this.applicant);
            this.fromKey(key);
            return true;
        }
        return false;
    }
    
    private boolean isBlocked() {
        return this.applicant.isRemote() && this.isServerOnly;
    }
    
    public IStateEnum<E> getKey() {
        return this.key;
    }
    
    public void fromKey(IStateEnum<E> key) {
        this.state = (this.key = key).getState(this.applicant);
        if (this.isBlocked()) { return; }
        this.state.apply(this.applicant);
    }

    public void fromKey(String key) {
        this.fromKey(this.key.fromKey(key));
    }
    
    public void render(MatrixStack stack, float partialTickTime) {
        this.state.render(this.applicant, stack, partialTickTime);
    }
    
    public void setRotationAngles(IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {
        this.state.setRotationAngles(model, this.applicant, limbSwing, limbSwingAmount, ageInTicks);
    }
}
