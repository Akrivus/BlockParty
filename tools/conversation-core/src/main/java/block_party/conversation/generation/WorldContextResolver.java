package block_party.conversation.generation;

import block_party.conversation.io.ProjectJson;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class WorldContextResolver {
    public ResolvedGenerationContext resolve(GenerationBrief brief, Path repositoryRoot) throws Exception {
        if (!brief.automaticContext()) {
            return ResolvedGenerationContext.empty();
        }
        Path contextRoot = repositoryRoot.resolve("tools/conversation-core/context");
        ContextManifest manifest = ProjectJson.gson().fromJson(
                Files.readString(contextRoot.resolve("context-format.json"), StandardCharsets.UTF_8),
                ContextManifest.class);
        BlockTagResolver tagResolver = new BlockTagResolver(repositoryRoot);
        Map<String, List<String>> subjectTags = new LinkedHashMap<>();
        List<String> warnings = new ArrayList<>();
        List<PendingInclusion> pending = new ArrayList<>();
        for (String path : manifest.always()) {
            pending.add(new PendingInclusion(path, "MANDATORY_WORLD_RULE", null));
        }
        for (String tag : brief.contextTags()) {
            String path = manifest.tags().get(tag);
            if (path == null) warnings.add("Explicit context tag has no document mapping: " + tag);
            else pending.add(new PendingInclusion(path, "EXPLICIT_TAG", tag));
        }
        for (GenerationSubject subject : brief.subjects()) {
            String block = subject.resolvedBlock();
            if (block == null || block.isBlank()) {
                warnings.add("Subject " + subject.id() + " has no block identity to resolve.");
                continue;
            }
            List<String> tags = tagResolver.tagsFor(block);
            subjectTags.put(subject.id(), tags);
            Set<String> selectedTags = selectExclusive(tags, manifest.exclusiveGroups(), warnings, block);
            for (String tag : tags) {
                String path = manifest.tags().get(tag);
                if (path != null && selectedTags.contains(tag)) {
                    pending.add(new PendingInclusion(path, "TAG", tag));
                }
            }
            String blockPath = "blocks/" + normalize(block) + ".md";
            if (Files.isRegularFile(contextRoot.resolve(blockPath))) {
                pending.add(new PendingInclusion(blockPath, "BLOCK_PROFILE", block));
            }
            if ("CHARACTER".equals(subject.kind())) {
                String characterPath = "characters/" + normalize(subject.id()) + ".md";
                if (Files.isRegularFile(contextRoot.resolve(characterPath))) {
                    pending.add(new PendingInclusion(characterPath, "CHARACTER_PROFILE", subject.id()));
                }
            }
        }
        warnings.addAll(tagResolver.warnings());
        Map<String, ContextInclusion> inclusions = new LinkedHashMap<>();
        for (PendingInclusion item : pending) {
            if (inclusions.containsKey(item.path())) {
                continue;
            }
            Path path = contextRoot.resolve(item.path()).normalize();
            if (!path.startsWith(contextRoot) || !Files.isRegularFile(path)) {
                throw new IllegalStateException("Missing automatic context document: " + item.path());
            }
            byte[] bytes = Files.readAllBytes(path);
            String content = new String(bytes, StandardCharsets.UTF_8);
            inclusions.put(item.path(), new ContextInclusion(
                    item.path(), title(content, item.path()), item.reason(), item.source(), hash(bytes), content));
        }
        return new ResolvedGenerationContext(brief.subjects(), subjectTags, inclusions.values().stream().toList(), warnings);
    }

    private static Set<String> selectExclusive(
            List<String> tags,
            Map<String, List<String>> groups,
            List<String> warnings,
            String block) {
        Set<String> selected = new LinkedHashSet<>(tags);
        for (Map.Entry<String, List<String>> group : groups.entrySet()) {
            List<String> matches = group.getValue().stream().filter(tags::contains).toList();
            if (matches.size() > 1) {
                warnings.add(block + " belongs to multiple " + group.getKey()
                        + " tags; using " + matches.getFirst() + " to match game precedence.");
                selected.removeAll(matches.subList(1, matches.size()));
            }
        }
        return selected;
    }

    private static String normalize(String id) {
        return id.toLowerCase(java.util.Locale.ROOT).replace(':', '.').replace('/', '.');
    }

    private static String title(String content, String path) {
        return content.lines()
                .filter(line -> line.startsWith("# "))
                .map(line -> line.substring(2).trim())
                .findFirst()
                .orElse(path);
    }

    private static String hash(byte[] content) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
    }

    private record PendingInclusion(String path, String reason, String source) {
    }

    private record ContextManifest(
            int format,
            List<String> always,
            Map<String, String> tags,
            Map<String, List<String>> exclusiveGroups) {
        private ContextManifest {
            always = always == null ? List.of() : List.copyOf(always);
            tags = tags == null ? Map.of() : Map.copyOf(tags);
            exclusiveGroups = exclusiveGroups == null ? Map.of() : Map.copyOf(exclusiveGroups);
        }
    }
}
