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
    public static final RegistryObject<Builder> DIALOGUE = BlockParty.SCENE_ACTIONS.register("dialogue", () -> f(() -> new Dialogue()));
    public static final RegistryObject<Builder> RESPONSE = BlockParty.SCENE_ACTIONS.register("response", () -> f(() -> new Dialogue()));
    public static final RegistryObject<Builder> END = BlockParty.SCENE_ACTIONS.register("end", () -> f(() -> new EndAction()));

    public static void add(DeferredRegister<Builder> registry, IEventBus bus) {
        registry.makeRegistry("scene_action", () -> new RegistryBuilder<Builder>().setType(Builder.class));
        registry.register(bus);
    }

    public static ISceneAction build(RegistryObject<Builder> action) {
        return action.get().build();
    }

    private static Builder f(Supplier<ISceneAction> action) {
        return new Builder(action);
    }

    public static class Builder extends ForgeRegistryEntry<Builder> {
        private final Supplier<ISceneAction> builder;

        public Builder(Supplier<ISceneAction> factory) {
            this.builder = factory;
        }

        public ISceneAction build() {
            return this.builder.get();
        }
    }
}
