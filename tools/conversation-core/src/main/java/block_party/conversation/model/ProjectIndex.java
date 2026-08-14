package block_party.conversation.model;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ProjectIndex {
    private final ScenePackProject project;
    private final Map<String, SceneNode> nodes = new LinkedHashMap<>();
    private final Map<String, StateDeclaration> state = new LinkedHashMap<>();

    public ProjectIndex(ScenePackProject project) {
        this.project = project;
        project.nodes().forEach(node -> nodes.putIfAbsent(node.id(), node));
        project.state().forEach(value -> state.putIfAbsent(value.id(), value));
    }

    public ScenePackProject project() {
        return project;
    }

    public Map<String, SceneNode> nodes() {
        return nodes;
    }

    public Map<String, StateDeclaration> state() {
        return state;
    }

    public String compiledState(String id) {
        if (id == null || id.contains(".")) {
            return id;
        }
        return project.pack().namespace() + "." + project.pack().id() + "." + id;
    }
}
