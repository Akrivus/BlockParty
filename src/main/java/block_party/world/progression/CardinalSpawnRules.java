package block_party.world.progression;

import block_party.registry.CustomTags;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class CardinalSpawnRules {
    private static final List<Rule> RULES = List.of(
            Rule.open(state -> state.is(Blocks.BELL)),
            Rule.gated(state -> state.is(CustomTags.SAMURAI_WOOD_CARDINALS),
                    ProgressionGate.worldCookie(SamuraiProgression.TORII_GATE_OPENED)),
            Rule.gated(state -> state.is(CustomTags.SAMURAI_DIRT_CARDINALS),
                    ProgressionGate.playerCookie(SamuraiProgression.BOOTS_OBTAINED)),
            Rule.gated(state -> state.is(CustomTags.SAMURAI_ORE_CARDINALS),
                    ProgressionGate.playerCookie(SamuraiProgression.LEGS_OBTAINED)),
            Rule.gated(state -> state.is(Blocks.CRYING_OBSIDIAN),
                    ProgressionGate.playerCookie(SamuraiProgression.BOOTS_OBTAINED),
                    ProgressionGate.playerCookie(SamuraiProgression.LEGS_OBTAINED),
                    ProgressionGate.playerCookie(SamuraiProgression.DOU_OBTAINED)));

    private CardinalSpawnRules() {
    }

    public static boolean canSpawn(ServerLevel level, BlockState state, UUID player) {
        if (!state.is(CustomTags.CARDINAL)) {
            return true;
        }
        for (Rule rule : RULES) {
            if (rule.matches(state)) {
                return rule.passes(level, player);
            }
        }
        return false;
    }

    private record Rule(Predicate<BlockState> matcher, BiPredicate<ServerLevel, UUID> gate, List<ProgressionGate> gates) {
        static Rule open(Predicate<BlockState> matcher) {
            return new Rule(matcher, (level, player) -> true, List.of());
        }

        static Rule gated(Predicate<BlockState> matcher, ProgressionGate... gates) {
            return new Rule(matcher, (level, player) -> true, List.of(gates));
        }

        static Rule gated(Predicate<BlockState> matcher, BiPredicate<ServerLevel, UUID> gate) {
            return new Rule(matcher, gate, List.of());
        }

        boolean matches(BlockState state) {
            return this.matcher.test(state);
        }

        boolean passes(ServerLevel level, UUID player) {
            if (!this.gate.test(level, player)) {
                return false;
            }
            for (ProgressionGate gate : this.gates) {
                if (!gate.passes(level, player)) {
                    return false;
                }
            }
            return true;
        }
    }
}
