package block_party.scene;

import block_party.entities.BlockPartyNPC;

import java.util.function.Predicate;

public enum SceneObservation implements ISceneObservation {
    ALWAYS((npc) -> true),
    NEVER((npc) -> false),
    IS_CORPOREAL((npc) -> npc.isCorporeal()),
    IS_ETHEREAL((npc) -> npc.isEthereal()),
    RAINING((npc) -> npc.level.isRaining()),
    SUNNY((npc) -> !npc.level.isRaining()),
    FULL_MOON((npc) -> npc.level.getMoonBrightness() == 1.0F),
    GIBBOUS_MOON((npc) -> npc.level.getMoonBrightness() == 0.75F),
    HALF_MOON((npc) -> npc.level.getMoonBrightness() == 0.5F),
    CRESCENT_MOON((npc) -> npc.level.getMoonBrightness() == 0.25F),
    NEW_MOON((npc) -> npc.level.getMoonBrightness() == 0.0F),
    MORNING((npc) -> npc.isTimeBetween(0, 4000)),
    NOON((npc) -> npc.isTimeBetween(4000, 8000)),
    EVENING((npc) -> npc.isTimeBetween(8000, 12000)),
    NIGHT((npc) -> npc.isTimeBetween(12000, 16000)),
    MIDNIGHT((npc) -> npc.isTimeBetween(16000, 20000)),
    DAWN((npc) -> npc.isTimeBetween(20000, 24000));

    private final Predicate<BlockPartyNPC> condition;

    SceneObservation(Predicate<BlockPartyNPC> condition) {
        this.condition = condition;
    }

    public boolean verify(BlockPartyNPC npc) {
        return this.condition.test(npc);
    }
}
