package block_party.conversation.workbench;

import block_party.conversation.io.ProjectJson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

final class WorkbenchStateStore {
    private final Path path;
    private State state;

    WorkbenchStateStore(Path path) {
        this.path = path.toAbsolutePath().normalize();
        this.state = read();
    }

    static WorkbenchStateStore userStore() {
        return new WorkbenchStateStore(Path.of(System.getProperty("user.home"), ".blockparty", "workbench-state.json"));
    }

    synchronized State state() { return state; }

    synchronized void recentProject(Path path, String title) {
        state = state.withRecent(state.recentProjects(), new Recent(path.toAbsolutePath().normalize().toString(), title, Instant.now().toEpochMilli()), false);
        write();
    }

    synchronized void recentSolution(Path path, String title) {
        state = state.withRecent(state.recentSolutions(), new Recent(path.toAbsolutePath().normalize().toString(), title, Instant.now().toEpochMilli()), true);
        write();
    }

    synchronized void pin(String kind, Path path, boolean pinned) {
        List<String> current = new ArrayList<>("solution".equals(kind) ? state.pinnedSolutions() : state.pinnedProjects());
        String value = path.toAbsolutePath().normalize().toString();
        current.removeIf(value::equalsIgnoreCase);
        if (pinned) current.addFirst(value);
        state = "solution".equals(kind) ? state.withPinnedSolutions(current) : state.withPinnedProjects(current);
        write();
    }

    synchronized void lastSolution(Path value) {
        state = state.withLastSolution(value == null ? "" : value.toAbsolutePath().normalize().toString());
        write();
    }

    private State read() {
        if (!Files.isRegularFile(path)) return State.empty();
        try {
            State value = ProjectJson.gson().fromJson(Files.readString(path), State.class);
            return value == null ? State.empty() : value.normalized();
        } catch (Exception exception) {
            try { Files.move(path, path.resolveSibling(path.getFileName() + ".corrupt-" + System.currentTimeMillis())); }
            catch (Exception ignored) {}
            return State.empty();
        }
    }

    private void write() {
        try {
            Files.createDirectories(path.getParent());
            Path temporary = path.resolveSibling(path.getFileName() + ".tmp");
            Files.writeString(temporary, ProjectJson.gson().toJson(state) + System.lineSeparator());
            try { Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (java.nio.file.AtomicMoveNotSupportedException ignored) { Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING); }
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to save Workbench state: " + exception.getMessage(), exception);
        }
    }

    record Recent(String path, String title, long opened) {}
    record State(int stateFormat, List<Recent> recentSolutions, List<Recent> recentProjects,
                 List<String> pinnedSolutions, List<String> pinnedProjects, String lastSolution) {
        State normalized() {
            return new State(1, copy(recentSolutions), copy(recentProjects), strings(pinnedSolutions), strings(pinnedProjects), lastSolution == null ? "" : lastSolution);
        }
        static State empty() { return new State(1, List.of(), List.of(), List.of(), List.of(), ""); }
        State withRecent(List<Recent> source, Recent item, boolean solution) {
            List<Recent> values = new ArrayList<>(source);
            values.removeIf(value -> value.path().equalsIgnoreCase(item.path()));
            values.addFirst(item);
            values = values.stream().limit(20).toList();
            return solution ? new State(1, values, recentProjects, pinnedSolutions, pinnedProjects, item.path())
                    : new State(1, recentSolutions, values, pinnedSolutions, pinnedProjects, lastSolution);
        }
        State withPinnedSolutions(List<String> values) { return new State(1, recentSolutions, recentProjects, List.copyOf(values), pinnedProjects, lastSolution); }
        State withPinnedProjects(List<String> values) { return new State(1, recentSolutions, recentProjects, pinnedSolutions, List.copyOf(values), lastSolution); }
        State withLastSolution(String value) { return new State(1, recentSolutions, recentProjects, pinnedSolutions, pinnedProjects, value); }
        private static <T> List<T> copy(List<T> values) { return values == null ? List.of() : List.copyOf(values); }
        private static List<String> strings(List<String> values) { return values == null ? List.of() : List.copyOf(values); }
    }
}
