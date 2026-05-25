package block_party.scene;

import block_party.entities.Moe;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public enum SceneObservations implements SceneObservation {
    ALWAYS(moe -> true),
    NEVER(moe -> false),
    IS_CORPOREAL(Moe::isCorporeal),
    IS_CARDINAL(moe -> !moe.isCorporeal()),
    RAINING(moe -> moe.level().isRaining()),
    SUNNY(moe -> !moe.level().isRaining()),
    FULL_MOON(moe -> moe.level().getMoonBrightness() == 1.0F),
    GIBBOUS_MOON(moe -> moe.level().getMoonBrightness() == 0.75F),
    HALF_MOON(moe -> moe.level().getMoonBrightness() == 0.5F),
    CRESCENT_MOON(moe -> moe.level().getMoonBrightness() == 0.25F),
    NEW_MOON(moe -> moe.level().getMoonBrightness() == 0.0F),
    MORNING(moe -> isTimeBetween(moe, 0, 4000)),
    NOON(moe -> isTimeBetween(moe, 4000, 8000)),
    EVENING(moe -> isTimeBetween(moe, 8000, 12000)),
    NIGHT(moe -> isTimeBetween(moe, 12000, 16000)),
    MIDNIGHT(moe -> isTimeBetween(moe, 16000, 20000)),
    DAWN(moe -> isTimeBetween(moe, 20000, 24000)),
    BLOOD_TYPE_AB(moe -> "AB".equalsIgnoreCase(moe.getBloodType())),
    BLOOD_TYPE_B(moe -> "B".equalsIgnoreCase(moe.getBloodType())),
    BLOOD_TYPE_A(moe -> "A".equalsIgnoreCase(moe.getBloodType())),
    BLOOD_TYPE_O(moe -> "O".equalsIgnoreCase(moe.getBloodType())),
    HIMEDERE(moe -> trait(moe.getDere(), "HIMEDERE")),
    KUUDERE(moe -> trait(moe.getDere(), "KUUDERE")),
    TSUNDERE(moe -> trait(moe.getDere(), "TSUNDERE")),
    YANDERE(moe -> trait(moe.getDere(), "YANDERE")),
    DEREDERE(moe -> trait(moe.getDere(), "DEREDERE")),
    DANDERE(moe -> trait(moe.getDere(), "DANDERE")),
    ANGRY(moe -> trait(moe.getEmotion(), "ANGRY")),
    BEGGING(moe -> trait(moe.getEmotion(), "BEGGING")),
    CONFUSED(moe -> trait(moe.getEmotion(), "CONFUSED")),
    CRYING(moe -> trait(moe.getEmotion(), "CRYING")),
    MISCHIEVOUS(moe -> trait(moe.getEmotion(), "MISCHIEVOUS")),
    EMBARRASSED(moe -> trait(moe.getEmotion(), "EMBARRASSED")),
    HAPPY(moe -> trait(moe.getEmotion(), "HAPPY")),
    NORMAL(moe -> trait(moe.getEmotion(), "NORMAL")),
    PAINED(moe -> trait(moe.getEmotion(), "PAINED")),
    PSYCHOTIC(moe -> trait(moe.getEmotion(), "PSYCHOTIC")),
    SCARED(moe -> trait(moe.getEmotion(), "SCARED")),
    SICK(moe -> trait(moe.getEmotion(), "SICK")),
    SNOOTY(moe -> trait(moe.getEmotion(), "SNOOTY")),
    SMITTEN(moe -> trait(moe.getEmotion(), "SMITTEN")),
    TIRED(moe -> trait(moe.getEmotion(), "TIRED")),
    MALE(moe -> trait(moe.getGender(), "MALE")),
    FEMALE(moe -> trait(moe.getGender(), "FEMALE")),
    NONBINARY(moe -> trait(moe.getGender(), "NONBINARY"));

    private static final Map<String, SceneObservations> IDS = Map.ofEntries(
            Map.entry("always", ALWAYS),
            Map.entry("never", NEVER),
            Map.entry("is_corporeal", IS_CORPOREAL),
            Map.entry("is_cardinal", IS_CARDINAL),
            Map.entry("if_raining", RAINING),
            Map.entry("if_sunny", SUNNY),
            Map.entry("if_full_moon", FULL_MOON),
            Map.entry("if_gibbous_moon", GIBBOUS_MOON),
            Map.entry("if_half_moon", HALF_MOON),
            Map.entry("if_crescent_moon", CRESCENT_MOON),
            Map.entry("if_new_moon", NEW_MOON),
            Map.entry("if_morning", MORNING),
            Map.entry("if_noon", NOON),
            Map.entry("if_evening", EVENING),
            Map.entry("if_night", NIGHT),
            Map.entry("if_midnight", MIDNIGHT),
            Map.entry("if_dawn", DAWN),
            Map.entry("if_blood_type_ab", BLOOD_TYPE_AB),
            Map.entry("if_blood_type_b", BLOOD_TYPE_B),
            Map.entry("if_blood_type_a", BLOOD_TYPE_A),
            Map.entry("if_blood_type_o", BLOOD_TYPE_O),
            Map.entry("if_himedere", HIMEDERE),
            Map.entry("if_kuudere", KUUDERE),
            Map.entry("if_tsundere", TSUNDERE),
            Map.entry("if_yandere", YANDERE),
            Map.entry("if_deredere", DEREDERE),
            Map.entry("if_dandere", DANDERE),
            Map.entry("if_angry", ANGRY),
            Map.entry("if_begging", BEGGING),
            Map.entry("if_confused", CONFUSED),
            Map.entry("if_crying", CRYING),
            Map.entry("if_mischievous", MISCHIEVOUS),
            Map.entry("if_embarrassed", EMBARRASSED),
            Map.entry("if_happy", HAPPY),
            Map.entry("if_normal", NORMAL),
            Map.entry("if_pained", PAINED),
            Map.entry("if_psychotic", PSYCHOTIC),
            Map.entry("if_scared", SCARED),
            Map.entry("if_sick", SICK),
            Map.entry("if_snooty", SNOOTY),
            Map.entry("if_smitten", SMITTEN),
            Map.entry("if_tired", TIRED),
            Map.entry("if_male", MALE),
            Map.entry("if_female", FEMALE),
            Map.entry("if_nonbinary", NONBINARY));

    private final Predicate<Moe> condition;

    SceneObservations(Predicate<Moe> condition) {
        this.condition = condition;
    }

    public static Optional<SceneObservation> byPath(String path) {
        return Optional.ofNullable(IDS.get(path.toLowerCase(Locale.ROOT))).map(observation -> observation);
    }

    @Override
    public boolean verify(Moe moe) {
        return this.condition.test(moe);
    }

    private static boolean isTimeBetween(Moe moe, int start, int end) {
        long time = moe.level().getDayTime() % 24000L;
        return start <= time && time < end;
    }

    private static boolean trait(String actual, String expected) {
        return expected.equalsIgnoreCase(actual);
    }
}
