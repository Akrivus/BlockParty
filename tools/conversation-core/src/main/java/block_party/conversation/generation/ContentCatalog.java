package block_party.conversation.generation;

import java.util.List;

public record ContentCatalog(
        String blockPartyVersion,
        List<String> characters,
        List<String> actions,
        List<String> conditions,
        List<String> responseCues,
        List<CatalogDocument> documents,
        ResolvedGenerationContext context) {
    public ContentCatalog {
        characters = List.copyOf(characters);
        actions = List.copyOf(actions);
        conditions = List.copyOf(conditions);
        responseCues = List.copyOf(responseCues);
        documents = List.copyOf(documents);
        context = context == null ? ResolvedGenerationContext.empty() : context;
    }
}
