package io.clavis.unified;

import io.clavis.core.mcp.MCPServer;
import io.javalin.Javalin;
import io.javalin.http.sse.SseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE Transport wrapper for the Unified CLAVIS Server.
 * Enables integration with web-based MCP clients like LibreChat.
 */
public class UnifiedSseServer extends UnifiedServer {
    private static final Logger logger = LoggerFactory.getLogger(UnifiedSseServer.class);
    private final Map<String, SseClient> sessions = new ConcurrentHashMap<>();

    public UnifiedSseServer() {
        super();
    }

    /**
     * Starts the SSE server on the specified port.
     * 
     * @param port The port to listen on.
     */
    public void startSse(int port) {
        logger.info("Starting Unified SSE Server on port {}", port);

        // Register tools first
        registerTools();

        Javalin app = Javalin.create(config -> {
            config.router.mount(router -> {
                router.before(ctx -> {
                    ctx.header("Access-Control-Allow-Origin", "*");
                    ctx.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    ctx.header("Access-Control-Allow-Headers", "*");
                });
            });
        }).start(port);

        // SSE Endpoint
        app.sse("/sse", client -> {
            String sessionId = UUID.randomUUID().toString();
            sessions.put(sessionId, client);

            logger.info("New SSE session established: {}", sessionId);

            // MCP SSE protocol: client needs to know where to post messages
            client.sendEvent("endpoint", "/message?sessionId=" + sessionId);

            client.onClose(() -> {
                logger.info("SSE session closed: {}", sessionId);
                sessions.remove(sessionId);
            });
        });

        // Message Endpoint
        app.post("/message", ctx -> {
            String sessionId = ctx.queryParam("sessionId");
            if (sessionId == null || !sessions.containsKey(sessionId)) {
                ctx.status(400).result("Missing or invalid sessionId");
                return;
            }

            String requestBody = ctx.body();
            logger.debug("Received message for session {}: {}", sessionId, requestBody);

            // Process the message using the base MCPServer logic
            String response = handleMessage(requestBody);

            if (response != null) {
                SseClient client = sessions.get(sessionId);
                if (client != null) {
                    logger.debug("Sending response to session {}: {}", sessionId, response);
                    client.sendEvent("message", response);
                }
            }

            ctx.status(202);
        });

        logger.info("Unified SSE Server is running at http://localhost:{}/sse", port);
    }

    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.warn("Invalid port specified, using default 8080");
            }
        }

        UnifiedSseServer sseServer = new UnifiedSseServer();
        sseServer.startSse(port);
    }
}
