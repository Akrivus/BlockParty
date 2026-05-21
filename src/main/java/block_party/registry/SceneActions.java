package block_party.registry;

import block_party.BlockParty;
import block_party.scene.ISceneAction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SceneActions {
    public static final DeferredRegister<Builder> SCENE_ACTIONS = DeferredRegister.create(new ResourceLocation(BlockParty.ID, "actions"), BlockParty.ID);
    public static final Supplier<IForgeRegistry<Builder>> ACTIONS = SCENE_ACTIONS.makeRegistry(() -> new RegistryBuilder<Builder>()
            .setMaxID(Integer.MAX_VALUE - 1));

    public static final RegistryObject<Builder> SEND_DIALOGUE = register("send_dialogue");
    public static final RegistryObject<Builder> SEND_RESPONSE = register("send_response");
    public static final RegistryObject<Builder> HEALTH = register("health");
    public static final RegistryObject<Builder> FOOD_LEVEL = register("food_level");
    public static final RegistryObject<Builder> LOYALTY = register("loyalty");
    public static final RegistryObject<Builder> STRESS = register("stress");
    public static final RegistryObject<Builder> COOKIE = register("cookie");
    public static final RegistryObject<Builder> COUNTER = register("counter");
    public static final RegistryObject<Builder> HIDE = register("hide");
    public static final RegistryObject<Builder> END = register("end");

    public static void setup(NewRegistryEvent e) {
        e.create(new RegistryBuilder<Builder>()
                .setName(BlockParty.source("actions"))
                .setMaxID(Integer.MAX_VALUE - 1));
    }

    public static void register(IEventBus bus) {
        SCENE_ACTIONS.register(bus);
    }

    public static ISceneAction build(RegistryObject<Builder> action) {
        return action.get().build();
    }

    public static ISceneAction build(ResourceLocation location) {
        IForgeRegistry<Builder> registry = ACTIONS.get();
        if (registry != null) {
            Builder builder = registry.getValue(location);
            return builder == null ? null : builder.build();
        }
        for (RegistryObject<Builder> action : SCENE_ACTIONS.getEntries()) {
            if (action.getId().equals(location)) {
                if (action.isPresent()) { return action.get().build(); }
                return SceneCodecRegistries.buildAction(location);
            }
        }
        return null;
    }

    private static Builder f(Supplier<ISceneAction> action) {
        return new Builder(action);
    }

    private static RegistryObject<Builder> register(String name) {
        return SCENE_ACTIONS.register(name, () -> f(SceneCodecRegistries.actionFactory(name)));
    }

    public static class Builder {
        private final Supplier<ISceneAction> builder;

        public Builder(Supplier<ISceneAction> factory) {
            this.builder = factory;
        }

        public ISceneAction build() {
            return this.builder.get();
        }
    }
}
