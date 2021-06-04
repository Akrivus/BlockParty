package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class MarkovChain implements IState {
    private final NavigableMap<Double, IState> states = new TreeMap<>();
    private double total = 0;

    @Override
    public void terminate(AbstractNPCEntity npc) { }

    @Override
    public void onTransfer(AbstractNPCEntity npc) { }

    @Override
    public IState transfer(AbstractNPCEntity npc) {
        double weight = Math.random() * this.total;
        Map.Entry<Double, IState> entry = this.states.higherEntry(weight);
        if (entry == null) { return State.RESET; }
        return entry.getValue();
    }

    @Override
    public boolean isDone(AbstractNPCEntity npc) {
        return true;
    }

    public MarkovChain chain(double probability, IState state) {
        this.total += probability;
        this.states.put(probability, state);
        return this;
    }

    public static MarkovChain start(double probability, IState state) {
        return new MarkovChain().chain(probability, state);
    }
}