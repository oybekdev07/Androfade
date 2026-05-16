import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    
    private static final int PORT = 5000;
    private static ServerSocket serverSocket;
    private static List<ClientHandler> connectedClients = new ArrayList<>();
    private static ServerGUI gui;

    public static void main(String[] args) {
        gui = new ServerGUI();
        gui.show();
        
        try {
            serverSocket = new ServerSocket(PORT);
            gui.log("Server started on port " + PORT);
            gui.log("Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                gui.log("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                ClientHandler handler = new ClientHandler(clientSocket, gui);
                connectedClients.add(handler);
                new Thread(handler).start();
                
                gui.updateClientList(connectedClients);
            }
        } catch (IOException e) {
            gui.log("Server error: " + e.getMessage());
        }
    }

    public static void removeClient(ClientHandler client) {
        connectedClients.remove(client);
    }

    public static List<ClientHandler> getConnectedClients() {
        return connectedClients;
    }
}
