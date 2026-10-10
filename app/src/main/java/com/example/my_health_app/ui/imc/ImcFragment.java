package com.example.my_health_app.ui.imc;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;

import com.example.my_health_app.R;
import androidx.annotation.NonNull;

import com.example.my_health_app.databinding.FragmentImcBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.highsoft.highcharts.common.hichartsclasses.HIButtonOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;
import com.highsoft.highcharts.common.hichartsclasses.HIExporting;
import com.highsoft.highcharts.common.hichartsclasses.HINavigation;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HISeries;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;

import androidx.fragment.app.Fragment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class ImcFragment extends Fragment {
    private FragmentImcBinding binding;

    private EditText poidsEditText;
    private EditText tailleEditText;
    private TextView resultatTextView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference bmis;
    private DatabaseReference mDatabase;
    public ArrayList<String>  date = new ArrayList<>();

    public ArrayList taillesList = new ArrayList<>();
    public ArrayList poidsList = new ArrayList<>();
    public ArrayList bmiList = new ArrayList<>();
    private HIChartView chartView;
    private HIOptions options;
    private HIChart chart;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentImcBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase.getInstance();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        bmis = firebaseDatabase.getReference("Bmi");
        poidsEditText = root.findViewById(R.id.edit_text_weight);
        tailleEditText = root.findViewById(R.id.edit_text_height);
        resultatTextView = root.findViewById(R.id.text_view_result);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Bmi");
        Button calculerButton = root.findViewById(R.id.button_calculate);
        chartView = root.findViewById(R.id.hc);
        chartView.setWillNotDraw(true);
        options = new HIOptions();
        chart = new HIChart();
        chartView.setOptions(options);
        calculerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bmiList.clear();
                poidsList.clear();
                taillesList.clear();
                calculerIMC();
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
                                        /* long tailleLong = (long) snapshot.child("taille").getValue();
                                        double tailleNumber = (double) tailleLong;
                                        taillesList.add(tailleNumber);

                                         */
                                        //long tailleDouble = (long) snapshot.child("taille").getValue();
                                        taillesList.add(snapshot.child("taille").getValue());

                                        //long poidsLong = (long) snapshot.child("poids").getValue();
                                        //double poidsNumber = (double) poidsLong;
                                        poidsList.add(snapshot.child("poids").getValue());

                                        String dateString = Long.toString((Long) dateMap.get("dayOfMonth"));
                                        assert dateString != null;
                                        LocalDateTime now = LocalDateTime.now();

// Définir le format du formateur de date
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

// Formater la date actuelle en utilisant le formateur
                                        date.add(now.format(formatter));

                                        //long bmi = (long) snapshot.child("bmi_value").getValue();
                                        bmiList.add(snapshot.child("bmi_value").getValue());

                                    }
                                }
                            }
                        }

                        HITitle title = new HITitle();
                        title.setText("BMI report");
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
                        yAxis1.getTitle().setText("Height (cm)");
                        yAxis1.setOpposite(true);

                        HIYAxis yAxis2 = new HIYAxis();
                        yAxis2.setTitle(new HITitle());
                        yAxis2.getTitle().setText("Weight (kg)");


                        options.setYAxis(new ArrayList<>(Arrays.asList(yAxis1,yAxis2)));

                        // Création des séries de données
                        HISeries series1 = new HISeries();
                        series1.setName("Height");
                        series1.setType("column");
                        series1.setYAxis(0);

                        series1.setData(taillesList); // Données pour la première série

                        HISeries series3 = new HISeries();
                        series3.setName("Weight");
                        series3.setType("column");
                        series3.setYAxis(1);
                        series3.setData(poidsList);

                        HISeries series2 = new HISeries();
                        series2.setName(" Bmi");
                        series2.setType("spline");
                        series2.setData(bmiList);



                        // Ajout des séries de données au graphique
                        options.setSeries(new ArrayList<>(Arrays.asList(series3,series1,series2)));
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
            }

        });


        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void calculerIMC() {
        try {
            // Vérification du format des champs de poids et de taille
            String poidsText = poidsEditText.getText().toString();
            String tailleText = tailleEditText.getText().toString();

            if (poidsText.isEmpty() || tailleText.isEmpty()) {
                resultatTextView.setText("Veuillez remplir tous les champs.");
                return;
            }

            double poids = Double.parseDouble(poidsText);
            double taille;
            try {
                taille = Double.parseDouble(tailleText);
                // Vérification si la taille est dans un format valide
                if (taille <= 0 || taille >= 3) {
                    resultatTextView.setText("Veuillez entrer une taille valide en mètres (ex: 1.75).");
                    return;
                }
            } catch (NumberFormatException e) {
                resultatTextView.setText("Veuillez entrer une taille valide en mètres.");
                return;
            }

            // Vérification si les valeurs de poids et de taille sont positives
            if (poids <= 0) {
                resultatTextView.setText("Veuillez entrer un poids positif.");
                return;
            }
            double imc = poids / (taille * taille);

            String interpretation;
            if (imc < 18.5) {
                interpretation = "Insuffisance pondérale";
            } else if (imc < 25) {
                interpretation = "Poids normal";
            } else if (imc < 30) {
                interpretation = "Surpoids";
            } else {
                interpretation = "Obésité";
            }

            // Mise en forme du résultat
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Votre IMC : ");
            int start = builder.length();
            builder.append(String.format("%.2f", imc));
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("\nInterprétation : ");
            builder.append(interpretation);
            resultatTextView.setText(builder);
            String ANDROID_ID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            Imc bmi = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                bmi = new Imc(ANDROID_ID,(int)imc,taille*100,poids, LocalDateTime.now());
            }
            bmis.push().setValue(bmi);
        } catch (NumberFormatException e) {
            resultatTextView.setText("Veuillez entrer des valeurs valides.");
        }
    }

}
