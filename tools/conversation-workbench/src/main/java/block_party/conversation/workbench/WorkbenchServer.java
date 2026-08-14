package block_party.conversation.workbench;

import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.simulation.SimulationScenario;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

public final class WorkbenchServer {
    private final WorkbenchService service;
    private final HttpServer server;

    private WorkbenchServer(Path project, int port) throws Exception {
        service = new WorkbenchService(project);
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        server.createContext("/api/project", exchange -> api(exchange, this::project));
        server.createContext("/api/validate", exchange -> api(exchange, this::validate));
        server.createContext("/api/simulate", exchange -> api(exchange, this::simulate));
        server.createContext("/api/save", exchange -> api(exchange, this::save));
        server.createContext("/api/export", exchange -> api(exchange, this::export));
        server.createContext("/", this::staticAsset);
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: conversation-workbench <project.json|generation-directory> [--port <port>] [--no-open]");
            System.exit(2);
        }
        int port = 0;
        boolean open = true;
        for (int i = 1; i < args.length; i++) {
            if ("--port".equals(args[i]) && i + 1 < args.length) port = Integer.parseInt(args[++i]);
            else if ("--no-open".equals(args[i])) open = false;
        }
        WorkbenchServer workbench = new WorkbenchServer(Path.of(args[0]), port);
        workbench.server.start();
        URI uri = URI.create("http://localhost:" + workbench.server.getAddress().getPort() + "/");
        System.out.println("Block Party Conversation Workbench: " + uri);
        System.out.println("Editing: " + workbench.service.projectPath());
        if (open && Desktop.isDesktopSupported()) Desktop.getDesktop().browse(uri);
    }

    private Object project(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "GET");
        JsonObject response = new JsonObject();
        response.addProperty("path", service.projectPath().toString());
        response.add("project", ProjectJson.gson().toJsonTree(service.load()));
        return response;
    }

    private Object validate(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        return service.validate(readProject(exchange));
    }

    private Object simulate(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        JsonObject body = readObject(exchange);
        ScenePackProject project = ProjectJson.gson().fromJson(body.get("project"), ScenePackProject.class);
        SimulationScenario scenario = body.has("scenario")
                ? ProjectJson.gson().fromJson(body.get("scenario"), SimulationScenario.class)
                : new SimulationScenario(null, null, null);
        return service.simulate(project, scenario);
    }

    private Object save(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        service.save(readProject(exchange));
        return Map.of("saved", true, "path", service.projectPath().toString());
    }

    private Object export(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        JsonObject body = readObject(exchange);
        return service.export(ProjectJson.gson().fromJson(body.get("project"), ScenePackProject.class), Path.of(body.get("output").getAsString()));
    }

    private ScenePackProject readProject(HttpExchange exchange) throws IOException {
        JsonObject body = readObject(exchange);
        return ProjectJson.gson().fromJson(body.has("project") ? body.get("project") : body, ScenePackProject.class);
    }

    private JsonObject readObject(HttpExchange exchange) throws IOException {
        return ProjectJson.gson().fromJson(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), JsonObject.class);
    }

    private void api(HttpExchange exchange, ApiHandler handler) throws IOException {
        try {
            send(exchange, 200, "application/json", ProjectJson.gson().toJson(handler.handle(exchange)));
        } catch (Exception exception) {
            send(exchange, 400, "application/json", ProjectJson.gson().toJson(Map.of("error", exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage())));
        }
    }

    private void staticAsset(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/".equals(path)) path = "/index.html";
        if (!path.matches("/[a-zA-Z0-9._-]+")) {
            send(exchange, 404, "text/plain", "Not found");
            return;
        }
        try (var stream = WorkbenchServer.class.getResourceAsStream("/workbench" + path)) {
            if (stream == null) {
                send(exchange, 404, "text/plain", "Not found");
                return;
            }
            String type = path.endsWith(".css") ? "text/css" : path.endsWith(".js") ? "text/javascript" : "text/html";
            byte[] bytes = stream.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", type + "; charset=utf-8");
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        }
    }

    private static void requireMethod(HttpExchange exchange, String method) {
        if (!method.equals(exchange.getRequestMethod())) throw new IllegalArgumentException("Expected " + method + " request.");
    }

    private static void send(HttpExchange exchange, int status, String type, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", type + "; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    @FunctionalInterface
    private interface ApiHandler {
        Object handle(HttpExchange exchange) throws Exception;
    }
}
