package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

public class Automaton<E extends AbstractNPCEntity, O extends IStateEnum> {
    protected final E applicant;
    protected final O natural;
    protected final int timeout;
    protected int timeUntilCheck;
    protected IState state;
    protected O token;

    public Automaton(E applicant, O natural, int timeout) {
        this.applicant = applicant;
        this.natural = natural;
        this.timeout = timeout;
        this.setFirstState(natural);
    }

    public Automaton(E applicant, O natural) {
        this(applicant, natural, 1000);
    }

    public void setNextState(O token, int timeUntilCheck) {
        IState state = token.getState(this.applicant);
        if (state.canApply(this.applicant)) {
            this.state.clear(this.applicant);
            this.state = state;
            this.timeUntilCheck = timeUntilCheck;
            this.state.apply(this.applicant);
            this.token = token;
        }
    }

    public void setNextState(O token) {
        this.setNextState(token, this.timeout);
    }

    private void setFirstState(O token) {
        this.state = token.getState(this.applicant);
        this.state.apply(this.applicant);
        this.token = token;
    }

    public void update() {
        if (--this.timeUntilCheck > 0) { return; }
        if (this.state.canClear(this.applicant)) {
            this.state.clear(this.applicant);
            this.setNextState(this.natural, this.timeout);
        }
    }

    public O getToken() {
        return this.token;
    }
}
