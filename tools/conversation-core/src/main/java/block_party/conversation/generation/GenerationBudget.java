package block_party.conversation.generation;

public record GenerationBudget(int maximumCalls, int maximumInputCharacters, int maximumOutputCharacters) {
    public GenerationBudget {
        maximumCalls = maximumCalls <= 0 ? 20 : maximumCalls;
        maximumInputCharacters = maximumInputCharacters <= 0 ? 500_000 : maximumInputCharacters;
        maximumOutputCharacters = maximumOutputCharacters <= 0 ? 200_000 : maximumOutputCharacters;
    }
}
