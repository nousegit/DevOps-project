package com.todo;

import io.javalin.Javalin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private Javalin app;
    private HttpClient client;

    @BeforeEach void startServer() {
        Main.resetTodos();
        app = Main.createApp().start(0);
        client = HttpClient.newHttpClient();
    }

    @AfterEach void stopServer() { app.stop(); }

    private HttpResponse<String> request(String method, String path, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + app.port() + path));
        if (body != null) builder.header("Content-Type", "application/json");
        HttpRequest.BodyPublisher publisher = body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body);
        return client.send(builder.method(method, publisher).build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test void healthEndpointReturnsUp() throws Exception {
        var response = request("GET", "/health", null);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("UP"));
    }

    @Test void servesBrowserUiFromRoot() throws Exception {
        var response = request("GET", "/", null);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Make room for"));
    }

    @Test void createsListsCompletesAndDeletesTask() throws Exception {
        var created = request("POST", "/api/todos", "{\"title\":\"Write tests\"}");
        assertEquals(201, created.statusCode());
        assertTrue(created.body().contains("Write tests"));
        String id = created.body().replaceAll(".*\\\"id\\\":(\\d+).*", "$1");

        assertEquals(200, request("GET", "/api/todos", null).statusCode());
        assertEquals(200, request("PATCH", "/api/todos/" + id, "{\"completed\":true}").statusCode());
        assertEquals(204, request("DELETE", "/api/todos/" + id, null).statusCode());
        assertEquals(404, request("DELETE", "/api/todos/" + id, null).statusCode());
    }

    @Test void rejectsBlankTaskTitle() throws Exception {
        var response = request("POST", "/api/todos", "{\"title\":\"   \"}");
        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("required"));
    }
}
