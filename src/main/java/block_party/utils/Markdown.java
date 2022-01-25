package block_party.utils;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Markdown {
    private static final BiFunction<String, String, Pattern> HTML = (p, b) -> Pattern.compile(p+"(.+?)"+b);

    public static String highlight(String line, Pattern pattern, String color, Function<String, String> replacement) {
        return replace(line, pattern, "<color="+color+"><b>%s</b></color>", replacement);
    }

    public static String replace(String line, Pattern pattern, String markdown, Function<String, String> replacement) {
        Matcher matcher = pattern.matcher(line);
        while (matcher.find())
            line = matcher.replaceFirst(String.format(markdown, replacement.apply(matcher.group(1))));
        return line;
    }

    public static String parse(String line) {
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
