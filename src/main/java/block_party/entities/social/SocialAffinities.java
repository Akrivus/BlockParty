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
        float affinity = 0.0F;
        float tension = 0.0F;
        float interest = 0.0F;
        for (Rule rule : rules) {
            if (rule.matches(observer, target)) {
                affinity += rule.affinity();
                tension += rule.tension();
                interest += rule.interest();
            }
        }
        return new MoeSocialRules.SocialSignal(clamp(affinity), clamp(tension), clamp(interest));
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

    public record Rule(Matcher observer, Matcher target, float affinity, float tension, float interest) {
        public boolean matches(Profile observerProfile, Profile targetProfile) {
            return this.observer.matches(observerProfile) && this.target.matches(targetProfile);
        }
    }
}
