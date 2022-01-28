package block_party.utils;

import block_party.entities.BlockPartyNPC;
import block_party.scene.PlayerSceneManager;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Markdown {
    private static final BiFunction<String, String, Pattern> HTML = (p, b) -> Pattern.compile(p+"(.+?)"+b);
    private static final Pattern COUNTER_PATTERN = Pattern.compile("#(\\w+)");
    private static final Pattern PLAYER_COUNTER_PATTERN = Pattern.compile("#\\.(\\w+)");
    private static final Pattern COOKIE_PATTERN = Pattern.compile("@(\\w+)");
    private static final Pattern PLAYER_COOKIE_PATTERN = Pattern.compile("@\\.(\\w+)");

    public static String highlight(String line, Pattern pattern, String color, Function<String, String> mapper) {
        return replace(line, pattern, "<color="+color+"><b>%s</b></color>", mapper);
    }

    public static String replace(String line, Pattern pattern, String markdown, Function<String, String> mapper) {
        Matcher matcher = pattern.matcher(line);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group(1);
            String replacement = String.format(markdown, mapper.apply(group));
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static String markWithSubs(String line, BlockPartyNPC npc) {
        line = highlight(line, COUNTER_PATTERN, "yellow", (match) -> String.valueOf(npc.sceneManager.counters.get(match)));
        line = highlight(line, PLAYER_COUNTER_PATTERN, "yellow", (match) -> String.valueOf(PlayerSceneManager.getCountersFor(npc.getServerPlayer()).get(match)));
        line = highlight(line, COOKIE_PATTERN, "cyan", (match) -> npc.sceneManager.cookies.get(match));
        line = highlight(line, PLAYER_COOKIE_PATTERN, "cyan", (match) -> PlayerSceneManager.getCookiesFor(npc.getServerPlayer()).get(match));
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
        line = parse(line, "\\*", "l"); // bold
        line = parse(line,   "/", "o"); // italic
        line = parse(line,   "_", "n"); // underline
        line = parse(line,   "-", "m"); // strike
        return line;
    }

    private static String parse(String line, String tag, String end, String code) {
        return parse(line, tag, end, code, "f");
    }

    private static String parse(String line, String tag, String code) {
        return parse(line, tag, tag, code, "r");
    }

    private static String parse(String line, String tag, String end, String code, String term) {
        Matcher parser = HTML.apply(tag, end).matcher(line);
        while (parser.find()) {
            line = parser.replaceFirst(String.format("\u00a7%s%s\u00a7%s", code, parser.group(1), term));
            parser = HTML.apply(tag, end).matcher(line);
        }
        return line;
    }
}
