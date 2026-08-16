package block_party.conversation.workbench;

import block_party.conversation.compile.DatapackCompiler;
import block_party.conversation.graph.MermaidExporter;
import block_party.conversation.generation.ContentCatalog;
import block_party.conversation.generation.ContentCataloger;
import block_party.conversation.generation.DialogueAlternative;
import block_party.conversation.generation.DialogueRevision;
import block_party.conversation.generation.DialogueRevisionService;
import block_party.conversation.generation.GenerationBrief;
import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.EditorPosition;
import block_party.conversation.model.NodeType;
import block_party.conversation.model.PackContract;
import block_party.conversation.model.PackMetadata;
import block_party.conversation.model.ProjectTarget;
import block_party.conversation.model.ResponseEdge;
import block_party.conversation.model.SceneNode;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.model.TransitionType;
import block_party.conversation.report.BuildReportWriter;
import block_party.conversation.simulation.ProjectSimulator;
import block_party.conversation.simulation.SimulationReport;
import block_party.conversation.simulation.SimulationScenario;
import block_party.conversation.validation.ProjectValidator;
import block_party.conversation.validation.ValidationReport;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;

final class WorkbenchService {
    private volatile Path projectPath;
    private final Path workingDirectory;
    private final GenerationRun generationRun;

    WorkbenchService(Path source) {
        Path normalized = source.toAbsolutePath().normalize();
        projectPath = Files.isDirectory(normalized) ? normalized.resolve("project.json") : normalized;
        workingDirectory = Path.of("").toAbsolutePath().normalize();
        generationRun = new GenerationRun(this::repositoryRoot, path -> projectPath = path);
        if (!Files.isRegularFile(projectPath)) {
            throw new IllegalArgumentException("No project.json found at " + projectPath);
        }
    }

    Path projectPath() {
        return projectPath;
    }

    Path defaultExportPath(ScenePackProject project) {
        String packName = project.pack() == null ? null : project.pack().id();
        if (packName == null || packName.isBlank()) {
            packName = "conversation-pack";
        }
        packName = packName.replaceAll("[^a-zA-Z0-9._-]", "-");
        return workingDirectory.resolve("dist").resolve(packName).normalize();
    }

    Path liveResourcesPath(ScenePackProject project) {
        return repositoryRoot().resolve("src/main/resources/data/block_party/scenes")
                .resolve(project.pack().id()).toAbsolutePath().normalize();
    }

    static void createStarter(Path path) throws Exception {
        createStarter(path, "new_conversation", "New Conversation");
    }

    static void createStarter(Path path, String requestedId, String requestedTitle) throws Exception {
        path = path.toAbsolutePath().normalize();
        if (Files.exists(path)) {
            throw new IllegalArgumentException("New project path already exists: " + path);
        }
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        SceneNode introduction = new SceneNode("introduction", NodeType.DIALOGUE, "Introduction",
                "block_party:right_click", List.of(), "Write the opening dialogue.", false, null,
                List.of(new ResponseEdge("next_response", "Continue", "ending", TransitionType.IMMEDIATE, List.of())),
                List.of(), null, null, null, new EditorPosition(100, 100));
        SceneNode ending = new SceneNode("ending", NodeType.END, "Ending", null, List.of(), null, false, null,
                List.of(), List.of(), null, "ending", null, new EditorPosition(410, 100));
        String id = WorkbenchSession.safeId(requestedId);
        String title = requestedTitle == null || requestedTitle.isBlank()
                ? id.replace('_', ' ')
                : requestedTitle.trim();
        ScenePackProject project = new ScenePackProject(2,
                new ProjectTarget("block_party", "26.6", 1, 61),
                new PackMetadata(id, id, title, 61),
                new PackContract(List.of(), List.of(), List.of("ending")), List.of(), false,
                "introduction", List.of(introduction, ending));
        Files.writeString(path, ProjectJson.gson().toJson(project) + System.lineSeparator(), StandardCharsets.UTF_8);
    }

    ScenePackProject load() throws Exception {
        return ProjectJson.read(projectPath);
    }

    ContentCatalog catalog(GenerationBrief brief) throws Exception {
        return new ContentCataloger().catalog(brief, repositoryRoot());
    }

    void startGeneration(GenerationBrief brief, Path output) {
        generationRun.start(brief, output);
    }

    JsonObject generationStatus() {
        return generationRun.statusJson();
    }

    JsonObject provenance() throws Exception {
        return new GenerationArchiveReader().read(projectPath);
    }

    DialogueRevision requestRevision(ScenePackProject project, String node, String instruction,
            String provider, String modelName, String recordedResponses) throws Exception {
        GenerationBrief providerBrief = new GenerationBrief(
                1, "revision", "revision", "Revision", instruction,
                List.of(), false, List.of(), List.of(), List.of(), List.of(),
                null, List.of(), null, null, provider, modelName, recordedResponses);
        return new DialogueRevisionService().request(NarrativeModels.create(providerBrief, repositoryRoot()), project, node, instruction,
                neighboringDialogue(project, node), revisionArchive());
    }

