package com.example.my_health_app.ui.step;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getColor;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import android.app.PendingIntent;
import android.content.Context;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.my_health_app.ui.Global;
import com.example.my_health_app.R;
import com.example.my_health_app.databinding.FragmentStepBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class StepFragment extends Fragment implements OnMapReadyCallback, LocationListener, SensorEventListener {
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationLayerPlugin;
    private LatLng previousLocation;
    private LocationComponent locationComponent;
    private LocationManager locationManager;
    private static final int PERMISSION_REQUEST_LOCATION = 123;

    private TextView stepCountView;
    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private int stepCount = 0;
    private int initialStepCount = -1; // on stock le nombre initial de pas
    private ProgressBar progressBar;
    private float stepLength = 0.762f;
    private int stepCountTarget = 10000;
    private TextView stepCountTextView;
    private TextView stepCountDistance;
    private int distance;
    private int speed;
    private TextView stepCountTargetTextView;
    int[] neededSensorTypes = {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_STEP_COUNTER};
    List<Sensor> neededSensors = new ArrayList<>();
    private Button start_session;
    private Button end_session;

    private FloatingActionButton focus_location;

    private TextView speedView;
    private Chronometer chronometer;

    private List<Point> polylinePoints = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference runs;
    private final ArrayList<Double> speeds = new ArrayList<>();
    private Boolean start_run = false;

    @Override
    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase.getInstance();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        Mapbox.getInstance(getContext(), "mapbox token");
        firebaseDatabase = FirebaseDatabase.getInstance();
        runs = firebaseDatabase.getReference("Run");
        FragmentStepBinding binding = FragmentStepBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return root;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        start_session = root.findViewById(R.id.start_button);
        end_session = root.findViewById(R.id.end_button);
        focus_location = root.findViewById(R.id.focusLocation);
        focus_location.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                LatLng newLocation = new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
                mapboxMap.setCameraPosition(new CameraPosition.Builder().zoom(15.0).target(newLocation).build());
            }
        });
        end_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEndSession(v);
            }
        });
        start_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartSession(v);
            }
        });
        end_session.setVisibility(View.GONE);
        speedView = root.findViewById(R.id.speedTextView);
        stepCountView = root.findViewById(R.id.stepCountTextView);
        stepCountDistance = root.findViewById(R.id.stepCountDistance);
        chronometer = root.findViewById(R.id.chronometer);
        stepCountTargetTextView = root.findViewById(R.id.stepCountTargetTextView);
        progressBar = root.findViewById(R.id.progressBar);
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        neededSensors = getNeededSensors(neededSensorTypes, sensorManager);
        progressBar.setMax(stepCountTarget);
        /*initializestepCountTargetTextView();*/
        stepCountTargetTextView.setText("Step Goal: " + stepCountTarget);
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return root;
    }

    private void initializeLocationEngine() {
        // Initialize location engine
    }

    private void initializeLocationComponent(@NonNull Style style) {
        // Get an instance of the LocationComponent.
        locationComponent = mapboxMap.getLocationComponent();

        // Activate the LocationComponent with options.
        locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(getContext(), style).build()
        );

        // Enable to make the LocationComponent visible
        if (checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationComponent.setLocationComponentEnabled(true);

        // Set the LocationComponent camera mode
        locationComponent.setCameraMode(com.mapbox.mapboxsdk.location.modes.CameraMode.TRACKING);

        // Set the LocationComponent render mode
        locationComponent.setRenderMode(com.mapbox.mapboxsdk.location.modes.RenderMode.NORMAL);
        LatLng newLocation = new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
        mapboxMap.setCameraPosition(new CameraPosition.Builder().zoom(15.0).target(newLocation).build());

    }

    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {

            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Initialisez et activez le composant de localisation ici
                initializeLocationComponent(style);
            }
        });
    }


    @SuppressWarnings({"MissingPermission"})
    public void onConnected() {
        // Start updating location
    }

    @SuppressWarnings({"MissingPermission"})
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        // Explain why the app needs location permissions
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    private void addPointToPolyline(LatLng point) {
        polylinePoints.add(Point.fromLngLat(point.getLongitude(), point.getLatitude()));
    }

    private void drawPolyline() {
        if (mapboxMap != null) {
            if (polylinePoints.size() > 1) {
                LineString lineString = LineString.fromLngLats(polylinePoints);
                GeoJsonSource source = mapboxMap.getStyle().getSourceAs("line-source");
                if (source != null) {
                    source.setGeoJson(lineString);
                } else {
                    mapboxMap.getStyle().addSource(new GeoJsonSource("line-source", lineString));
                    mapboxMap.getStyle().addLayer(new LineLayer("line-layer", "line-source")
                            .withProperties(
                                    lineColor(getColor(getContext(), R.color.search_opaque)),
                                    lineWidth(5f)
                            ));
                }
            }
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (start_run) {
            addPointToPolyline(newLocation);
        }
        drawPolyline();
    }


    @Override
    public void onResume() {
        super.onResume();
        for (Sensor sensor : neededSensors) {
            if (verifySensor(sensor)) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        start_run = false;
        if (stepCountSensor != null) {
            sensorManager.unregisterListener(this);
        }
        locationManager.removeUpdates(this);
        mapView.onStop();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_COUNTER:
                if (initialStepCount < 0) {
                    //Si c'est le premier événement de capteur
                    initialStepCount = (int) event.values[0];
                }

                stepCount = (int) event.values[0] - initialStepCount;
                stepCountView.setText(String.valueOf(stepCount));
                distance = (int) (stepCount * stepLength);
                stepCountDistance.setText(String.valueOf(distance));
                progressBar.setProgress(stepCount);

                if (stepCount >= stepCountTarget) {
                    stepCountTargetTextView.setText("Congratulations!");
                }
                break;
            case Sensor.TYPE_ACCELEROMETER:
                speed = (int) event.values[0];
                if(speed<0){
                    speed=0;
                }
                speedView.setText(String.valueOf(Math.ceil(speed* 3.6)));
                break;
            case Sensor.TYPE_GYROSCOPE:
                break;
        }
    }

    private List<Sensor> getNeededSensors(int[] neededSensorTypes, SensorManager sensorManager) {
        List<Sensor> neededSensors = new ArrayList<>();
        for (int sensorType : neededSensorTypes) {
            Sensor sensor = sensorManager.getDefaultSensor(sensorType);
            if (verifySensor(sensor)) {
                neededSensors.add(sensor);
            } else {
                switch (sensorType) {
                    case Sensor.TYPE_STEP_COUNTER:
                        stepCountView.setText("Step Counter not available");
                        break;
                    case Sensor.TYPE_ACCELEROMETER:
                        speedView.setText("Step Counter not available");
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        break;
                }
            }
        }
        return neededSensors;
    }

    public boolean verifySensor(Sensor sensor) {
        return sensor != null;
    }

    public void onStartSession(View view) {
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        polylinePoints.clear();
        start_run = true;
        distance=0;
        stepCount=0;
        stepCountView.setText(String.valueOf(stepCount));
        stepCountDistance.setText(String.valueOf(distance));
        start_session.setVisibility(View.GONE);
        end_session.setVisibility(View.VISIBLE);
        neededSensors = getNeededSensors(neededSensorTypes, sensorManager);
        onResume();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (speed > 0) {
                    speeds.add(speed * 3.6);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 30000);
    }

    public void onEndSession(View view) {
        end_session.setVisibility(View.GONE);
        neededSensors.clear();
        sensorManager.unregisterListener(this);
        chronometer.stop();
        String uniqueID = UUID.randomUUID().toString();
        String ANDROID_ID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Run run = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            double average_speed = speeds.stream().mapToInt(Double::intValue).average().orElse(0);
            run = new Run(ANDROID_ID, stepCount, distance, stepCount >= stepCountTarget, (int)average_speed, LocalDateTime.now(), SystemClock.elapsedRealtime() - chronometer.getBase());
        } else {
            // run = new Run(uniqueID,stepCount,distance,stepCount >=stepCountTarget,speeds, new Date().getTime(), SystemClock.elapsedRealtime() - chronometer.getBase());
        }
        runs.push().setValue(run);
        initialStepCount = -1;
        start_session.setVisibility(View.VISIBLE);

    }


}


