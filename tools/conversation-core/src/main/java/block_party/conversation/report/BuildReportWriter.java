package block_party.conversation.report;

import block_party.conversation.graph.MermaidExporter;
import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.simulation.SimulationReport;
import block_party.conversation.validation.ValidationReport;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BuildReportWriter {
    public void write(Path output, ScenePackProject project, ValidationReport validation, SimulationReport simulation)
            throws IOException {
        Files.createDirectories(output);
        write(output.resolve("validation-report.json"), ProjectJson.gson().toJson(validation));
        write(output.resolve("simulation-report.json"), ProjectJson.gson().toJson(simulation));
        write(output.resolve("simulation-report.md"), markdown(project, validation, simulation));
        write(output.resolve("graph.mmd"), new MermaidExporter().export(project));
    }

    private static String markdown(ScenePackProject project, ValidationReport validation, SimulationReport simulation) {
        StringBuilder report = new StringBuilder();
        report.append("# ").append(project.pack().title()).append("\n\n");
        report.append("- Cards: ").append(project.nodes().size()).append("\n");
        report.append("- Routes: ").append(simulation.routes()).append("\n");
        report.append("- Endings: ").append(String.join(", ", simulation.endings())).append("\n");
        report.append("- Gameplay gates: ").append(simulation.gameplayGates().size()).append("\n");
        report.append("- Validation errors: ").append(validation.errors()).append("\n");
        report.append("- Validation warnings: ").append(validation.warnings()).append("\n\n");
        report.append("## External gameplay requirements\n\n");
        if (simulation.externalRequirements().isEmpty()) report.append("None.\n");
        simulation.externalRequirements().forEach(value -> report.append("- ").append(value).append("\n"));
        report.append("\n## Routes\n");
        for (int index = 0; index < simulation.traces().size(); ++index) {
            report.append("\n### Route ").append(index + 1).append("\n\n");
            simulation.traces().get(index).forEach(value -> report.append("1. ").append(value).append("\n"));
        }
        return report.toString();
    }

    private static void write(Path path, String content) throws IOException {
        Files.writeString(path, content + (content.endsWith("\n") ? "" : System.lineSeparator()), StandardCharsets.UTF_8);
    }
}
