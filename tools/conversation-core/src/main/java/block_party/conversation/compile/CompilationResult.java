package block_party.conversation.compile;

import java.nio.file.Path;
import java.util.List;

public record CompilationResult(Path output, List<Path> files) {
    public CompilationResult {
        files = List.copyOf(files);
    }
}
