package block_party.conversation.report;

import block_party.conversation.model.ActionType;
import block_party.conversation.model.PackAction;
import block_party.conversation.model.PackCondition;
import block_party.conversation.model.ProjectIndex;
import block_party.conversation.model.ResponseEdge;
import block_party.conversation.model.SceneNode;
import block_party.conversation.model.ScenePackProject;
import java.util.ArrayList;
import java.util.List;

public final class NodeExplainer {
    public String explain(ScenePackProject project, String id) {
        ProjectIndex index = new ProjectIndex(project);
        SceneNode node = index.nodes().get(id);
        if (node == null) throw new IllegalArgumentException("Unknown node '" + id + "'.");
        List<String> incoming = new ArrayList<>();
        for (SceneNode candidate : project.nodes()) {
            if (id.equals(candidate.next())) incoming.add(candidate.id() + " (gameplay)");
            for (ResponseEdge edge : candidate.responses()) {
                if (id.equals(edge.target())) incoming.add(candidate.id() + " via " + edge.cue());
            }
        }
        StringBuilder text = new StringBuilder();
        text.append("Node: ").append(node.id()).append("\n");
        text.append("Type: ").append(node.type()).append("\n");
        text.append("Incoming: ").append(incoming.isEmpty() ? "entry" : String.join(", ", incoming)).append("\n");
        text.append("Conditions:\n");
        for (PackCondition condition : node.conditions()) {
            text.append("  - ").append(condition.type());
            if (condition.state() != null) text.append(" ").append(condition.state());
            if (condition.item() != null) text.append(" ").append(condition.count()).append(" x ").append(condition.item());
            if (condition.not()) text.append(" (not)");
            text.append("\n");
        }
        text.append("Writes/effects:\n");
        appendActions(text, node.actions());
        node.responses().forEach(edge -> appendActions(text, edge.actions()));
        text.append("Targets: ");
        List<String> targets = new ArrayList<>();
        if (node.next() != null) targets.add(node.next());
        node.responses().stream().map(ResponseEdge::target).forEach(targets::add);
        text.append(targets.isEmpty() ? "none" : String.join(", ", targets)).append("\n");
        return text.toString();
    }

    private static void appendActions(StringBuilder text, List<PackAction> actions) {
        for (PackAction action : actions) {
            text.append("  - ").append(action.type());
            if (action.state() != null) text.append(" ").append(action.state());
            if (action.item() != null) text.append(" ").append(Math.max(1, action.count())).append(" x ").append(action.item());
            if (action.type() == ActionType.CHANGE_COUNTER) text.append(" by ").append(action.amount());
            text.append("\n");
        }
    }
}
