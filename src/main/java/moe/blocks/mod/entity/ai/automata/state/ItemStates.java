package moe.blocks.mod.entity.ai.automata.state;

import moe.blocks.mod.entity.ai.automata.BlankState;
import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.ai.goal.TryStuffItemGoal;
import moe.blocks.mod.entity.ai.goal.attack.BowAttackGoal;
import moe.blocks.mod.entity.ai.goal.items.HarvestCropsGoal;
import moe.blocks.mod.entity.ai.goal.items.MineOresGoal;
import moe.blocks.mod.entity.ai.goal.target.HostileMobsTarget;
import moe.blocks.mod.entity.partial.NPCEntity;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.HashMap;
import java.util.List;

public enum ItemStates {
    DEFAULT(new BlankState()),
    FARMING_TOOLS(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {
            goals.add(new TryStuffItemGoal(entity, MoeTags.DROPPED_CROPS));
            goals.add(new HarvestCropsGoal(entity));
        }
    }, MoeTags.FARMING_TOOLS),
    MELEE_WEAPONS(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {
            goals.add(new TryStuffItemGoal(entity, MoeTags.DROPPED_LOOT));
            goals.add(new HostileMobsTarget(entity));
        }
    }, MoeTags.MELEE_WEAPONS),
    MINING_TOOLS(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {
            goals.add(new TryStuffItemGoal(entity, MoeTags.DROPPED_ORES));
            goals.add(new MineOresGoal(entity));
        }
    }, MoeTags.MINING_TOOLS),
    RANGED_WEAPONS(new State<NPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, NPCEntity entity) {
            goals.add(new TryStuffItemGoal(entity, MoeTags.DROPPED_LOOT));
            goals.add(new BowAttackGoal(entity));
        }
    }, MoeTags.RANGED_WEAPONS);

    public final State state;

    ItemStates(State state, ITag.INamedTag<Item> tag) {
        this(state, tag.getAllElements().toArray(new Item[0]));
    }

    ItemStates(State state, Item... items) {
        this.state = state;
        for (Item item : items) { Registry.SET.put(item, this); }
    }

    public static ItemStates get(ItemStack stack) {
        return Registry.SET.getOrDefault(stack.getItem(), DEFAULT);
    }

    protected static class Registry {
        public static HashMap<Item, ItemStates> SET = new HashMap<>();
    }
}
