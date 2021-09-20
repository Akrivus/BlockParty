package block_party.npc.automata;

import block_party.npc.BlockPartyNPC;

import java.util.function.Predicate;

public enum Condition implements ICondition {
    ALWAYS((npc) -> true, 0), NEVER((npc) -> false, 0), MORNING((npc) -> npc.isTimeBetween(0, 4000), 1000), NOON((npc) -> npc.isTimeBetween(4000, 8000), 1000), EVENING((npc) -> npc.isTimeBetween(8000, 12000), 1000), NIGHT((npc) -> npc.isTimeBetween(12000, 16000), 1000), MIDNIGHT((npc) -> npc.isTimeBetween(16000, 20000), 1000), DAWN((npc) -> npc.isTimeBetween(20000, 24000), 1000);

    private final Predicate<BlockPartyNPC> condition;
    private final int timeout;
    private final IState[] states;

    Condition(Predicate<BlockPartyNPC> condition, int timeout, IState... states) {
        this.condition = condition;
        this.timeout = timeout;
        this.states = states;
    }

    public boolean isTrue(BlockPartyNPC npc) {
        return this.condition.test(npc);
    }

    public int getTimeout() {
        return this.timeout;
    }

    public IState getStemState() {
        if (this.states.length == 0) { return null; }
        int index = (int) (Math.random() * this.states.length);
        return this.states[index];
    }
}
