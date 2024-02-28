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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SceneFilters {
    public static final RegistryObject<Builder> ALWAYS = BlockParty.SCENE_FILTERS.register("always", () -> f(() -> SceneObservation.ALWAYS));
    public static final RegistryObject<Builder> NEVER = BlockParty.SCENE_FILTERS.register("never", () -> f(() -> SceneObservation.NEVER));
    public static final RegistryObject<Builder> IS_CORPOREAL = BlockParty.SCENE_FILTERS.register("is_corporeal", () -> f(() -> SceneObservation.IS_CORPOREAL));
    public static final RegistryObject<Builder> IS_ETHEREAL = BlockParty.SCENE_FILTERS.register("is_ethereal", () -> f(() -> SceneObservation.IS_ETHEREAL));
    public static final RegistryObject<Builder> RAINING = BlockParty.SCENE_FILTERS.register("if_raining", () -> f(() -> SceneObservation.RAINING));
    public static final RegistryObject<Builder> SUNNY = BlockParty.SCENE_FILTERS.register("if_sunny", () -> f(() -> SceneObservation.SUNNY));
    public static final RegistryObject<Builder> FULL_MOON = BlockParty.SCENE_FILTERS.register("if_full_moon", () -> f(() -> SceneObservation.FULL_MOON));
    public static final RegistryObject<Builder> GIBBOUS_MOON = BlockParty.SCENE_FILTERS.register("if_gibbous_moon", () -> f(() -> SceneObservation.GIBBOUS_MOON));
    public static final RegistryObject<Builder> HALF_MOON = BlockParty.SCENE_FILTERS.register("if_half_moon", () -> f(() -> SceneObservation.HALF_MOON));
    public static final RegistryObject<Builder> CRESCENT_MOON = BlockParty.SCENE_FILTERS.register("if_crescent_moon", () -> f(() -> SceneObservation.CRESCENT_MOON));
    public static final RegistryObject<Builder> NEW_MOON = BlockParty.SCENE_FILTERS.register("if_new_moon", () -> f(() -> SceneObservation.NEW_MOON));
    public static final RegistryObject<Builder> MORNING = BlockParty.SCENE_FILTERS.register("if_morning", () -> f(() -> SceneObservation.MORNING));
    public static final RegistryObject<Builder> NOON = BlockParty.SCENE_FILTERS.register("if_noon", () -> f(() -> SceneObservation.NOON));
    public static final RegistryObject<Builder> EVENING = BlockParty.SCENE_FILTERS.register("if_evening", () -> f(() -> SceneObservation.EVENING));
    public static final RegistryObject<Builder> NIGHT = BlockParty.SCENE_FILTERS.register("if_night", () -> f(() -> SceneObservation.NIGHT));
    public static final RegistryObject<Builder> MIDNIGHT = BlockParty.SCENE_FILTERS.register("if_midnight", () -> f(() -> SceneObservation.MIDNIGHT));
    public static final RegistryObject<Builder> DAWN = BlockParty.SCENE_FILTERS.register("if_dawn", () -> f(() -> SceneObservation.DAWN));
    public static final RegistryObject<Builder> TIME = BlockParty.SCENE_FILTERS.register("if_time", () -> f(() -> new AbstractInteger((npc) -> (int) npc.level.getDayTime())));
    public static final RegistryObject<Builder> BLOOD_TYPE_AB = BlockParty.SCENE_FILTERS.register("if_blood_type_ab", () -> f(() -> BloodType.AB));
    public static final RegistryObject<Builder> BLOOD_TYPE_B = BlockParty.SCENE_FILTERS.register("if_blood_type_b", () -> f(() -> BloodType.B));
    public static final RegistryObject<Builder> BLOOD_TYPE_A = BlockParty.SCENE_FILTERS.register("if_blood_type_a", () -> f(() -> BloodType.A));
    public static final RegistryObject<Builder> BLOOD_TYPE_O = BlockParty.SCENE_FILTERS.register("if_blood_type_o", () -> f(() -> BloodType.O));
    public static final RegistryObject<Builder> HIMEDERE = BlockParty.SCENE_FILTERS.register("if_himedere", () -> f(() -> Dere.HIMEDERE));
    public static final RegistryObject<Builder> KUUDERE = BlockParty.SCENE_FILTERS.register("if_kuudere", () -> f(() -> Dere.KUUDERE));
    public static final RegistryObject<Builder> TSUNDERE = BlockParty.SCENE_FILTERS.register("if_tsundere", () -> f(() -> Dere.TSUNDERE));
    public static final RegistryObject<Builder> YANDERE = BlockParty.SCENE_FILTERS.register("if_yandere", () -> f(() -> Dere.YANDERE));
    public static final RegistryObject<Builder> DEREDERE = BlockParty.SCENE_FILTERS.register("if_deredere", () -> f(() -> Dere.DEREDERE));
    public static final RegistryObject<Builder> DANDERE = BlockParty.SCENE_FILTERS.register("if_dandere", () -> f(() -> Dere.DANDERE));
    public static final RegistryObject<Builder> ANGRY = BlockParty.SCENE_FILTERS.register("if_angry", () -> f(() -> Emotion.ANGRY));
    public static final RegistryObject<Builder> BEGGING = BlockParty.SCENE_FILTERS.register("if_begging", () -> f(() -> Emotion.BEGGING));
    public static final RegistryObject<Builder> CONFUSED = BlockParty.SCENE_FILTERS.register("if_confused", () -> f(() -> Emotion.CONFUSED));
    public static final RegistryObject<Builder> CRYING = BlockParty.SCENE_FILTERS.register("if_crying", () -> f(() -> Emotion.CRYING));
    public static final RegistryObject<Builder> MISCHIEVOUS = BlockParty.SCENE_FILTERS.register("if_mischievous", () -> f(() -> Emotion.MISCHIEVOUS));
    public static final RegistryObject<Builder> EMBARRASSED = BlockParty.SCENE_FILTERS.register("if_embarrassed", () -> f(() -> Emotion.EMBARRASSED));
    public static final RegistryObject<Builder> HAPPY = BlockParty.SCENE_FILTERS.register("if_happy", () -> f(() -> Emotion.HAPPY));
    public static final RegistryObject<Builder> NORMAL = BlockParty.SCENE_FILTERS.register("if_normal", () -> f(() -> Emotion.NORMAL));
    public static final RegistryObject<Builder> PAINED = BlockParty.SCENE_FILTERS.register("if_pained", () -> f(() -> Emotion.PAINED));
    public static final RegistryObject<Builder> PSYCHOTIC = BlockParty.SCENE_FILTERS.register("if_psychotic", () -> f(() -> Emotion.PSYCHOTIC));
    public static final RegistryObject<Builder> SCARED = BlockParty.SCENE_FILTERS.register("if_scared", () -> f(() -> Emotion.SCARED));
    public static final RegistryObject<Builder> SICK = BlockParty.SCENE_FILTERS.register("if_sick", () -> f(() -> Emotion.SICK));
    public static final RegistryObject<Builder> SNOOTY = BlockParty.SCENE_FILTERS.register("if_snooty", () -> f(() -> Emotion.SNOOTY));
    public static final RegistryObject<Builder> SMITTEN = BlockParty.SCENE_FILTERS.register("if_smitten", () -> f(() -> Emotion.SMITTEN));
    public static final RegistryObject<Builder> TIRED = BlockParty.SCENE_FILTERS.register("if_tired", () -> f(() -> Emotion.TIRED));
    public static final RegistryObject<Builder> MALE = BlockParty.SCENE_FILTERS.register("if_male", () -> f(() -> Gender.MALE));
    public static final RegistryObject<Builder> FEMALE = BlockParty.SCENE_FILTERS.register("if_female", () -> f(() -> Gender.FEMALE));
    public static final RegistryObject<Builder> NONBINARY = BlockParty.SCENE_FILTERS.register("if_nonbinary", () -> f(() -> Gender.NONBINARY));
    public static final RegistryObject<Builder> SELF = BlockParty.SCENE_FILTERS.register("self", () -> f(() -> new AbstractEntity((npc) -> npc)));
    public static final RegistryObject<Builder> HEALTH = BlockParty.SCENE_FILTERS.register("health", () -> f(() -> new AbstractFloat(BlockPartyNPC::getHealth)));
    public static final RegistryObject<Builder> FOOD_LEVEL = BlockParty.SCENE_FILTERS.register("food_level", () -> f(() -> new AbstractFloat(BlockPartyNPC::getFoodLevel)));
    public static final RegistryObject<Builder> LOYALTY = BlockParty.SCENE_FILTERS.register("loyalty", () -> f(() -> new AbstractFloat(BlockPartyNPC::getLoyalty)));
    public static final RegistryObject<Builder> STRESS = BlockParty.SCENE_FILTERS.register("stress", () -> f(() -> new AbstractFloat(BlockPartyNPC::getStress)));
    public static final RegistryObject<Builder> PLAYER_COUNTER = BlockParty.SCENE_FILTERS.register("player_counter", () -> f(WithCounter.Player::new));
    public static final RegistryObject<Builder> PLAYER_HAS_COOKIE = BlockParty.SCENE_FILTERS.register("player_has_cookie", () -> f(WithCookie.Player::new));
    public static final RegistryObject<Builder> PLAYER_HELD_ITEM = BlockParty.SCENE_FILTERS.register("player_held_item", () -> f(WithHeldItem.Player::new));
    public static final RegistryObject<Builder> COUNTER = BlockParty.SCENE_FILTERS.register("counter", () -> f(WithCounter::new));
    public static final RegistryObject<Builder> HAS_COOKIE = BlockParty.SCENE_FILTERS.register("has_cookie", () -> f(WithCookie::new));
    public static final RegistryObject<Builder> HELD_ITEM = BlockParty.SCENE_FILTERS.register("held_item", () -> f(WithHeldItem::new));
    public static final RegistryObject<Builder> BLOCK = BlockParty.SCENE_FILTERS.register("block", () -> f(() -> new AbstractBlock(BlockPartyNPC::getVisibleBlockState)));
    public static final RegistryObject<Builder> FAMILY = BlockParty.SCENE_FILTERS.register("family_name", () -> f(() -> new AbstractString(BlockPartyNPC::getFamilyName)));
    public static final RegistryObject<Builder> NAME = BlockParty.SCENE_FILTERS.register("name", () -> f(() -> new AbstractString(BlockPartyNPC::getGivenName)));

    public static ISceneObservation build(RegistryObject<Builder> action) {
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
