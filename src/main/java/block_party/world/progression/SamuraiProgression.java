package block_party.world.progression;

import block_party.registry.CustomItems;
import block_party.scene.SceneVariables;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class SamuraiProgression {
    public static final String BOOTS_OBTAINED = "samurai_boots_obtained";
    public static final String LEGS_OBTAINED = "samurai_legs_obtained";
    public static final String DOU_OBTAINED = "samurai_dou_obtained";
    public static final String KABUTO_OBTAINED = "samurai_kabuto_obtained";
    public static final String SWORD_OBTAINED = "samurai_sword_obtained";
    public static final String BROKEN_MASK_OBTAINED = "samurai_broken_mask_obtained";
    public static final String MASK_BROKEN = "samurai_mask_broken";
    public static final String SPIRIT_SUMMONED = "samurai_spirit_summoned";
    public static final String SPIRIT_DEFEATED = "samurai_spirit_defeated";
    public static final String SUZU_TRUST_READY = "suzu_trust_ready";
    public static final String CRYING_OBSIDIAN_ARC_COMPLETE = "crying_obsidian_arc_complete";
    public static final String TORII_VISITOR_MODE_UNLOCKED = "torii_visitor_mode_unlocked";

    private SamuraiProgression() {
    }

    public static EnumSet<Piece> getPieces(Player player) {
        EnumSet<Piece> pieces = EnumSet.noneOf(Piece.class);
        for (Piece piece : Piece.values()) {
            if (hasPiece(player, piece)) {
                pieces.add(piece);
            }
        }
        return pieces;
    }

    public static boolean hasPiece(Player player, Piece piece) {
        if (player == null || piece == null) {
            return false;
        }
        if (hasItem(player, piece.item())) {
            return true;
        }
        if (player.level() instanceof ServerLevel level) {
            UUID uuid = player.getUUID();
            return SceneVariables.get(level).playerCookies(uuid).has(piece.cookie());
        }
        return false;
    }

    public static boolean hasCompleteArmor(Player player) {
        return hasPiece(player, Piece.KABUTO)
                && hasPiece(player, Piece.DOU)
                && hasPiece(player, Piece.LEGS)
                && hasPiece(player, Piece.BOOTS);
    }

    public static void refresh(Player player) {
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        var cookies = SceneVariables.get(level).playerCookies(player.getUUID());
        for (Piece piece : Piece.values()) {
            if (hasItem(player, piece.item())) {
                cookies.set(piece.cookie(), "true");
            }
        }
        if (hasItem(player, CustomItems.ENTRIES.get("samurai_katana").get())) {
            cookies.set(SWORD_OBTAINED, "true");
        }
    }

    private static boolean hasItem(Player player, Item item) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(item)) {
                return true;
            }
        }
        for (ItemStack stack : player.getInventory().armor) {
            if (stack.is(item)) {
                return true;
            }
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(item)) {
                return true;
            }
        }
        return false;
    }

    public enum Piece {
        BOOTS(BOOTS_OBTAINED, "samurai_sabaton"),
        LEGS(LEGS_OBTAINED, "samurai_chausses"),
        DOU(DOU_OBTAINED, "samurai_cuirass"),
        KABUTO(KABUTO_OBTAINED, "masked_samurai_kabuto");

        private final String cookie;
        private final String itemId;

        Piece(String cookie, String itemId) {
            this.cookie = cookie;
            this.itemId = itemId;
        }

        public String cookie() {
            return this.cookie;
        }

        public Item item() {
            return CustomItems.ENTRIES.get(this.itemId).get();
        }

        public static Piece fromValue(String value) {
            for (Piece piece : values()) {
                if (piece.name().equalsIgnoreCase(value)) {
                    return piece;
                }
            }
            throw new IllegalArgumentException("Unknown samurai armor piece: " + value);
        }
    }
}
