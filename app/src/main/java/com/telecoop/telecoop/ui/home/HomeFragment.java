package com.telecoop.telecoop.ui.home;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telecoop.telecoop.R;
import com.telecoop.telecoop.databinding.FragmentHomeBinding;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

/**
 * HomeFragment utilisant UsageEvents pour un suivi plus "en temps réel"
 * du temps d'utilisation depuis minuit.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler usageStatsHandler;

    // Stocke la progression cumulée par package depuis minuit
    private Map<String, Long> currentUsageStats = new HashMap<>();
    private Runnable usageStatsRunnable;
    // Intervalle de rafraîchissement (1 seconde ici)
    private static final long REFRESH_INTERVAL = 1000;
    // Timestamp du début de la session en cours (lorsque l'app est active)
    private long sessionStartTime = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Vérifie si l'application dispose de la permission d'accéder aux statistiques d'usage
    private boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    // Redirige l'utilisateur vers les paramètres pour activer l'accès aux stats d'usage
    private void requestUsageStatsPermission() {
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    /**
     * Charge et affiche les stats d'usage "en direct" en parcourant les événements UsageEvents.
     * On calcule le temps d'utilisation depuis minuit jusqu'à maintenant.
     */
    private void loadUsageStats() {
        UsageStatsManager usageStatsManager =
                (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);

        // Période : depuis minuit jusqu'à maintenant
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long midnight = calendar.getTimeInMillis();

        // Utiliser le plus grand entre minuit et sessionStartTime
        long startTime = Math.max(midnight, sessionStartTime);
        long endTime = System.currentTimeMillis();

        // Récupère tous les événements UsageEvents (MOVE_TO_FOREGROUND, MOVE_TO_BACKGROUND, etc.)
        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);
        if (usageEvents == null) {
            return;
        }

        // Maps pour calculer le temps d'utilisation par package
        Map<String, Long> usageTimeMap = new HashMap<>();       // Accumule le temps total par package
        Map<String, Long> lastForegroundMap = new HashMap<>(); // Retient le dernier timestamp de passage en foreground

        // Parcourt tous les événements
        UsageEvents.Event event = new UsageEvents.Event();
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            String packageName = event.getPackageName();
            long timestamp = event.getTimeStamp();

            switch (event.getEventType()) {
                case UsageEvents.Event.MOVE_TO_FOREGROUND:
                    // L'appli passe au premier plan
                    lastForegroundMap.put(packageName, timestamp);
                    break;

                case UsageEvents.Event.MOVE_TO_BACKGROUND:
                    // L'appli passe en arrière-plan
                    if (lastForegroundMap.containsKey(packageName)) {
                        long start = lastForegroundMap.get(packageName);
                        long duration = timestamp - start; // temps passé en foreground
                        long totalSoFar = usageTimeMap.getOrDefault(packageName, 0L);
                        usageTimeMap.put(packageName, totalSoFar + duration);

                        // On supprime le timestamp, car l'appli n'est plus en foreground
                        lastForegroundMap.remove(packageName);
                    }
                    break;

                default:
                    // D'autres types d'événements (configuration change, etc.) qu'on ignore
                    break;
            }
        }

        // Si une appli est encore en foreground actuellement, on ajoute le temps depuis son dernier foreground
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : lastForegroundMap.entrySet()) {
            String pkg = entry.getKey();
            long start = entry.getValue();
            long duration = now - start;
            long totalSoFar = usageTimeMap.getOrDefault(pkg, 0L);
            usageTimeMap.put(pkg, totalSoFar + duration);
        }

        // Charger les statistiques cumulées avant que l'utilisateur quitte la page
        Map<String, Long> persistedStats = loadPersistedUsageTimeMap();
        for (Map.Entry<String, Long> entry : persistedStats.entrySet()) {
            String pkg = entry.getKey();
            long persistedTime = entry.getValue();
            long currentTime = usageTimeMap.getOrDefault(pkg, 0L);
            usageTimeMap.put(pkg, currentTime + persistedTime);
        }

        // On stocke la map cumulée pour la sauvegarde ultérieure
        currentUsageStats = usageTimeMap;

        // Convertit usageTimeMap en liste d'AppUsageInfo
        List<AppUsageAdapter.AppUsageInfo> usageInfos = new ArrayList<>();
        PackageManager pm = requireContext().getPackageManager();

        // 1) Calculer la somme totale d'utilisation
        long totalUsage = 0;
        for (long val : usageTimeMap.values()) {
            totalUsage += val;
        }

        // 2) Construire la liste
        for (Map.Entry<String, Long> entry : usageTimeMap.entrySet()) {
            long time = entry.getValue();
            // On ignore les applis à 0 ms
            if (time <= 0) continue;

            String pkg = entry.getKey();
            try {
                ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
                String appName = pm.getApplicationLabel(ai).toString();
                Drawable icon = pm.getApplicationIcon(ai);

                // Calcul du pourcentage (0-100) par rapport au total
                int percent = (totalUsage > 0) ? (int) ((time * 100) / totalUsage) : 0;

                usageInfos.add(new AppUsageAdapter.AppUsageInfo(appName, icon, time, percent));

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        // 3) Trie la liste pour afficher l'appli la plus utilisée en premier
        Collections.sort(usageInfos, (o1, o2) ->
                Long.compare(o2.getUsageTime(), o1.getUsageTime()));

        // 4) Affiche dans le RecyclerView
        showUsageInRecyclerView(usageInfos);
    }

    // Affiche la liste dans le RecyclerView
    private void showUsageInRecyclerView(List<AppUsageAdapter.AppUsageInfo> usageInfos) {
        RecyclerView recyclerView = binding.recyclerViewUsage;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        AppUsageAdapter adapter = new AppUsageAdapter(usageInfos, new AppUsageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AppUsageAdapter.AppUsageInfo usageInfo, int position) {
                openFeedbackOverlay(usageInfo);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    // Sauvegarde currentUsageStats dans SharedPreferences
    private void saveUsageTimeMap(Map<String, Long> usageTimeMap) {
        SharedPreferences prefs = requireContext().getSharedPreferences("UsageStatsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Supprime d'anciennes entrées commençant par "usage_"
        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith("usage_")) {
                editor.remove(key);
            }
        }
        // Sauvegarde chaque valeur avec la clé "usage_<package>"
        for (Map.Entry<String, Long> entry : usageTimeMap.entrySet()) {
            editor.putLong("usage_" + entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    // Charge la map d'usage persistée depuis SharedPreferences
    private Map<String, Long> loadPersistedUsageTimeMap() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UsageStatsPrefs", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        Map<String, Long> map = new HashMap<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("usage_") && entry.getValue() instanceof Long) {
                // On enlève le préfixe "usage_" pour retrouver le nom du package
                String packageName = key.substring("usage_".length());
                map.put(packageName, (Long) entry.getValue());
            }
        }
        return map;
    }

    // Méthode pour ouvrir l'overlay de feedback pour une application cliquée
    private void openFeedbackOverlay(AppUsageAdapter.AppUsageInfo usageInfo) {
        // Récupérer l'overlay inclus dans fragment_home.xml
        View feedbackOverlay = requireView().findViewById(R.id.feedbackOverlay);
        feedbackOverlay.setVisibility(View.VISIBLE);

        // Récupérer les vues dans l'overlay
        TextView tvFeedbackQuestion = feedbackOverlay.findViewById(R.id.tvFeedbackQuestion);
        TextView tvFeedbackReasonPrompt = feedbackOverlay.findViewById(R.id.tvFeedbackReasonPrompt);
        RatingBar ratingBarFeedback = feedbackOverlay.findViewById(R.id.ratingBarFeedback);
        Button btnCloseFeedback = feedbackOverlay.findViewById(R.id.btnCloseFeedback);

        // Récupérer les boutons de raison
        Button btnReasonTravail = feedbackOverlay.findViewById(R.id.btnReasonTravail);
        Button btnReasonDivertissement = feedbackOverlay.findViewById(R.id.btnReasonDivertissement);
        Button btnReasonCommuniquer = feedbackOverlay.findViewById(R.id.btnReasonCommuniquer);
        Button btnReasonApprendre = feedbackOverlay.findViewById(R.id.btnReasonApprendre);
        Button btnReasonOrganisation = feedbackOverlay.findViewById(R.id.btnReasonOrganisation);
        Button btnReasonActiviteCreative = feedbackOverlay.findViewById(R.id.btnReasonActiviteCreative);

        // Obtenir le nom de l'application depuis l'item cliqué
        String appName = usageInfo.getAppName();

        // Mettre à jour le texte en insérant le nom de l'application
        tvFeedbackQuestion.setText("Qu'avez-vous ressenti en utilisant " + appName + " aujourd'hui ?");
        tvFeedbackReasonPrompt.setText("Pourquoi avez-vous utilisé " + appName + " aujourd'hui ?");

        // Utiliser la date actuelle (formatée) pour indexer le feedback
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Utiliser SharedPreferences pour stocker la note pour cette app et ce jour
        SharedPreferences prefsFeedback = requireContext().getSharedPreferences("FeedbackPrefs", Context.MODE_PRIVATE);
        String key = "feedback_" + appName + "_" + currentDate;

        // Lire la note enregistrée en utilisant -1.0 comme valeur sentinelle
        float savedRating = prefsFeedback.getFloat(key, -1f);
        if (savedRating == -1f) {
            // Aucune note enregistrée, on fixe la valeur par défaut (3 étoiles)
            savedRating = 3f;
            prefsFeedback.edit().putFloat(key, savedRating).apply();
        }

        Log.d("FeedbackLog", "Initial rating for " + appName + " on " + currentDate + ": " + savedRating);
        ratingBarFeedback.setRating(savedRating);

        // Variable finale pour l'utiliser dans l'inner class
        final float initialRating = savedRating;

        // Lorsque l'utilisateur change la note, on la sauvegarde
        ratingBarFeedback.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    prefsFeedback.edit().putFloat(key, rating).apply();
                    Log.d("FeedbackLog", "Initial rating for " + appName + " on " + currentDate + ": " + rating);
                }
            }
        });

        btnCloseFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackOverlay.setVisibility(View.GONE);
                float finalRating = prefsFeedback.getFloat(key, initialRating);
                Log.d("FeedbackLog", "Final rating for " + appName + " on " + currentDate + ": " + finalRating);
            }
        });

        // Configurer les boutons toggle pour les raisons
        setupToggleButton(btnReasonTravail, appName, currentDate, "travail");
        setupToggleButton(btnReasonDivertissement, appName, currentDate, "divertissement");
        setupToggleButton(btnReasonCommuniquer, appName, currentDate, "communiquer");
        setupToggleButton(btnReasonApprendre, appName, currentDate, "apprendre");
        setupToggleButton(btnReasonOrganisation, appName, currentDate, "organisation");
        setupToggleButton(btnReasonActiviteCreative, appName, currentDate, "activite_creative");
    }

    private void updateButtonAppearance(Button button, boolean isActive) {
        if (isActive) {
            // Apparence active
            button.setBackgroundColor(getResources().getColor(R.color.active_button_color));
            button.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            // Apparence inactive (grisée)
            button.setBackgroundColor(getResources().getColor(R.color.inactive_button_color));
            button.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void setupToggleButton(final Button button, final String appName, final String currentDate, final String reason) {
        final String key = "reason_" + appName + "_" + currentDate + "_" + reason;
        final SharedPreferences prefsToggle = requireContext().getSharedPreferences("FeedbackPrefs", Context.MODE_PRIVATE);
        // Charger l'état sauvegardé, par défaut false (inactif)
        boolean isActive = prefsToggle.getBoolean(key, false);
        updateButtonAppearance(button, isActive);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean currentState = prefsToggle.getBoolean(key, false);
                boolean newState = !currentState;
                prefsToggle.edit().putBoolean(key, newState).apply();
                updateButtonAppearance(button, newState);
                Log.d("FeedbackLog", "Reason " + reason + " for " + appName + " on " + currentDate + " set to " + newState);
            }
        });
    }

    // Verifier et reinitialiser les données de la journée
    private void checkAndResetUsageStatsForNewDay() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UsageStatsPrefs", Context.MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        // Utilise null comme valeur par défaut pour différencier "non défini"
        String lastDate = prefs.getString("usage_lastDate", null);

        if (lastDate == null) {
            // Première ouverture du jour : ne réinitialise rien, juste stocke la date d'aujourd'hui
            prefs.edit().putString("usage_lastDate", today).apply();
            Log.d("UsageStats", "First launch today, set usage_lastDate to " + today);
        } else if (!today.equals(lastDate)) {
            // La journée a changé : réinitialiser les statistiques
            SharedPreferences.Editor editor = prefs.edit();
            for (String key : prefs.getAll().keySet()) {
                if (key.startsWith("usage_") && !key.equals("usage_lastDate")) {
                    editor.remove(key);
                }
            }
            // Mettre à jour la date enregistrée avec la date d'aujourd'hui
            editor.putString("usage_lastDate", today);
            editor.apply();
            Log.d("UsageStats", "Usage stats reset for new day: " + today);
        } else {
            Log.d("UsageStats", "Same day (" + today + "), no reset needed.");
        }
    }


    // Vérifie la permission, la demande si nécessaire, et lance le rafraîchissement
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Vérifie si c'est un nouveau jour et réinitialise les stats si nécessaire
        checkAndResetUsageStatsForNewDay();

        // Initialiser sessionStartTime une seule fois
        if (sessionStartTime == 0) {
            sessionStartTime = System.currentTimeMillis();
        }

        // Configurer le handler pour rafraîchir les statistiques toutes les 1 seconde
        usageStatsHandler = new Handler(Looper.getMainLooper());
        usageStatsRunnable = new Runnable() {
            @Override
            public void run() {
                if (hasUsageStatsPermission(requireContext())) {
                    loadUsageStats();
                }
                usageStatsHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        usageStatsHandler.post(usageStatsRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (usageStatsHandler != null && usageStatsRunnable != null) {
            usageStatsHandler.removeCallbacks(usageStatsRunnable);
        }
        // Sauvegarder le temps de pause pour ne pas compter la période inactive
        SharedPreferences prefsUsage = requireContext().getSharedPreferences("UsageStatsPrefs", Context.MODE_PRIVATE);
        prefsUsage.edit().putLong("lastActiveTime", System.currentTimeMillis()).apply();
        // Conserver le temps d'utilisation de chaque app depuis minuit
        saveUsageTimeMap(currentUsageStats);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Récupérer la dernière heure d'activité sauvegardée (si présente)
        SharedPreferences prefs = requireContext().getSharedPreferences("UsageStatsPrefs", Context.MODE_PRIVATE);
        sessionStartTime = prefs.getLong("lastActiveTime", 0);
        if (sessionStartTime == 0) {
            // Première ouverture de la session : démarre à l'instant présent
            sessionStartTime = System.currentTimeMillis();
        }
        // Relancer le rafraîchissement des stats
        usageStatsHandler.post(usageStatsRunnable);
    }

}
