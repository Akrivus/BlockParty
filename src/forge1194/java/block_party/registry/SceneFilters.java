package block_party.registry;

import block_party.BlockParty;
import block_party.scene.ISceneObservation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SceneFilters {
    public static final DeferredRegister<Builder> SCENE_FILTERS = DeferredRegister.create(new ResourceLocation(BlockParty.ID, "filters"), BlockParty.ID);
    public static final Supplier<IForgeRegistry<Builder>> FILTERS = SCENE_FILTERS.makeRegistry(() -> new RegistryBuilder<Builder>()
            .setMaxID(Integer.MAX_VALUE - 1));

    public static final RegistryObject<Builder> ALWAYS = register("always");
    public static final RegistryObject<Builder> NEVER = register("never");
    public static final RegistryObject<Builder> IS_CORPOREAL = register("is_corporeal");
    public static final RegistryObject<Builder> IS_ETHEREAL = register("is_ethereal");
    public static final RegistryObject<Builder> RAINING = register("if_raining");
    public static final RegistryObject<Builder> SUNNY = register("if_sunny");
    public static final RegistryObject<Builder> FULL_MOON = register("if_full_moon");
    public static final RegistryObject<Builder> GIBBOUS_MOON = register("if_gibbous_moon");
    public static final RegistryObject<Builder> HALF_MOON = register("if_half_moon");
    public static final RegistryObject<Builder> CRESCENT_MOON = register("if_crescent_moon");
    public static final RegistryObject<Builder> NEW_MOON = register("if_new_moon");
    public static final RegistryObject<Builder> MORNING = register("if_morning");
    public static final RegistryObject<Builder> NOON = register("if_noon");
    public static final RegistryObject<Builder> EVENING = register("if_evening");
    public static final RegistryObject<Builder> NIGHT = register("if_night");
    public static final RegistryObject<Builder> MIDNIGHT = register("if_midnight");
    public static final RegistryObject<Builder> DAWN = register("if_dawn");
    public static final RegistryObject<Builder> TIME = register("if_time");
    public static final RegistryObject<Builder> BLOOD_TYPE_AB = register("if_blood_type_ab");
    public static final RegistryObject<Builder> BLOOD_TYPE_B = register("if_blood_type_b");
    public static final RegistryObject<Builder> BLOOD_TYPE_A = register("if_blood_type_a");
    public static final RegistryObject<Builder> BLOOD_TYPE_O = register("if_blood_type_o");
    public static final RegistryObject<Builder> HIMEDERE = register("if_himedere");
    public static final RegistryObject<Builder> KUUDERE = register("if_kuudere");
    public static final RegistryObject<Builder> TSUNDERE = register("if_tsundere");
    public static final RegistryObject<Builder> YANDERE = register("if_yandere");
    public static final RegistryObject<Builder> DEREDERE = register("if_deredere");
    public static final RegistryObject<Builder> DANDERE = register("if_dandere");
    public static final RegistryObject<Builder> ANGRY = register("if_angry");
    public static final RegistryObject<Builder> BEGGING = register("if_begging");
    public static final RegistryObject<Builder> CONFUSED = register("if_confused");
    public static final RegistryObject<Builder> CRYING = register("if_crying");
    public static final RegistryObject<Builder> MISCHIEVOUS = register("if_mischievous");
    public static final RegistryObject<Builder> EMBARRASSED = register("if_embarrassed");
    public static final RegistryObject<Builder> HAPPY = register("if_happy");
    public static final RegistryObject<Builder> NORMAL = register("if_normal");
    public static final RegistryObject<Builder> PAINED = register("if_pained");
    public static final RegistryObject<Builder> PSYCHOTIC = register("if_psychotic");
    public static final RegistryObject<Builder> SCARED = register("if_scared");
    public static final RegistryObject<Builder> SICK = register("if_sick");
    public static final RegistryObject<Builder> SNOOTY = register("if_snooty");
    public static final RegistryObject<Builder> SMITTEN = register("if_smitten");
    public static final RegistryObject<Builder> TIRED = register("if_tired");
    public static final RegistryObject<Builder> MALE = register("if_male");
    public static final RegistryObject<Builder> FEMALE = register("if_female");
    public static final RegistryObject<Builder> NONBINARY = register("if_nonbinary");
    public static final RegistryObject<Builder> SELF = register("self");
    public static final RegistryObject<Builder> HEALTH = register("health");
    public static final RegistryObject<Builder> FOOD_LEVEL = register("food_level");
    public static final RegistryObject<Builder> LOYALTY = register("loyalty");
    public static final RegistryObject<Builder> STRESS = register("stress");
    public static final RegistryObject<Builder> PLAYER_COUNTER = register("player_counter");
    public static final RegistryObject<Builder> PLAYER_HAS_COOKIE = register("player_has_cookie");
    public static final RegistryObject<Builder> PLAYER_HELD_ITEM = register("player_held_item");
    public static final RegistryObject<Builder> COUNTER = register("counter");
    public static final RegistryObject<Builder> HAS_COOKIE = register("has_cookie");
    public static final RegistryObject<Builder> HELD_ITEM = register("held_item");
    public static final RegistryObject<Builder> BLOCK = register("block");
    public static final RegistryObject<Builder> FAMILY = register("family_name");
    public static final RegistryObject<Builder> NAME = register("name");

    public static ISceneObservation build(RegistryObject<Builder> action) {
        return action.get().build();
    }

    public static void register(IEventBus bus) {
        SCENE_FILTERS.register(bus);
    }

    public static ISceneObservation build(ResourceLocation location) {
        IForgeRegistry<Builder> registry = FILTERS.get();
        if (registry != null) {
            Builder builder = registry.getValue(location);
            return builder == null ? null : builder.build();
        }
        for (RegistryObject<Builder> filter : SCENE_FILTERS.getEntries()) {
            if (filter.getId().equals(location)) {
                if (filter.isPresent()) { return filter.get().build(); }
                return SceneCodecRegistries.buildFilter(location);
            }
        }
        return null;
    }

    private static Builder f(Supplier<ISceneObservation> action) {
        return new Builder(action);
    }

    private static RegistryObject<Builder> register(String name) {
        return SCENE_FILTERS.register(name, () -> f(SceneCodecRegistries.filterFactory(name)));
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
