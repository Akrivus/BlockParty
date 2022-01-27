package block_party.registry;

import block_party.BlockParty;
import block_party.npc.BlockPartyNPC;
import block_party.scene.filters.BloodType;
import block_party.scene.filters.Dere;
import block_party.scene.filters.Emotion;
import block_party.scene.filters.Gender;
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
    public static final RegistryObject<Builder> ALWAYS = BlockParty.SCENE_FILTERS.register("always", () -> f(() -> SceneFilter.ALWAYS));
    public static final RegistryObject<Builder> NEVER = BlockParty.SCENE_FILTERS.register("never", () -> f(() -> SceneFilter.NEVER));
    public static final RegistryObject<Builder> RAINING = BlockParty.SCENE_FILTERS.register("raining", () -> f(() -> SceneFilter.RAINING));
    public static final RegistryObject<Builder> SUNNY = BlockParty.SCENE_FILTERS.register("sunny", () -> f(() -> SceneFilter.SUNNY));
    public static final RegistryObject<Builder> FULL_MOON = BlockParty.SCENE_FILTERS.register("full_moon", () -> f(() -> SceneFilter.FULL_MOON));
    public static final RegistryObject<Builder> GIBBOUS_MOON = BlockParty.SCENE_FILTERS.register("gibbous_moon", () -> f(() -> SceneFilter.GIBBOUS_MOON));
    public static final RegistryObject<Builder> HALF_MOON = BlockParty.SCENE_FILTERS.register("half_moon", () -> f(() -> SceneFilter.HALF_MOON));
    public static final RegistryObject<Builder> CRESCENT_MOON = BlockParty.SCENE_FILTERS.register("crescent_moon", () -> f(() -> SceneFilter.CRESCENT_MOON));
    public static final RegistryObject<Builder> NEW_MOON = BlockParty.SCENE_FILTERS.register("new_moon", () -> f(() -> SceneFilter.NEW_MOON));
    public static final RegistryObject<Builder> MORNING = BlockParty.SCENE_FILTERS.register("morning", () -> f(() -> SceneFilter.MORNING));
    public static final RegistryObject<Builder> NOON = BlockParty.SCENE_FILTERS.register("noon", () -> f(() -> SceneFilter.NOON));
    public static final RegistryObject<Builder> EVENING = BlockParty.SCENE_FILTERS.register("evening", () -> f(() -> SceneFilter.EVENING));
    public static final RegistryObject<Builder> NIGHT = BlockParty.SCENE_FILTERS.register("night", () -> f(() -> SceneFilter.NIGHT));
    public static final RegistryObject<Builder> MIDNIGHT = BlockParty.SCENE_FILTERS.register("midnight", () -> f(() -> SceneFilter.MIDNIGHT));
    public static final RegistryObject<Builder> DAWN = BlockParty.SCENE_FILTERS.register("dawn", () -> f(() -> SceneFilter.DAWN));
    public static final RegistryObject<Builder> TIME = BlockParty.SCENE_FILTERS.register("time", () -> f(() -> new IntegerFilter((npc) -> (int) npc.level.getDayTime())));
    public static final RegistryObject<Builder> BLOOD_TYPE_AB = BlockParty.SCENE_FILTERS.register("blood_type_ab", () -> f(() -> BloodType.AB));
    public static final RegistryObject<Builder> BLOOD_TYPE_B = BlockParty.SCENE_FILTERS.register("blood_type_b", () -> f(() -> BloodType.B));
    public static final RegistryObject<Builder> BLOOD_TYPE_A = BlockParty.SCENE_FILTERS.register("blood_type_a", () -> f(() -> BloodType.A));
    public static final RegistryObject<Builder> BLOOD_TYPE_O = BlockParty.SCENE_FILTERS.register("blood_type_o", () -> f(() -> BloodType.O));
    public static final RegistryObject<Builder> HIMEDERE = BlockParty.SCENE_FILTERS.register("himedere", () -> f(() -> Dere.HIMEDERE));
    public static final RegistryObject<Builder> KUUDERE = BlockParty.SCENE_FILTERS.register("kuudere", () -> f(() -> Dere.KUUDERE));
    public static final RegistryObject<Builder> TSUNDERE = BlockParty.SCENE_FILTERS.register("tsundere", () -> f(() -> Dere.TSUNDERE));
    public static final RegistryObject<Builder> YANDERE = BlockParty.SCENE_FILTERS.register("yandere", () -> f(() -> Dere.YANDERE));
    public static final RegistryObject<Builder> DEREDERE = BlockParty.SCENE_FILTERS.register("deredere", () -> f(() -> Dere.DEREDERE));
    public static final RegistryObject<Builder> DANDERE = BlockParty.SCENE_FILTERS.register("dandere", () -> f(() -> Dere.DANDERE));
    public static final RegistryObject<Builder> ANGRY = BlockParty.SCENE_FILTERS.register("angry", () -> f(() -> Emotion.ANGRY));
    public static final RegistryObject<Builder> BEGGING = BlockParty.SCENE_FILTERS.register("begging", () -> f(() -> Emotion.BEGGING));
    public static final RegistryObject<Builder> CONFUSED = BlockParty.SCENE_FILTERS.register("confused", () -> f(() -> Emotion.CONFUSED));
    public static final RegistryObject<Builder> CRYING = BlockParty.SCENE_FILTERS.register("crying", () -> f(() -> Emotion.CRYING));
    public static final RegistryObject<Builder> MISCHIEVOUS = BlockParty.SCENE_FILTERS.register("mischievous", () -> f(() -> Emotion.MISCHIEVOUS));
    public static final RegistryObject<Builder> EMBARRASSED = BlockParty.SCENE_FILTERS.register("embarrassed", () -> f(() -> Emotion.EMBARRASSED));
    public static final RegistryObject<Builder> HAPPY = BlockParty.SCENE_FILTERS.register("happy", () -> f(() -> Emotion.HAPPY));
    public static final RegistryObject<Builder> NORMAL = BlockParty.SCENE_FILTERS.register("normal", () -> f(() -> Emotion.NORMAL));
    public static final RegistryObject<Builder> PAINED = BlockParty.SCENE_FILTERS.register("pained", () -> f(() -> Emotion.PAINED));
    public static final RegistryObject<Builder> PSYCHOTIC = BlockParty.SCENE_FILTERS.register("psychotic", () -> f(() -> Emotion.PSYCHOTIC));
    public static final RegistryObject<Builder> SCARED = BlockParty.SCENE_FILTERS.register("scared", () -> f(() -> Emotion.SCARED));
    public static final RegistryObject<Builder> SICK = BlockParty.SCENE_FILTERS.register("sick", () -> f(() -> Emotion.SICK));
    public static final RegistryObject<Builder> SNOOTY = BlockParty.SCENE_FILTERS.register("snooty", () -> f(() -> Emotion.SNOOTY));
    public static final RegistryObject<Builder> SMITTEN = BlockParty.SCENE_FILTERS.register("smitten", () -> f(() -> Emotion.SMITTEN));
    public static final RegistryObject<Builder> TIRED = BlockParty.SCENE_FILTERS.register("tired", () -> f(() -> Emotion.TIRED));
    public static final RegistryObject<Builder> MALE = BlockParty.SCENE_FILTERS.register("male", () -> f(() -> Gender.MALE));
    public static final RegistryObject<Builder> FEMALE = BlockParty.SCENE_FILTERS.register("female", () -> f(() -> Gender.FEMALE));
    public static final RegistryObject<Builder> NONBINARY = BlockParty.SCENE_FILTERS.register("nonbinary", () -> f(() -> Gender.NONBINARY));
    public static final RegistryObject<Builder> HEALTH = BlockParty.SCENE_FILTERS.register("health", () -> f(() -> new FloatFilter(BlockPartyNPC::getHealth)));
    public static final RegistryObject<Builder> FULLNESS = BlockParty.SCENE_FILTERS.register("fullness", () -> f(() -> new FloatFilter(BlockPartyNPC::getFullness)));
    public static final RegistryObject<Builder> LOYALTY = BlockParty.SCENE_FILTERS.register("loyalty", () -> f(() -> new FloatFilter(BlockPartyNPC::getLoyalty)));
    public static final RegistryObject<Builder> STRESS = BlockParty.SCENE_FILTERS.register("stress", () -> f(() -> new FloatFilter(BlockPartyNPC::getStress)));
    public static final RegistryObject<Builder> PLAYER_COUNTER = BlockParty.SCENE_FILTERS.register("player_counter", () -> f(CounterFilter.Player::new));
    public static final RegistryObject<Builder> PLAYER_HAS_COOKIE = BlockParty.SCENE_FILTERS.register("player_has_cookie", () -> f(CookieFilter.Player::new));
    public static final RegistryObject<Builder> PLAYER_HELD_ITEM = BlockParty.SCENE_FILTERS.register("player_held_item", () -> f(HeldItemFilter.Player::new));
    public static final RegistryObject<Builder> COUNTER = BlockParty.SCENE_FILTERS.register("counter", () -> f(CounterFilter::new));
    public static final RegistryObject<Builder> HAS_COOKIE = BlockParty.SCENE_FILTERS.register("has_cookie", () -> f(CookieFilter::new));
    public static final RegistryObject<Builder> HELD_ITEM = BlockParty.SCENE_FILTERS.register("held_item", () -> f(HeldItemFilter::new));
    public static final RegistryObject<Builder> BLOCK = BlockParty.SCENE_FILTERS.register("block", () -> f(() -> new BlockFilter(BlockPartyNPC::getVisibleBlockState)));
    public static final RegistryObject<Builder> FAMILY = BlockParty.SCENE_FILTERS.register("family", () -> f(() -> new StringFilter(BlockPartyNPC::getBlockName)));
    public static final RegistryObject<Builder> NAME = BlockParty.SCENE_FILTERS.register("name", () -> f(() -> new StringFilter(BlockPartyNPC::getGivenName)));

    public static void add(DeferredRegister<Builder> registry, IEventBus bus) {
        registry.makeRegistry("scene_filter", () -> new RegistryBuilder<Builder>().setType(Builder.class));
        registry.register(bus);
    }

    public static ISceneFilter build(RegistryObject<Builder> action) {
        return action.get().build();
    }

    private static Builder f(Supplier<ISceneFilter> action) {
        return new Builder(action);
    }

    public static class Builder extends ForgeRegistryEntry<Builder> {
        private final Supplier<ISceneFilter> builder;

        public Builder(Supplier<ISceneFilter> builder) {
            this.builder = builder;
        }

        public ISceneFilter build() {
            return this.builder.get();
        }
    }
}
