package block_party.entities.profile;

import block_party.entities.Moe;
import block_party.registry.CustomTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class MoeBlockProfile {
    private MoeBlockProfile() {
    }

    public static void applyIdentity(Moe moe, BlockState state) {
        if (state.is(CustomTags.HAS_MALE_PRONOUNS)) {
            moe.setGender("MALE");
        } else if (state.is(CustomTags.HAS_NONBINARY_PRONOUNS)) {
            moe.setGender("NONBINARY");
        } else if (state.is(CustomTags.HAS_FEMALE_PRONOUNS)) {
            moe.setGender("FEMALE");
        }

        if (state.is(CustomTags.BLOOD_TYPE_A)) {
            moe.setBloodType("A");
        } else if (state.is(CustomTags.BLOOD_TYPE_AB)) {
            moe.setBloodType("AB");
        } else if (state.is(CustomTags.BLOOD_TYPE_B)) {
            moe.setBloodType("B");
        } else if (state.is(CustomTags.BLOOD_TYPE_O)) {
            moe.setBloodType("O");
        }

        if (state.is(CustomTags.NYANDERE)) {
            moe.setDere("NYANDERE");
        } else if (state.is(CustomTags.HIMEDERE)) {
            moe.setDere("HIMEDERE");
        } else if (state.is(CustomTags.KUUDERE)) {
            moe.setDere("KUUDERE");
        } else if (state.is(CustomTags.TSUNDERE)) {
            moe.setDere("TSUNDERE");
        } else if (state.is(CustomTags.YANDERE)) {
            moe.setDere("YANDERE");
        } else if (state.is(CustomTags.DEREDERE)) {
            moe.setDere("DEREDERE");
        } else if (state.is(CustomTags.DANDERE)) {
            moe.setDere("DANDERE");
        }

        if (state.is(CustomTags.ARIES)) {
            moe.setZodiac("ARIES");
        } else if (state.is(CustomTags.TAURUS)) {
            moe.setZodiac("TAURUS");
        } else if (state.is(CustomTags.GEMINI)) {
            moe.setZodiac("GEMINI");
        } else if (state.is(CustomTags.CANCER)) {
            moe.setZodiac("CANCER");
        } else if (state.is(CustomTags.LEO)) {
            moe.setZodiac("LEO");
        } else if (state.is(CustomTags.VIRGO)) {
            moe.setZodiac("VIRGO");
        } else if (state.is(CustomTags.LIBRA)) {
            moe.setZodiac("LIBRA");
        } else if (state.is(CustomTags.SCORPIO)) {
            moe.setZodiac("SCORPIO");
        } else if (state.is(CustomTags.SAGITTARIUS)) {
            moe.setZodiac("SAGITTARIUS");
        } else if (state.is(CustomTags.CAPRICORN)) {
            moe.setZodiac("CAPRICORN");
        } else if (state.is(CustomTags.AQUARIUS)) {
            moe.setZodiac("AQUARIUS");
        } else if (state.is(CustomTags.PISCES)) {
            moe.setZodiac("PISCES");
        }

        if (ignoresVolume(state)) {
            moe.setMoeScale(1.0F);
        }
        if (!moe.isCardinal()) {
            moe.assignUniqueNameIfDefault();
        }
    }

    public static boolean hasWings(BlockState state) {
        return state.is(CustomTags.HAS_WINGS);
    }

    public static boolean hasCatFeatures(BlockState state) {
        return state.is(CustomTags.HAS_CAT_FEATURES);
    }

    public static boolean hasGlow(BlockState state) {
        return state.is(CustomTags.HAS_GLOW);
    }

    public static boolean ignoresVolume(BlockState state) {
        return state.is(CustomTags.IGNORES_VOLUME);
    }

    public static boolean ignoresRain(BlockState state) {
        return state.is(CustomTags.IGNORES_RAIN);
    }

    public static boolean ignoresDarkness(BlockState state) {
        return state.is(CustomTags.IGNORES_DARKNESS);
    }

    public static float volume(BlockState state) {
        double volume = 0.0D;
        for (AABB aabb : state.getOcclusionShape().toAabbs()) {
            volume += (aabb.maxX - aabb.minX) * (aabb.maxY - aabb.minY) * (aabb.maxZ - aabb.minZ);
        }
        volume = Math.cbrt(volume);
        if (!Double.isFinite(volume) || volume < 0.25D) {
            volume = 1.0D;
        }
        return (float) volume * 0.9375F;
    }

    public static float blockBuffer(Moe moe) {
        return 0.5F / (moe.getActualBlockState().getDestroySpeed(moe.level(), moe.blockPosition()) + 1.0F);
    }

    public static boolean fireImmune(Moe moe) {
        return !moe.getActualBlockState().isFlammable(moe.level(), moe.blockPosition(), moe.getDirection());
    }
}
