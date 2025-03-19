package com.telecoop.telecoop.ui.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.telecoop.telecoop.R;
import com.telecoop.telecoop.ui.dashboard.SimpleBarChartView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AppTabFragment extends Fragment {

    private static final String ARG_APP_NAME = "arg_app_name";
    private String appName;

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

        // Mettre à jour le titre
        TextView txtAppTitle = root.findViewById(R.id.txtAppTitle);
        txtAppTitle.setText(appName);

        // Récupérer la vue du graphique en barres
        SimpleBarChartView barChartView = root.findViewById(R.id.simpleBarChartView);
        List<Float> dataPoints = generateDummyHoursPerDay();
        barChartView.setDataPoints(dataPoints);

        // Optionnel : définir des labels personnalisés pour les jours
        String[] customLabels = {"L", "M", "M", "J", "V", "S", "D"};
        barChartView.setDayLabels(customLabels);

        // Mettre à jour le texte explicatif
        TextView txtUsageReasons = root.findViewById(R.id.txtUsageReasons);
        txtUsageReasons.setText("Cette semaine, vous avez utilisé " + appName + " pour :");

        // Mettre à jour la légende avec la moyenne
        TextView txtLegend = root.findViewById(R.id.txtLegend);
        float avg = calculateAverage(dataPoints);
        txtLegend.setText(": " + String.format("%.1fh", avg));

        // Gérer les boutons de raison (à adapter selon vos besoins)
        Button btnReason1 = root.findViewById(R.id.btnReason1);
        Button btnReason2 = root.findViewById(R.id.btnReason2);
        // Par exemple, on pourrait remplir les boutons ici...

        return root;
    }

    // Génère 7 valeurs fictives entre 0 et 5 heures
    private List<Float> generateDummyHoursPerDay() {
        List<Float> hours = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 7; i++) {
            // Générer une valeur entre 0 et 24 (avec 1 décimale)
            float value = rand.nextFloat() * 24f;
            hours.add(value);
        }
        return hours;
    }

    // Calcule la moyenne des valeurs de la liste
    private float calculateAverage(List<Float> values) {
        float sum = 0f;
        for (Float v : values) {
            sum += v;
        }
        return values.size() > 0 ? sum / values.size() : 0f;
    }
}
