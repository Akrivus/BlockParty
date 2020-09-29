package moe.blocks.mod.entity.ai.trigger;

import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;

import java.util.ArrayList;
import java.util.List;

public class Trigger {
    public static List<AbstractTrigger> REGISTRY = new ArrayList<>();

    static {
        REGISTRY.add(new StressTrigger.Emotional(0, Emotions.NORMAL, 0.0F, 20.0F, Deres.KUUDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.PSYCHOTIC, 16.0F, 20.0F, Deres.values()));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.ANGRY, 12.0F, 16.0F, Deres.values()));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.SCARED, 8.0F, 12.0F, Deres.values()));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.TIRED, 4.0F, 8.0F, Deres.values()));
        REGISTRY.add(new DereSpecificTrigger.Emotional(4, Emotions.TIRED, 20, (entity) -> entity.isTimeToSleep(), Deres.values()));
        REGISTRY.add(new DereSpecificTrigger.Emotional(4, Emotions.NORMAL, 100, Deres.values()));
        REGISTRY.sort(AbstractTrigger::compareTo);
    }
}
