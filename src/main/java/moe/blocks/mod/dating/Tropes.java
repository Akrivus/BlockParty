package moe.blocks.mod.dating;

import moe.blocks.mod.entity.ai.BloodTypes;
import moe.blocks.mod.entity.ai.automata.state.Deres;

public enum Tropes {
    HIMEDERE_O(Deres.HIMEDERE, BloodTypes.O), HIMEDERE_A(Deres.HIMEDERE, BloodTypes.A), HIMEDERE_B(Deres.HIMEDERE, BloodTypes.B), HIMEDERE_AB(Deres.HIMEDERE, BloodTypes.AB),
    KUUDERE_O(Deres.KUUDERE, BloodTypes.O), KUUDERE_A(Deres.KUUDERE, BloodTypes.A), KUUDERE_B(Deres.KUUDERE, BloodTypes.B), KUUDERE_AB(Deres.KUUDERE, BloodTypes.AB),
    TSUNDERE_O(Deres.TSUNDERE, BloodTypes.O), TSUNDERE_A(Deres.TSUNDERE, BloodTypes.A), TSUNDERE_B(Deres.TSUNDERE, BloodTypes.B), TSUNDERE_AB(Deres.TSUNDERE, BloodTypes.AB),
    YANDERE_O(Deres.YANDERE, BloodTypes.O), YANDERE_A(Deres.YANDERE, BloodTypes.A), YANDERE_B(Deres.YANDERE, BloodTypes.B), YANDERE_AB(Deres.YANDERE, BloodTypes.AB),
    DEREDERE_O(Deres.DEREDERE, BloodTypes.O), DEREDERE_A(Deres.DEREDERE, BloodTypes.A), DEREDERE_B(Deres.DEREDERE, BloodTypes.B), DEREDERE_Ab(Deres.DEREDERE, BloodTypes.AB),
    DANDERE_O(Deres.DANDERE, BloodTypes.O), DANDERE_A(Deres.DANDERE, BloodTypes.A), DANDERE_B(Deres.DANDERE, BloodTypes.B), DANDERE_AB(Deres.DANDERE, BloodTypes.AB);

    private final Deres dere;
    private final BloodTypes bloodType;

    Tropes(Deres dere, BloodTypes bloodType) {
        this.dere = dere;
        this.bloodType = bloodType;
    }

    public static Tropes get(Deres dere, BloodTypes bloodType) {
        return Tropes.valueOf(String.format("%s_%s", dere.name(), bloodType.name()));
    }
}

