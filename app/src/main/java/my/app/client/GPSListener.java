package my.app.client;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

public class GPSListener implements LocationListener {
    
    private LocationManager locationManager;
    private Context context;
    private Location lastLocation;
    private static final long MIN_TIME = 5000; // 5 seconds
    private static final float MIN_DISTANCE = 10; // 10 meters

    public GPSListener(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startTracking() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME,
                    MIN_DISTANCE,
                    this
            );
        } catch (Exception e) {
            android.util.Log.e("GPS", "Error starting GPS: " + e.getMessage());
        }
    }

    public void stopTracking() {
        try {
            locationManager.removeUpdates(this);
        } catch (Exception e) {
            android.util.Log.e("GPS", "Error stopping GPS: " + e.getMessage());
        }
    }

    public String getCurrentLocation() {
        if (lastLocation != null) {
            return lastLocation.getLatitude() + "|" +
                   lastLocation.getLongitude() + "|" +
                   lastLocation.getAccuracy() + "|" +
                   lastLocation.getAltitude();
        }
        return "No location available";
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lastLocation = location;
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
