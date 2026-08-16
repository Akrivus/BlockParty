package block_party.conversation.workbench;

import block_party.conversation.generation.DialogueRevisionService;
import block_party.conversation.generation.MechanicsFingerprint;
import block_party.conversation.generation.model.RecordedDirectoryModel;
import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ActionType;
import block_party.conversation.model.ConditionType;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.model.SceneNode;
import block_party.conversation.model.SpeakerPresentation;
import block_party.conversation.simulation.SimulationScenario;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WorkbenchSelfTest {
    private WorkbenchSelfTest() {
    }

    public static void main(String[] args) throws Exception {
        WorkbenchService service = new WorkbenchService(Path.of(args[0]));
        var project = service.load();
        verifyProjectOperations(service, project);
        verifyDialogueRevision(Path.of(args[0]), project);
        verifyStaticResources();
        verifyAuthoringSchema();
        verifyExportAndStarter(service, project);
        verifyLiveResourcesExport(project);
        verifyBatchGenerationArchive();
        verifySessionFlow(Path.of(args[0]));
        System.out.println("Workbench check passed.");
    }

    private static void verifyProjectOperations(
            WorkbenchService service, ScenePackProject project) throws Exception {
        if (!service.defaultExportPath(project).getFileName().toString().equals(project.pack().id())) {
            throw new AssertionError("Default export path must end with the pack id.");
        }
        if (!service.validate(project).valid()) {
            throw new AssertionError("Fixture must validate.");
        }
        if (service.simulate(project, new SimulationScenario(null, null, null)).routes() < 1) {
            throw new AssertionError("Fixture must have a simulated route.");
        }
    }

    private static void verifyDialogueRevision(Path fixture, ScenePackProject project) throws Exception {
        Path responses = fixture.toAbsolutePath().getParent().resolve("generation/responses/flower");
        var revisionService = new DialogueRevisionService();
        var revisions = revisionService.request(
                new RecordedDirectoryModel(responses),
                project,
                "introduction",
                "Make it warmer.",
                "",
                null);
        var revised = revisionService.apply(
                project, "introduction", revisions.alternatives().getFirst());
        if (!MechanicsFingerprint.of(project).equals(MechanicsFingerprint.of(revised))
                || project.nodes().getFirst().text().equals(revised.nodes().getFirst().text())) {
            throw new AssertionError("Dialogue revision must change prose without changing mechanics.");
        }
    }

    private static void verifyStaticResources() {
        String[] resources = {
                "/workbench/index.html",
                "/workbench/app.css",
                "/workbench/app.js"
        };
        for (String resource : resources) {
            if (WorkbenchSelfTest.class.getResource(resource) == null) {
                throw new AssertionError("Missing resource " + resource);
            }
        }
    }

    private static void verifyAuthoringSchema() {
        var schema = AuthoringSchema.describe();
        int actions = ((java.util.Map<?, ?>) schema.get("actions")).size();
        int conditions = ((java.util.Map<?, ?>) schema.get("conditions")).size();
        if (actions != ActionType.values().length || conditions != ConditionType.values().length) {
            throw new AssertionError("Authoring schema does not cover every primitive type.");
        }
        var enums = (java.util.Map<?, ?>) schema.get("enums");
        if (!SpeakerPresentation.EMOTIONS.equals(enums.get("emotion"))
                || !SpeakerPresentation.ANIMATIONS.equals(enums.get("animation"))) {
            throw new AssertionError("Authoring schema does not expose the runtime speaker presentation keys.");
        }
        if (!Integer.valueOf(SceneNode.MAX_RESPONSES).equals(schema.get("maximumResponses"))) {
            throw new AssertionError("Authoring schema does not expose the dialogue response limit.");
        }
    }

    private static void verifyExportAndStarter(
            WorkbenchService service, ScenePackProject project) throws Exception {
        Path output = Files.createTempDirectory("block-party-workbench-check-").resolve("export");
        service.export(project, output);
        if (!Files.isRegularFile(output.resolve("project.json")) || !Files.isDirectory(output.resolve("datapack"))) {
            throw new AssertionError("Workbench export is incomplete.");
        }
        Path starter = output.getParent().resolve("starter.project.json");
        WorkbenchService.createStarter(starter);
        WorkbenchService starterService = new WorkbenchService(starter);
        if (!starterService.validate(starterService.load()).valid()) {
            throw new AssertionError("New-project starter must validate.");
        }
    }

    private static void verifySessionFlow(Path fixture) throws Exception {
        WorkbenchSession session = new WorkbenchSession(null);
        if ((boolean) session.describe().get("projectOpen")) {
            throw new AssertionError("A pathless workbench must start without a project.");
        }
        session.open(fixture);
        if (!(boolean) session.describe().get("projectOpen")) {
            throw new AssertionError("Opening a fixture must activate the session.");
        }
        session.close();
        Path source = Files.createTempDirectory("block-party-session-check-")
                .resolve("new-pack/project.json");
        session.create(source, "My New Pack", "My New Pack");
        ScenePackProject created = session.requireProject().load();
        if (!"my_new_pack".equals(created.pack().id())
                || !source.toAbsolutePath().normalize().equals(session.requireProject().projectPath())) {
            throw new AssertionError("Session creation must normalize identity and open the new project.");
        }
    }

    private static void verifyLiveResourcesExport(ScenePackProject project) throws Exception {
        Path repository = Files.createTempDirectory("block-party-live-export-check-");
        Files.writeString(repository.resolve("settings.gradle"), "");
        Path projectPath = repository.resolve("authoring/live/project.json");
        Files.createDirectories(projectPath.getParent());
        Files.writeString(projectPath, ProjectJson.gson().toJson(project));
        WorkbenchService service = new WorkbenchService(projectPath);
        Path expected = repository.resolve("src/main/resources/data/block_party/scenes")
                .resolve(project.pack().id()).toAbsolutePath().normalize();
        if (!expected.equals(service.liveResourcesPath(project))) {
            throw new AssertionError("Live resource export path is incorrect.");
        }
        service.exportLiveResources(project);
        if (!Files.isDirectory(expected)) {
            throw new AssertionError("Live resource export did not create the pack directory.");
        }
        try (var files = Files.list(expected)) {
            if (files.findAny().isEmpty()) {
                throw new AssertionError("Live resource export did not write scene files.");
            }
        }
        Files.writeString(expected.resolve("stale.json"), "{}");
        service.exportLiveResources(project);
        if (Files.exists(expected.resolve("stale.json"))) {
            throw new AssertionError("Repeated live resource export must replace the current pack directory.");
        }
    }

    private static void verifyBatchGenerationArchive() throws Exception {
        Path job = Files.createTempDirectory("block-party-batch-archive-check-");
        Path project = job.resolve("project.json");
        Files.writeString(project, "{}");
        Path generated = job.resolve("generated/generation");
        Files.createDirectories(generated);
        if (!job.resolve("generated").equals(GenerationArchiveReader.generationRoot(project))) {
            throw new AssertionError("Workbench must discover batch generation provenance.");
        }
    }
}
