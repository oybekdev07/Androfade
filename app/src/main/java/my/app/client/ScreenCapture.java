package my.app.client;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;

public class ScreenCapture {
    private Context context;
    private static final String SCREENSHOT_DIR = "AndroFade/screenshots";

    public ScreenCapture(Context context) {
        this.context = context;
    }

    public String takeScreenshot() {
        try {
            File screenshotDir = new File(context.getExternalFilesDir(null), SCREENSHOT_DIR);
            if (!screenshotDir.exists()) screenshotDir.mkdirs();

            View rootView = getRootView();
            if (rootView != null) {
                Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                rootView.draw(canvas);

                String filename = "screenshot_" + System.currentTimeMillis() + ".png";
                File screenshotFile = new File(screenshotDir, filename);
                FileOutputStream fos = new FileOutputStream(screenshotFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                bitmap.recycle();

                return screenshotFile.getAbsolutePath();
            }
        } catch (Exception e) {
            android.util.Log.e("ScreenCapture", "Error: " + e.getMessage());
        }
        return null;
    }

    private View getRootView() {
        try {
            android.app.Activity activity = getActivity();
            if (activity != null) return activity.getWindow().getDecorView().getRootView();
        } catch (Exception e) {
            android.util.Log.e("ScreenCapture", "Get root view error: " + e.getMessage());
        }
        return null;
    }

    private android.app.Activity getActivity() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Object activities = activityThreadClass.getMethod("getActivities").invoke(activityThread);
            
            if (activities instanceof java.util.Map) {
                java.util.Map map = (java.util.Map) activities;
                if (!map.isEmpty()) return (android.app.Activity) map.values().iterator().next();
            }
        } catch (Exception e) {
            android.util.Log.e("ScreenCapture", "Get activity error: " + e.getMessage());
        }
        return null;
    }
}
