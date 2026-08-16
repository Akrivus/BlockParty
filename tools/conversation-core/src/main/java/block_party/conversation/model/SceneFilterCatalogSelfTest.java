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
        String[] environmentFilters = {
                "{\"type\":\"block_party:time_period\",\"value\":\"evening\"}",
                "{\"type\":\"block_party:weather\",\"value\":\"thunder\"}",
                "{\"type\":\"block_party:dimension\",\"value\":\"minecraft:overworld\"}",
                "{\"type\":\"block_party:biome\",\"value\":\"#minecraft:is_forest\"}",
                "{\"type\":\"block_party:at_location\",\"scope\":\"player\",\"name\":\"first_date\",\"radius\":4}"
        };
        for (String value : environmentFilters) {
            var filter = JsonParser.parseString(value).getAsJsonObject();
            String problem = SceneFilterCatalog.validate(filter);
            if (problem != null) throw new AssertionError("Environment filter did not validate: " + problem);
        }
        System.out.println("Scene filter catalog check passed.");
    }
}
