package block_party.scene;

import block_party.npc.BlockPartyNPC;

import java.util.function.Predicate;

public enum SceneRequirement implements ISceneRequirement {
    ALWAYS((npc) -> true), NEVER((npc) -> false), MORNING((npc) -> npc.isTimeBetween(0, 4000)), NOON((npc) -> npc.isTimeBetween(4000, 8000)), EVENING((npc) -> npc.isTimeBetween(8000, 12000)), NIGHT((npc) -> npc.isTimeBetween(12000, 16000)), MIDNIGHT((npc) -> npc.isTimeBetween(16000, 20000)), DAWN((npc) -> npc.isTimeBetween(20000, 24000));

    private final Predicate<BlockPartyNPC> condition;

    SceneRequirement(Predicate<BlockPartyNPC> condition) {
        this.condition = condition;
    }

    public boolean verify(BlockPartyNPC npc) {
        return this.condition.test(npc);
    }
}
