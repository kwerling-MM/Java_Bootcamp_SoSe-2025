import com.sun.net.httpserver.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleHttpServerJSON {

    private final HttpServer server;

    public SimpleHttpServerJSON(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/hello", new HelloHandler());
        server.createContext("/json", new JsonHandler());
 
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

    public static void main(String[] args) {
        try {
            SimpleGreetServer server = new SimpleGreetServer(8000);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}