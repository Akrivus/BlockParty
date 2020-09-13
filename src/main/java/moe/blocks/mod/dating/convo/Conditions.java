package moe.blocks.mod.dating.convo;

import moe.blocks.mod.entity.partial.CharacterEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.BiPredicate;

public enum Conditions {
    ALWAYS((character, player) -> true);

    private final BiPredicate<CharacterEntity, PlayerEntity> condition;

    Conditions(BiPredicate<CharacterEntity, PlayerEntity> condition) {
        this.condition = condition;
    }

    public boolean test(CharacterEntity character, PlayerEntity player) {
        return this.condition.test(character, player);
    }
}
