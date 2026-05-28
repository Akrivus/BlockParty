package block_party.scene;

import block_party.entities.Moe;
import block_party.entities.environment.MoeEnvironmentalObservation;
import block_party.entities.environment.MoeEnvironmentalRules;
import block_party.entities.environment.MoePlaceMemory;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public enum SceneObservations implements SceneObservation {
    ALWAYS(moe -> true),
    NEVER(moe -> false),
    IS_CORPOREAL(Moe::isCorporeal),
    IS_CARDINAL(moe -> !moe.isCorporeal()),
    IS_FOLLOWING(Moe::isFollowing),
    CAN_FOLLOW_ACROSS_DIMENSIONS(Moe::canFollowAcrossDimensions),
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
    REMEMBERS_PLACE(moe -> moe.rememberedPlace().isPresent()),
    REMEMBERS_HOUSE(moe -> rememberedPlaceType(moe, MoePlaceMemory.PlaceType.HOUSE)),
    REMEMBERS_SHELTER(moe -> rememberedPlaceType(moe, MoePlaceMemory.PlaceType.SHELTER)),
    REMEMBERS_GARDEN(moe -> rememberedPlaceType(moe, MoePlaceMemory.PlaceType.GARDEN)),
    REMEMBERS_GROVE(moe -> rememberedPlaceType(moe, MoePlaceMemory.PlaceType.GROVE)),
    REMEMBERS_FIELD(moe -> rememberedPlaceType(moe, MoePlaceMemory.PlaceType.FIELD)),
    REMEMBERS_WORKSHOP(moe -> rememberedPlaceType(moe, MoePlaceMemory.PlaceType.WORKSHOP)),
    REMEMBERS_WATERFRONT(moe -> rememberedPlaceType(moe, MoePlaceMemory.PlaceType.WATERFRONT)),
    REMEMBERS_CAVE(moe -> rememberedPlaceType(moe, MoePlaceMemory.PlaceType.CAVE)),
    REMEMBERS_SHRINE(moe -> rememberedPlaceType(moe, MoePlaceMemory.PlaceType.SHRINE)),
    REMEMBERS_FARM(moe -> rememberedPlaceType(moe, MoePlaceMemory.PlaceType.FARM)),
    AT_REMEMBERED_PLACE(moe -> moe.rememberedPlace()
            .map(place -> moe.blockPosition().distSqr(place.pos()) <= 4.0D)
            .orElse(false)),
    REMEMBERED_PLACE_OVERCROWDED(moe -> moe.rememberedPlace()
            .map(place -> MoePlaceMemory.evaluate(moe, place.pos()).overcrowded())
            .orElse(false)),
    REMEMBERED_PLACE_INVALID(moe -> moe.rememberedPlace()
            .map(place -> !MoePlaceMemory.stillValid(moe, place))
            .orElse(false)),
    HAS_ENVIRONMENTAL_OBSERVATION(moe -> moe.latestEnvironmentalObservation().isPresent()),
    OBSERVED_AWE(moe -> latestObservationKind(moe, MoeEnvironmentalObservation.Kind.AWE)),
    OBSERVED_AFFINITY(moe -> latestObservationKind(moe, MoeEnvironmentalObservation.Kind.AFFINITY)),
    OBSERVED_TENSION(moe -> latestObservationKind(moe, MoeEnvironmentalObservation.Kind.TENSION)),
    HAS_GIFT_MEMORY(moe -> moe.latestGiftPreferenceSignal().isPresent()),
    LIKED_GIFT(moe -> moe.latestGiftPreferenceSignal().map(block_party.entities.preferences.MoeItemPreferences.PreferenceSignal::liked).orElse(false)),
    DISLIKED_GIFT(moe -> moe.latestGiftPreferenceSignal().map(block_party.entities.preferences.MoeItemPreferences.PreferenceSignal::disliked).orElse(false)),
    INTERESTING_GIFT(moe -> moe.latestGiftPreferenceSignal().map(block_party.entities.preferences.MoeItemPreferences.PreferenceSignal::interesting).orElse(false)),
    BEGGED_FOR_GIFT(moe -> moe.latestGiftPreferenceSignal().map(block_party.entities.preferences.MoeItemPreferences.PreferenceSignal::wantsToBeg).orElse(false)),
    SHELTERING_FROM_RAIN(moe -> moe.level().isRaining()
            && MoeEnvironmentalRules.isStrongShelter(moe.level(), moe.blockPosition())),
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
            Map.entry("is_following", IS_FOLLOWING),
            Map.entry("can_follow_across_dimensions", CAN_FOLLOW_ACROSS_DIMENSIONS),
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
            Map.entry("if_remembers_place", REMEMBERS_PLACE),
            Map.entry("if_remembers_house", REMEMBERS_HOUSE),
            Map.entry("if_remembers_shelter", REMEMBERS_SHELTER),
            Map.entry("if_remembers_garden", REMEMBERS_GARDEN),
            Map.entry("if_remembers_grove", REMEMBERS_GROVE),
            Map.entry("if_remembers_field", REMEMBERS_FIELD),
            Map.entry("if_remembers_workshop", REMEMBERS_WORKSHOP),
            Map.entry("if_remembers_waterfront", REMEMBERS_WATERFRONT),
            Map.entry("if_remembers_cave", REMEMBERS_CAVE),
            Map.entry("if_remembers_shrine", REMEMBERS_SHRINE),
            Map.entry("if_remembers_farm", REMEMBERS_FARM),
            Map.entry("if_at_remembered_place", AT_REMEMBERED_PLACE),
            Map.entry("if_remembered_place_overcrowded", REMEMBERED_PLACE_OVERCROWDED),
            Map.entry("if_remembered_place_invalid", REMEMBERED_PLACE_INVALID),
            Map.entry("if_has_environmental_observation", HAS_ENVIRONMENTAL_OBSERVATION),
            Map.entry("if_observed_awe", OBSERVED_AWE),
            Map.entry("if_observed_affinity", OBSERVED_AFFINITY),
            Map.entry("if_observed_tension", OBSERVED_TENSION),
            Map.entry("if_has_gift_memory", HAS_GIFT_MEMORY),
            Map.entry("if_liked_gift", LIKED_GIFT),
            Map.entry("if_disliked_gift", DISLIKED_GIFT),
            Map.entry("if_interesting_gift", INTERESTING_GIFT),
            Map.entry("if_begged_for_gift", BEGGED_FOR_GIFT),
            Map.entry("if_sheltering_from_rain", SHELTERING_FROM_RAIN),
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

    private static boolean rememberedPlaceType(Moe moe, MoePlaceMemory.PlaceType type) {
        return moe.rememberedPlace()
                .map(place -> place.type() == type)
                .orElse(false);
    }

    private static boolean latestObservationKind(Moe moe, MoeEnvironmentalObservation.Kind kind) {
        return moe.latestEnvironmentalObservation()
                .map(observation -> observation.kind() == kind)
                .orElse(false);
    }
}
