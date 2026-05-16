import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    
    private static final int PORT = 5000;
    private static final int BROADCAST_PORT = 5001;
    private static ServerSocket serverSocket;
    private static DatagramSocket broadcastSocket;
    private static List<ClientHandler> connectedClients = new ArrayList<>();
    private static ServerGUI gui;
    private static boolean isRunning = true;

    public static void main(String[] args) {
        gui = new ServerGUI();
        gui.show();
        gui.log("AndroFade Server Starting...");
        
        try {
            // Start TCP server
            serverSocket = new ServerSocket(PORT);
            gui.log("Server listening on port " + PORT);
            
            // Start broadcast for auto-discovery
            startBroadcast();
            
            // Accept client connections
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                String clientIP = clientSocket.getInetAddress().getHostAddress();
                gui.log("Client connected from: " + clientIP);
                
                ClientHandler handler = new ClientHandler(clientSocket, gui);
                connectedClients.add(handler);
                new Thread(handler).start();
                
                gui.updateClientList(connectedClients);
            }
        } catch (IOException e) {
            gui.log("Server error: " + e.getMessage());
        }
    }

    private static void startBroadcast() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    broadcastSocket = new DatagramSocket();
                    broadcastSocket.setBroadcast(true);
                    
                    while (isRunning) {
                        // Broadcast server availability on local network
                        String broadcastMessage = "ANDRORAT_SERVER_AVAILABLE";
                        byte[] buffer = broadcastMessage.getBytes();
                        
                        InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, BROADCAST_PORT);
                        
                        broadcastSocket.send(packet);
                        
                        Thread.sleep(5000); // Broadcast every 5 seconds
                    }
                } catch (Exception e) {
                    gui.log("Broadcast error: " + e.getMessage());
                }
            }
        }).start();
    }

    public static void removeClient(ClientHandler client) {
        connectedClients.remove(client);
    }

    public static List<ClientHandler> getConnectedClients() {
        return connectedClients;
    }
}
