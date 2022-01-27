package block_party.scene.actions;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneAction;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ActionSeries implements ISceneAction {
    private final NavigableMap<Double, ISceneAction> actions = new TreeMap<>();
    private double total = 0;

    @Override
    public void apply(BlockPartyNPC npc) {
        double weight = Math.random() * this.total;
        Map.Entry<Double, ISceneAction> entry = this.actions.higherEntry(weight);
        if (entry == null) { return; }
        npc.sceneManager.putAction(entry.getValue());
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void onComplete() {

    }

    public static ActionSeries start(double probability, ISceneAction state) {
        return new ActionSeries().chain(probability, state);
    }

    public ActionSeries chain(double probability, ISceneAction state) {
        this.total += probability;
        this.actions.put(probability, state);
        return this;
    }
}
