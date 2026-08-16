package block_party.conversation.model;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.regex.Pattern;

public final class SceneFilterCatalogSelfTest {
    private SceneFilterCatalogSelfTest() {}

    public static void main(String[] args) throws Exception {
        String source = Files.readString(Path.of(args[0]).resolve("src/main/java/block_party/registry/SceneFilters.java"));
        int start = source.indexOf("registerAll(");
        int end = source.indexOf(");", start);
        var matcher = Pattern.compile("\"([a-z0-9_]+)\"").matcher(source.substring(start, end));
        var missing = new HashSet<String>();
        while (matcher.find()) if (!SceneFilterCatalog.known(matcher.group(1))) missing.add(matcher.group(1));
        if (!missing.isEmpty()) throw new AssertionError("Tool filter catalog is missing runtime filters: " + missing);
        var invalid = JsonParser.parseString("{\"type\":\"block_party:not_registered\"}").getAsJsonObject();
        if (SceneFilterCatalog.validate(invalid) == null) throw new AssertionError("Unknown scene filter unexpectedly validated.");
        System.out.println("Scene filter catalog check passed.");
    }
}
