package block_party.entities.social;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class SocialAffinities {
    private static List<Rule> rules = List.of();

    private SocialAffinities() {
    }

    public static MoeSocialRules.SocialSignal signal(Profile observer, Profile target) {
        return signal(observer, target, RuleLayer.ALL);
    }

    public static MoeSocialRules.SocialSignal signal(Profile observer, Profile target, RuleLayer layer) {
        float affinity = 0.0F;
        float tension = 0.0F;
        float interest = 0.0F;
        for (Rule rule : rules) {
            if ((layer == RuleLayer.ALL || rule.layer() == layer) && rule.matches(observer, target)) {
                affinity += rule.affinity();
                tension += rule.tension();
                interest += rule.interest();
            }
        }
        return new MoeSocialRules.SocialSignal(clamp(affinity), clamp(tension), clamp(interest));
    }

    public static LayeredSignal layeredSignal(Profile observer, Profile target) {
        return new LayeredSignal(
                signal(observer, target, RuleLayer.BLOCK),
                signal(observer, target, RuleLayer.BLOOD_TYPE),
                signal(observer, target, RuleLayer.DERE),
                signal(observer, target, RuleLayer.ZODIAC),
                signal(observer, target, RuleLayer.EMOTION),
                signal(observer, target, RuleLayer.GENERAL));
    }

    public static int ruleCount() {
        return rules.size();
    }

    public static void replaceRules(List<Rule> next) {
        rules = List.copyOf(next);
    }

    public static Optional<BlockMatcher> parseBlockMatcher(Optional<String> block, Optional<String> blockTag) {
        Optional<Block> exact = block.flatMap(SocialAffinities::block);
        Optional<TagKey<Block>> tag = blockTag.map(value -> TagKey.create(Registries.BLOCK, ResourceLocation.parse(stripTagPrefix(value))));
        if (block.isPresent() && exact.isEmpty()) {
            return Optional.empty();
        }
        return exact.isPresent() || tag.isPresent() ? Optional.of(new BlockMatcher(exact, tag)) : Optional.empty();
    }

    private static Optional<Block> block(String value) {
        ResourceLocation id = ResourceLocation.parse(stripTagPrefix(value));
        return BuiltInRegistries.BLOCK
                .get(ResourceKey.create(Registries.BLOCK, id))
                .map(Holder.Reference::value);
    }

    private static String stripTagPrefix(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        return normalized.startsWith("#") ? normalized.substring(1) : normalized;
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? "" : value.toUpperCase(Locale.ROOT);
    }

    public record Profile(
            BlockState blockState,
            String bloodType,
            String dere,
            String zodiac,
            String gender,
            String emotion) {
        public Profile {
            bloodType = normalize(bloodType);
            dere = normalize(dere);
            zodiac = normalize(zodiac);
            gender = normalize(gender);
            emotion = normalize(emotion);
        }
    }

    public record Matcher(
            Optional<BlockMatcher> block,
            Optional<String> bloodType,
            Optional<String> dere,
            Optional<String> zodiac,
            Optional<String> gender,
            Optional<String> emotion) {
        public Matcher {
            block = block == null ? Optional.empty() : block;
            bloodType = bloodType.map(SocialAffinities::normalize);
            dere = dere.map(SocialAffinities::normalize);
            zodiac = zodiac.map(SocialAffinities::normalize);
            gender = gender.map(SocialAffinities::normalize);
            emotion = emotion.map(SocialAffinities::normalize);
        }

        public boolean isEmpty() {
            return this.block.isEmpty()
                    && this.bloodType.isEmpty()
                    && this.dere.isEmpty()
                    && this.zodiac.isEmpty()
                    && this.gender.isEmpty()
                    && this.emotion.isEmpty();
        }

        public boolean matches(Profile profile) {
            return this.block.map(value -> value.matches(profile.blockState())).orElse(true)
                    && matches(this.bloodType, profile.bloodType())
                    && matches(this.dere, profile.dere())
                    && matches(this.zodiac, profile.zodiac())
                    && matches(this.gender, profile.gender())
                    && matches(this.emotion, profile.emotion());
        }

        public RuleLayer inferredLayer() {
            if (this.block.isPresent()) {
                return RuleLayer.BLOCK;
            }
            if (this.zodiac.isPresent()) {
                return RuleLayer.ZODIAC;
            }
            if (this.dere.isPresent()) {
                return RuleLayer.DERE;
            }
            if (this.bloodType.isPresent()) {
                return RuleLayer.BLOOD_TYPE;
            }
            if (this.emotion.isPresent()) {
                return RuleLayer.EMOTION;
            }
            return RuleLayer.GENERAL;
        }

        private static boolean matches(Optional<String> expected, String actual) {
            return expected.isEmpty() || expected.get().equals(actual);
        }
    }

    public record BlockMatcher(Optional<Block> block, Optional<TagKey<Block>> tag) {
        public boolean matches(BlockState state) {
            if (state == null) {
                return false;
            }
            return this.block.map(value -> state.getBlock() == value).orElse(true)
                    && this.tag.map(state::is).orElse(true);
        }
    }

    public enum RuleLayer {
        ALL,
        BLOCK,
        BLOOD_TYPE,
        DERE,
        ZODIAC,
        EMOTION,
        GENERAL;

        public static RuleLayer fromString(String value) {
            if (value == null || value.isBlank()) {
                return GENERAL;
            }
            try {
                return RuleLayer.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return GENERAL;
            }
        }
    }

    public record LayeredSignal(
            MoeSocialRules.SocialSignal block,
            MoeSocialRules.SocialSignal bloodType,
            MoeSocialRules.SocialSignal dere,
            MoeSocialRules.SocialSignal zodiac,
            MoeSocialRules.SocialSignal emotion,
            MoeSocialRules.SocialSignal general) {
        public MoeSocialRules.SocialSignal combined() {
            return MoeSocialRules.combine(
                    MoeSocialRules.combine(MoeSocialRules.combine(this.block, this.bloodType), MoeSocialRules.combine(this.dere, this.zodiac)),
                    MoeSocialRules.combine(this.emotion, this.general));
        }

        public RuleLayer strongestLayer() {
            RuleLayer strongest = RuleLayer.GENERAL;
            float best = strength(this.general);
            if (strength(this.block) > best) {
                strongest = RuleLayer.BLOCK;
                best = strength(this.block);
            }
            if (strength(this.bloodType) > best) {
                strongest = RuleLayer.BLOOD_TYPE;
                best = strength(this.bloodType);
            }
            if (strength(this.dere) > best) {
                strongest = RuleLayer.DERE;
                best = strength(this.dere);
            }
            if (strength(this.zodiac) > best) {
                strongest = RuleLayer.ZODIAC;
                best = strength(this.zodiac);
            }
            if (strength(this.emotion) > best) {
                strongest = RuleLayer.EMOTION;
            }
            return strongest;
        }

        private static float strength(MoeSocialRules.SocialSignal signal) {
            return signal.affinity() + signal.tension() + signal.interest();
        }
    }

    public record Rule(Matcher observer, Matcher target, RuleLayer layer, float affinity, float tension, float interest) {
        public Rule(Matcher observer, Matcher target, float affinity, float tension, float interest) {
            this(observer, target, inferLayer(observer, target), affinity, tension, interest);
        }

        public boolean matches(Profile observerProfile, Profile targetProfile) {
            return this.observer.matches(observerProfile) && this.target.matches(targetProfile);
        }

        public static RuleLayer inferLayer(Matcher observer, Matcher target) {
            RuleLayer observerLayer = observer.inferredLayer();
            RuleLayer targetLayer = target.inferredLayer();
            if (observerLayer == RuleLayer.BLOCK || targetLayer == RuleLayer.BLOCK) {
                return RuleLayer.BLOCK;
            }
            if (observerLayer == RuleLayer.ZODIAC || targetLayer == RuleLayer.ZODIAC) {
                return RuleLayer.ZODIAC;
            }
            if (observerLayer == RuleLayer.DERE || targetLayer == RuleLayer.DERE) {
                return RuleLayer.DERE;
            }
            if (observerLayer == RuleLayer.BLOOD_TYPE || targetLayer == RuleLayer.BLOOD_TYPE) {
                return RuleLayer.BLOOD_TYPE;
            }
            if (observerLayer == RuleLayer.EMOTION || targetLayer == RuleLayer.EMOTION) {
                return RuleLayer.EMOTION;
            }
            return RuleLayer.GENERAL;
        }
    }
}
