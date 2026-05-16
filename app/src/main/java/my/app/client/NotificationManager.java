package my.app.client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationManager {
    
    private Context context;
    private android.app.NotificationManager notificationManager;
    private static final String CHANNEL_ID = "AndroFade";

    public NotificationManager(Context context) {
        this.context = context;
        this.notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "AndroFade Service",
                    android.app.NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("AndroFade background service");
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notification buildNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);

        return builder.build();
    }

    public void showNotification(int id, String title, String message) {
        Notification notification = buildNotification(title, message);
        notificationManager.notify(id, notification);
    }
}
