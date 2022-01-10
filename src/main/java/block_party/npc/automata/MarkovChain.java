package block_party.npc.automata;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneAction;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class MarkovChain implements ISceneAction {
    private final NavigableMap<Double, ISceneAction> states = new TreeMap<>();
    private double total = 0;

    @Override
    public void apply(BlockPartyNPC npc) {
        double weight = Math.random() * this.total;
        Map.Entry<Double, ISceneAction> entry = this.states.higherEntry(weight);
        if (entry == null) { return; }
        npc.automaton.putAction(entry.getValue());
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void onComplete() {

    }

    public static MarkovChain start(double probability, ISceneAction state) {
        return new MarkovChain().chain(probability, state);
    }

    public MarkovChain chain(double probability, ISceneAction state) {
        this.total += probability;
        this.states.put(probability, state);
        return this;
    }
}
