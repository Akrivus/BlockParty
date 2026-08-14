package block_party.conversation.model;

public record StateDeclaration(
        String id,
        StateType type,
        StateScope scope,
        boolean initialCookie,
        int initialCounter,
        Integer minimum,
        Integer maximum) {
}
