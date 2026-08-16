package block_party.conversation.workbench;

import block_party.conversation.generation.DialogueAlternative;
import block_party.conversation.generation.GenerationBrief;
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
    private final WorkbenchSession session;
    private final HttpServer server;

    private WorkbenchServer(Path project, int port) throws Exception {
        session = new WorkbenchSession(project);
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        server.createContext("/api/session", exchange -> api(exchange, this::session));
        server.createContext("/api/open", exchange -> api(exchange, this::open));
        server.createContext("/api/new", exchange -> api(exchange, this::create));
        server.createContext("/api/close", exchange -> api(exchange, this::close));
        server.createContext("/api/project", exchange -> api(exchange, this::project));
        server.createContext("/api/schema", exchange -> api(exchange, this::schema));
        server.createContext("/api/validate", exchange -> api(exchange, this::validate));
        server.createContext("/api/simulate", exchange -> api(exchange, this::simulate));
        server.createContext("/api/save", exchange -> api(exchange, this::save));
        server.createContext("/api/export", exchange -> api(exchange, this::export));
        server.createContext("/api/provenance", exchange -> api(exchange, this::provenance));
        server.createContext("/api/catalog", exchange -> api(exchange, this::catalog));
        server.createContext("/api/generation/start", exchange -> api(exchange, this::startGeneration));
        server.createContext("/api/generation/status", exchange -> api(exchange, this::generationStatus));
        server.createContext("/api/revision/request", exchange -> api(exchange, this::requestRevision));
        server.createContext("/api/revision/apply", exchange -> api(exchange, this::applyRevision));
        server.createContext("/", this::staticAsset);
    }

    public static void main(String[] args) throws Exception {
        boolean create = false;
        int port = 0;
        boolean open = true;
        Path source = null;
        for (int i = 0; i < args.length; i++) {
            if ("--port".equals(args[i]) && i + 1 < args.length) {
                port = Integer.parseInt(args[++i]);
            } else if ("--no-open".equals(args[i])) {
                open = false;
            } else if ("--new".equals(args[i])) {
                create = true;
            } else if (!args[i].startsWith("--") && source == null) {
                source = Path.of(args[i]);
            }
        }
        if (create) {
            if (source == null) {
                throw new IllegalArgumentException("--new requires a project path.");
            }
            WorkbenchService.createStarter(source);
        }
        WorkbenchServer workbench = new WorkbenchServer(source, port);
        workbench.server.start();
        URI uri = URI.create("http://localhost:" + workbench.server.getAddress().getPort() + "/");
        System.out.println("Block Party Conversation Workbench: " + uri);
        if (source == null) {
            System.out.println("No pack selected; opening the start screen.");
        } else {
            System.out.println("Editing: " + workbench.session.requireProject().projectPath());
        }
        if (open && Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(uri);
        }
    }

    private Object session(HttpExchange exchange) {
        requireMethod(exchange, "GET");
        return session.describe();
    }

    private Object open(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        JsonObject body = readObject(exchange);
        return session.open(Path.of(requiredString(body, "path")));
    }

    private Object create(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        JsonObject body = readObject(exchange);
        String id = requiredString(body, "id");
        String title = body.has("title") ? body.get("title").getAsString() : id;
        Path path = body.has("path") && !body.get("path").getAsString().isBlank()
                ? Path.of(body.get("path").getAsString())
                : null;
        return session.create(path, id, title);
    }

    private Object close(HttpExchange exchange) {
        requireMethod(exchange, "POST");
        return session.close();
    }

    private Object project(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "GET");
        WorkbenchService service = session.requireProject();
        ScenePackProject project = service.load();
        JsonObject response = new JsonObject();
        response.addProperty("path", service.projectPath().toString());
        response.addProperty("defaultExportPath", service.defaultExportPath(project).toString());
        response.addProperty("liveResourcesPath", service.liveResourcesPath(project).toString());
        response.add("project", ProjectJson.gson().toJsonTree(project));
        return response;
    }

    private Object validate(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        return session.requireProject().validate(readProject(exchange));
    }

    private Object schema(HttpExchange exchange) {
        requireMethod(exchange, "GET");
        return AuthoringSchema.describe();
    }

    private Object simulate(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        JsonObject body = readObject(exchange);
        ScenePackProject project = ProjectJson.gson().fromJson(body.get("project"), ScenePackProject.class);
        SimulationScenario scenario = body.has("scenario")
                ? ProjectJson.gson().fromJson(body.get("scenario"), SimulationScenario.class)
                : new SimulationScenario(null, null, null);
        return session.requireProject().simulate(project, scenario);
    }

    private Object save(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        WorkbenchService service = session.requireProject();
        service.save(readProject(exchange));
        return Map.of("saved", true, "path", service.projectPath().toString());
    }

    private Object export(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        JsonObject body = readObject(exchange);
        ScenePackProject project = ProjectJson.gson().fromJson(
                body.get("project"), ScenePackProject.class);
        if (body.has("liveResources") && body.get("liveResources").getAsBoolean()) {
            return session.requireProject().exportLiveResources(project);
        }
        return session.requireProject().export(project, Path.of(body.get("output").getAsString()));
    }

    private Object provenance(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "GET");
        return session.requireProject().provenance();
    }

    private Object catalog(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        JsonObject body = readObject(exchange);
        GenerationBrief brief = ProjectJson.gson().fromJson(body.get("brief"), GenerationBrief.class);
        return session.requireProject().catalog(brief);
    }

    private Object startGeneration(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        JsonObject body = readObject(exchange);
        GenerationBrief brief = ProjectJson.gson().fromJson(body.get("brief"), GenerationBrief.class);
        WorkbenchService service = session.requireProject();
        service.startGeneration(brief, Path.of(body.get("output").getAsString()));
        return service.generationStatus();
    }

    private Object generationStatus(HttpExchange exchange) {
        requireMethod(exchange, "GET");
        return session.requireProject().generationStatus();
    }

    private Object requestRevision(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        JsonObject body = readObject(exchange);
        return session.requireProject().requestRevision(
                ProjectJson.gson().fromJson(body.get("project"), ScenePackProject.class),
                body.get("node").getAsString(), body.get("instruction").getAsString(),
                body.get("provider").getAsString(), body.get("model").getAsString(),
                body.has("recordedResponses") ? body.get("recordedResponses").getAsString() : null);
    }

    private Object applyRevision(HttpExchange exchange) throws Exception {
        requireMethod(exchange, "POST");
        JsonObject body = readObject(exchange);
        ScenePackProject project = ProjectJson.gson().fromJson(
                body.get("project"), ScenePackProject.class);
        DialogueAlternative alternative = ProjectJson.gson().fromJson(
                body.get("alternative"), DialogueAlternative.class);
        return session.requireProject().applyRevision(
                project, body.get("node").getAsString(), alternative);
    }

    private ScenePackProject readProject(HttpExchange exchange) throws IOException {
        JsonObject body = readObject(exchange);
        return ProjectJson.gson().fromJson(body.has("project") ? body.get("project") : body, ScenePackProject.class);
    }

    private JsonObject readObject(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return ProjectJson.gson().fromJson(body, JsonObject.class);
    }

    private static String requiredString(JsonObject body, String name) {
        if (!body.has(name) || body.get(name).getAsString().isBlank()) {
            throw new IllegalArgumentException(name + " is required.");
        }
        return body.get(name).getAsString();
    }

    private void api(HttpExchange exchange, ApiHandler handler) throws IOException {
        try {
            String response = ProjectJson.gson().toJson(handler.handle(exchange));
            send(exchange, 200, "application/json", response);
        } catch (Exception exception) {
            String response = ProjectJson.gson().toJson(Map.of("error", errorMessage(exception)));
            send(exchange, 400, "application/json", response);
        }
    }

    private void staticAsset(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/".equals(path)) {
            path = "/index.html";
        }
        if (!path.matches("/[a-zA-Z0-9._-]+")) {
            send(exchange, 404, "text/plain", "Not found");
            return;
        }
        try (var stream = WorkbenchServer.class.getResourceAsStream("/workbench" + path)) {
            if (stream == null) {
                send(exchange, 404, "text/plain", "Not found");
                return;
            }
            String type = contentType(path);
            byte[] bytes = stream.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", type + "; charset=utf-8");
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        }
    }

    private static void requireMethod(HttpExchange exchange, String method) {
        if (!method.equals(exchange.getRequestMethod())) {
            throw new IllegalArgumentException("Expected " + method + " request.");
        }
    }

    private static String errorMessage(Exception exception) {
        return exception.getMessage() == null
                ? exception.getClass().getSimpleName()
                : exception.getMessage();
    }

    private static String contentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
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
