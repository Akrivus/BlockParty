package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneAction;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Markov extends Abstract1Shot {
    private final NavigableMap<Double, ISceneAction> actions = new TreeMap<>();
    private double total = 0;

    @Override
    public void apply(BlockPartyNPC npc) {
        double weight = Math.random() * this.total;
        Map.Entry<Double, ISceneAction> entry = this.actions.higherEntry(weight);
        if (entry == null) { return; }
        npc.sceneManager.putAction(entry.getValue());
    }

    public static Markov start(double probability, ISceneAction state) {
        return new Markov().chain(probability, state);
    }

    public Markov chain(double probability, ISceneAction state) {
        this.total += probability;
        this.actions.put(probability, state);
        return this;
    }
}
