package moeblocks.automata;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.animation.AnimationState;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;

public class Automaton<E extends AbstractNPCEntity, O extends IStateEnum> {
    protected final E applicant;
    protected final O natural;
    protected final int timeout;
    protected int timeUntilCheck;
    protected IState state;
    protected O token;
    private boolean isServerOnly = true;
    private boolean canUpdate = true;

    public Automaton(E applicant, O natural) {
        this(applicant, natural, 1000);
    }

    public Automaton(E applicant, O natural, int timeout) {
        this.applicant = applicant;
        this.natural = natural;
        this.timeout = timeout;
        this.setFirstState(natural);
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
        this.setFirstState(this.natural);
        return this;
    }

    private void setFirstState(O token) {
        this.state = token.getState(this.applicant);
        this.token = token;
        if (this.isBlocked()) { return; }
        this.state.apply(this.applicant);
    }

    public void update() {
        if (this.isBlocked()) { return; }
        if (--this.timeUntilCheck > 0) { return; }
        if (this.state.canClear(this.applicant)) {
            this.state.clear(this.applicant);
            this.setNextState(this.natural, this.timeout);
        } else if (this.canUpdate) {
            this.state.tick(this.applicant);
        }
    }

    public void setNextState(O token) {
        this.setNextState(token, this.timeout);
    }

    public boolean setNextState(O token, int timeUntilCheck) {
        if (this.isBlocked()) { return false; }
        IState state = token.getState(this.applicant);
        if (state.canApply(this.applicant)) {
            this.state.clear(this.applicant);
            this.state = state;
            this.timeUntilCheck = timeUntilCheck;
            this.state.apply(this.applicant);
            this.token = token;
            return true;
        } else {
            return false;
        }
    }

    public boolean isNatural() {
        return this.token.equals(this.natural);
    }

    public O getToken() {
        return this.token;
    }

    public void fromToken(String token) {
        this.setFirstState((O)(this.token.fromToken(token)));
    }

    public void render(MatrixStack stack, float partialTickTime) {
        this.state.render(this.applicant, stack, partialTickTime);
    }

    public void setRotationAngles(IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {
        if (this.state instanceof AnimationState) { ((AnimationState)(this.state)).setRotationAngles(model, this.applicant, limbSwing, limbSwingAmount, ageInTicks); }
    }

    private boolean isBlocked() {
        return this.applicant.isRemote() && this.isServerOnly;
    }
}
