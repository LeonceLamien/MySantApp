package com.example.my_health_app.ui.stat;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.highsoft.highcharts.common.hichartsclasses.HIButtonOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;
import com.highsoft.highcharts.common.hichartsclasses.HIExporting;
import com.highsoft.highcharts.common.hichartsclasses.HINavigation;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HISeries;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.my_health_app.R;
import com.example.my_health_app.databinding.FragmentStatBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.OptionalDouble;

public class StatFragment extends Fragment {
    private FragmentStatBinding binding;
    private DatabaseReference mDatabase;
    public ArrayList<String> phone_id = new ArrayList<>() ;

    public ArrayList<Integer> step_number = new ArrayList<>();
    public ArrayList<Integer> distance = new ArrayList<>();
    public ArrayList<Long> run_time = new ArrayList<>();

    public ArrayList<Integer> average_speed = new ArrayList<>();
    public ArrayList<String>  date = new ArrayList<>();

    /*public ArrayList step_number = new ArrayList<>();
    public ArrayList distance = new ArrayList<>();
    public ArrayList average_speed = new ArrayList<>();
    public ArrayList<String>  date = new ArrayList<>();

     */
    public long time;
    private WebView webView;
    private HIChartView chartView;
    private HIOptions options;
    private HIChart chart;
    private TextView AverageDistance;
    private TextView AverageStep;
    private TextView Time;
    private TextView Speed;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Run");
        chartView = root.findViewById(R.id.hc);
        chartView.setWillNotDraw(true);
        AverageDistance = root.findViewById(R.id.averagedistancetext);
        AverageStep = root.findViewById(R.id.averagesteptext);
        Time = root.findViewById(R.id.timespendtext);
        Speed = root.findViewById(R.id.averagespeedtext);
        options = new HIOptions();
        chart = new HIChart();
        chartView.setOptions(options);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ANDROID_ID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String phoneId = (String) snapshot.child("phone_id").getValue();

                    if(phoneId.equals(ANDROID_ID)){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            HashMap<String, Object> dateMap = (HashMap<String, Object>) snapshot.child("date").getValue();

                            if (LocalDateTime.now().getMonth().toString().equals(dateMap.get("month").toString())) {
                                long stepNumberLong = (Long) snapshot.child("step_number").getValue();
                                int stepNumber = (int) stepNumberLong;
                                step_number.add(stepNumber);

                                long distanceLong = (Long) snapshot.child("distance").getValue();
                                int distance_total = (int) distanceLong;
                                distance.add(distance_total);

                                String dateString = Long.toString((Long) dateMap.get("dayOfMonth"));
                                assert dateString != null;
                                LocalDateTime now = LocalDateTime.now();

// Définir le format du formateur de date
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

// Formater la date actuelle en utilisant le formateur
                                date.add(now.format(formatter));
                                long time_run = (long) snapshot.child("time").getValue();
                                run_time.add(time_run);
                                /*if (snapshot.child("average_speed").getValue() instanceof Double){
                                    System.out.println("long = _____");
                                    average_speed.add((Double) snapshot.child("average_speed").getValue());
                                } else if (snapshot.child("average_speed").getValue() instanceof Long) {
                                    average_speed.add((Double) snapshot.child("average_speed").getValue());
                                }else {
                                    System.out.println("else = _____");
                                }

                                 */
                                long speedLong = (Long) snapshot.child("average_speed").getValue();
                                int speed_total = (int) speedLong;
                                average_speed.add(speed_total);
                            }
                        }
                      }
                }
                OptionalDouble averageDistanceOptional = distance.stream().mapToInt(Integer::intValue).average();
                double averageDistance = averageDistanceOptional.orElse(0.0);
                AverageDistance.setText( Double.toString(Math.ceil(averageDistance/1000)));


                OptionalDouble averageStepOptional = step_number.stream().mapToInt(Integer::intValue).average();
                double averageStep = averageStepOptional.orElse(0.0);
                AverageStep.setText(Double.toString(Math.ceil(averageStep)));

                long mean_time = (long) run_time.stream().mapToInt(Long::intValue).average().orElse(0);
                Time.setText(convertMillisecondsToTime(mean_time));

                OptionalDouble averageSpeedOptional = average_speed.stream().mapToInt(Integer::intValue).average();
                double averageSpeed = averageSpeedOptional.orElse(0.0);
                Speed.setText(Double.toString(Math.ceil(averageSpeed)));

                HITitle title = new HITitle();
                title.setText("Monthly report");
                chart.setType("column");
                HINavigation navigation = new HINavigation();
                HIButtonOptions buttonOptions = new HIButtonOptions();
                buttonOptions.setEnabled(true);
                navigation.setButtonOptions(buttonOptions);
                navigation.setEnabled(true);
                HIExporting exporting = new HIExporting();
                exporting.setEnabled(false);
                options.setExporting(exporting);
                options.setNavigation(navigation);

                options.setChart(chart);
                options.setTitle(title);
                HIXAxis xAxis = new HIXAxis();
                xAxis.setType("category");
                xAxis.setCategories(date);
                xAxis.setTitle(new HITitle());
                xAxis.getTitle().setText("Day month");
                options.setXAxis(new ArrayList<>(Arrays.asList(xAxis)));

                HIYAxis yAxis1 = new HIYAxis();
                yAxis1.setTitle(new HITitle());
                yAxis1.getTitle().setText("Distance (m)");
                yAxis1.setOpposite(true);

                HIYAxis yAxis2 = new HIYAxis();
                yAxis2.setTitle(new HITitle());
                yAxis2.getTitle().setText("Speed (km/h)");

                options.setYAxis(new ArrayList<>(Arrays.asList(yAxis1,yAxis2)));

                // Création des séries de données
                HISeries series1 = new HISeries();
                series1.setName("Distance");
                series1.setType("column");
                series1.setYAxis(0);

                series1.setData(distance); // Données pour la première série


                HISeries series2 = new HISeries();
                series2.setName(" Speed");
                series2.setType("spline");
                series2.setYAxis(1);
                series2.setData(average_speed);



                // Ajout des séries de données au graphique
                options.setSeries(new ArrayList<>(Arrays.asList(series1,series2)));
                chartView.setOptions(options);
                chartView.setWillNotDraw(false);

                // Afficher le graphique
                chartView.setOptions(options);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Échec de la récupération des données.", databaseError.toException());
            }
        });

        return root;
    }
    public static String convertMillisecondsToTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        long remainingMilliseconds = milliseconds % 1000;

        return String.format("%d:%d:%d.%d", hours, minutes, remainingSeconds, remainingMilliseconds);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