    ScenePackProject applyRevision(ScenePackProject project, String node, DialogueAlternative alternative) {
        return new DialogueRevisionService().apply(project, node, alternative);
    }

    private String neighboringDialogue(ScenePackProject project, String nodeId) {
        return project.nodes().stream()
                .filter(node -> !node.id().equals(nodeId) && node.text() != null)
                .limit(4)
                .map(node -> node.id() + ": " + node.text())
                .collect(java.util.stream.Collectors.joining("\n"));
    }

    private Path revisionArchive() {
        Path root = GenerationArchiveReader.generationRoot(projectPath);
        return root == null ? projectPath.getParent().resolve("generation") : root.resolve("generation");
    }

    private Path repositoryRoot() {
        Path current = projectPath.getParent();
        while (current != null) {
            if (Files.isRegularFile(current.resolve("settings.gradle"))) {
                return current;
            }
            current = current.getParent();
        }
        return workingDirectory;
    }

    ValidationReport validate(ScenePackProject project) {
        return new ProjectValidator().validate(project);
    }

    SimulationReport simulate(ScenePackProject project, SimulationScenario scenario) {
        ValidationReport validation = validate(project);
        if (!validation.valid()) {
            throw new IllegalArgumentException("Simulation requires a valid project.");
        }
        return new ProjectSimulator().simulate(project, scenario);
    }

    void save(ScenePackProject project) throws Exception {
        ValidationReport validation = validate(project);
        if (!validation.valid()) {
            throw new IllegalArgumentException("Save refused: project has validation errors.");
        }
        Path temporary = projectPath.resolveSibling(projectPath.getFileName() + ".workbench.tmp");
        Files.writeString(temporary, ProjectJson.gson().toJson(project) + System.lineSeparator(), StandardCharsets.UTF_8);
        try {
            Files.move(temporary, projectPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(temporary, projectPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    JsonObject export(ScenePackProject project, Path output) throws Exception {
        ValidationReport validation = validate(project);
        if (!validation.valid()) {
            throw new IllegalArgumentException("Export refused: project has validation errors.");
        }
        if (Files.exists(output)) {
            if (!Files.isDirectory(output)) {
                throw new IllegalArgumentException("Export target is not a directory.");
            }
            try (var children = Files.list(output)) {
                if (children.findAny().isPresent()) {
                    throw new IllegalArgumentException("Export target is not empty.");
                }
            }
        }
        Files.createDirectories(output);
        SimulationReport simulation = new ProjectSimulator().simulate(project);
        Files.writeString(
                output.resolve("project.json"),
                ProjectJson.gson().toJson(project) + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Files.writeString(output.resolve("graph.mmd"), new MermaidExporter().export(project), StandardCharsets.UTF_8);
        new BuildReportWriter().write(output, project, validation, simulation);
        var compilation = new DatapackCompiler().compile(project, output.resolve("datapack"));
        JsonObject result = new JsonObject();
        result.addProperty("output", output.toAbsolutePath().toString());
        result.addProperty("routes", simulation.routes());
        result.addProperty("datapackFiles", compilation.files().size());
        return result;
    }

    JsonObject exportLiveResources(ScenePackProject project) throws Exception {
        ValidationReport validation = validate(project);
        if (!validation.valid()) {
            throw new IllegalArgumentException("Live export refused: project has validation errors.");
        }
        Path scenesRoot = repositoryRoot().resolve("src/main/resources/data/block_party/scenes")
                .toAbsolutePath().normalize();
        Path output = liveResourcesPath(project);
        if (!scenesRoot.equals(output.getParent())) {
            throw new IllegalArgumentException("Live export target must be one pack directory beneath " + scenesRoot + ".");
        }
        Path staging = Files.createTempDirectory("block-party-live-export-");
        try {
            var compilation = new DatapackCompiler().compile(project, staging);
            Path compiledScenes = staging.resolve("data").resolve(project.pack().namespace())
                    .resolve("scenes").resolve(project.pack().id());
            deleteDirectory(output);
            Files.createDirectories(output);
            try (var files = Files.walk(compiledScenes)) {
                for (Path source : files.toList()) {
                    Path target = output.resolve(compiledScenes.relativize(source)).normalize();
                    if (!target.startsWith(output)) {
                        throw new IllegalArgumentException("Compiled scene path escaped the live export directory.");
                    }
                    if (Files.isDirectory(source)) Files.createDirectories(target);
                    else Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            SimulationReport simulation = new ProjectSimulator().simulate(project);
            JsonObject result = new JsonObject();
            result.addProperty("output", output.toString());
            result.addProperty("routes", simulation.routes());
            result.addProperty("datapackFiles", compilation.files().stream()
                    .filter(path -> path.startsWith(compiledScenes)).count());
            result.addProperty("liveResources", true);
            return result;
        } finally {
            deleteDirectory(staging);
        }
    }

    private static void deleteDirectory(Path directory) throws Exception {
        if (!Files.exists(directory)) return;
        try (var paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.delete(path);
        }
    }
}
