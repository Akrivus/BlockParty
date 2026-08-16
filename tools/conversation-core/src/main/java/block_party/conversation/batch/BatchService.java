package block_party.conversation.batch;

import block_party.conversation.batch.BatchDefinition.BatchFamily;
import block_party.conversation.batch.BatchDefinition.BatchMatrix;
import block_party.conversation.batch.BatchDefinition.MatrixAxis;
import block_party.conversation.compile.DatapackCompiler;
import block_party.conversation.generation.GenerationBrief;
import block_party.conversation.generation.GenerationConstraints;
import block_party.conversation.generation.GenerationPipeline;
import block_party.conversation.generation.model.NarrativeModel;
import block_party.conversation.generation.model.OpenAiResponsesModel;
import block_party.conversation.generation.model.RecordedDirectoryModel;
import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.SceneFilterCatalog;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.validation.ProjectValidator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BatchService {
    private static final int LARGE_BATCH_THRESHOLD = 100;
    private final Path specification;
    private final Path repository;
    private final BatchDefinition definition;

    public BatchService(Path specification) throws Exception {
        this.specification = specification.toAbsolutePath().normalize();
        this.repository = repositoryRoot(this.specification);
        this.definition = ProjectJson.gson().fromJson(Files.readString(this.specification), BatchDefinition.class);
        validateDefinition();
    }

    public List<BatchJob> jobs() {
        List<BatchJob> jobs = new ArrayList<>();
        for (BatchFamily family : definition.families()) {
            List<Selection> selections = selections(family);
            for (Selection selection : selections) {
                int variations = family.variations() > 0 ? family.variations() : definition.defaults().variations();
                for (int variation = 1; variation <= variations; variation++) {
                    String suffix = selection.id().isBlank() ? "" : "__" + selection.id();
                    String id = safe(definition.id() + "__" + family.id() + suffix + "__" + String.format("%02d", variation));
                    Path directory = specification.getParent().resolve("jobs").resolve(id);
                    List<JsonObject> filters = new ArrayList<>();
                    List<String> contextTags = new ArrayList<>();
                    StringBuilder prompt = new StringBuilder(replace(family.prompt(), selection.values(), variation));
                    for (String tagName : family.tags()) {
                        var tag = definition.selectorTags().get(tagName);
                        if (tag == null) throw new IllegalArgumentException("Unknown selector tag '" + tagName + "'.");
                        filters.addAll(tag.filters().stream().map(JsonObject::deepCopy).toList());
                        contextTags.addAll(tag.contextTags());
                        if (tag.promptContext() != null && !tag.promptContext().isBlank()) prompt.append(" ").append(tag.promptContext());
                    }
                    filters.addAll(selection.filters());
                    contextTags.addAll(selection.contextTags());
                    if (!filters.isEmpty()) {
                        prompt.append(" Runtime scene selectors are locked to: ")
                                .append(ProjectJson.gson().toJson(filters)).append(". Write for that situation; do not redesign the selectors.");
                    }
                    String title = definition.title() + " — " + family.id().replace('_', ' ') + " " + variation;
                    GenerationConstraints constraints = new GenerationConstraints(
                            definition.defaults().minimumCards(), definition.defaults().maximumCards(),
                            definition.defaults().maximumDialogueCharacters(), definition.defaults().dialogueStyle(),
                            List.of(), List.of(), List.of(), List.of());
                    GenerationBrief brief = new GenerationBrief(
                            1, id, definition.namespace(), title, prompt.toString(), definition.defaults().subjects(),
                            definition.defaults().automaticContext(), contextTags, List.of(), definition.defaults().documents(),
                            List.of(), definition.defaults().trigger(), filters, constraints, definition.defaults().budget(),
                            definition.provider(), definition.model(), definition.recordedResponses());
                    jobs.add(new BatchJob(id, family.id(), variation, directory, brief, definition.defaults().trigger(), filters));
                }
            }
        }
        return List.copyOf(jobs);
    }

    public JsonObject plan() throws Exception {
        List<BatchJob> jobs = jobs();
        JsonObject result = new JsonObject();
        result.addProperty("batch", definition.id());
        result.addProperty("jobs", jobs.size());
        result.addProperty("maximumModelCalls", jobs.size() * definition.defaults().budget().maximumCalls());
        long existing = jobs.stream().filter(job -> Files.isRegularFile(job.directory().resolve("project.json"))).count();
        result.addProperty("existingProjects", existing);
        result.addProperty("missingProjects", jobs.size() - existing);
        if (jobs.size() > LARGE_BATCH_THRESHOLD) {
            result.addProperty("warning", "Large batch requires --confirm-large-batch before generation.");
        }
        JsonObject families = new JsonObject();
        jobs.stream().collect(java.util.stream.Collectors.groupingBy(BatchJob::family, LinkedHashMap::new, java.util.stream.Collectors.counting()))
                .forEach(families::addProperty);
        result.add("families", families);
        result.addProperty("workspace", specification.getParent().toString());
        return result;
    }

    public void expand() throws Exception {
        for (BatchJob job : jobs()) {
            Files.createDirectories(job.directory());
            Files.writeString(job.directory().resolve("brief.json"),
                    ProjectJson.gson().toJson(job.brief()) + System.lineSeparator(), StandardCharsets.UTF_8);
        }
        writeManifest(statuses("PLANNED"));
    }

    public int generate(boolean resume, String only, boolean force, boolean confirmLargeBatch) throws Exception {
        if (force && (only == null || only.isBlank())) throw new IllegalArgumentException("--force requires an exact --only job id.");
        if (jobs().size() > LARGE_BATCH_THRESHOLD && !confirmLargeBatch) {
            throw new IllegalArgumentException("Batch expands to " + jobs().size() + " jobs; inspect batch plan and pass --confirm-large-batch.");
        }
        expand();
        Map<String, JobStatus> statuses = statuses("PLANNED");
        int failures = 0;
        for (BatchJob job : jobs()) {
            if (only != null && !only.equals(job.id())) continue;
            Path projectPath = job.directory().resolve("project.json");
            if (Files.isRegularFile(projectPath) && !(force && job.id().equals(only))) {
                if (!resume) throw new IllegalStateException("Project already exists for " + job.id() + "; use --resume to skip existing work.");
                statuses.put(job.id(), new JobStatus("SKIPPED_EXISTING", 0, 0, 0, null));
                writeManifest(statuses);
                continue;
            }
            Path generated = job.directory().resolve("generated");
            if (Files.exists(generated)) deleteDirectory(generated);
            statuses.put(job.id(), new JobStatus("GENERATING", 0, 0, 0, null));
            writeManifest(statuses);
            try {
                NarrativeModel model = model(job.brief(), job.directory());
                var result = new GenerationPipeline(model).generate(job.brief(), repository, generated);
                ScenePackProject locked = result.project();
                var validation = new ProjectValidator().validate(locked);
                if (!validation.valid()) throw new IllegalStateException("Locked project has " + validation.errors() + " validation error(s).");
                ProjectJson.write(generated.resolve("project.json"), locked);
                ProjectJson.write(projectPath, locked);
                statuses.put(job.id(), new JobStatus("VALID", result.modelCalls(), result.inputTokens(), result.outputTokens(), null));
            } catch (Exception exception) {
                failures++;
                statuses.put(job.id(), new JobStatus("FAILED", 0, 0, 0, exception.getMessage()));
            }
            writeManifest(statuses);
        }
        if (only != null && jobs().stream().noneMatch(job -> only.equals(job.id()))) {
            throw new IllegalArgumentException("Unknown batch job '" + only + "'.");
        }
        return failures == 0 ? 0 : 1;
    }

    public int validateProjects() throws Exception {
        int failures = 0;
        Map<String, JobStatus> statuses = statuses("PLANNED");
        for (BatchJob job : jobs()) {
            Path project = job.directory().resolve("project.json");
            if (!Files.isRegularFile(project)) {
                failures++;
                statuses.put(job.id(), new JobStatus("INVALID", 0, 0, 0, "Missing project.json"));
                continue;
            }
            var report = new ProjectValidator().validate(ProjectJson.read(project));
            String status = report.valid() ? "VALID" : "INVALID";
            if (!report.valid()) failures++;
            JobStatus previous = statuses.get(job.id());
            statuses.put(job.id(), new JobStatus(status, previous.calls(), previous.input(), previous.output(),
                    report.valid() ? null : report.errors() + " validation error(s)"));
        }
        writeManifest(statuses);
        return failures == 0 ? 0 : 1;
    }

    public int compile(Path output) throws Exception {
        if (validateProjects() != 0) return 1;
        refuseNonEmpty(output);
        Files.createDirectories(output);
        Map<Path, String> owners = new LinkedHashMap<>();
        for (BatchJob job : jobs()) {
            ScenePackProject project = ProjectJson.read(job.directory().resolve("project.json"));
            Path temporary = Files.createTempDirectory("block-party-batch-compile-");
            try {
                new DatapackCompiler().compile(project, temporary);
                copyTree(temporary.resolve("data"), output.resolve("data"), owners, job.id());
            } finally {
                deleteDirectory(temporary);
            }
        }
        JsonObject pack = new JsonObject();
        JsonObject metadata = new JsonObject();
        metadata.addProperty("pack_format", 61);
        metadata.addProperty("description", definition.title());
        pack.add("pack", metadata);
        Files.writeString(output.resolve("pack.mcmeta"), ProjectJson.gson().toJson(pack) + System.lineSeparator());
        return 0;
    }

    public int installLive() throws Exception {
        if (validateProjects() != 0) return 1;
        Path scenesRoot = repository.resolve("src/main/resources/data/block_party/scenes").normalize();
        for (BatchJob job : jobs()) {
            ScenePackProject project = ProjectJson.read(job.directory().resolve("project.json"));
            Path target = scenesRoot.resolve(project.pack().id()).normalize();
            if (!scenesRoot.equals(target.getParent())) throw new IllegalArgumentException("Unsafe live target " + target);
            Path temporary = Files.createTempDirectory("block-party-batch-live-");
            try {
                new DatapackCompiler().compile(project, temporary);
                Path source = temporary.resolve("data").resolve(project.pack().namespace()).resolve("scenes").resolve(project.pack().id());
                deleteDirectory(target);
                Files.createDirectories(target);
                copyTree(source, target, new LinkedHashMap<>(), job.id());
            } finally {
                deleteDirectory(temporary);
            }
        }
        return 0;
    }

    private List<Selection> selections(BatchFamily family) {
        BatchMatrix matrix = family.matrix();
        if (matrix == null) return List.of(new Selection("", List.of(), List.of(), Map.of()));
        if ("product".equalsIgnoreCase(matrix.mode())) {
            List<Selection> result = new ArrayList<>();
            expandProduct(matrix.axes(), 0, new Selection("", List.of(), List.of(), Map.of()), result);
            return result;
        }
        if (matrix.selector() == null || matrix.selector().filter() == null || matrix.values().isEmpty()) {
            throw new IllegalArgumentException("Each matrix for family '" + family.id() + "' requires selector.filter and values.");
        }
        List<Selection> result = new ArrayList<>();
        for (String value : matrix.values()) {
            Map<String, String> values = Map.of("value", value, "axis", family.id());
            JsonObject filter = substitute(matrix.selector().filter(), values);
            String context = replace(matrix.selector().contextTag(), values, 0);
            result.add(new Selection(safe(value), List.of(filter), context.isBlank() ? List.of() : List.of(context), values));
        }
        return result;
    }

    private void expandProduct(List<MatrixAxis> axes, int index, Selection current, List<Selection> output) {
        if (index == axes.size()) { output.add(current); return; }
        MatrixAxis axis = axes.get(index);
        for (String value : axis.values()) {
            Map<String, String> variables = new LinkedHashMap<>(current.values());
            variables.put(axis.id(), value);
            variables.put("value", value);
            List<JsonObject> filters = new ArrayList<>(current.filters());
            filters.add(substitute(axis.filter(), variables));
            List<String> tags = new ArrayList<>(current.contextTags());
            String tag = replace(axis.contextTag(), variables, 0);
            if (!tag.isBlank()) tags.add(tag);
            String id = current.id().isBlank() ? axis.id() + "_" + value : current.id() + "__" + axis.id() + "_" + value;
            expandProduct(axes, index + 1, new Selection(safe(id), filters, tags, variables), output);
        }
    }

    private void validateDefinition() {
        if (definition.batchFormat() != 1) throw new IllegalArgumentException("Unsupported batchFormat " + definition.batchFormat());
        if (definition.id() == null || !definition.id().matches("[a-z0-9_]+")) throw new IllegalArgumentException("Batch id must use lowercase letters, digits, and underscores.");
        if (definition.families().isEmpty()) throw new IllegalArgumentException("Batch requires at least one family.");
        for (BatchJob job : jobsUnchecked()) for (JsonObject filter : job.filters()) {
            String problem = SceneFilterCatalog.validate(filter);
            if (problem != null) throw new IllegalArgumentException(job.id() + ": " + problem);
        }
    }

    private List<BatchJob> jobsUnchecked() {
        return jobs();
    }

    private NarrativeModel model(GenerationBrief brief, Path directory) {
        if ("openai".equalsIgnoreCase(brief.provider())) return new OpenAiResponsesModel(brief.model(), System.getenv("OPENAI_API_KEY"));
        if (brief.recordedResponses() == null || brief.recordedResponses().isBlank()) throw new IllegalArgumentException("recordedResponses is required for recorded batch jobs.");
        Path responses = Path.of(brief.recordedResponses());
        return new RecordedDirectoryModel((responses.isAbsolute() ? responses : repository.resolve(responses)).normalize());
    }

    private Map<String, JobStatus> statuses(String initial) {
        Map<String, JobStatus> result = new LinkedHashMap<>();
        for (BatchJob job : jobs()) result.put(job.id(), new JobStatus(initial, 0, 0, 0, null));
        Path manifest = specification.getParent().resolve("batch-manifest.json");
        if (Files.isRegularFile(manifest)) {
            try {
                JsonArray entries = ProjectJson.gson().fromJson(Files.readString(manifest), JsonObject.class).getAsJsonArray("jobs");
                for (JsonElement element : entries) {
                    JsonObject entry = element.getAsJsonObject();
                    String id = entry.get("id").getAsString();
                    if (result.containsKey(id)) result.put(id, new JobStatus(
                            entry.get("status").getAsString(), integer(entry, "modelCalls"), integer(entry, "inputTokens"),
                            integer(entry, "outputTokens"), entry.has("error") ? entry.get("error").getAsString() : null));
                }
            } catch (Exception ignored) {
                // A malformed or old manifest is derived state; expansion rebuilds it from the specification.
            }
        }
        return result;
    }

    private void writeManifest(Map<String, JobStatus> statuses) throws Exception {
        JsonObject manifest = new JsonObject();
        manifest.addProperty("batchFormat", 1);
        manifest.addProperty("batchId", definition.id());
        manifest.addProperty("specificationHash", hash(Files.readAllBytes(specification)));
        JsonArray entries = new JsonArray();
        for (BatchJob job : jobs()) {
            JobStatus status = statuses.getOrDefault(job.id(), new JobStatus("PLANNED", 0, 0, 0, null));
            JsonObject entry = new JsonObject();
            entry.addProperty("id", job.id()); entry.addProperty("family", job.family()); entry.addProperty("variation", job.variation());
            entry.addProperty("brief", relative(job.directory().resolve("brief.json")));
            entry.addProperty("project", relative(job.directory().resolve("project.json")));
            entry.addProperty("status", status.status()); entry.addProperty("modelCalls", status.calls());
            entry.addProperty("inputTokens", status.input()); entry.addProperty("outputTokens", status.output());
            if (status.error() != null) entry.addProperty("error", status.error());
            JsonArray filters = new JsonArray(); job.filters().forEach(filters::add); entry.add("selectors", filters);
            entries.add(entry);
        }
        manifest.add("jobs", entries);
        Files.writeString(specification.getParent().resolve("batch-manifest.json"), ProjectJson.gson().toJson(manifest) + System.lineSeparator());
    }

    private String relative(Path path) { return specification.getParent().relativize(path).toString().replace('\\', '/'); }

    private static JsonObject substitute(JsonObject object, Map<String, String> values) {
        String json = ProjectJson.gson().toJson(object);
        for (var entry : values.entrySet()) json = json.replace("{" + entry.getKey() + "}", entry.getValue());
        return ProjectJson.gson().fromJson(json, JsonObject.class);
    }

    private static String replace(String text, Map<String, String> values, int variation) {
        if (text == null) return "";
        String result = text.replace("{variation}", String.valueOf(variation));
        for (var entry : values.entrySet()) result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        return result;
    }

    private static String safe(String value) { return value.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9_]+", "_").replaceAll("^_+|_+$", ""); }

    private static Path repositoryRoot(Path path) {
        Path current = Files.isDirectory(path) ? path : path.getParent();
        while (current != null) { if (Files.isRegularFile(current.resolve("settings.gradle"))) return current; current = current.getParent(); }
        throw new IllegalArgumentException("Could not locate repository root above " + path);
    }

    private static void refuseNonEmpty(Path output) throws IOException {
        if (!Files.isDirectory(output)) return;
        try (var files = Files.list(output)) { if (files.findAny().isPresent()) throw new IOException("Output is not empty: " + output); }
    }

    private static void copyTree(Path source, Path target, Map<Path, String> owners, String owner) throws Exception {
        try (var files = Files.walk(source)) {
            for (Path file : files.toList()) {
                Path destination = target.resolve(source.relativize(file)).normalize();
                if (!destination.startsWith(target.normalize())) throw new IOException("Copy escaped output directory.");
                if (Files.isDirectory(file)) Files.createDirectories(destination);
                else {
                    String previous = owners.putIfAbsent(destination, owner);
                    if (previous != null && !destination.getFileName().toString().equals("pack.mcmeta")) throw new IOException("Output collision between " + previous + " and " + owner + ": " + destination);
                    Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static void deleteDirectory(Path directory) throws Exception {
        if (!Files.exists(directory)) return;
        try (var paths = Files.walk(directory)) { for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.delete(path); }
    }

    private static String hash(byte[] bytes) throws Exception { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)); }

    private static int integer(JsonObject object, String name) { return object.has(name) ? object.get(name).getAsInt() : 0; }

    private record Selection(String id, List<JsonObject> filters, List<String> contextTags, Map<String, String> values) {}
    private record JobStatus(String status, int calls, int input, int output, String error) {}
}
