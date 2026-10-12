import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * WebServer.java — HTTP server entry point for the Digital Library Search System.
 *
 * Starts a Java built-in HttpServer on port 8080.
 * Serves static files from the web/ directory.
 * Delegates /api/* routes to ApiHandler.
 *
 * Start with:
 *   javac --add-modules jdk.httpserver *.java
 *   java WebServer
 *
 * Then open: http://localhost:8080
 */
public class WebServer {

    private static final int PORT = 8080;
    private static final String CORPUS_PATH = "corpus";
    private static final String WEB_DIR = "web";

    // Shared objects used by the API handler
    static List<LibraryDocument> documents = new ArrayList<>();
    static final CorpusReader corpusReader = new CorpusReader();
    static final KMP kmp = new KMP();
    static final LibrarySearchEngine searchEngine = new LibrarySearchEngine(kmp);

    public static void main(String[] args) throws IOException {
        // Load corpus
        documents = corpusReader.loadCorpus(CORPUS_PATH);

        // Start server
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // API routes
        server.createContext("/api/", new ApiHandler(CORPUS_PATH));

        // Static file serving (web/ directory)
        server.createContext("/", new StaticFileHandler(WEB_DIR));

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        // Startup banner
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║    DIGITAL LIBRARY SEARCH SYSTEM         ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Web server running at:                  ║");
        System.out.println("║  http://localhost:" + PORT + "                   ║");
        System.out.println("║  Corpus documents loaded: " + padRight(String.valueOf(documents.size()), 15) + " ║");
        System.out.println("║  Status: Ready                           ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    // -----------------------------------------------------------------------
    // Static file handler — serves web/ directory contents safely
    // -----------------------------------------------------------------------
    static class StaticFileHandler implements HttpHandler {
        private final String webDir;

        StaticFileHandler(String webDir) { this.webDir = webDir; }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            String requestPath = ex.getRequestURI().getPath();

            // Default to index.html
            if (requestPath.equals("/") || requestPath.isEmpty()) {
                requestPath = "/index.html";
            }

            // Security: prevent path traversal
            String normalized = requestPath.replaceAll("\\.\\./", "").replaceAll("\\\\", "");

            Path filePath = Paths.get(webDir + normalized).toAbsolutePath().normalize();
            Path webRoot  = Paths.get(webDir).toAbsolutePath().normalize();

            if (!filePath.startsWith(webRoot)) {
                sendResponse(ex, 403, "text/plain", "Forbidden");
                return;
            }

            File file = filePath.toFile();
            if (!file.exists() || !file.isFile()) {
                // Try serving index.html for SPA navigation
                File index = new File(webDir + "/index.html");
                if (index.exists()) {
                    serveFile(ex, index, "text/html");
                } else {
                    sendResponse(ex, 404, "text/plain", "Not found");
                }
                return;
            }

            serveFile(ex, file, contentType(file.getName()));
        }

        private void serveFile(HttpExchange ex, File file, String type) throws IOException {
            byte[] data = Files.readAllBytes(file.toPath());
            ex.getResponseHeaders().set("Content-Type", type);
            ex.sendResponseHeaders(200, data.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(data); }
        }

        private String contentType(String name) {
            if (name.endsWith(".html")) return "text/html; charset=utf-8";
            if (name.endsWith(".css"))  return "text/css; charset=utf-8";
            if (name.endsWith(".js"))   return "application/javascript; charset=utf-8";
            if (name.endsWith(".json")) return "application/json; charset=utf-8";
            if (name.endsWith(".svg"))  return "image/svg+xml";
            if (name.endsWith(".ico"))  return "image/x-icon";
            return "application/octet-stream";
        }
    }

    // -----------------------------------------------------------------------
    // Helper shared by handler
    // -----------------------------------------------------------------------
    static void sendResponse(HttpExchange ex, int code, String type, String body) throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type + "; charset=utf-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.sendResponseHeaders(code, data.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(data); }
    }

    /** Parse query parameters from a URI query string. */
    static String getParam(URI uri, String key) {
        String query = uri.getRawQuery();
        if (query == null) return null;
        for (String part : query.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                try {
                    String k = URLDecoder.decode(kv[0], "UTF-8");
                    if (k.equals(key)) return URLDecoder.decode(kv[1], "UTF-8");
                } catch (Exception ignored) {}
            }
        }
        return null;
    }

    /** Read the full request body as a UTF-8 string. */
    static String readBody(HttpExchange ex) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString().trim();
        }
    }
}
