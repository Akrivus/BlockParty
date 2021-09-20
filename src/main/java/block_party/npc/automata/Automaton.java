package block_party.npc.automata;

import block_party.npc.BlockPartyNPC;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class Automaton {
    protected final BlockPartyNPC applicant;
    protected final Map<Condition, Integer> timeouts = new HashMap<>();
    protected IState state;

    public Automaton(BlockPartyNPC applicant) {
        this.applicant = applicant;
        for (Condition condition : Condition.values()) {
            this.timeouts.put(condition, 0);
        }
    }

    public void read(CompoundTag compound) {
        for (Condition condition : Condition.values()) {
            this.timeouts.put(condition, compound.getInt(condition.name()));
        }
    }

    public void write(CompoundTag compound) {
        for (Condition condition : Condition.values()) {
            compound.putInt(condition.name(), this.timeouts.get(condition));
        }
    }

    public void tick(BlockPartyNPC npc) {
        if (this.state == null) { this.reset(npc); }
        if (this.state != null && this.state.isDone(npc)) {
            this.setState(npc, this.state.transfer(npc));
        }
    }

    public IState reset(BlockPartyNPC npc) {
        for (Condition condition : Condition.values()) {
            if (this.timeouts.get(condition) < 0) { continue; }
            if (condition.isTrue(npc)) {
                this.setState(npc, condition.getStemState());
            }
        }
        return this.state;
    }

    public void setState(BlockPartyNPC npc, IState state, IState... states) {
        if (this.state != null) { this.state.terminate(npc); }
        this.state = state;
        if (this.state != null) {
            this.state.onTransfer(npc);
        }
    }
}
