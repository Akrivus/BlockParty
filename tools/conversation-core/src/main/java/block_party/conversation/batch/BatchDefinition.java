package block_party.conversation.batch;

import block_party.conversation.generation.GenerationBudget;
import block_party.conversation.generation.GenerationSubject;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;

public record BatchDefinition(
        int batchFormat,
        String id,
        String namespace,
        String title,
        String provider,
        String model,
        String recordedResponses,
        BatchDefaults defaults,
        Map<String, SelectorTag> selectorTags,
        List<BatchFamily> families,
        SolutionTarget solution) {
    public BatchDefinition {
        namespace = namespace == null || namespace.isBlank() ? "block_party" : namespace;
        title = title == null || title.isBlank() ? id : title;
        provider = provider == null || provider.isBlank() ? "openai" : provider;
        model = model == null || model.isBlank() ? "gpt-5.6-luna" : model;
        defaults = defaults == null ? new BatchDefaults(null, null, 0, 0, 0, 0, null, null, null, null) : defaults;
        selectorTags = selectorTags == null ? Map.of() : Map.copyOf(selectorTags);
        families = families == null ? List.of() : List.copyOf(families);
    }

    public record SolutionTarget(String path, String group, Boolean addGeneratedProjects) {
        public SolutionTarget {
            group = group == null || group.isBlank() ? "Projects" : group;
            addGeneratedProjects = addGeneratedProjects == null ? Boolean.FALSE : addGeneratedProjects;
        }
    }

    public record BatchDefaults(
            Boolean automaticContext,
            String trigger,
            int variations,
            int minimumCards,
            int maximumCards,
            int maximumDialogueCharacters,
            String dialogueStyle,
            List<GenerationSubject> subjects,
            List<String> documents,
            GenerationBudget budget) {
        public BatchDefaults {
            automaticContext = automaticContext == null ? Boolean.TRUE : automaticContext;
            trigger = trigger == null || trigger.isBlank() ? "right_click" : trigger;
            variations = variations <= 0 ? 1 : variations;
            minimumCards = minimumCards <= 0 ? 1 : minimumCards;
            maximumCards = maximumCards < minimumCards ? Math.max(3, minimumCards) : maximumCards;
            maximumDialogueCharacters = maximumDialogueCharacters <= 0 ? 160 : maximumDialogueCharacters;
            subjects = subjects == null ? List.of() : List.copyOf(subjects);
            documents = documents == null ? List.of() : List.copyOf(documents);
            budget = budget == null ? new GenerationBudget(20, 500_000, 200_000) : budget;
        }
    }

    public record SelectorTag(List<JsonObject> filters, List<String> contextTags, String promptContext) {
        public SelectorTag {
            filters = filters == null ? List.of() : filters.stream().map(JsonObject::deepCopy).toList();
            contextTags = contextTags == null ? List.of() : List.copyOf(contextTags);
        }
    }

    public record BatchFamily(
            String id, String prompt, List<String> tags, int variations, BatchMatrix matrix) {
        public BatchFamily {
            tags = tags == null ? List.of() : List.copyOf(tags);
        }
    }

    public record BatchMatrix(
            String mode, MatrixSelector selector, List<String> values, List<MatrixAxis> axes) {
        public BatchMatrix {
            mode = mode == null ? "each" : mode;
            values = values == null ? List.of() : List.copyOf(values);
            axes = axes == null ? List.of() : List.copyOf(axes);
        }
    }

    public record MatrixSelector(JsonObject filter, String contextTag) {}

    public record MatrixAxis(String id, JsonObject filter, String contextTag, List<String> values) {
        public MatrixAxis {
            values = values == null ? List.of() : List.copyOf(values);
        }
    }
}
