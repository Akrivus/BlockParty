package block_party.conversation.cli;

import block_party.conversation.batch.BatchService;
import block_party.conversation.compile.CompilationResult;
import block_party.conversation.compile.DatapackCompiler;
import block_party.conversation.graph.MermaidExporter;
import block_party.conversation.generation.ContentCatalog;
import block_party.conversation.generation.ContentCataloger;
import block_party.conversation.generation.GenerationBrief;
import block_party.conversation.generation.GenerationPipeline;
import block_party.conversation.generation.GenerationResult;
import block_party.conversation.generation.model.NarrativeModel;
import block_party.conversation.generation.model.OpenAiResponsesModel;
import block_party.conversation.generation.model.RecordedDirectoryModel;
import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.report.BuildReportWriter;
import block_party.conversation.report.NodeExplainer;
import block_party.conversation.simulation.ProjectSimulator;
import block_party.conversation.simulation.SimulationReport;
import block_party.conversation.simulation.SimulationScenario;
import block_party.conversation.validation.Diagnostic;
import block_party.conversation.validation.ProjectValidator;
import block_party.conversation.validation.ValidationReport;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConversationCli {
    private ConversationCli() {
    }

    public static void main(String[] args) {
        try {
            int result = run(args);
            if (result != 0) {
                System.exit(result);
            }
        } catch (Exception exception) {
            System.err.println("error: " + exception.getMessage());
            System.exit(1);
        }
    }

    static int run(String[] args) throws Exception {
        if (args.length == 0 || "help".equals(args[0]) || "--help".equals(args[0])) {
            help();
            return 0;
        }
        if ("batch".equals(args[0])) return batch(args);
        if (args.length < 2) {
            help();
            return 2;
        }
        if ("generate".equals(args[0])) return generate(Path.of(args[1]), requiredOutput(args));
        if ("catalog".equals(args[0])) return catalog(Path.of(args[1]), requiredOutput(args));
        if ("replay".equals(args[0])) return replay(Path.of(args[1]), requiredOutput(args));
        Path projectPath = Path.of(args[1]);
        ScenePackProject project = ProjectJson.read(projectPath);
        return switch (args[0]) {
            case "validate" -> validate(project);
            case "simulate" -> simulate(project, args.length >= 3 ? Path.of(args[2]) : null);
            case "compile" -> compile(project, requiredOutput(args));
            case "graph" -> graph(project, requiredOutput(args));
            case "build" -> build(project, requiredOutput(args));
            case "explain" -> explain(project, requiredValue(args));
            default -> {
                System.err.println("Unknown command: " + args[0]);
                help();
                yield 2;
            }
        };
    }

    private static int batch(String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException("Batch commands require an operation and batch specification path.");
        }
        String operation = args[1];
        BatchService batch = new BatchService(Path.of(args[2]));
        return switch (operation) {
            case "plan" -> {
                System.out.println(ProjectJson.gson().toJson(batch.plan()));
                yield 0;
            }
            case "expand" -> {
                batch.expand();
                System.out.println("Expanded batch authoring sources.");
                yield 0;
            }
            case "generate" -> batch.generate(has(args, "--resume"), option(args, "--only"), has(args, "--force"),
                    has(args, "--confirm-large-batch"));
            case "validate" -> batch.validateProjects();
            case "compile" -> {
                if (args.length < 4) throw new IllegalArgumentException("Batch compile requires an output directory.");
                yield batch.compile(Path.of(args[3]));
            }
            case "install" -> {
                if (!has(args, "--live")) throw new IllegalArgumentException("Batch install currently requires --live.");
                yield batch.installLive();
            }
            default -> throw new IllegalArgumentException("Unknown batch operation '" + operation + "'.");
        };
    }

    private static boolean has(String[] args, String option) {
        return java.util.Arrays.asList(args).contains(option);
    }

    private static String option(String[] args, String option) {
        for (int index = 0; index + 1 < args.length; index++) if (option.equals(args[index])) return args[index + 1];
        return null;
    }

    private static int generate(Path briefPath, Path output) throws Exception {
        briefPath = briefPath.toAbsolutePath().normalize();
        GenerationBrief brief = readBrief(briefPath);
        NarrativeModel model = model(brief, briefPath.getParent());
        GenerationResult result = new GenerationPipeline(model).generate(brief, repositoryRoot(briefPath), output);
        System.out.printf("Generated %s with %d model call(s); %d input token(s), %d output token(s).%n",
                result.project().pack().id(), result.modelCalls(), result.inputTokens(), result.outputTokens());
        return 0;
    }

    private static int replay(Path previous, Path output) throws Exception {
        previous = previous.toAbsolutePath().normalize();
        GenerationBrief brief = readBrief(previous.resolve("brief.json"));
        ContentCatalog catalog = ProjectJson.gson().fromJson(
                Files.readString(previous.resolve("catalog.json"), StandardCharsets.UTF_8),
                ContentCatalog.class);
        GenerationResult result = new GenerationPipeline(new RecordedDirectoryModel(previous.resolve("generation")))
                .generate(brief, repositoryRoot(previous), output, catalog);
        System.out.printf("Replayed %s with %d archived call(s).%n", result.project().pack().id(), result.modelCalls());
        return 0;
    }

    private static int catalog(Path briefPath, Path output) throws Exception {
        briefPath = briefPath.toAbsolutePath().normalize();
        GenerationBrief brief = readBrief(briefPath);
        var catalog = new ContentCataloger().catalog(brief, repositoryRoot(briefPath));
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.writeString(output, ProjectJson.gson().toJson(catalog) + System.lineSeparator(), StandardCharsets.UTF_8);
        System.out.println("Wrote catalog to " + output.toAbsolutePath());
        return 0;
    }

    private static GenerationBrief readBrief(Path path) throws Exception {
        return ProjectJson.gson().fromJson(Files.readString(path, StandardCharsets.UTF_8), GenerationBrief.class);
    }

    private static Path repositoryRoot(Path startingPath) {
        Path current = Files.isDirectory(startingPath) ? startingPath : startingPath.getParent();
        while (current != null) {
            if (Files.isRegularFile(current.resolve("settings.gradle"))) return current;
            current = current.getParent();
        }
        throw new IllegalArgumentException("Could not locate the repository root above " + startingPath);
    }

    private static NarrativeModel model(GenerationBrief brief, Path briefDirectory) {
        if ("openai".equalsIgnoreCase(brief.provider())) {
            return new OpenAiResponsesModel(brief.model(), System.getenv("OPENAI_API_KEY"));
        }
        Path base = briefDirectory == null ? Path.of(".") : briefDirectory;
        if (brief.recordedResponses() == null || brief.recordedResponses().isBlank()) {
            throw new IllegalArgumentException("recordedResponses is required for the recorded provider.");
        }
        return new RecordedDirectoryModel(base.resolve(brief.recordedResponses()).normalize());
    }

    private static int validate(ScenePackProject project) {
        ValidationReport report = new ProjectValidator().validate(project);
        for (Diagnostic issue : report.diagnostics()) {
            System.out.printf("%s %s%s: %s%n", issue.severity(), issue.code(),
                    issue.node() == null ? "" : " [" + issue.node() + "]", issue.message());
        }
        System.out.printf("Validation: %d error(s), %d warning(s).%n", report.errors(), report.warnings());
        return report.valid() ? 0 : 1;
    }

    private static int simulate(ScenePackProject project, Path scenarioPath) throws Exception {
        ValidationReport validation = new ProjectValidator().validate(project);
        if (!validation.valid()) {
            System.err.println("Simulation refused: project has " + validation.errors() + " validation error(s).");
            return 1;
        }
        SimulationScenario scenario = scenarioPath == null
                ? new SimulationScenario(null, null, null)
                : ProjectJson.gson().fromJson(Files.readString(scenarioPath, StandardCharsets.UTF_8), SimulationScenario.class);
        SimulationReport report = new ProjectSimulator().simulate(project, scenario);
        System.out.println("Routes: " + report.routes());
        System.out.println("Endings: " + report.endings());
        System.out.println("Gameplay gates: " + report.gameplayGates());
        System.out.println("External requirements: " + report.externalRequirements());
        System.out.println("Cycles: " + report.cycles().size());
        return report.cycles().isEmpty() ? 0 : 1;
    }

    private static int compile(ScenePackProject project, Path output) throws Exception {
        CompilationResult result = new DatapackCompiler().compile(project, output);
        System.out.printf("Compiled %d file(s) to %s%n", result.files().size(), result.output().toAbsolutePath());
        return 0;
    }

    private static int graph(ScenePackProject project, Path output) throws Exception {
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(output, new MermaidExporter().export(project), StandardCharsets.UTF_8);
        System.out.println("Wrote graph to " + output.toAbsolutePath());
        return 0;
    }

    private static int build(ScenePackProject project, Path output) throws Exception {
        ValidationReport validation = new ProjectValidator().validate(project);
        if (!validation.valid()) {
            System.err.println("Build refused: project has " + validation.errors() + " validation error(s).");
            return 1;
        }
        if (Files.isDirectory(output)) {
            try (var children = Files.list(output)) {
                if (children.findAny().isPresent()) throw new IllegalArgumentException("Build output is not empty: " + output);
            }
        }
        Files.createDirectories(output);
        SimulationReport simulation = new ProjectSimulator().simulate(project);
        new BuildReportWriter().write(output, project, validation, simulation);
        CompilationResult result = new DatapackCompiler().compile(project, output.resolve("datapack"));
        System.out.printf("Built %s: %d route(s), %d ending(s), %d datapack file(s).%n",
                project.pack().id(), simulation.routes(), simulation.endings().size(), result.files().size());
        return simulation.cycles().isEmpty() ? 0 : 1;
    }

    private static int explain(ScenePackProject project, String node) {
        System.out.print(new NodeExplainer().explain(project, node));
        return 0;
    }

    private static Path requiredOutput(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("This command requires an output path.");
        }
        return Path.of(args[2]);
    }

    private static String requiredValue(String[] args) {
        if (args.length < 3) throw new IllegalArgumentException("This command requires a node id.");
        return args[2];
    }

    private static void help() {
        System.out.println("Block Party conversation tool");
        System.out.println("  validate <project.json>");
        System.out.println("  simulate <project.json> [scenario.json]");
        System.out.println("  compile <project.json> <empty-output-directory>");
        System.out.println("  graph <project.json> <output.mmd>");
        System.out.println("  build <project.json> <empty-output-directory>");
        System.out.println("  explain <project.json> <node-id>");
        System.out.println("  catalog <brief.json> <catalog.json>");
        System.out.println("  generate <brief.json> <empty-output-directory>");
        System.out.println("  replay <previous-generation-directory> <empty-output-directory>");
        System.out.println("  batch plan <batch.json>");
        System.out.println("  batch expand <batch.json>");
        System.out.println("  batch generate <batch.json> [--resume] [--only <job-id> --force] [--confirm-large-batch]");
        System.out.println("  batch validate <batch.json>");
        System.out.println("  batch compile <batch.json> <empty-output-directory>");
        System.out.println("  batch install <batch.json> --live");
    }
}
