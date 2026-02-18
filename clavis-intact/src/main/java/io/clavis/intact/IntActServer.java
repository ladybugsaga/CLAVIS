package io.clavis.intact;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntActServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(IntActServer.class);

    public IntActServer() {
        super("clavis-intact", "1.0.0");
    }

    @Override
    protected void registerTools() {
        this.tools.addAll(new IntActTools().createAllTools());
    }

    public static void main(String[] args) {
        new IntActServer().start();
    }
}
