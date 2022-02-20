package block_party.registry;

import block_party.BlockParty;
import block_party.entities.BlockPartyNPC;
import block_party.scene.actions.*;
import block_party.scene.ISceneAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SceneActions {
    public static final RegistryObject<Builder> SEND_DIALOGUE = BlockParty.SCENE_ACTIONS.register("send_dialogue", () -> f(() -> new SendDialogue()));
    public static final RegistryObject<Builder> SEND_RESPONSE = BlockParty.SCENE_ACTIONS.register("send_response", () -> f(() -> new SendResponse()));
    public static final RegistryObject<Builder> HEALTH = BlockParty.SCENE_ACTIONS.register("health", () -> f(() -> new AbstractFloat(BlockPartyNPC::getHealth, BlockPartyNPC::setHealth)));
    public static final RegistryObject<Builder> FOOD_LEVEL = BlockParty.SCENE_ACTIONS.register("food_level", () -> f(() -> new AbstractFloat(BlockPartyNPC::getFoodLevel, BlockPartyNPC::setFoodLevel)));
    public static final RegistryObject<Builder> LOYALTY = BlockParty.SCENE_ACTIONS.register("loyalty", () -> f(() -> new AbstractFloat(BlockPartyNPC::getLoyalty, BlockPartyNPC::setLoyalty)));
    public static final RegistryObject<Builder> STRESS = BlockParty.SCENE_ACTIONS.register("stress", () -> f(() -> new AbstractFloat(BlockPartyNPC::getStress, BlockPartyNPC::setStress)));
    public static final RegistryObject<Builder> COOKIE = BlockParty.SCENE_ACTIONS.register("cookie", () -> f(DoCookie::new));
    public static final RegistryObject<Builder> COUNTER = BlockParty.SCENE_ACTIONS.register("counter", () -> f(DoCounter::new));
    public static final RegistryObject<Builder> HIDE = BlockParty.SCENE_ACTIONS.register("hide", () -> f(Hide::new));
    public static final RegistryObject<Builder> END = BlockParty.SCENE_ACTIONS.register("end", () -> f(() -> new End()));

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
