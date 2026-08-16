package block_party.conversation.workbench;

import block_party.conversation.generation.GenerationBrief;
import block_party.conversation.generation.GenerationPipeline;
import block_party.conversation.generation.GenerationProgressListener;
import block_party.conversation.generation.GenerationStage;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class GenerationRun {
    private final Supplier<Path> repositoryRoot;
    private final Consumer<Path> openGeneratedProject;
    private volatile Status status = new Status("IDLE", "IDLE", 0, null);

    GenerationRun(Supplier<Path> repositoryRoot, Consumer<Path> openGeneratedProject) {
        this.repositoryRoot = repositoryRoot;
        this.openGeneratedProject = openGeneratedProject;
    }

    synchronized void start(GenerationBrief brief, Path output) {
        if (status.running()) {
            throw new IllegalStateException("A generation is already running.");
        }

        status = new Status("RUNNING", "CATALOG", 0, null);
        Thread.ofVirtual().name("conversation-generation").start(() -> generate(brief, output));
    }

    JsonObject statusJson() {
        return status.toJson();
    }

    private void generate(GenerationBrief brief, Path output) {
        try {
            Path root = repositoryRoot.get();
            var model = NarrativeModels.create(brief, root);
            var result = new GenerationPipeline(model, progressListener()).generate(brief, root, output);
            openGeneratedProject.accept(result.output().resolve("project.json").toAbsolutePath().normalize());
            status = new Status("COMPLETE", "COMPLETE", result.modelCalls(), null);
        } catch (Exception exception) {
            String message = exception.getMessage() == null
                    ? exception.getClass().getSimpleName()
                    : exception.getMessage();
            Status failedAt = status;
            String stage = failedAt.stage().replace("_COMPLETE", "");
            status = new Status("FAILED", stage, failedAt.calls(), message);
        }
    }

    private GenerationProgressListener progressListener() {
        return new GenerationProgressListener() {
            @Override
            public void stageStarted(GenerationStage stage, int call) {
                status = new Status("RUNNING", stage.name(), call, null);
            }

            @Override
            public void stageCompleted(GenerationStage stage, int call) {
                status = new Status("RUNNING", stage.name() + "_COMPLETE", call, null);
            }
        };
    }

    private record Status(String state, String stage, int calls, String error) {
        boolean running() {
            return "RUNNING".equals(state);
        }

        JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("state", state);
            json.addProperty("stage", stage);
            json.addProperty("calls", calls);
            if (error != null) {
                json.addProperty("error", error);
            }
            return json;
        }
    }
}
