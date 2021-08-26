package block_party.mob.automata.trait;

import block_party.mob.Partyer;
import block_party.mob.automata.IState;
import block_party.mob.automata.ITrait;

public enum Dere implements ITrait<Dere> {
    NYANDERE(0xffffff), HIMEDERE(0xcc00ff), KUUDERE(0x0000ff), TSUNDERE(0xffcc00), YANDERE(0xff0000), DEREDERE(0x0000ff), DANDERE(0x00ccff);

    private final int color;

    Dere(int color) {
        this.color = color;
    }

    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public Dere fromValue(String key) {
        try {
            return Dere.valueOf(key);
        } catch (IllegalArgumentException e) {
            return Dere.NYANDERE;
        }
    }

    @Override
    public boolean isTrue(Partyer entity) {
        return entity.getDere() == this;
    }

    @Override
    public IState getStemState() {
        return null;
    }

    public static Dere random() {
        return Dere.values()[(int) (1 + 6 * Math.random())];
    }
}
