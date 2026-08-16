package block_party.conversation.generation;

import java.util.List;
import java.util.Map;

public record ResolvedGenerationContext(
        List<GenerationSubject> subjects,
        Map<String, List<String>> subjectTags,
        List<ContextInclusion> inclusions,
        List<String> warnings) {
    public ResolvedGenerationContext {
        subjects = subjects == null ? List.of() : List.copyOf(subjects);
        subjectTags = subjectTags == null ? Map.of() : Map.copyOf(subjectTags);
        inclusions = inclusions == null ? List.of() : List.copyOf(inclusions);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    public static ResolvedGenerationContext empty() {
        return new ResolvedGenerationContext(null, null, null, null);
    }
}
