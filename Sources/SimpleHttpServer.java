import com.sun.net.httpserver.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleHttpServer {

    private final HttpServer server;

    public SimpleHttpServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/hello", new HelloHandler());
 
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

    public static void main(String[] args) {
        try {
            SimpleGreetServer server = new SimpleGreetServer(8000);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}