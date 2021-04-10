package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Map;

public class Automaton {
    protected final AbstractNPCEntity applicant;
    protected final Map<Condition, Integer> timeouts = new HashMap<>();
    protected IState state;

    public Automaton(AbstractNPCEntity applicant) {
        this.applicant = applicant;
        for (Condition condition : Condition.values()) {
            this.timeouts.put(condition, 0);
        }
    }

    public void read(CompoundNBT compound) {
        for (Condition condition : Condition.values()) {
            this.timeouts.put(condition, compound.getInt(condition.name()));
        }
    }

    public void write(CompoundNBT compound) {
        for (Condition condition : Condition.values()) {
            compound.putInt(condition.name(), this.timeouts.get(condition));
        }
    }

    public void tick(AbstractNPCEntity npc) {
        if (this.state == null) { this.reset(npc); }
        if (this.state != null && this.state.isDone(npc)) {
            this.state.terminate(npc);
            this.state = this.state.transfer(npc);
        }
    }

    public void reset(AbstractNPCEntity npc) {
        for (Condition condition : Condition.values()) {
            if (this.timeouts.get(condition) < 0) { continue; }
            if (condition.isTrue(npc)) {
                this.state = condition.getStemState();
            }
        }
    }
}
