import com.sun.net.httpserver.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SimpleGreetServer {

    private final HttpServer server;

    public SimpleGreetServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/hello", new HelloHandler());
        server.createContext("/json", new JsonHandler());
        server.createContext("/greet", new GreetHandler());

 
        server.setExecutor(null); // use default executor
    }

    public void start() {
        server.start();
        System.out.println("✅ Server started on http://localhost:" + server.getAddress().getPort());
    }

    public void stop(int delaySeconds) {
        server.stop(delaySeconds);
        System.out.println("🛑 Server stopped.");
    }

    // --- Inner class for the /hello endpoint ---
    private class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello from JDK HTTP Server!";
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private class JsonHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}", "application/json");
                return;
            }
    
            String json = """
                {
                  "message": "Hello, JSON!",
                  "status": "success"
                }
                """;
    
            sendResponse(exchange, 200, json, "application/json");
        }
    }

    private class GreetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}", "application/json");
                return;
            }
    
            String query = exchange.getRequestURI().getQuery();
            String name = getQueryParam(query, "name");
            String age = getQueryParam(query, "age");
    
            if (name == null) name = "Guest";
            if (age == null) age = "?";
    
            String json = String.format("""
                {
                  "message": "Hello, %s! You are %s years old."
                }
                """, name, age);
    
            sendResponse(exchange, 200, json, "application/json");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        sendResponse(exchange, statusCode, responseText, "text/plain; charset=UTF-8");
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String responseText, String contentType) throws IOException {
        byte[] responseBytes = responseText.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private String getQueryParam(String query, String key) {
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length == 2 && parts[0].equalsIgnoreCase(key)) {
                return URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            SimpleGreetServer server = new SimpleGreetServer(8000);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}