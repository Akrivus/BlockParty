package moe.blocks.mod.data.conversation;

import moe.blocks.mod.entity.AbstractNPCEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.BiPredicate;

public enum Conditions {
    ALWAYS((character, player) -> true);

    private final BiPredicate<AbstractNPCEntity, PlayerEntity> condition;

    Conditions(BiPredicate<AbstractNPCEntity, PlayerEntity> condition) {
        this.condition = condition;
    }

    public boolean test(AbstractNPCEntity character, PlayerEntity player) {
        return this.condition.test(character, player);
    }
}
