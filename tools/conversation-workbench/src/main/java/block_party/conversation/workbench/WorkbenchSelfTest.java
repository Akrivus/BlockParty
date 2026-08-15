package block_party.conversation.workbench;

import block_party.conversation.generation.DialogueRevisionService;
import block_party.conversation.generation.MechanicsFingerprint;
import block_party.conversation.generation.model.RecordedDirectoryModel;
import block_party.conversation.model.ActionType;
import block_party.conversation.model.ConditionType;
import block_party.conversation.model.ScenePackProject;
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
}
