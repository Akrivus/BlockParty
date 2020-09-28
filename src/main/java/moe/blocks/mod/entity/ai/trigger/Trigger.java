package moe.blocks.mod.entity.ai.trigger;

import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;

import java.util.ArrayList;
import java.util.List;

public class Trigger {
    public static List<AbstractTrigger> REGISTRY = new ArrayList<>();

    static {
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.CRYING, 320, 15.0F, 20.0F, false, Deres.HIMEDERE, Deres.TSUNDERE, Deres.YANDERE, Deres.DEREDERE, Deres.DANDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.PAINED, 160, 10.0F, 15.0F, false, Deres.HIMEDERE, Deres.TSUNDERE, Deres.YANDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.SCARED, 160, 10.0F, 15.0F, false, Deres.DEREDERE, Deres.DANDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.TIRED, 80, 5.0F, 10.0F, false, Deres.HIMEDERE, Deres.TSUNDERE, Deres.YANDERE, Deres.DEREDERE, Deres.DANDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.NORMAL, 40, -5.0F, 5.0F, false, Deres.HIMEDERE, Deres.TSUNDERE, Deres.YANDERE, Deres.DEREDERE, Deres.DANDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.HAPPY, 20, -10.0F, -5.0F, false, Deres.HIMEDERE, Deres.TSUNDERE, Deres.YANDERE, Deres.DEREDERE, Deres.DANDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.PSYCHOTIC, 320, 15.0F, 20.0F, true, Deres.HIMEDERE, Deres.TSUNDERE, Deres.YANDERE, Deres.DEREDERE, Deres.DANDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.ANGRY, 160, 10.0F, 15.0F, true, Deres.HIMEDERE, Deres.TSUNDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.CRYING, 160, 10.0F, 15.0F, true, Deres.DEREDERE, Deres.DANDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.PAINED, 80, 5.0F, 10.0F, true, Deres.HIMEDERE, Deres.TSUNDERE, Deres.DEREDERE, Deres.DANDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.HAPPY, 40, 5.0F, 15.0F, true, Deres.YANDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.NORMAL, 20, -5.0F, 5.0F, true, Deres.HIMEDERE, Deres.TSUNDERE, Deres.YANDERE, Deres.DEREDERE, Deres.DANDERE));
        REGISTRY.add(new StressTrigger.Emotional(1, Emotions.NORMAL, 320, -10.0F, 20.0F, true, Deres.KUUDERE));
        REGISTRY.sort(AbstractTrigger::compareTo);
    }
}
