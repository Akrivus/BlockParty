package block_party.client;

import block_party.network.payload.NpcDetailPayload;
import java.util.Locale;
import net.minecraft.network.chat.Component;

public final class ClientTranslations {
    private ClientTranslations() {
    }

    public static Component displayName(NpcDetailPayload npc) {
        if (npc.name().isBlank()) {
            return Component.translatable("entity.block_party.moe");
        }
        return Component.literal(npc.name());
    }

    public static Component page(int page, int pages) {
        return Component.translatable("gui.block_party.label.page", page, pages);
    }

    public static Component bloodType(String value) {
        return Component.translatable(traitKey("bloodtype", value));
    }

    public static Component dere(String value) {
        return Component.translatable(traitKey("dere", value));
    }

    public static Component zodiac(String value) {
        return Component.translatable(traitKey("zodiac", value));
    }

    public static Component gender(String value) {
        return Component.translatable(traitKey("gender", value));
    }

    public static Component relationship(boolean dead, float loyalty) {
        if (dead) {
            return Component.translatable("trait.block_party.relationship.dead");
        }
        if (loyalty > 18.0F) {
            return Component.translatable("trait.block_party.relationship.obsessed");
        }
        if (loyalty > 15.0F) {
            return Component.translatable("trait.block_party.relationship.close");
        }
        if (loyalty > 10.0F) {
            return Component.translatable("trait.block_party.relationship.friendly");
        }
        return Component.translatable("trait.block_party.relationship.acquainted");
    }

    private static String traitKey(String trait, String value) {
        String key = value == null || value.isBlank() ? "unknown" : value.toLowerCase(Locale.ROOT);
        return "trait.block_party." + trait + "." + key;
    }
}
