package block_party.registry;

import block_party.BlockParty;
import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneAction;
import block_party.scene.actions.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class SceneActions {
    public static final Supplier<Builder> SEND_DIALOGUE = BlockParty.SCENE_ACTIONS.register("send_dialogue", () -> f(() -> new SendDialogue()));
    public static final Supplier<Builder> SEND_RESPONSE = BlockParty.SCENE_ACTIONS.register("send_response", () -> f(() -> new SendResponse()));
    public static final Supplier<Builder> HEALTH = BlockParty.SCENE_ACTIONS.register("health", () -> f(() -> new AbstractFloat(BlockPartyNPC::getHealth, BlockPartyNPC::setHealth)));
    public static final Supplier<Builder> FOOD_LEVEL = BlockParty.SCENE_ACTIONS.register("food_level", () -> f(() -> new AbstractFloat(BlockPartyNPC::getFoodLevel, BlockPartyNPC::setFoodLevel)));
    public static final Supplier<Builder> LOYALTY = BlockParty.SCENE_ACTIONS.register("loyalty", () -> f(() -> new AbstractFloat(BlockPartyNPC::getLoyalty, BlockPartyNPC::setLoyalty)));
    public static final Supplier<Builder> STRESS = BlockParty.SCENE_ACTIONS.register("stress", () -> f(() -> new AbstractFloat(BlockPartyNPC::getStress, BlockPartyNPC::setStress)));
    public static final Supplier<Builder> COOKIE = BlockParty.SCENE_ACTIONS.register("cookie", () -> f(DoCookie::new));
    public static final Supplier<Builder> COUNTER = BlockParty.SCENE_ACTIONS.register("counter", () -> f(DoCounter::new));
    public static final Supplier<Builder> HIDE = BlockParty.SCENE_ACTIONS.register("hide", () -> f(Hide::new));
    public static final Supplier<Builder> END = BlockParty.SCENE_ACTIONS.register("end", () -> f(() -> new End()));

    public static void setup(NewRegistryEvent e) {
        e.create(new RegistryBuilder<Builder>()
                .setName(BlockParty.source("actions"))
                .setMaxID(Integer.MAX_VALUE - 1));
    }

    public static ISceneAction build(Supplier<Builder> action) {
        return action.get().build();
    }

    private static Builder f(Supplier<ISceneAction> action) {
        return new Builder(action);
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
