package block_party.scene.filters.traits;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ITrait;

public enum Dere implements ITrait<Dere> {
    NYANDERE(0xffffff), HIMEDERE(0xcc00ff), KUUDERE(0x0000ff), TSUNDERE(0xffcc00), YANDERE(0xff0000), DEREDERE(0x0000ff), DANDERE(0x00ccff);

    private final int color;

    Dere(int color) {
        this.color = color;
    }

    @Override
    public boolean isSharedWith(BlockPartyNPC npc) {
        return npc.getDere() == this;
    }

    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public Dere fromValue(String key) {
        try {
            return Dere.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return this;
        }
    }

    public static Dere random() {
        return Dere.values()[(int) (1 + 6 * Math.random())];
    }
}
