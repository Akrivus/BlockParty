package block_party.conversation.workbench;

import block_party.conversation.simulation.SimulationScenario;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WorkbenchSelfTest {
    private WorkbenchSelfTest() {
    }

    public static void main(String[] args) throws Exception {
        WorkbenchService service = new WorkbenchService(Path.of(args[0]));
        var project = service.load();
        if (!service.validate(project).valid()) throw new AssertionError("Fixture must validate.");
        if (service.simulate(project, new SimulationScenario(null, null, null)).routes() < 1) {
            throw new AssertionError("Fixture must have a simulated route.");
        }
        for (String resource : new String[] {"/workbench/index.html", "/workbench/app.css", "/workbench/app.js"}) {
            if (WorkbenchSelfTest.class.getResource(resource) == null) throw new AssertionError("Missing resource " + resource);
        }
        Path output = Files.createTempDirectory("block-party-workbench-check-").resolve("export");
        service.export(project, output);
        if (!Files.isRegularFile(output.resolve("project.json")) || !Files.isDirectory(output.resolve("datapack"))) {
            throw new AssertionError("Workbench export is incomplete.");
        }
        System.out.println("Workbench check passed.");
    }
}
