package block_party.conversation.generation;

import block_party.conversation.model.ActionType;
import block_party.conversation.model.ConditionType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

public final class ContentCataloger {
    private static final int DOCUMENT_LIMIT = 24_000;

    public ContentCatalog catalog(GenerationBrief brief, Path repositoryRoot) throws Exception {
        Path root = repositoryRoot.toAbsolutePath().normalize();
        ResolvedGenerationContext context = new WorldContextResolver().resolve(brief, root);
        List<CatalogDocument> documents = new ArrayList<>();
        for (ContextInclusion inclusion : context.inclusions()) {
            documents.add(new CatalogDocument(
                    "context/" + inclusion.path(), inclusion.sha256(), inclusion.content()));
        }
        for (String value : brief.documents()) {
            Path path = root.resolve(value).normalize();
            if (!path.startsWith(root) || !Files.isRegularFile(path)) {
                throw new IllegalArgumentException(
                        "Catalog document is outside the repository or missing: " + value);
            }
            String content = Files.readString(path, StandardCharsets.UTF_8);
            if (content.length() > DOCUMENT_LIMIT) {
                content = content.substring(0, DOCUMENT_LIMIT);
            }
            documents.add(new CatalogDocument(
                    value.replace('\\', '/'), hash(Files.readAllBytes(path)), content));
        }
        return new ContentCatalog(
                "26.6",
                brief.characters(),
                names(ActionType.values()),
                names(ConditionType.values()),
                List.of("green_checkmark", "red_x", "chat_bubble", "lovely_heart", "trusty_armor",
                        "stressful_skull", "leather_bag", "anvil", "next_response", "close_dialogue", "open_dialogue"),
                documents,
                context);
    }

    private static List<String> names(Enum<?>[] values) {
        return Arrays.stream(values).map(Enum::name).sorted().toList();
    }

    private static String hash(byte[] content) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
    }
}
