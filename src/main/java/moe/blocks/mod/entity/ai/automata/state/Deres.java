package moe.blocks.mod.entity.ai.automata.state;

import moe.blocks.mod.MoeMod;
import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.partial.DieEntity;
import moe.blocks.mod.entity.partial.InteractEntity;
import moe.blocks.mod.init.MoeTags;
import moe.blocks.mod.util.Trans;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.List;

public enum Deres {
    HIMEDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.ONE, MoeTags.FARMER),
    KUUDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.TWO, MoeTags.ARCHER),
    TSUNDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.THREE, MoeTags.FIGHTER),
    YANDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.FOUR, MoeTags.FIGHTER),
    DEREDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.FIVE, MoeTags.BREEDER),
    DANDERE(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, DieEntity.Face.SIX, MoeTags.MINER);

    public final State state;
    private final ITag.INamedTag<Item> tools;

    Deres(State state, DieEntity.Face face, ITag.INamedTag<Item> tools) {
        this.state = state;
        MoeTags.LOVED_GIFTS.put(this, ItemTags.createOptional(new ResourceLocation(MoeMod.ID, String.format("gifts/loved/%s", this.name().toLowerCase()))));
        MoeTags.LIKED_GIFTS.put(this, ItemTags.createOptional(new ResourceLocation(MoeMod.ID, String.format("gifts/liked/%s", this.name().toLowerCase()))));
        MoeTags.HATED_GIFTS.put(this, ItemTags.createOptional(new ResourceLocation(MoeMod.ID, String.format("gifts/hated/%s", this.name().toLowerCase()))));
        Registry.SET.put(face, this);
        this.tools = tools;
    }

    public static Deres get(DieEntity.Face face) {
        return Registry.SET.getOrDefault(face, HIMEDERE);
    }

    public static MaterialColor getAura(Deres dere) {
        switch (dere) {
        case HIMEDERE:
            return MaterialColor.PURPLE;
        case KUUDERE:
            return MaterialColor.BLUE;
        case TSUNDERE:
            return MaterialColor.ADOBE;
        case YANDERE:
            return MaterialColor.RED;
        case DEREDERE:
            return MaterialColor.GREEN;
        default:
            return MaterialColor.YELLOW;
        }
    }

    public float getGiftFactor(ItemStack stack) {
        if (this.isLoved(stack)) { return 2.0F; }
        if (this.isLiked(stack)) { return 1.0F; }
        if (this.isHated(stack)) {
            return -1.0F;
        }
        return 0.0F;
    }

    public boolean isLoved(ItemStack stack) {
        return stack.getItem().isIn(MoeTags.LOVED_GIFTS.get(this));
    }

    public boolean isLiked(ItemStack stack) {
        return stack.getItem().isIn(MoeTags.LIKED_GIFTS.get(this));
    }

    public boolean isHated(ItemStack stack) {
        return stack.getItem().isIn(MoeTags.HATED_GIFTS.get(this));
    }

    public boolean isFavorite(ItemStack stack) {
        return stack.getItem().isIn(this.tools);
    }

    @Override
    public String toString() {
        return Trans.late(String.format("debug.moeblocks.deres.%s", this.name().toLowerCase()));
    }

    protected static class Registry {
        public static HashMap<DieEntity.Face, Deres> SET = new HashMap<>();
    }
}
