package com.telecoop.telecoop.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.telecoop.telecoop.R;
import com.telecoop.telecoop.ui.dashboard.SimpleBarChartView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class AppTabFragment extends Fragment {

    private static final String ARG_APP_NAME = "arg_app_name";
    private String appName;

    // Vue graphique et légende
    private SimpleBarChartView barChartView;
    private Spinner spinnerScale;
    private TextView txtLegend, txtAppTitle, txtUsageReasons;
    private ImageView ivPrev, ivNext;
    // Stocke l'option sélectionnée dans le Spinner
    private String selectedScale = "Jours de la semaine";
    // Représente la date "réelle" d'aujourd'hui, pour savoir ce qui est futur ou passé
    private Calendar todayCal;
    // Représente la date actuellement affichée (qu'on modifie en cliquant sur les flèches)
    private Calendar currentCal;

    public static AppTabFragment newInstance(String appName) {
        AppTabFragment fragment = new AppTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APP_NAME, appName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_app_tab, container, false);

        if (getArguments() != null) {
            appName = getArguments().getString(ARG_APP_NAME, "Application");
        }

        // Initialiser le Calendar à la date actuelle
        todayCal = Calendar.getInstance();
        currentCal = (Calendar) todayCal.clone();

        txtAppTitle = root.findViewById(R.id.txtAppTitle);
        txtAppTitle.setText(appName);
        txtUsageReasons = root.findViewById(R.id.txtUsageReasons);
        txtUsageReasons.setText("Cette période, vous avez utilisé " + appName + " pour :");

        barChartView = root.findViewById(R.id.simpleBarChartView);
        spinnerScale = root.findViewById(R.id.spinnerScale);
        txtLegend = root.findViewById(R.id.txtLegend);
        ivPrev = root.findViewById(R.id.ivPrev);
        ivNext = root.findViewById(R.id.ivNext);

        // Configurer le spinner d'échelle
        ArrayAdapter<CharSequence> scaleAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.scale_options, android.R.layout.simple_spinner_item);
        scaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerScale.setAdapter(scaleAdapter);

        // Par défaut "Jours de la semaine"
        spinnerScale.setSelection(0);
        selectedScale = "Jours de la semaine";

        spinnerScale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedScale = parent.getItemAtPosition(position).toString();
                updateChartForScale(selectedScale);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Gérer les flèches
        ivPrev.setOnClickListener(v -> {
            navigateInPast();
            updateChartForScale(selectedScale);
        });
        ivNext.setOnClickListener(v -> {
            navigateInFuture();
            updateChartForScale(selectedScale);
        });

        // Premier affichage
        updateChartForScale(selectedScale);

        return root;
    }

    // Selon selectedScale, recule d'une semaine, d'un mois ou d'un an
    private void navigateInPast() {
        switch (selectedScale) {
            case "Jours de la semaine":
                // Reculer d'une semaine
                currentCal.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case "Semaines du mois":
                // Reculer d'un mois
                currentCal.add(Calendar.MONTH, -1);
                break;
            case "Mois de l'année":
                // Reculer d'un an
                currentCal.add(Calendar.YEAR, -1);
                break;
        }
    }

    // Selon selectedScale, avance d'une semaine, d'un mois ou d'un an
    private void navigateInFuture() {
        switch (selectedScale) {
            case "Jours de la semaine":
                currentCal.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case "Semaines du mois":
                currentCal.add(Calendar.MONTH, 1);
                break;
            case "Mois de l'année":
                currentCal.add(Calendar.YEAR, 1);
                break;
        }
    }

    // Met à jour le graphique et la légende en fonction de l'échelle sélectionnée
    // Et de la date stockée dans currentCal
    private void updateChartForScale(String scaleOption) {
        List<Float> data;
        String[] labels;
        float avg;

        switch (scaleOption) {
            case "Jours de la semaine":
                // Génère data pour la semaine de currentCal
                data = generateDailyDataWithFutureCheck(currentCal, todayCal);
                labels = new String[]{"L", "M", "M", "J", "V", "S", "D"};
                break;
            case "Semaines du mois":
                // Génère data pour le mois de currentCal
                data = generateWeeklyDataWithFutureCheck(currentCal, todayCal);
                // Nombre de semaines dans le mois
                Calendar tmp = (Calendar) currentCal.clone();
                int maxWeeks = tmp.getActualMaximum(Calendar.WEEK_OF_MONTH);
                labels = new String[maxWeeks];
                for (int i = 0; i < maxWeeks; i++) {
                    labels[i] = "S" + (i+1);
                }
                break;
            case "Mois de l'année":
                // Génère data pour l'année de currentCal
                data = generateMonthlyDataWithFutureCheck(currentCal, todayCal);
                labels = new String[]{"Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};
                break;
            default:
                // Par défaut, "Jours de la semaine"
                data = generateDailyDataWithFutureCheck(currentCal, todayCal);                labels = new String[]{"L", "M", "M", "J", "V", "S", "D"};
                break;
        }

        avg = calculateAverageNonZero(data);
        barChartView.setDataPoints(data);
        barChartView.setXLabels(labels);
        txtLegend.setText(": " + String.format("%.1fh", avg));
    }

    /**
     * Génère 7 valeurs (lundi->dimanche) pour la semaine de currentCal
     * Si le jour est dans le futur par rapport à todayCal, la valeur est 0
     */
    private List<Float> generateDailyDataWithFutureCheck(Calendar currentCal, Calendar todayCal) {
        List<Float> daily = new ArrayList<>();
        Calendar cloneCal = (Calendar) currentCal.clone();
        // Se placer au lundi de la semaine
        cloneCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Random rand = new Random();
        for (int i = 0; i < 7; i++) {
            if (cloneCal.after(todayCal)) {
                daily.add(0f);
            } else {
                daily.add(1f + rand.nextFloat() * 23f);
            }
            // Passer au jour suivant
            cloneCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return daily;
    }

    /**
     * Génère un tableau pour les semaines du mois de currentCal
     * Si la semaine est dans le futur, on met 0
     */
    private List<Float> generateWeeklyDataWithFutureCheck(Calendar currentCal, Calendar todayCal) {
        List<Float> weekly = new ArrayList<>();
        Calendar cloneCal = (Calendar) currentCal.clone();
        // Nombre total de semaines
        int maxWeeks = cloneCal.getActualMaximum(Calendar.WEEK_OF_MONTH);

        // Se placer au début du mois
        cloneCal.set(Calendar.DAY_OF_MONTH, 1);

        Random rand = new Random();
        for (int i = 0; i < maxWeeks; i++) {
            if (cloneCal.after(todayCal)) {
                weekly.add(0f);
            } else {
                weekly.add(1f + rand.nextFloat() * 23f);
            }
            // Passer à la semaine suivante
            cloneCal.add(Calendar.WEEK_OF_MONTH, 1);
        }
        return weekly;
    }

    /**
     * Génère 12 valeurs pour l'année de currentCal
     * Si le mois est dans le futur, on met 0
     */
    private List<Float> generateMonthlyDataWithFutureCheck(Calendar currentCal, Calendar todayCal) {
        List<Float> monthly = new ArrayList<>();
        Calendar cloneCal = (Calendar) currentCal.clone();
        // Se placer au début de l'année
        cloneCal.set(Calendar.MONTH, Calendar.JANUARY);
        cloneCal.set(Calendar.DAY_OF_MONTH, 1);

        Random rand = new Random();
        for (int i = 0; i < 12; i++) {
            // Si ce mois est dans le futur par rapport à todayCal
            if (cloneCal.after(todayCal)) {
                monthly.add(0f);
            } else {
                monthly.add(1f + rand.nextFloat() * 23f);
            }
            // Passer au mois suivant
            cloneCal.add(Calendar.MONTH, 1);
        }
        return monthly;
    }

    // Calcule la moyenne uniquement sur les valeurs > 0
    private float calculateAverageNonZero(List<Float> data) {
        float sum = 0f;
        int count = 0;
        for (float v : data) {
            if (v > 0) {
                sum += v;
                count++;
            }
        }
        return count > 0 ? sum / count : 0f;
    }
}
