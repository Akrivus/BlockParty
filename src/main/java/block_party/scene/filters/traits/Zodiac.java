package block_party.scene.filters.traits;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ITrait;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum Zodiac implements ITrait<Zodiac> {
    ARIES, TAURUS, GEMINI, CANCER,
    LEO, VIRGO, LIBRA, SCORPIO,
    SAGITTARIUS, CAPRICORN, AQUARIUS, PISCES;

    @Override
    public boolean isSharedWith(BlockPartyNPC npc) {
        return npc.getZodiac() == this;
    }

    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public Zodiac fromValue(String key) {
        try {
            return Zodiac.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return this;
        }
    }
}
