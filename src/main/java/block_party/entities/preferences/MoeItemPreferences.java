package block_party.entities.preferences;

import block_party.entities.Moe;
import block_party.entities.social.SocialAffinities;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class MoeItemPreferences {
    private static List<Rule> rules = List.of();

    private MoeItemPreferences() {
    }

    public static PreferenceSignal signal(Moe moe, ItemStack stack) {
        return signal(new SocialAffinities.Profile(
                moe.getActualBlockState(),
                moe.getBloodType(),
                moe.getDere(),
                moe.getZodiac(),
                moe.getGender(),
                moe.getEmotion()), stack);
    }

    public static PreferenceSignal signal(SocialAffinities.Profile observer, ItemStack stack) {
        return signal(observer, stack, PreferenceLayer.ALL);
    }

    public static PreferenceSignal signal(SocialAffinities.Profile observer, ItemStack stack, PreferenceLayer layer) {
        float preference = 0.0F;
        float aversion = 0.0F;
        float interest = 0.0F;
        float begging = 0.0F;
        for (Rule rule : rules) {
            if ((layer == PreferenceLayer.ALL || rule.layer() == layer) && rule.matches(observer, stack)) {
                preference += rule.preference();
                aversion += rule.aversion();
                interest += rule.interest();
                begging += rule.begging();
            }
        }
        return new PreferenceSignal(clamp(preference), clamp(aversion), clamp(interest), clamp(begging));
    }

    public static LayeredSignal layeredSignal(SocialAffinities.Profile observer, ItemStack stack) {
        return new LayeredSignal(
                signal(observer, stack, PreferenceLayer.BLOCK),
                signal(observer, stack, PreferenceLayer.BLOOD_TYPE),
                signal(observer, stack, PreferenceLayer.DERE),
                signal(observer, stack, PreferenceLayer.ZODIAC),
                signal(observer, stack, PreferenceLayer.GENERAL));
    }

    public static int ruleCount() {
        return rules.size();
    }

    public static void replaceRules(List<Rule> next) {
        rules = List.copyOf(next);
    }

    public static Optional<ItemMatcher> parseItemMatcher(Optional<String> item, Optional<String> itemTag) {
        Optional<Item> exact = item.flatMap(MoeItemPreferences::item);
        Optional<TagKey<Item>> tag = itemTag.map(value -> TagKey.create(Registries.ITEM, ResourceLocation.parse(stripTagPrefix(value))));
        if (item.isPresent() && exact.isEmpty()) {
            return Optional.empty();
        }
        return exact.isPresent() || tag.isPresent() ? Optional.of(new ItemMatcher(exact, tag)) : Optional.empty();
    }

    private static Optional<Item> item(String value) {
        ResourceLocation id = ResourceLocation.parse(stripTagPrefix(value));
        return BuiltInRegistries.ITEM
                .get(ResourceKey.create(Registries.ITEM, id))
                .map(Holder.Reference::value);
    }

    private static String stripTagPrefix(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        return normalized.startsWith("#") ? normalized.substring(1) : normalized;
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    public record ItemMatcher(Optional<Item> item, Optional<TagKey<Item>> tag) {
        public boolean matches(ItemStack stack) {
            if (stack == null || stack.isEmpty()) {
                return false;
            }
            return this.item.map(stack::is).orElse(true)
                    && this.tag.map(stack::is).orElse(true);
        }
    }

    public enum PreferenceLayer {
        ALL,
        BLOCK,
        BLOOD_TYPE,
        DERE,
        ZODIAC,
        GENERAL;

        public static PreferenceLayer fromString(String value) {
            if (value == null || value.isBlank()) {
                return GENERAL;
            }
            try {
                return PreferenceLayer.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return GENERAL;
            }
        }

        public static PreferenceLayer fromSocialLayer(SocialAffinities.RuleLayer layer) {
            return switch (layer) {
                case BLOCK -> BLOCK;
                case BLOOD_TYPE -> BLOOD_TYPE;
                case DERE -> DERE;
                case ZODIAC -> ZODIAC;
                default -> GENERAL;
            };
        }
    }

    public record PreferenceSignal(float preference, float aversion, float interest, float begging) {
        public boolean wantsToBeg() {
            return this.begging >= 0.5F && this.preference > this.aversion;
        }

        public boolean liked() {
            return this.preference >= 0.35F && this.preference >= this.aversion;
        }

        public boolean disliked() {
            return this.aversion >= 0.35F && this.aversion > this.preference;
        }

        public boolean interesting() {
            return this.interest >= 0.35F;
        }

        public static PreferenceSignal neutral() {
            return new PreferenceSignal(0.0F, 0.0F, 0.0F, 0.0F);
        }
    }

    public record LayeredSignal(
            PreferenceSignal block,
            PreferenceSignal bloodType,
            PreferenceSignal dere,
            PreferenceSignal zodiac,
            PreferenceSignal general) {
        public PreferenceSignal combined() {
            return new PreferenceSignal(
                    clamp(this.block.preference() + this.bloodType.preference() + this.dere.preference() + this.zodiac.preference() + this.general.preference()),
                    clamp(this.block.aversion() + this.bloodType.aversion() + this.dere.aversion() + this.zodiac.aversion() + this.general.aversion()),
                    clamp(this.block.interest() + this.bloodType.interest() + this.dere.interest() + this.zodiac.interest() + this.general.interest()),
                    clamp(this.block.begging() + this.bloodType.begging() + this.dere.begging() + this.zodiac.begging() + this.general.begging()));
        }

        public PreferenceLayer strongestLayer() {
            PreferenceLayer strongest = PreferenceLayer.GENERAL;
            float best = strength(this.general);
            if (strength(this.block) > best) {
                strongest = PreferenceLayer.BLOCK;
                best = strength(this.block);
            }
            if (strength(this.bloodType) > best) {
                strongest = PreferenceLayer.BLOOD_TYPE;
                best = strength(this.bloodType);
            }
            if (strength(this.dere) > best) {
                strongest = PreferenceLayer.DERE;
                best = strength(this.dere);
            }
            if (strength(this.zodiac) > best) {
                strongest = PreferenceLayer.ZODIAC;
            }
            return strongest;
        }

        private static float strength(PreferenceSignal signal) {
            return signal.preference() + signal.aversion() + signal.interest() + signal.begging();
        }
    }

    public record Rule(
            SocialAffinities.Matcher observer,
            ItemMatcher item,
            PreferenceLayer layer,
            float preference,
            float aversion,
            float interest,
            float begging) {
        public Rule(SocialAffinities.Matcher observer, ItemMatcher item, float preference, float aversion, float interest, float begging) {
            this(observer, item, PreferenceLayer.fromSocialLayer(observer.inferredLayer()), preference, aversion, interest, begging);
        }

        public boolean matches(SocialAffinities.Profile observerProfile, ItemStack stack) {
            return this.observer.matches(observerProfile) && this.item.matches(stack);
        }
    }
}
