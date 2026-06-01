import java.io.*;
import java.net.*;
import java.util.*;

public class AdminServer {
    private static final int PORT = 5000;
    private static ServerSocket serverSocket;
    private static List<ClientHandler> connectedClients = new ArrayList<>();
    private static AdminPanel adminPanel;

    public static void main(String[] args) {
        adminPanel = new AdminPanel();
        adminPanel.show();
        adminPanel.log("🚀 AndroFade Admin Server Starting...");
        
        try {
            serverSocket = new ServerSocket(PORT);
            adminPanel.log("✅ Server listening on port " + PORT);
            adminPanel.log("⏳ Waiting for connections...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientIP = clientSocket.getInetAddress().getHostAddress();
                adminPanel.log("🔗 New client connected: " + clientIP);
                
                ClientHandler handler = new ClientHandler(clientSocket, adminPanel);
                connectedClients.add(handler);
                new Thread(handler).start();
                
                adminPanel.updateDeviceList(connectedClients);
            }
        } catch (IOException e) {
            adminPanel.log("❌ Server error: " + e.getMessage());
        }
    }

    public static void removeClient(ClientHandler client) {
        connectedClients.remove(client);
    }

    public static List<ClientHandler> getConnectedClients() {
        return connectedClients;
    }
}
