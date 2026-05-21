package block_party.registry;

import block_party.BlockParty;
import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneAction;
import block_party.scene.ISceneObservation;
import block_party.scene.SceneObservation;
import block_party.scene.actions.DoCounter;
import block_party.scene.actions.DoCookie;
import block_party.scene.actions.End;
import block_party.scene.actions.Hide;
import block_party.scene.actions.SendDialogue;
import block_party.scene.actions.SendResponse;
import block_party.scene.observations.AbstractBlock;
import block_party.scene.observations.AbstractEntity;
import block_party.scene.observations.WithCookie;
import block_party.scene.observations.WithCounter;
import block_party.scene.observations.WithHeldItem;
import block_party.scene.traits.BloodType;
import block_party.scene.traits.Dere;
import block_party.scene.traits.Emotion;
import block_party.scene.traits.Gender;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class SceneCodecRegistries {
    private static final Map<ResourceLocation, Supplier<ISceneAction>> ACTIONS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Supplier<ISceneObservation>> FILTERS = new LinkedHashMap<>();

    static {
        action("send_dialogue", SendDialogue::new);
        action("send_response", SendResponse::new);
        action("health", () -> new block_party.scene.actions.AbstractFloat(BlockPartyNPC::getHealth, BlockPartyNPC::setHealth));
        action("food_level", () -> new block_party.scene.actions.AbstractFloat(BlockPartyNPC::getFoodLevel, BlockPartyNPC::setFoodLevel));
        action("loyalty", () -> new block_party.scene.actions.AbstractFloat(BlockPartyNPC::getLoyalty, BlockPartyNPC::setLoyalty));
        action("stress", () -> new block_party.scene.actions.AbstractFloat(BlockPartyNPC::getStress, BlockPartyNPC::setStress));
        action("cookie", DoCookie::new);
        action("counter", DoCounter::new);
        action("hide", Hide::new);
        action("end", End::new);

        filter("always", () -> SceneObservation.ALWAYS);
        filter("never", () -> SceneObservation.NEVER);
        filter("is_corporeal", () -> SceneObservation.IS_CORPOREAL);
        filter("is_ethereal", () -> SceneObservation.IS_ETHEREAL);
        filter("if_raining", () -> SceneObservation.RAINING);
        filter("if_sunny", () -> SceneObservation.SUNNY);
        filter("if_full_moon", () -> SceneObservation.FULL_MOON);
        filter("if_gibbous_moon", () -> SceneObservation.GIBBOUS_MOON);
        filter("if_half_moon", () -> SceneObservation.HALF_MOON);
        filter("if_crescent_moon", () -> SceneObservation.CRESCENT_MOON);
        filter("if_new_moon", () -> SceneObservation.NEW_MOON);
        filter("if_morning", () -> SceneObservation.MORNING);
        filter("if_noon", () -> SceneObservation.NOON);
        filter("if_evening", () -> SceneObservation.EVENING);
        filter("if_night", () -> SceneObservation.NIGHT);
        filter("if_midnight", () -> SceneObservation.MIDNIGHT);
        filter("if_dawn", () -> SceneObservation.DAWN);
        filter("if_time", () -> new block_party.scene.observations.AbstractInteger((npc) -> (int) npc.level.getDayTime()));
        filter("if_blood_type_ab", () -> BloodType.AB);
        filter("if_blood_type_b", () -> BloodType.B);
        filter("if_blood_type_a", () -> BloodType.A);
        filter("if_blood_type_o", () -> BloodType.O);
        filter("if_himedere", () -> Dere.HIMEDERE);
        filter("if_kuudere", () -> Dere.KUUDERE);
        filter("if_tsundere", () -> Dere.TSUNDERE);
        filter("if_yandere", () -> Dere.YANDERE);
        filter("if_deredere", () -> Dere.DEREDERE);
        filter("if_dandere", () -> Dere.DANDERE);
        filter("if_angry", () -> Emotion.ANGRY);
        filter("if_begging", () -> Emotion.BEGGING);
        filter("if_confused", () -> Emotion.CONFUSED);
        filter("if_crying", () -> Emotion.CRYING);
        filter("if_mischievous", () -> Emotion.MISCHIEVOUS);
        filter("if_embarrassed", () -> Emotion.EMBARRASSED);
        filter("if_happy", () -> Emotion.HAPPY);
        filter("if_normal", () -> Emotion.NORMAL);
        filter("if_pained", () -> Emotion.PAINED);
        filter("if_psychotic", () -> Emotion.PSYCHOTIC);
        filter("if_scared", () -> Emotion.SCARED);
        filter("if_sick", () -> Emotion.SICK);
        filter("if_snooty", () -> Emotion.SNOOTY);
        filter("if_smitten", () -> Emotion.SMITTEN);
        filter("if_tired", () -> Emotion.TIRED);
        filter("if_male", () -> Gender.MALE);
        filter("if_female", () -> Gender.FEMALE);
        filter("if_nonbinary", () -> Gender.NONBINARY);
        filter("self", () -> new AbstractEntity((npc) -> npc));
        filter("health", () -> new block_party.scene.observations.AbstractFloat(BlockPartyNPC::getHealth));
        filter("food_level", () -> new block_party.scene.observations.AbstractFloat(BlockPartyNPC::getFoodLevel));
        filter("loyalty", () -> new block_party.scene.observations.AbstractFloat(BlockPartyNPC::getLoyalty));
        filter("stress", () -> new block_party.scene.observations.AbstractFloat(BlockPartyNPC::getStress));
        filter("player_counter", WithCounter.Player::new);
        filter("player_has_cookie", WithCookie.Player::new);
        filter("player_held_item", WithHeldItem.Player::new);
        filter("counter", WithCounter::new);
        filter("has_cookie", WithCookie::new);
        filter("held_item", WithHeldItem::new);
        filter("block", () -> new AbstractBlock(BlockPartyNPC::getVisibleBlockState));
        filter("family_name", () -> new block_party.scene.observations.AbstractString(BlockPartyNPC::getFamilyName));
        filter("name", () -> new block_party.scene.observations.AbstractString(BlockPartyNPC::getGivenName));
    }

    private SceneCodecRegistries() {
    }

    public static Supplier<ISceneAction> actionFactory(String name) {
        return ACTIONS.get(location(name));
    }

    public static Supplier<ISceneObservation> filterFactory(String name) {
        return FILTERS.get(location(name));
    }

    public static ISceneAction buildAction(ResourceLocation location) {
        Supplier<ISceneAction> factory = ACTIONS.get(location);
        return factory == null ? null : factory.get();
    }

    public static ISceneObservation buildFilter(ResourceLocation location) {
        Supplier<ISceneObservation> factory = FILTERS.get(location);
        return factory == null ? null : factory.get();
    }

    private static void action(String name, Supplier<ISceneAction> factory) {
        ACTIONS.put(location(name), factory);
    }

    private static void filter(String name, Supplier<ISceneObservation> factory) {
        FILTERS.put(location(name), factory);
    }

    private static ResourceLocation location(String name) {
        return new ResourceLocation(BlockParty.ID, name);
    }
}
