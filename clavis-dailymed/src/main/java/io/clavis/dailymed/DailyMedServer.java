package io.clavis.dailymed;

import io.clavis.core.mcp.MCPServer;

public class DailyMedServer extends MCPServer {

    public DailyMedServer() {
        super("clavis-dailymed", "1.0.0");
    }

    @Override
    protected void registerTools() {
        DailyMedClient client = new DailyMedClient();
        DailyMedTools toolFactory = new DailyMedTools(client);
        tools.addAll(toolFactory.getAllTools());
    }

    public static void main(String[] args) {
        new DailyMedServer().start();
    }
}
