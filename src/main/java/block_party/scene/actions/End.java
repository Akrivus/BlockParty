package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;

public class End extends Abstract1Shot {
    @Override
    public void apply(BlockPartyNPC npc) {
        npc.setDialogue(null);
    }
}
