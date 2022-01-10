package block_party.scene;

import block_party.npc.BlockPartyNPC;

import java.util.List;

public class Scene {
    private final List<ISceneRequirement> requirements;
    private final List<ISceneAction> actions;

    public Scene(List<ISceneRequirement> requirements, List<ISceneAction> actions) {
        this.requirements = requirements;
        this.actions = actions;
    }

    public boolean fulfills(BlockPartyNPC npc) {
        for (ISceneRequirement requirement : this.requirements)
            if (!requirement.verify(npc))
                return false;
        return true;
    }

    public List<ISceneAction> getActions() {
        return this.actions;
    }
}
