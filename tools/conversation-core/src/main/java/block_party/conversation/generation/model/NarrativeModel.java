package block_party.conversation.generation.model;

public interface NarrativeModel {
    ModelResponse generate(ModelRequest request) throws Exception;
}
