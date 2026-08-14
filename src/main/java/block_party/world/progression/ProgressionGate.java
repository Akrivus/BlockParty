package block_party.world.progression;

import block_party.scene.SceneVariables;
import block_party.scene.data.Counters;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;

public record ProgressionGate(Scope scope, Kind kind, String name, Operation operation, String value) {
    public static ProgressionGate playerCookie(String name) {
        return new ProgressionGate(Scope.PLAYER, Kind.COOKIE, name, Operation.EXISTS, "");
    }

    public static ProgressionGate worldCookie(String name) {
        return new ProgressionGate(Scope.WORLD, Kind.COOKIE, name, Operation.EXISTS, "");
    }

    public static ProgressionGate playerCookieEquals(String name, String value) {
        return new ProgressionGate(Scope.PLAYER, Kind.COOKIE, name, Operation.EQUALS, value);
    }

    public static ProgressionGate worldCookieEquals(String name, String value) {
        return new ProgressionGate(Scope.WORLD, Kind.COOKIE, name, Operation.EQUALS, value);
    }

    public static ProgressionGate playerCounterAtLeast(String name, int value) {
        return new ProgressionGate(Scope.PLAYER, Kind.COUNTER, name, Operation.AT_LEAST, Integer.toString(value));
    }

    public static ProgressionGate worldCounterAtLeast(String name, int value) {
        return new ProgressionGate(Scope.WORLD, Kind.COUNTER, name, Operation.AT_LEAST, Integer.toString(value));
    }

    public boolean passes(ServerLevel level, UUID player) {
        SceneVariables variables = SceneVariables.get(level);
        return switch (this.scope) {
            case PLAYER -> player != null && this.passes(variables.player(player));
            case WORLD -> this.passes(variables.world());
        };
    }

    private boolean passes(block_party.scene.SceneVariableStore store) {
        return switch (this.kind) {
            case COOKIE -> this.cookiePasses(store.cookies().get(this.name));
            case COUNTER -> this.counterPasses(store.counters());
        };
    }

    private boolean cookiePasses(String actual) {
        return switch (this.operation) {
            case EXISTS -> actual != null;
            case EQUALS -> this.value.equals(actual);
            case AT_LEAST -> false;
        };
    }

    private boolean counterPasses(Counters counters) {
        int actual = counters.get(this.name) == null ? 0 : counters.get(this.name);
        int expected;
        try {
            expected = Integer.parseInt(this.value);
        } catch (NumberFormatException exception) {
            return false;
        }
        return switch (this.operation) {
            case EXISTS -> counters.has(this.name);
            case EQUALS -> actual == expected;
            case AT_LEAST -> actual >= expected;
        };
    }

    public enum Scope {
        PLAYER,
        WORLD
    }

    public enum Kind {
        COOKIE,
        COUNTER
    }

    public enum Operation {
        EXISTS,
        EQUALS,
        AT_LEAST
    }
}
