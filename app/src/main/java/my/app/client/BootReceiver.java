package my.app.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Start client service on boot
            Intent serviceIntent = new Intent(context, Client.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
