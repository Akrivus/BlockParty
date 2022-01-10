package block_party.registry;

import block_party.BlockParty;
import block_party.scene.ISceneRequirement;
import block_party.scene.SceneRequirement;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SceneRequirements {
    public static final RegistryObject<SceneRequirements.Factory> ALWAYS = BlockParty.SCENE_REQUIREMENTS.register("always", () -> f(() -> SceneRequirement.ALWAYS));
    public static final RegistryObject<SceneRequirements.Factory> NEVER = BlockParty.SCENE_REQUIREMENTS.register("never", () -> f(() -> SceneRequirement.NEVER));
    public static final RegistryObject<SceneRequirements.Factory> MORNING = BlockParty.SCENE_REQUIREMENTS.register("morning", () -> f(() -> SceneRequirement.MORNING));
    public static final RegistryObject<SceneRequirements.Factory> NOON = BlockParty.SCENE_REQUIREMENTS.register("noon", () -> f(() -> SceneRequirement.NOON));
    public static final RegistryObject<SceneRequirements.Factory> EVENING = BlockParty.SCENE_REQUIREMENTS.register("evening", () -> f(() -> SceneRequirement.EVENING));
    public static final RegistryObject<SceneRequirements.Factory> NIGHT = BlockParty.SCENE_REQUIREMENTS.register("night", () -> f(() -> SceneRequirement.NIGHT));
    public static final RegistryObject<SceneRequirements.Factory> MIDNIGHT = BlockParty.SCENE_REQUIREMENTS.register("midnight", () -> f(() -> SceneRequirement.MIDNIGHT));
    public static final RegistryObject<SceneRequirements.Factory> DAWN = BlockParty.SCENE_REQUIREMENTS.register("dawn", () -> f(() -> SceneRequirement.DAWN));

    public static void add(DeferredRegister<Factory> registry, IEventBus bus) {
        registry.makeRegistry("scene_requirement", () -> new RegistryBuilder<Factory>().setType(Factory.class));
        registry.register(bus);
    }

    public static ISceneRequirement get(RegistryObject<SceneRequirements.Factory> action) {
        return action.get().get();
    }

    private static SceneRequirements.Factory f(Supplier<ISceneRequirement> action) {
        return new SceneRequirements.Factory(action);
    }

    public static class Factory extends ForgeRegistryEntry<Factory> {
        private final Supplier<ISceneRequirement> factory;

        public Factory(Supplier<ISceneRequirement> factory) {
            this.factory = factory;
        }

        public ISceneRequirement get() {
            return this.factory.get();
        }
    }
}
