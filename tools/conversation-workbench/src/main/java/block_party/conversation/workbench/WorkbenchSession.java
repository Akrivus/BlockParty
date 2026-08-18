package block_party.conversation.workbench;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class WorkbenchSession {
    private final Path workingDirectory;
    private final WorkbenchStateStore stateStore;
    private final Map<String, Document> documents = new LinkedHashMap<>();
    private volatile String activeDocument;
    private volatile Path solutionPath;
    private volatile WorkbenchSolution solution;

    WorkbenchSession(Path source) { this(source, WorkbenchStateStore.userStore()); }

    WorkbenchSession(Path source, WorkbenchStateStore stateStore) {
        this.stateStore = stateStore;
        workingDirectory = Path.of("").toAbsolutePath().normalize();
        if (source != null) open(source);
    }

    synchronized Map<String, Object> open(Path source) {
        Path normalized = source.toAbsolutePath().normalize();
        if (normalized.getFileName().toString().endsWith(".bpsolution.json")) return openSolution(normalized);
        return openProject(normalized);
    }

    synchronized Map<String, Object> openProject(Path source) {
        WorkbenchService service = new WorkbenchService(source);
        String existing = documentId(service.projectPath());
        if (existing != null) { activeDocument = existing; return describe(); }
        String id = "document-" + Integer.toUnsignedString(service.projectPath().toString().toLowerCase().hashCode(), 36);
        documents.put(id, new Document(id, service));
        activeDocument = id;
        try {
            var project = service.load();
            stateStore.recentProject(service.projectPath(), project.pack() == null ? service.projectPath().getFileName().toString() : project.pack().title());
        } catch (Exception ignored) {}
        return describe();
    }

    synchronized Map<String, Object> create(Path source, String id, String title) throws Exception {
        return create(source, id, title, true);
    }

    synchronized Map<String, Object> create(Path source, String id, String title, boolean addToSolution) throws Exception {
        Path target = source == null ? defaultProjectPath(id) : source;
        WorkbenchService.createStarter(target, id, title);
        Map<String, Object> result = openProject(target);
        if (solution != null && addToSolution) addProjectToSolution(target, "Projects");
        return result;
    }

    synchronized Map<String, Object> createSolution(Path source, String name) throws Exception {
        Path target = source.toAbsolutePath().normalize();
        if (!target.getFileName().toString().endsWith(".bpsolution.json")) target = target.resolveSibling(target.getFileName() + ".bpsolution.json");
        WorkbenchSolution created = new WorkbenchSolution(1, name, List.of());
        created.write(target);
        solutionPath = target;
        solution = created;
        stateStore.recentSolution(target, created.name());
        return describe();
    }

    synchronized Map<String, Object> openSolution(Path source) {
        try {
            solutionPath = source.toAbsolutePath().normalize();
            solution = WorkbenchSolution.read(solutionPath);
            stateStore.recentSolution(solutionPath, solution.name());
            for (WorkbenchSolution.ProjectReference reference : solution.projects()) {
                Path project = solution.resolve(solutionPath, reference);
                if (Files.isRegularFile(project) || Files.isDirectory(project)) {
                    try { openProject(project); } catch (Exception ignored) {}
                    break;
                }
            }
            return describe();
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to open solution: " + exception.getMessage(), exception);
        }
    }

    synchronized Map<String, Object> addProjectToSolution(Path source, String group) throws Exception {
        if (solution == null) throw new IllegalStateException("No solution is open.");
        WorkbenchService candidate = new WorkbenchService(source);
        solution = solution.add(solutionPath, candidate.projectPath(), group);
        solution.write(solutionPath);
        return describe();
    }

    synchronized Map<String, Object> registerProject(Path targetSolution, String name, Path project, String group) throws Exception {
        Path target = targetSolution.toAbsolutePath().normalize();
        if (!target.getFileName().toString().endsWith(".bpsolution.json")) {
            target = target.resolveSibling(target.getFileName() + ".bpsolution.json");
        }
        WorkbenchSolution updated;
        if (Files.isRegularFile(target)) {
            updated = WorkbenchSolution.read(target);
        } else {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("A name is required when creating a solution.");
            updated = new WorkbenchSolution(1, name, List.of());
        }
        WorkbenchService candidate = new WorkbenchService(project);
        updated = updated.add(target, candidate.projectPath(), group);
        updated.write(target);
        solutionPath = target;
        solution = updated;
        stateStore.recentSolution(target, updated.name());
        return describe();
    }

    synchronized Map<String, Object> removeProjectFromSolution(String id) throws Exception {
        if (solution == null) throw new IllegalStateException("No solution is open.");
        solution = solution.remove(id);
        solution.write(solutionPath);
        return describe();
    }

    synchronized Map<String, Object> activate(String id) { requireDocument(id); activeDocument = id; return describe(); }

    synchronized Map<String, Object> closeDocument(String id) {
        documents.remove(id);
        if (id != null && id.equals(activeDocument)) activeDocument = documents.keySet().stream().findFirst().orElse(null);
        return describe();
    }

    synchronized Map<String, Object> close() {
        documents.clear(); activeDocument = null; solution = null; solutionPath = null;
        stateStore.lastSolution(null);
        return describe();
    }

    synchronized void pin(String kind, Path path, boolean pinned) { stateStore.pin(kind, path, pinned); }

    WorkbenchService requireProject() { return requireDocument(activeDocument).service(); }
    WorkbenchService requireProject(String id) { return requireDocument(id == null || id.isBlank() ? activeDocument : id).service(); }

    Map<String, Object> describe() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("projectOpen", activeDocument != null);
        result.put("workingDirectory", workingDirectory.toString());
        result.put("suggestedAuthoringDirectory", workingDirectory.resolve("authoring").toString());
        result.put("suggestedExportDirectory", workingDirectory.resolve("dist").toString());
        result.put("activeDocument", activeDocument == null ? "" : activeDocument);
        List<Map<String, Object>> open = new ArrayList<>();
        for (Document document : documents.values()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", document.id()); item.put("path", document.service().projectPath().toString());
            try { var project = document.service().load(); item.put("title", project.pack() == null ? document.service().projectPath().getFileName().toString() : project.pack().title()); }
            catch (Exception exception) { item.put("title", document.service().projectPath().getFileName().toString()); }
            open.add(item);
        }
        result.put("documents", open);
        if (activeDocument != null) result.put("projectPath", requireProject().projectPath().toString());
        if (solution != null) {
            result.put("solutionOpen", true); result.put("solutionPath", solutionPath.toString()); result.put("solution", solutionDescription());
        } else result.put("solutionOpen", false);
        result.put("userState", stateStore.state());
        result.put("recentGenerations", recentGenerations());
        return result;
    }

    private List<Map<String, Object>> recentGenerations() {
        Path authoring = workingDirectory.resolve("authoring");
        if (!Files.isDirectory(authoring)) return List.of();
        try (var paths = Files.find(authoring, 5, (path, attributes) -> attributes.isRegularFile()
                && path.getFileName().toString().equals("project.json")
                && path.getParent() != null && path.getParent().getFileName().toString().equals("generated"))) {
            return paths.filter(path -> path.getParent().getParent() == null
                            || !path.getParent().getParent().getFileName().toString().startsWith("test_"))
                    .sorted((left, right) -> {
                        try { return Files.getLastModifiedTime(right).compareTo(Files.getLastModifiedTime(left)); }
                        catch (Exception ignored) { return 0; }
                    }).limit(12).map(path -> {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("path", path.toAbsolutePath().normalize().toString());
                        item.put("title", path.getParent().getParent().getFileName().toString());
                        try {
                            var project = new WorkbenchService(path).load();
                            if (project.pack() != null) item.put("title", project.pack().title());
                        } catch (Exception ignored) {}
                        return item;
                    }).toList();
        } catch (Exception exception) { return List.of(); }
    }

    private Map<String, Object> solutionDescription() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", solution.name()); result.put("path", solutionPath.toString());
        result.put("projects", solution.projects().stream().map(reference -> Map.of(
                "id", reference.id(), "path", solution.resolve(solutionPath, reference).toString(), "storedPath", reference.path(),
                "group", reference.group(), "missing", !Files.isRegularFile(solution.resolve(solutionPath, reference)))).toList());
        return result;
    }

    private Document requireDocument(String id) {
        Document document = id == null ? null : documents.get(id);
        if (document == null) throw new IllegalStateException("No conversation pack is open.");
        return document;
    }

    private String documentId(Path path) {
        Path normalized = path.toAbsolutePath().normalize();
        for (Document document : documents.values()) if (document.service().projectPath().equals(normalized)) return document.id();
        return null;
    }

    Path defaultProjectPath(String id) { return workingDirectory.resolve("authoring").resolve(safeId(id)).resolve("project.json"); }

    static String safeId(String value) {
        String id = value == null ? "" : value.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
        return id.isBlank() ? "new_conversation" : id;
    }

    private record Document(String id, WorkbenchService service) {}
}
