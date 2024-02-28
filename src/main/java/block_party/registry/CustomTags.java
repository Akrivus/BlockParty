package block_party.registry;

import block_party.BlockParty;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CustomTags {
    public static final TagKey<Block> HAS_CAT_FEATURES = bind(Type.BLOCK, "moe/has_cat_features");
    public static final TagKey<Block> HAS_FESTIVE_TEXTURES = bind(Type.BLOCK, "moe/has_festive_textures");
    public static final TagKey<Block> HAS_GLOW = bind(Type.BLOCK, "moe/has_glow");
    public static final TagKey<Block> HAS_MALE_PRONOUNS = bind(Type.BLOCK, "moe/has_male_pronouns");
    public static final TagKey<Block> HAS_NONBINARY_PRONOUNS = bind(Type.BLOCK, "moe/has_nonbinary_pronouns");
    public static final TagKey<Block> HAS_FEMALE_PRONOUNS = bind(Type.BLOCK, "moe/has_female_pronouns");
    public static final TagKey<Block> HAS_WINGS = bind(Type.BLOCK, "moe/has_wings");
    public static final TagKey<Block> IGNORES_VOLUME = bind(Type.BLOCK, "moe/ignores_volume");
    public static final TagKey<Block> BLOOD_TYPE_A = bind(Type.BLOCK, "moe/traits/blood_type/a");
    public static final TagKey<Block> BLOOD_TYPE_AB = bind(Type.BLOCK, "moe/traits/blood_type/ab");
    public static final TagKey<Block> BLOOD_TYPE_B = bind(Type.BLOCK, "moe/traits/blood_type/b");
    public static final TagKey<Block> BLOOD_TYPE_O = bind(Type.BLOCK, "moe/traits/blood_type/o");
    public static final TagKey<Block> NYANDERE = bind(Type.BLOCK, "moe/traits/dere/nyandere");
    public static final TagKey<Block> HIMEDERE = bind(Type.BLOCK, "moe/traits/dere/himedere");
    public static final TagKey<Block> KUUDERE = bind(Type.BLOCK, "moe/traits/dere/kuudere");
    public static final TagKey<Block> TSUNDERE = bind(Type.BLOCK, "moe/traits/dere/tsundere");
    public static final TagKey<Block> YANDERE = bind(Type.BLOCK, "moe/traits/dere/yandere");
    public static final TagKey<Block> DEREDERE = bind(Type.BLOCK, "moe/traits/dere/deredere");
    public static final TagKey<Block> DANDERE = bind(Type.BLOCK, "moe/traits/dere/dandere");
    public static final TagKey<Block> ARIES = bind(Type.BLOCK, "moe/traits/zodiac/aries");
    public static final TagKey<Block> TAURUS = bind(Type.BLOCK, "moe/traits/zodiac/taurus");
    public static final TagKey<Block> GEMINI = bind(Type.BLOCK, "moe/traits/zodiac/gemini");
    public static final TagKey<Block> CANCER = bind(Type.BLOCK, "moe/traits/zodiac/cancer");
    public static final TagKey<Block> LEO = bind(Type.BLOCK, "moe/traits/zodiac/leo");
    public static final TagKey<Block> VIRGO = bind(Type.BLOCK, "moe/traits/zodiac/virgo");
    public static final TagKey<Block> LIBRA = bind(Type.BLOCK, "moe/traits/zodiac/libra");
    public static final TagKey<Block> SCORPIO = bind(Type.BLOCK, "moe/traits/zodiac/scorpio");
    public static final TagKey<Block> SAGITTARIUS = bind(Type.BLOCK, "moe/traits/zodiac/sagittarius");
    public static final TagKey<Block> CAPRICORN = bind(Type.BLOCK, "moe/traits/zodiac/capricorn");
    public static final TagKey<Block> AQUARIUS = bind(Type.BLOCK, "moe/traits/zodiac/aquarius");
    public static final TagKey<Block> PISCES = bind(Type.BLOCK, "moe/traits/zodiac/pisces");

    public static class Blocks {
        public static final TagKey<Block> GINKGO_LOGS = bind(Type.BLOCK, "ginkgo_logs");
        public static final TagKey<Block> GINKGO_WOOD = bind(Type.BLOCK, "ginkgo_wood");
        public static final TagKey<Block> SAKURA_LOGS = bind(Type.BLOCK, "sakura_logs");
        public static final TagKey<Block> SAKURA_WOOD = bind(Type.BLOCK, "sakura_wood");
        public static final TagKey<Block> SHRINE_BASE_BLOCKS = bind(Type.BLOCK, "shrine_base_blocks");
        public static final TagKey<Block> SPAWNS_MOES = bind(Type.BLOCK, "spawns_moes");
        public static final TagKey<Block> SPAWNS_FIREFLIES = bind(Type.BLOCK, "spawns_fireflies");
        public static final TagKey<Block> WISTERIA = bind(Type.BLOCK, "wisteria");
    }

    public static class Items {
        public static final TagKey<Item> GINKGO_LOGS = bind(Type.ITEM, "ginkgo_logs");
        public static final TagKey<Item> GINKGO_WOOD = bind(Type.ITEM, "ginkgo_wood");
        public static final TagKey<Item> PARRY_SWORDS = bind(Type.ITEM, "parry_swords");
        public static final TagKey<Item> SAKURA_LOGS = bind(Type.ITEM, "sakura_logs");
        public static final TagKey<Item> SAKURA_WOOD = bind(Type.ITEM, "sakura_wood");
        public static final TagKey<Item> SAMURAI_ITEMS = bind(Type.ITEM, "samurai_items");
        public static final TagKey<Item> WISTERIA = bind(Type.ITEM, "wisteria");
    }

    public enum Type {
        BLOCK(Registries.BLOCK), ENTITY(Registries.ENTITY_TYPE), ITEM(Registries.ITEM);

        final ResourceKey<? extends Registry<?>> registry;

        Type(ResourceKey<? extends Registry<?>> registry) {
            this.registry = registry;
        }

        public <T> ResourceKey<? extends Registry<T>> registry() {
            return (ResourceKey<? extends Registry<T>>) this.registry;
        }
    }

    public static <T> TagKey<T> bind(Type type, ResourceLocation location) {
        return TagKey.<T>create(type.registry(), location);
    }

    public static <T> TagKey<T> bind(Type type, String domain, String name) {
        return bind(type, new ResourceLocation(domain, name));
    }

    public static <T> TagKey<T> bind(Type type, String name) {
        return bind(type, BlockParty.ID, name);
    }
}
