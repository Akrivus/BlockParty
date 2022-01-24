package block_party.registry;

import block_party.BlockParty;
import block_party.scene.Dialogue;
import block_party.scene.ISceneAction;
import block_party.scene.actions.EndAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SceneActions {
    public static final RegistryObject<Factory> DIALOGUE = BlockParty.SCENE_ACTIONS.register("dialogue", () -> f(() -> new Dialogue()));
    public static final RegistryObject<Factory> RESPONSE = BlockParty.SCENE_ACTIONS.register("response", () -> f(() -> new Dialogue()));
    public static final RegistryObject<Factory> END = BlockParty.SCENE_ACTIONS.register("end", () -> f(() -> new EndAction()));

    public static void add(DeferredRegister<Factory> registry, IEventBus bus) {
        registry.makeRegistry("scene_action", () -> new RegistryBuilder<Factory>().setType(Factory.class));
        registry.register(bus);
    }

    public static ISceneAction get(RegistryObject<Factory> action) {
        return action.get().get();
    }

    private static Factory f(Supplier<ISceneAction> action) {
        return new Factory(action);
    }

    public static class Factory extends ForgeRegistryEntry<Factory> {
        private final Supplier<ISceneAction> factory;

        public Factory(Supplier<ISceneAction> factory) {
            this.factory = factory;
        }

        public ISceneAction get() {
            return this.factory.get();
        }
    }
}
