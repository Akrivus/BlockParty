package block_party.conversation.workbench;

import block_party.conversation.io.ProjectJson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

record WorkbenchSolution(int solutionFormat, String name, List<ProjectReference> projects) {
    WorkbenchSolution {
        solutionFormat = solutionFormat <= 0 ? 1 : solutionFormat;
        name = name == null || name.isBlank() ? "Block Party Solution" : name;
        projects = projects == null ? List.of() : List.copyOf(projects);
    }

    record ProjectReference(String id, String path, String group) {
        ProjectReference {
            id = WorkbenchSession.safeId(id);
            path = path == null ? "" : path;
            group = group == null || group.isBlank() ? "Projects" : group;
        }
    }

    static WorkbenchSolution read(Path path) throws Exception {
        WorkbenchSolution value = ProjectJson.gson().fromJson(Files.readString(path), WorkbenchSolution.class);
        if (value == null || value.solutionFormat() != 1) {
            throw new IllegalArgumentException("Unsupported or empty solution file: " + path);
        }
        return value;
    }

    void write(Path path) throws Exception {
        Path normalized = path.toAbsolutePath().normalize();
        Files.createDirectories(normalized.getParent());
        Path temporary = normalized.resolveSibling(normalized.getFileName() + ".workbench.tmp");
        Files.writeString(temporary, ProjectJson.gson().toJson(this) + System.lineSeparator());
        try {
            Files.move(temporary, normalized, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
            Files.move(temporary, normalized, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    Path resolve(Path solutionPath, ProjectReference reference) {
        Path value = Path.of(reference.path());
        return (value.isAbsolute() ? value : solutionPath.getParent().resolve(value)).toAbsolutePath().normalize();
    }

    WorkbenchSolution add(Path solutionPath, Path projectPath, String group) {
        Path normalized = projectPath.toAbsolutePath().normalize();
        for (ProjectReference reference : projects) {
            if (resolve(solutionPath, reference).equals(normalized)) return this;
        }
        String relative;
        try {
            relative = solutionPath.getParent().relativize(normalized).toString();
        } catch (IllegalArgumentException exception) {
            relative = normalized.toString();
        }
        String base = WorkbenchSession.safeId(normalized.getParent() == null
                ? normalized.getFileName().toString() : normalized.getParent().getFileName().toString());
        String id = base;
        int suffix = 2;
        while (containsId(id)) id = base + "_" + suffix++;
        List<ProjectReference> updated = new ArrayList<>(projects);
        updated.add(new ProjectReference(id, relative, group));
        return new WorkbenchSolution(solutionFormat, name, updated);
    }

    WorkbenchSolution remove(String id) {
        return new WorkbenchSolution(solutionFormat, name,
                projects.stream().filter(reference -> !reference.id().equals(id)).toList());
    }

    private boolean containsId(String id) {
        for (ProjectReference reference : projects) if (reference.id().equals(id)) return true;
        return false;
    }
}
