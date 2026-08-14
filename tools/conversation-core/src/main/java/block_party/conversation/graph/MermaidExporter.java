package block_party.conversation.graph;

import block_party.conversation.model.ResponseEdge;
import block_party.conversation.model.SceneNode;
import block_party.conversation.model.ScenePackProject;

public final class MermaidExporter {
    public String export(ScenePackProject project) {
        StringBuilder output = new StringBuilder("flowchart LR\n");
        for (SceneNode node : project.nodes()) {
            output.append("    ").append(safe(node.id())).append("[")
                    .append(escape(node.title() == null ? node.id() : node.title())).append("]\n");
        }
        for (SceneNode node : project.nodes()) {
            for (ResponseEdge edge : node.responses()) {
                output.append("    ").append(safe(node.id())).append(" -->|")
                        .append(escape(edge.label() == null ? edge.cue() : edge.label())).append("| ")
                        .append(safe(edge.target())).append("\n");
            }
            if (node.next() != null && !node.next().isBlank()) {
                output.append("    ").append(safe(node.id())).append(" -. gameplay .-> ")
                        .append(safe(node.next())).append("\n");
            }
        }
        return output.toString();
    }

    private static String safe(String value) {
        return "n_" + value.replaceAll("[^A-Za-z0-9_]", "_");
    }

    private static String escape(String value) {
        return value.replace("\"", "'").replace("[", "(").replace("]", ")");
    }
}
