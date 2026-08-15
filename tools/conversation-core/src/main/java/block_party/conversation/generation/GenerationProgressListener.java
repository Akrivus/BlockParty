package block_party.conversation.generation;

public interface GenerationProgressListener {
    GenerationProgressListener NONE = new GenerationProgressListener() { };

    default void stageStarted(GenerationStage stage, int callNumber) { }

    default void stageCompleted(GenerationStage stage, int callNumber) { }
}
