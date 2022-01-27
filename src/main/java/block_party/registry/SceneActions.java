package block_party.registry;

import block_party.BlockParty;
import block_party.entities.BlockPartyNPC;
import block_party.scene.Dialogue;
import block_party.scene.ISceneAction;
import block_party.scene.actions.CookieAction;
import block_party.scene.actions.CounterAction;
import block_party.scene.actions.EndAction;
import block_party.scene.actions.FloatAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SceneActions {
    public static final RegistryObject<Builder> DIALOGUE = BlockParty.SCENE_ACTIONS.register("dialogue", () -> f(() -> new Dialogue()));
    public static final RegistryObject<Builder> RESPONSE = BlockParty.SCENE_ACTIONS.register("response", () -> f(() -> new Dialogue()));
    public static final RegistryObject<Builder> HEALTH = BlockParty.SCENE_ACTIONS.register("health", () -> f(() -> new FloatAction(BlockPartyNPC::getHealth, BlockPartyNPC::setHealth)));
    public static final RegistryObject<Builder> FOOD_LEVEL = BlockParty.SCENE_ACTIONS.register("food_level", () -> f(() -> new FloatAction(BlockPartyNPC::getFoodLevel, BlockPartyNPC::setFoodLevel)));
    public static final RegistryObject<Builder> LOYALTY = BlockParty.SCENE_ACTIONS.register("loyalty", () -> f(() -> new FloatAction(BlockPartyNPC::getLoyalty, BlockPartyNPC::setLoyalty)));
    public static final RegistryObject<Builder> STRESS = BlockParty.SCENE_ACTIONS.register("stress", () -> f(() -> new FloatAction(BlockPartyNPC::getStress, BlockPartyNPC::setStress)));
    public static final RegistryObject<Builder> COUNTER = BlockParty.SCENE_ACTIONS.register("counter", () -> f(CounterAction::new));
    public static final RegistryObject<Builder> COOKIES = BlockParty.SCENE_ACTIONS.register("cookies", () -> f(CookieAction::new));
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
