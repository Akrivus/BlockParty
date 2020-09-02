package moe.blocks.mod.entity.util;

import moe.blocks.mod.entity.DieEntity;
import moe.blocks.mod.entity.ai.dere.*;
import moe.blocks.mod.entity.ai.dere.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Supplier;

public enum Deres {
    HIMEDERE(Himedere::new, DieEntity.Face.ONE),
    KUUDERE(Kuudere::new, DieEntity.Face.TWO),
    TSUNDERE(Tsundere::new, DieEntity.Face.THREE),
    YANDERE(Yandere::new, DieEntity.Face.FOUR),
    DEREDERE(Deredere::new, DieEntity.Face.FIVE),
    DANDERE(Dandere::new, DieEntity.Face.SIX);

    private final Supplier<? extends AbstractDere> dere;

    Deres(Supplier<? extends AbstractDere> dere, DieEntity.Face face) {
        this.dere = dere;
        Registry.FACES_TO_DERES.put(face, this);
    }

    public static Deres from(DieEntity.Face face) {
        return Registry.FACES_TO_DERES.getOrDefault(face, HIMEDERE);
    }

    public AbstractDere get() {
        return this.dere.get();
    }

    public boolean matches(Enum<?>... deres) {
        return Arrays.stream(deres).anyMatch(dere -> this == dere);
    }

    protected static class Registry {
        public static HashMap<DieEntity.Face, Deres> FACES_TO_DERES = new HashMap<>();
    }
}
