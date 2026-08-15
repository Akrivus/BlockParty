package block_party.conversation.workbench;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

final class WorkbenchSession {
    private final Path workingDirectory;
    private volatile WorkbenchService service;

    WorkbenchSession(Path source) {
        workingDirectory = Path.of("").toAbsolutePath().normalize();
        if (source != null) {
            open(source);
        }
    }

    synchronized Map<String, Object> open(Path source) {
        service = new WorkbenchService(source);
        return describe();
    }

    synchronized Map<String, Object> create(Path source, String id, String title) throws Exception {
        Path target = source == null ? defaultProjectPath(id) : source;
        WorkbenchService.createStarter(target, id, title);
        service = new WorkbenchService(target);
        return describe();
    }

    synchronized Map<String, Object> close() {
        service = null;
        return describe();
    }

    WorkbenchService requireProject() {
        WorkbenchService current = service;
        if (current == null) {
            throw new IllegalStateException("No conversation pack is open.");
        }
        return current;
    }

    Map<String, Object> describe() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("projectOpen", service != null);
        result.put("workingDirectory", workingDirectory.toString());
        result.put("suggestedAuthoringDirectory", workingDirectory.resolve("authoring").toString());
        result.put("suggestedExportDirectory", workingDirectory.resolve("dist").toString());
        if (service != null) {
            result.put("projectPath", service.projectPath().toString());
        }
        return result;
    }

    Path defaultProjectPath(String id) {
        return workingDirectory.resolve("authoring").resolve(safeId(id)).resolve("project.json");
    }

    static String safeId(String value) {
        String id = value == null ? "" : value.trim().toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
        return id.isBlank() ? "new_conversation" : id;
    }
}
