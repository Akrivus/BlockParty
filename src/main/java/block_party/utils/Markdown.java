package block_party.utils;

import block_party.entities.Moe;
import block_party.scene.SceneVariables;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class Markdown {
    private static final Pattern COUNTER_PATTERN = Pattern.compile("#(\\w+)");
    private static final Pattern PLAYER_COUNTER_PATTERN = Pattern.compile("#\\.(\\w+)");
    private static final Pattern COOKIE_PATTERN = Pattern.compile("@(\\w+)");
    private static final Pattern PLAYER_COOKIE_PATTERN = Pattern.compile("@\\.(\\w+)");

    private Markdown() {
    }

    public static String markWithSubs(String line, Moe moe) {
        line = highlight(line, COUNTER_PATTERN, "yellow", name -> String.valueOf(counterValue(moe, name)));
        line = highlight(line, COOKIE_PATTERN, "cyan", name -> SceneVariables.get(moe.level()).cookies(moe.getDatabaseID()).get(name));
        ServerPlayer player = targetPlayer(moe);
        if (player != null) {
            line = highlight(line, PLAYER_COUNTER_PATTERN, "yellow", name -> String.valueOf(playerCounterValue(player, name)));
            line = highlight(line, PLAYER_COOKIE_PATTERN, "cyan", name -> SceneVariables.get(player.level()).cookies(player.getUUID().getMostSignificantBits()).get(name));
        }
        return mark(line);
    }

    public static String mark(String line) {
        line = parse(line, "<color=black>", "</color>", "0");
        line = parse(line, "<color=navy>", "</color>", "1");
        line = parse(line, "<color=green>", "</color>", "2");
        line = parse(line, "<color=teal>", "</color>", "3");
        line = parse(line, "<color=maroon>", "</color>", "4");
        line = parse(line, "<color=purple>", "</color>", "5");
        line = parse(line, "<color=orange>", "</color>", "6");
        line = parse(line, "<color=silver>", "</color>", "7");
        line = parse(line, "<color=gray>", "</color>", "8");
        line = parse(line, "<color=blue>", "</color>", "9");
        line = parse(line, "<color=lime>", "</color>", "a");
        line = parse(line, "<color=cyan>", "</color>", "b");
        line = parse(line, "<color=red>", "</color>", "c");
        line = parse(line, "<color=magenta>", "</color>", "d");
        line = parse(line, "<color=yellow>", "</color>", "e");
        line = parse(line, "<color=white>", "</color>", "f");
        line = parse(line, "<b>", "</b>", "l", "r");
        line = parse(line, "<i>", "</i>", "o", "r");
        line = parse(line, "<u>", "</u>", "m", "r");
        line = parse(line, "<s>", "</s>", "n", "r");
        line = parse(line, "\\*", "l");
        line = parse(line, "/", "o");
        line = parse(line, "_", "n");
        line = parse(line, "-", "m");
        return line;
    }

    private static String highlight(String line, Pattern pattern, String color, Function<String, String> mapper) {
        return replace(line, pattern, "<color=" + color + "><b>%s</b></color>", mapper);
    }

    private static String replace(String line, Pattern pattern, String markdown, Function<String, String> mapper) {
        Matcher matcher = pattern.matcher(line);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String replacement = String.format(markdown, mapper.apply(matcher.group(1)));
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String parse(String line, String tag, String code) {
        return parse(line, tag, tag, code, "r");
    }

    private static String parse(String line, String tag, String end, String code) {
        return parse(line, tag, end, code, "f");
    }

    private static String parse(String line, String tag, String end, String code, String term) {
        Pattern pattern = Pattern.compile(tag + "(.+?)" + end);
        Matcher parser = pattern.matcher(line);
        while (parser.find()) {
            line = parser.replaceFirst(Matcher.quoteReplacement("\u00a7" + code + parser.group(1) + "\u00a7" + term));
            parser = pattern.matcher(line);
        }
        return line;
    }

    private static ServerPlayer targetPlayer(Moe moe) {
        if (!(moe.level() instanceof ServerLevel level)) {
            return null;
        }
        ServerPlayer target = level.getServer().getPlayerList().getPlayer(moe.getDialogueTarget());
        return target == null ? level.getServer().getPlayerList().getPlayer(moe.getPlayerUUID()) : target;
    }

    private static int counterValue(Moe moe, String name) {
        Integer value = SceneVariables.get(moe.level()).counters(moe.getDatabaseID()).get(name);
        return value == null ? 0 : value;
    }

    private static int playerCounterValue(ServerPlayer player, String name) {
        Integer value = SceneVariables.get(player.level()).counters(player.getUUID().getMostSignificantBits()).get(name);
        return value == null ? 0 : value;
    }
}
