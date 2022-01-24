package block_party.registry;

import block_party.BlockParty;
import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.trait.BloodType;
import block_party.npc.automata.trait.Dere;
import block_party.npc.automata.trait.Emotion;
import block_party.npc.automata.trait.Gender;
import block_party.scene.ISceneFilter;
import block_party.scene.SceneFilter;
import block_party.scene.filters.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SceneFilters {
    public static final RegistryObject<SceneFilters.Factory> ALWAYS = BlockParty.SCENE_FILTERS.register("always", () -> f(() -> SceneFilter.ALWAYS));
    public static final RegistryObject<SceneFilters.Factory> NEVER = BlockParty.SCENE_FILTERS.register("never", () -> f(() -> SceneFilter.NEVER));
    public static final RegistryObject<SceneFilters.Factory> RAINING = BlockParty.SCENE_FILTERS.register("raining", () -> f(() -> SceneFilter.RAINING));
    public static final RegistryObject<SceneFilters.Factory> SUNNY = BlockParty.SCENE_FILTERS.register("sunny", () -> f(() -> SceneFilter.SUNNY));
    public static final RegistryObject<SceneFilters.Factory> FULL_MOON = BlockParty.SCENE_FILTERS.register("full_moon", () -> f(() -> SceneFilter.FULL_MOON));
    public static final RegistryObject<SceneFilters.Factory> GIBBOUS_MOON = BlockParty.SCENE_FILTERS.register("gibbous_moon", () -> f(() -> SceneFilter.GIBBOUS_MOON));
    public static final RegistryObject<SceneFilters.Factory> HALF_MOON = BlockParty.SCENE_FILTERS.register("half_moon", () -> f(() -> SceneFilter.HALF_MOON));
    public static final RegistryObject<SceneFilters.Factory> CRESCENT_MOON = BlockParty.SCENE_FILTERS.register("crescent_moon", () -> f(() -> SceneFilter.CRESCENT_MOON));
    public static final RegistryObject<SceneFilters.Factory> NEW_MOON = BlockParty.SCENE_FILTERS.register("new_moon", () -> f(() -> SceneFilter.NEW_MOON));
    public static final RegistryObject<SceneFilters.Factory> MORNING = BlockParty.SCENE_FILTERS.register("morning", () -> f(() -> SceneFilter.MORNING));
    public static final RegistryObject<SceneFilters.Factory> NOON = BlockParty.SCENE_FILTERS.register("noon", () -> f(() -> SceneFilter.NOON));
    public static final RegistryObject<SceneFilters.Factory> EVENING = BlockParty.SCENE_FILTERS.register("evening", () -> f(() -> SceneFilter.EVENING));
    public static final RegistryObject<SceneFilters.Factory> NIGHT = BlockParty.SCENE_FILTERS.register("night", () -> f(() -> SceneFilter.NIGHT));
    public static final RegistryObject<SceneFilters.Factory> MIDNIGHT = BlockParty.SCENE_FILTERS.register("midnight", () -> f(() -> SceneFilter.MIDNIGHT));
    public static final RegistryObject<SceneFilters.Factory> DAWN = BlockParty.SCENE_FILTERS.register("dawn", () -> f(() -> SceneFilter.DAWN));
    public static final RegistryObject<SceneFilters.Factory> TIME = BlockParty.SCENE_FILTERS.register("time", () -> f(() -> new IntegerFilter((npc) -> (int) npc.level.getDayTime())));
    public static final RegistryObject<SceneFilters.Factory> BLOOD_TYPE_AB = BlockParty.SCENE_FILTERS.register("blood_type_ab", () -> f(() -> BloodType.AB));
    public static final RegistryObject<SceneFilters.Factory> BLOOD_TYPE_B = BlockParty.SCENE_FILTERS.register("blood_type_b", () -> f(() -> BloodType.B));
    public static final RegistryObject<SceneFilters.Factory> BLOOD_TYPE_A = BlockParty.SCENE_FILTERS.register("blood_type_a", () -> f(() -> BloodType.A));
    public static final RegistryObject<SceneFilters.Factory> BLOOD_TYPE_O = BlockParty.SCENE_FILTERS.register("blood_type_o", () -> f(() -> BloodType.O));
    public static final RegistryObject<SceneFilters.Factory> HIMEDERE = BlockParty.SCENE_FILTERS.register("himedere", () -> f(() -> Dere.HIMEDERE));
    public static final RegistryObject<SceneFilters.Factory> KUUDERE = BlockParty.SCENE_FILTERS.register("kuudere", () -> f(() -> Dere.KUUDERE));
    public static final RegistryObject<SceneFilters.Factory> TSUNDERE = BlockParty.SCENE_FILTERS.register("tsundere", () -> f(() -> Dere.TSUNDERE));
    public static final RegistryObject<SceneFilters.Factory> YANDERE = BlockParty.SCENE_FILTERS.register("yandere", () -> f(() -> Dere.YANDERE));
    public static final RegistryObject<SceneFilters.Factory> DEREDERE = BlockParty.SCENE_FILTERS.register("deredere", () -> f(() -> Dere.DEREDERE));
    public static final RegistryObject<SceneFilters.Factory> DANDERE = BlockParty.SCENE_FILTERS.register("dandere", () -> f(() -> Dere.DANDERE));
    public static final RegistryObject<SceneFilters.Factory> ANGRY = BlockParty.SCENE_FILTERS.register("angry", () -> f(() -> Emotion.ANGRY));
    public static final RegistryObject<SceneFilters.Factory> BEGGING = BlockParty.SCENE_FILTERS.register("begging", () -> f(() -> Emotion.BEGGING));
    public static final RegistryObject<SceneFilters.Factory> CONFUSED = BlockParty.SCENE_FILTERS.register("confused", () -> f(() -> Emotion.CONFUSED));
    public static final RegistryObject<SceneFilters.Factory> CRYING = BlockParty.SCENE_FILTERS.register("crying", () -> f(() -> Emotion.CRYING));
    public static final RegistryObject<SceneFilters.Factory> MISCHIEVOUS = BlockParty.SCENE_FILTERS.register("mischievous", () -> f(() -> Emotion.MISCHIEVOUS));
    public static final RegistryObject<SceneFilters.Factory> EMBARRASSED = BlockParty.SCENE_FILTERS.register("embarrassed", () -> f(() -> Emotion.EMBARRASSED));
    public static final RegistryObject<SceneFilters.Factory> HAPPY = BlockParty.SCENE_FILTERS.register("happy", () -> f(() -> Emotion.HAPPY));
    public static final RegistryObject<SceneFilters.Factory> NORMAL = BlockParty.SCENE_FILTERS.register("normal", () -> f(() -> Emotion.NORMAL));
    public static final RegistryObject<SceneFilters.Factory> PAINED = BlockParty.SCENE_FILTERS.register("pained", () -> f(() -> Emotion.PAINED));
    public static final RegistryObject<SceneFilters.Factory> PSYCHOTIC = BlockParty.SCENE_FILTERS.register("psychotic", () -> f(() -> Emotion.PSYCHOTIC));
    public static final RegistryObject<SceneFilters.Factory> SCARED = BlockParty.SCENE_FILTERS.register("scared", () -> f(() -> Emotion.SCARED));
    public static final RegistryObject<SceneFilters.Factory> SICK = BlockParty.SCENE_FILTERS.register("sick", () -> f(() -> Emotion.SICK));
    public static final RegistryObject<SceneFilters.Factory> SNOOTY = BlockParty.SCENE_FILTERS.register("snooty", () -> f(() -> Emotion.SNOOTY));
    public static final RegistryObject<SceneFilters.Factory> SMITTEN = BlockParty.SCENE_FILTERS.register("smitten", () -> f(() -> Emotion.SMITTEN));
    public static final RegistryObject<SceneFilters.Factory> TIRED = BlockParty.SCENE_FILTERS.register("tired", () -> f(() -> Emotion.TIRED));
    public static final RegistryObject<SceneFilters.Factory> MALE = BlockParty.SCENE_FILTERS.register("male", () -> f(() -> Gender.MALE));
    public static final RegistryObject<SceneFilters.Factory> FEMALE = BlockParty.SCENE_FILTERS.register("female", () -> f(() -> Gender.FEMALE));
    public static final RegistryObject<SceneFilters.Factory> NONBINARY = BlockParty.SCENE_FILTERS.register("nonbinary", () -> f(() -> Gender.NONBINARY));
    public static final RegistryObject<SceneFilters.Factory> HEALTH = BlockParty.SCENE_FILTERS.register("health", () -> f(() -> new FloatFilter(BlockPartyNPC::getHealth)));
    public static final RegistryObject<SceneFilters.Factory> FULLNESS = BlockParty.SCENE_FILTERS.register("fullness", () -> f(() -> new FloatFilter(BlockPartyNPC::getFullness)));
    public static final RegistryObject<SceneFilters.Factory> LOYALTY = BlockParty.SCENE_FILTERS.register("loyalty", () -> f(() -> new FloatFilter(BlockPartyNPC::getLoyalty)));
    public static final RegistryObject<SceneFilters.Factory> STRESS = BlockParty.SCENE_FILTERS.register("stress", () -> f(() -> new FloatFilter(BlockPartyNPC::getStress)));
    public static final RegistryObject<SceneFilters.Factory> PLAYER_COUNTER = BlockParty.SCENE_FILTERS.register("player_counter", () -> f(CounterFilter.Player::new));
    public static final RegistryObject<SceneFilters.Factory> PLAYER_HAS_COOKIE = BlockParty.SCENE_FILTERS.register("player_has_cookie", () -> f(CookieFilter.Player::new));
    public static final RegistryObject<SceneFilters.Factory> PLAYER_HELD_ITEM = BlockParty.SCENE_FILTERS.register("player_held_item", () -> f(HeldItemFilter.Player::new));
    public static final RegistryObject<SceneFilters.Factory> COUNTER = BlockParty.SCENE_FILTERS.register("counter", () -> f(CounterFilter::new));
    public static final RegistryObject<SceneFilters.Factory> HAS_COOKIE = BlockParty.SCENE_FILTERS.register("has_cookie", () -> f(CookieFilter::new));
    public static final RegistryObject<SceneFilters.Factory> HELD_ITEM = BlockParty.SCENE_FILTERS.register("held_item", () -> f(HeldItemFilter::new));
    public static final RegistryObject<SceneFilters.Factory> BLOCK = BlockParty.SCENE_FILTERS.register("block", () -> f(() -> new BlockFilter(BlockPartyNPC::getVisibleBlockState)));
    public static final RegistryObject<SceneFilters.Factory> FAMILY = BlockParty.SCENE_FILTERS.register("family", () -> f(() -> new StringFilter(BlockPartyNPC::getBlockName)));
    public static final RegistryObject<SceneFilters.Factory> NAME = BlockParty.SCENE_FILTERS.register("name", () -> f(() -> new StringFilter(BlockPartyNPC::getGivenName)));

    public static void add(DeferredRegister<Factory> registry, IEventBus bus) {
        registry.makeRegistry("scene_filter", () -> new RegistryBuilder<Factory>().setType(Factory.class));
        registry.register(bus);
    }

    public static ISceneFilter get(RegistryObject<SceneFilters.Factory> action) {
        return action.get().get();
    }

    private static SceneFilters.Factory f(Supplier<ISceneFilter> action) {
        return new SceneFilters.Factory(action);
    }

    public static class Factory extends ForgeRegistryEntry<Factory> {
        private final Supplier<ISceneFilter> factory;

        public Factory(Supplier<ISceneFilter> factory) {
            this.factory = factory;
        }

        public ISceneFilter get() {
            return this.factory.get();
        }
    }
}
