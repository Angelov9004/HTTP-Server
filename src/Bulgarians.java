import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;

public class Bulgarians {
    private static final String PUBLIC_HTML_DIRECTORY = "src/public_html";

    public static void main(String[] args) throws IOException {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new FileHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server is running on port " + port);
    }

    static class FileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestedPath = exchange.getRequestURI().getPath();
            File file;
            if ("/".equals(requestedPath)) {
                file = new File(PUBLIC_HTML_DIRECTORY + "/index.html");
            } else {
                file = new File(PUBLIC_HTML_DIRECTORY + requestedPath);
            }

            if (file.exists() && file.isFile()) {
                serveFile(exchange, file);
            } else {
                String response = "<html><body><h1>404 Not Found</h1></body></html>";
                sendResponse(exchange, response, 404);
            }
        }

        private void serveFile(HttpExchange exchange, File file) throws IOException {
            exchange.sendResponseHeaders(200, file.length());
            OutputStream os = exchange.getResponseBody();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            fis.close();
            os.close();
        }
    }

    static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
