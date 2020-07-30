package mod.moeblocks.entity.util;

import mod.moeblocks.entity.DieEntity;
import mod.moeblocks.entity.ai.dere.*;

import java.util.HashMap;
import java.util.function.Supplier;

public enum Deres {
    HIMEDERE(HimeDere::new, DieEntity.Face.ONE),
    KUUDERE(KuuDere::new, DieEntity.Face.TWO),
    TSUNDERE(TsunDere::new, DieEntity.Face.THREE),
    YANDERE(YanDere::new, DieEntity.Face.FOUR),
    DEREDERE(DereDere::new, DieEntity.Face.FIVE),
    DANDERE(DanDere::new, DieEntity.Face.SIX);

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

    protected static class Registry {
        public static HashMap<DieEntity.Face, Deres> FACES_TO_DERES = new HashMap<>();
    }
}
