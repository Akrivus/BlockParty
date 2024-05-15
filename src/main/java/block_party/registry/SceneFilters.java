package block_party.registry;

import block_party.BlockParty;
import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneObservation;
import block_party.scene.SceneObservation;
import block_party.scene.observations.*;
import block_party.scene.traits.BloodType;
import block_party.scene.traits.Dere;
import block_party.scene.traits.Emotion;
import block_party.scene.traits.Gender;

import java.util.function.Supplier;

public class SceneFilters {
    public static final Supplier<Builder> ALWAYS = BlockParty.SCENE_FILTERS.register("always", () -> f(() -> SceneObservation.ALWAYS));
    public static final Supplier<Builder> NEVER = BlockParty.SCENE_FILTERS.register("never", () -> f(() -> SceneObservation.NEVER));
    public static final Supplier<Builder> IS_CORPOREAL = BlockParty.SCENE_FILTERS.register("is_corporeal", () -> f(() -> SceneObservation.IS_CORPOREAL));
    public static final Supplier<Builder> IS_ETHEREAL = BlockParty.SCENE_FILTERS.register("is_ethereal", () -> f(() -> SceneObservation.IS_ETHEREAL));
    public static final Supplier<Builder> RAINING = BlockParty.SCENE_FILTERS.register("if_raining", () -> f(() -> SceneObservation.RAINING));
    public static final Supplier<Builder> SUNNY = BlockParty.SCENE_FILTERS.register("if_sunny", () -> f(() -> SceneObservation.SUNNY));
    public static final Supplier<Builder> FULL_MOON = BlockParty.SCENE_FILTERS.register("if_full_moon", () -> f(() -> SceneObservation.FULL_MOON));
    public static final Supplier<Builder> GIBBOUS_MOON = BlockParty.SCENE_FILTERS.register("if_gibbous_moon", () -> f(() -> SceneObservation.GIBBOUS_MOON));
    public static final Supplier<Builder> HALF_MOON = BlockParty.SCENE_FILTERS.register("if_half_moon", () -> f(() -> SceneObservation.HALF_MOON));
    public static final Supplier<Builder> CRESCENT_MOON = BlockParty.SCENE_FILTERS.register("if_crescent_moon", () -> f(() -> SceneObservation.CRESCENT_MOON));
    public static final Supplier<Builder> NEW_MOON = BlockParty.SCENE_FILTERS.register("if_new_moon", () -> f(() -> SceneObservation.NEW_MOON));
    public static final Supplier<Builder> MORNING = BlockParty.SCENE_FILTERS.register("if_morning", () -> f(() -> SceneObservation.MORNING));
    public static final Supplier<Builder> NOON = BlockParty.SCENE_FILTERS.register("if_noon", () -> f(() -> SceneObservation.NOON));
    public static final Supplier<Builder> EVENING = BlockParty.SCENE_FILTERS.register("if_evening", () -> f(() -> SceneObservation.EVENING));
    public static final Supplier<Builder> NIGHT = BlockParty.SCENE_FILTERS.register("if_night", () -> f(() -> SceneObservation.NIGHT));
    public static final Supplier<Builder> MIDNIGHT = BlockParty.SCENE_FILTERS.register("if_midnight", () -> f(() -> SceneObservation.MIDNIGHT));
    public static final Supplier<Builder> DAWN = BlockParty.SCENE_FILTERS.register("if_dawn", () -> f(() -> SceneObservation.DAWN));
    public static final Supplier<Builder> TIME = BlockParty.SCENE_FILTERS.register("if_time", () -> f(() -> new AbstractInteger((npc) -> (int) npc.level().getDayTime())));
    public static final Supplier<Builder> BLOOD_TYPE_AB = BlockParty.SCENE_FILTERS.register("if_blood_type_ab", () -> f(() -> BloodType.AB));
    public static final Supplier<Builder> BLOOD_TYPE_B = BlockParty.SCENE_FILTERS.register("if_blood_type_b", () -> f(() -> BloodType.B));
    public static final Supplier<Builder> BLOOD_TYPE_A = BlockParty.SCENE_FILTERS.register("if_blood_type_a", () -> f(() -> BloodType.A));
    public static final Supplier<Builder> BLOOD_TYPE_O = BlockParty.SCENE_FILTERS.register("if_blood_type_o", () -> f(() -> BloodType.O));
    public static final Supplier<Builder> HIMEDERE = BlockParty.SCENE_FILTERS.register("if_himedere", () -> f(() -> Dere.HIMEDERE));
    public static final Supplier<Builder> KUUDERE = BlockParty.SCENE_FILTERS.register("if_kuudere", () -> f(() -> Dere.KUUDERE));
    public static final Supplier<Builder> TSUNDERE = BlockParty.SCENE_FILTERS.register("if_tsundere", () -> f(() -> Dere.TSUNDERE));
    public static final Supplier<Builder> YANDERE = BlockParty.SCENE_FILTERS.register("if_yandere", () -> f(() -> Dere.YANDERE));
    public static final Supplier<Builder> DEREDERE = BlockParty.SCENE_FILTERS.register("if_deredere", () -> f(() -> Dere.DEREDERE));
    public static final Supplier<Builder> DANDERE = BlockParty.SCENE_FILTERS.register("if_dandere", () -> f(() -> Dere.DANDERE));
    public static final Supplier<Builder> ANGRY = BlockParty.SCENE_FILTERS.register("if_angry", () -> f(() -> Emotion.ANGRY));
    public static final Supplier<Builder> BEGGING = BlockParty.SCENE_FILTERS.register("if_begging", () -> f(() -> Emotion.BEGGING));
    public static final Supplier<Builder> CONFUSED = BlockParty.SCENE_FILTERS.register("if_confused", () -> f(() -> Emotion.CONFUSED));
    public static final Supplier<Builder> CRYING = BlockParty.SCENE_FILTERS.register("if_crying", () -> f(() -> Emotion.CRYING));
    public static final Supplier<Builder> MISCHIEVOUS = BlockParty.SCENE_FILTERS.register("if_mischievous", () -> f(() -> Emotion.MISCHIEVOUS));
    public static final Supplier<Builder> EMBARRASSED = BlockParty.SCENE_FILTERS.register("if_embarrassed", () -> f(() -> Emotion.EMBARRASSED));
    public static final Supplier<Builder> HAPPY = BlockParty.SCENE_FILTERS.register("if_happy", () -> f(() -> Emotion.HAPPY));
    public static final Supplier<Builder> NORMAL = BlockParty.SCENE_FILTERS.register("if_normal", () -> f(() -> Emotion.NORMAL));
    public static final Supplier<Builder> PAINED = BlockParty.SCENE_FILTERS.register("if_pained", () -> f(() -> Emotion.PAINED));
    public static final Supplier<Builder> PSYCHOTIC = BlockParty.SCENE_FILTERS.register("if_psychotic", () -> f(() -> Emotion.PSYCHOTIC));
    public static final Supplier<Builder> SCARED = BlockParty.SCENE_FILTERS.register("if_scared", () -> f(() -> Emotion.SCARED));
    public static final Supplier<Builder> SICK = BlockParty.SCENE_FILTERS.register("if_sick", () -> f(() -> Emotion.SICK));
    public static final Supplier<Builder> SNOOTY = BlockParty.SCENE_FILTERS.register("if_snooty", () -> f(() -> Emotion.SNOOTY));
    public static final Supplier<Builder> SMITTEN = BlockParty.SCENE_FILTERS.register("if_smitten", () -> f(() -> Emotion.SMITTEN));
    public static final Supplier<Builder> TIRED = BlockParty.SCENE_FILTERS.register("if_tired", () -> f(() -> Emotion.TIRED));
    public static final Supplier<Builder> MALE = BlockParty.SCENE_FILTERS.register("if_male", () -> f(() -> Gender.MALE));
    public static final Supplier<Builder> FEMALE = BlockParty.SCENE_FILTERS.register("if_female", () -> f(() -> Gender.FEMALE));
    public static final Supplier<Builder> NONBINARY = BlockParty.SCENE_FILTERS.register("if_nonbinary", () -> f(() -> Gender.NONBINARY));
    public static final Supplier<Builder> SELF = BlockParty.SCENE_FILTERS.register("self", () -> f(() -> new AbstractEntity((npc) -> npc)));
    public static final Supplier<Builder> HEALTH = BlockParty.SCENE_FILTERS.register("health", () -> f(() -> new AbstractFloat(BlockPartyNPC::getHealth)));
    public static final Supplier<Builder> FOOD_LEVEL = BlockParty.SCENE_FILTERS.register("food_level", () -> f(() -> new AbstractFloat(BlockPartyNPC::getFoodLevel)));
    public static final Supplier<Builder> LOYALTY = BlockParty.SCENE_FILTERS.register("loyalty", () -> f(() -> new AbstractFloat(BlockPartyNPC::getLoyalty)));
    public static final Supplier<Builder> STRESS = BlockParty.SCENE_FILTERS.register("stress", () -> f(() -> new AbstractFloat(BlockPartyNPC::getStress)));
    public static final Supplier<Builder> PLAYER_COUNTER = BlockParty.SCENE_FILTERS.register("player_counter", () -> f(WithCounter.Player::new));
    public static final Supplier<Builder> PLAYER_HAS_COOKIE = BlockParty.SCENE_FILTERS.register("player_has_cookie", () -> f(WithCookie.Player::new));
    public static final Supplier<Builder> PLAYER_HELD_ITEM = BlockParty.SCENE_FILTERS.register("player_held_item", () -> f(WithHeldItem.Player::new));
    public static final Supplier<Builder> COUNTER = BlockParty.SCENE_FILTERS.register("counter", () -> f(WithCounter::new));
    public static final Supplier<Builder> HAS_COOKIE = BlockParty.SCENE_FILTERS.register("has_cookie", () -> f(WithCookie::new));
    public static final Supplier<Builder> HELD_ITEM = BlockParty.SCENE_FILTERS.register("held_item", () -> f(WithHeldItem::new));
    public static final Supplier<Builder> BLOCK = BlockParty.SCENE_FILTERS.register("block", () -> f(() -> new AbstractBlock(BlockPartyNPC::getVisibleBlockState)));
    public static final Supplier<Builder> FAMILY = BlockParty.SCENE_FILTERS.register("family_name", () -> f(() -> new AbstractString(BlockPartyNPC::getFamilyName)));
    public static final Supplier<Builder> NAME = BlockParty.SCENE_FILTERS.register("name", () -> f(() -> new AbstractString(BlockPartyNPC::getGivenName)));

    public static ISceneObservation build(Supplier<Builder> action) {
        return action.get().build();
    }

    private static Builder f(Supplier<ISceneObservation> action) {
        return new Builder(action);
    }

    public static class Builder {
        private final Supplier<ISceneObservation> builder;

        public Builder(Supplier<ISceneObservation> builder) {
            this.builder = builder;
        }

        public ISceneObservation build() {
            return this.builder.get();
        }
    }
}
