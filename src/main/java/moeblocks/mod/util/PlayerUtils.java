package moeblocks.mod.util;

import moeblocks.mod.entity.StudentEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.TranslationTextComponent;

public class PlayerUtils {
    public static ActionResultType showResult(PlayerEntity player, StudentEntity entity, String key, ActionResultType result) {
        player.sendStatusMessage(new TranslationTextComponent(key + (result.isSuccessOrConsume() ? ".success" : ".fail"), entity.getPlainName()), true);
        return result;
    }
}
