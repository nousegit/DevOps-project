package com.todo;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    private static final List<Todo> todos = new CopyOnWriteArrayList<>();
    private static final AtomicLong ids = new AtomicLong(3);

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7070"));
        todos.add(new Todo(1, "Sketch the project plan", true));
        todos.add(new Todo(2, "Build the first version", false));
        todos.add(new Todo(3, "Ship it with confidence", false));
        createApp().start(port);
    }

    static Javalin createApp() {
        return Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "/public";
                staticFiles.location = io.javalin.http.staticfiles.Location.CLASSPATH;
            });
            config.routes.get("/api/todos", ctx -> ctx.json(todos));
            config.routes.post("/api/todos", ctx -> {
                CreateTodo input = ctx.bodyAsClass(CreateTodo.class);
                if (input.title == null || input.title.isBlank()) {
                    ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorMessage("Task title is required"));
                    return;
                }
                Todo todo = new Todo(ids.incrementAndGet(), input.title.trim(), false);
                todos.add(todo);
                ctx.status(HttpStatus.CREATED).json(todo);
            });
            config.routes.patch("/api/todos/{id}", ctx -> {
                long id = Long.parseLong(ctx.pathParam("id"));
                Todo existing = todos.stream().filter(todo -> todo.id == id).findFirst().orElse(null);
                if (existing == null) { ctx.status(HttpStatus.NOT_FOUND).json(new ErrorMessage("Task not found")); return; }
                UpdateTodo input = ctx.bodyAsClass(UpdateTodo.class);
                Todo updated = new Todo(existing.id, input.title == null ? existing.title : input.title.trim(), input.completed == null ? existing.completed : input.completed);
                todos.set(todos.indexOf(existing), updated);
                ctx.json(updated);
            });
            config.routes.delete("/api/todos/{id}", ctx -> {
                long id = Long.parseLong(ctx.pathParam("id"));
                boolean removed = todos.removeIf(todo -> todo.id == id);
                if (!removed) { ctx.status(HttpStatus.NOT_FOUND).json(new ErrorMessage("Task not found")); return; }
                ctx.status(HttpStatus.NO_CONTENT);
            });
            config.routes.get("/health", ctx -> ctx.json(new Health("UP")));
        });
    }

    static void resetTodos() { todos.clear(); ids.set(0); }

    public record Todo(long id, String title, boolean completed) {}
    public static class CreateTodo { public String title; }
    public static class UpdateTodo { public String title; public Boolean completed; }
    public record ErrorMessage(String error) {}
    public record Health(String status) {}
}
