package com.example.my_health_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import com.example.my_health_app.ui.Global;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.my_health_app.databinding.ActivityMenuBinding;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;

public class MenuActivity extends AppCompatActivity {
    private static final int PHYISCAL_ACTIVITY = 123;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PHYISCAL_ACTIVITY);
        }
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);
        setSupportActionBar(binding.appBarMenu.toolbar);
        /*binding.appBarMenu.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();

            }
        });*/
        DrawerLayout drawer = binding.drawerLayout;
        NavigationRailView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
/*public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int PHYISCAL_ACTIVITY = 123;
    private TextView stepCountView;
    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private int stepCount = 0;
    private int initialStepCount = -1; // on stock le nombre initial de pas
    private ProgressBar progressBar;
    private float stepLength = 0.762f;
    private int stepCountTarget = 10000;
    private TextView stepCountTargetTextView;
    int[] neededSensorTypes = {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE,Sensor.TYPE_HEART_RATE,Sensor.TYPE_ACCELEROMETER};
    List<Sensor> neededSensors = new ArrayList<>();
    @Override
    protected void onResume() {
        super.onResume();
        if (stepCountSensor != null) {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (stepCountSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        }
        setContentView(R.layout.activity_main);
        stepCountView = findViewById(R.id.stepCountTextView);
        stepCountTargetTextView = findViewById(R.id.stepCountTargetTextView);
        progressBar = findViewById(R.id.progressBar);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> neededSensors = getNeededSensors(neededSensorTypes,sensorManager);
        for (Sensor sensor : neededSensors) {
            System.out.println("Informations du capteur : ");
            System.out.println("Nom : " + sensor.getName());
            System.out.println("Type : " + sensor.getType());
            System.out.println("Fabricant : " + sensor.getVendor());
            System.out.println("Version : " + sensor.getVersion());
            System.out.println("Portée maximale : " + sensor.getMaximumRange());
            System.out.println("Résolution : " + sensor.getResolution());
            System.out.println("Consommation électrique : " + sensor.getPower() + " mA");
            System.out.println("---------------------------------");
        }
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        progressBar.setMax(stepCountTarget);
        stepCountTargetTextView.setText("Step Goal: " + stepCountTarget);
        if (stepCountSensor == null) {
            stepCountView.setText("Step Counter not available");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount < 0) {
                //Si c'est le premier événement de capteur
                initialStepCount = (int) event.values[0];
            }
            // Mise à jour du nombre de pas
            stepCount = (int) event.values[0] - initialStepCount;
            stepCountTargetTextView.setText("Step Count: " + stepCount);
            progressBar.setProgress(stepCount);
            if (stepCount >= stepCountTarget) {
                stepCountTargetTextView.setText("Congratulations!");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private List<Sensor> getNeededSensors(int[] neededSensorTypes, SensorManager sensorManager) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> neededSensors = new ArrayList<>();

        for (int sensorType : neededSensorTypes) {
            Sensor sensor = sensorManager.getDefaultSensor(sensorType);
            if (verifySensor(sensor)) {
                neededSensors.add(sensor);
            }
        }
        return neededSensors;
    }
    public boolean verifySensor (Sensor sensor){
        return sensor != null;
    }
}*/