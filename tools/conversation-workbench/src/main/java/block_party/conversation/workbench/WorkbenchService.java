package block_party.conversation.workbench;

import block_party.conversation.compile.DatapackCompiler;
import block_party.conversation.graph.MermaidExporter;
import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ScenePackProject;
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

final class WorkbenchService {
    private final Path projectPath;

    WorkbenchService(Path source) {
        Path normalized = source.toAbsolutePath().normalize();
        projectPath = Files.isDirectory(normalized) ? normalized.resolve("project.json") : normalized;
        if (!Files.isRegularFile(projectPath)) {
            throw new IllegalArgumentException("No project.json found at " + projectPath);
        }
    }

    Path projectPath() {
        return projectPath;
    }

    ScenePackProject load() throws Exception {
        return ProjectJson.read(projectPath);
    }

    ValidationReport validate(ScenePackProject project) {
        return new ProjectValidator().validate(project);
    }

    SimulationReport simulate(ScenePackProject project, SimulationScenario scenario) {
        ValidationReport validation = validate(project);
        if (!validation.valid()) throw new IllegalArgumentException("Simulation requires a valid project.");
        return new ProjectSimulator().simulate(project, scenario);
    }

    void save(ScenePackProject project) throws Exception {
        ValidationReport validation = validate(project);
        if (!validation.valid()) throw new IllegalArgumentException("Save refused: project has validation errors.");
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
        if (!validation.valid()) throw new IllegalArgumentException("Export refused: project has validation errors.");
        if (Files.exists(output)) {
            if (!Files.isDirectory(output)) throw new IllegalArgumentException("Export target is not a directory.");
            try (var children = Files.list(output)) {
                if (children.findAny().isPresent()) throw new IllegalArgumentException("Export target is not empty.");
            }
        }
        Files.createDirectories(output);
        SimulationReport simulation = new ProjectSimulator().simulate(project);
        Files.writeString(output.resolve("project.json"), ProjectJson.gson().toJson(project) + System.lineSeparator(), StandardCharsets.UTF_8);
        Files.writeString(output.resolve("graph.mmd"), new MermaidExporter().export(project), StandardCharsets.UTF_8);
        new BuildReportWriter().write(output, project, validation, simulation);
        var compilation = new DatapackCompiler().compile(project, output.resolve("datapack"));
        JsonObject result = new JsonObject();
        result.addProperty("output", output.toAbsolutePath().toString());
        result.addProperty("routes", simulation.routes());
        result.addProperty("datapackFiles", compilation.files().size());
        return result;
    }
}
