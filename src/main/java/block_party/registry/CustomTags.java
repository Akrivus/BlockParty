package block_party.registry;

import block_party.BlockParty;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class CustomTags {
    public static final TagKey<Block> SPAWNS_MOES = TagKey.create(Registries.BLOCK, BlockParty.source("spawns_moes"));
    public static final TagKey<Block> GINKGO_LOGS = block("ginkgo_logs");
    public static final TagKey<Block> SAKURA_LOGS = block("sakura_logs");
    public static final TagKey<Block> SAKURA_WOOD = block("sakura_wood");
    public static final TagKey<Block> SHRINE_BASE_BLOCKS = block("shrine_base_blocks");
    public static final TagKey<Block> WISTERIA = block("wisteria");
    public static final TagKey<Block> HAS_CAT_FEATURES = block("moe/has_cat_features");
    public static final TagKey<Block> HAS_FESTIVE_TEXTURES = block("moe/has_festive_textures");
    public static final TagKey<Block> HAS_GLOW = block("moe/has_glow");
    public static final TagKey<Block> HAS_MALE_PRONOUNS = block("moe/has_male_pronouns");
    public static final TagKey<Block> HAS_NONBINARY_PRONOUNS = block("moe/has_nonbinary_pronouns");
    public static final TagKey<Block> HAS_FEMALE_PRONOUNS = block("moe/has_female_pronouns");
    public static final TagKey<Block> HAS_WINGS = block("moe/has_wings");
    public static final TagKey<Block> IGNORES_VOLUME = block("moe/ignores_volume");
    public static final TagKey<Block> BLOOD_TYPE_A = block("moe/traits/blood_type/a");
    public static final TagKey<Block> BLOOD_TYPE_AB = block("moe/traits/blood_type/ab");
    public static final TagKey<Block> BLOOD_TYPE_B = block("moe/traits/blood_type/b");
    public static final TagKey<Block> BLOOD_TYPE_O = block("moe/traits/blood_type/o");
    public static final TagKey<Block> NYANDERE = block("moe/traits/dere/nyandere");
    public static final TagKey<Block> HIMEDERE = block("moe/traits/dere/himedere");
    public static final TagKey<Block> KUUDERE = block("moe/traits/dere/kuudere");
    public static final TagKey<Block> TSUNDERE = block("moe/traits/dere/tsundere");
    public static final TagKey<Block> YANDERE = block("moe/traits/dere/yandere");
    public static final TagKey<Block> DEREDERE = block("moe/traits/dere/deredere");
    public static final TagKey<Block> DANDERE = block("moe/traits/dere/dandere");
    public static final TagKey<Block> ARIES = block("moe/traits/zodiac/aries");
    public static final TagKey<Block> TAURUS = block("moe/traits/zodiac/taurus");
    public static final TagKey<Block> GEMINI = block("moe/traits/zodiac/gemini");
    public static final TagKey<Block> CANCER = block("moe/traits/zodiac/cancer");
    public static final TagKey<Block> LEO = block("moe/traits/zodiac/leo");
    public static final TagKey<Block> VIRGO = block("moe/traits/zodiac/virgo");
    public static final TagKey<Block> LIBRA = block("moe/traits/zodiac/libra");
    public static final TagKey<Block> SCORPIO = block("moe/traits/zodiac/scorpio");
    public static final TagKey<Block> SAGITTARIUS = block("moe/traits/zodiac/sagittarius");
    public static final TagKey<Block> CAPRICORN = block("moe/traits/zodiac/capricorn");
    public static final TagKey<Block> AQUARIUS = block("moe/traits/zodiac/aquarius");
    public static final TagKey<Block> PISCES = block("moe/traits/zodiac/pisces");

    private CustomTags() {
    }

    private static TagKey<Block> block(String path) {
        return TagKey.create(Registries.BLOCK, BlockParty.source(path));
    }

    public static final class Items {
        public static final TagKey<Item> PARRY_SWORDS = item("parry_swords");
        public static final TagKey<Item> SAMURAI_ITEMS = item("samurai_items");
        public static final TagKey<Item> NO_REPAIR = item("no_repair");

        private Items() {
        }

        private static TagKey<Item> item(String path) {
            return TagKey.create(Registries.ITEM, BlockParty.source(path));
        }
    }
}
