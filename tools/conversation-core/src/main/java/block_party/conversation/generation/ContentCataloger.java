package block_party.conversation.generation;

import block_party.conversation.model.ActionType;
import block_party.conversation.model.ConditionType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

public final class ContentCataloger {
    private static final int DOCUMENT_LIMIT = 24_000;

    public ContentCatalog catalog(GenerationBrief brief, Path repositoryRoot) throws Exception {
        Path root = repositoryRoot.toAbsolutePath().normalize();
        List<CatalogDocument> documents = brief.documents().stream().map(value -> {
            try {
                Path path = root.resolve(value).normalize();
                if (!path.startsWith(root) || !Files.isRegularFile(path)) {
                    throw new IllegalArgumentException("Catalog document is outside the repository or missing: " + value);
                }
                String content = Files.readString(path, StandardCharsets.UTF_8);
                if (content.length() > DOCUMENT_LIMIT) content = content.substring(0, DOCUMENT_LIMIT);
                return new CatalogDocument(value.replace('\\', '/'), hash(Files.readAllBytes(path)), content);
            } catch (Exception exception) {
                throw new CatalogFailure(exception);
            }
        }).toList();
        return new ContentCatalog(
                "26.6",
                brief.characters(),
                names(ActionType.values()),
                names(ConditionType.values()),
                List.of("green_checkmark", "red_x", "chat_bubble", "lovely_heart", "trusty_armor",
                        "stressful_skull", "leather_bag", "anvil", "next_response", "close_dialogue", "open_dialogue"),
                documents);
    }

    private static List<String> names(Enum<?>[] values) {
        return Arrays.stream(values).map(Enum::name).sorted().toList();
    }

    private static String hash(byte[] content) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
    }

    private static final class CatalogFailure extends RuntimeException {
        CatalogFailure(Throwable cause) {
            super(cause);
        }
    }
}
