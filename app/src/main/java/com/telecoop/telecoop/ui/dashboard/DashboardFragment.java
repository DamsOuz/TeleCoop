package com.telecoop.telecoop.ui.dashboard;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.telecoop.telecoop.R;
import com.telecoop.telecoop.ui.home.AppUsageAdapter;
import com.telecoop.telecoop.ui.home.AppUsageAdapter.AppUsageInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private DashboardPagerAdapter pagerAdapter;
    // Ne pas initialiser appNames ici, car requireContext() n'est pas encore disponible
    private List<String> appNames;

    /**
     * Cette méthode utilise UsageStatsManager pour récupérer, depuis minuit jusqu'à maintenant,
     * le temps d'utilisation réel de chaque application et renvoie une liste triée d'AppUsageInfo
     */
    private List<AppUsageInfo> loadUsageInfos() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);
        // Définir le début de la journée (minuit)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long midnight = calendar.getTimeInMillis();
        long now = System.currentTimeMillis();

        // Interroger les UsageEvents de minuit à maintenant
        UsageEvents usageEvents = usageStatsManager.queryEvents(midnight, now);
        if (usageEvents == null) {
            return new ArrayList<>();
        }

        // Accumuler le temps d'utilisation par application
        Map<String, Long> usageTimeMap = new HashMap<>();
        Map<String, Long> lastForegroundMap = new HashMap<>();
        UsageEvents.Event event = new UsageEvents.Event();
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            String packageName = event.getPackageName();
            long timestamp = event.getTimeStamp();
            switch (event.getEventType()) {
                case UsageEvents.Event.MOVE_TO_FOREGROUND:
                    lastForegroundMap.put(packageName, timestamp);
                    break;
                case UsageEvents.Event.MOVE_TO_BACKGROUND:
                    if (lastForegroundMap.containsKey(packageName)) {
                        long start = lastForegroundMap.get(packageName);
                        long duration = timestamp - start;
                        long total = usageTimeMap.getOrDefault(packageName, 0L);
                        usageTimeMap.put(packageName, total + duration);
                        lastForegroundMap.remove(packageName);
                    }
                    break;
                default:
                    break;
            }
        }
        // Pour les applications toujours en foreground, ajouter le temps jusqu'à maintenant
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : lastForegroundMap.entrySet()) {
            String pkg = entry.getKey();
            long start = entry.getValue();
            long duration = currentTime - start;
            long total = usageTimeMap.getOrDefault(pkg, 0L);
            usageTimeMap.put(pkg, total + duration);
        }
        // Conversion de la map en liste d'AppUsageInfo
        List<AppUsageInfo> usageInfos = new ArrayList<>();
        PackageManager pm = requireContext().getPackageManager();
        long totalUsage = 0;
        for (Long time : usageTimeMap.values()) {
            totalUsage += time;
        }
        for (Map.Entry<String, Long> entry : usageTimeMap.entrySet()) {
            long time = entry.getValue();
            if (time <= 0) continue;
            String pkg = entry.getKey();
            try {
                ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
                String appName = pm.getApplicationLabel(ai).toString();
                Drawable icon = pm.getApplicationIcon(ai);
                int percent = (totalUsage > 0) ? (int) ((time * 100) / totalUsage) : 0;
                usageInfos.add(new AppUsageInfo(appName, icon, time, percent));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        // Trier la liste pour que l'application la plus utilisée soit en première position
        Collections.sort(usageInfos, (o1, o2) -> Long.compare(o2.getUsageTime(), o1.getUsageTime()));
        return usageInfos;
    }

    //Extrait les noms des topN applications à partir de loadUsageInfos().
    private List<String> getTopUsedApps(int topN) {
        List<AppUsageInfo> usageInfos = loadUsageInfos();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, usageInfos.size()); i++) {
            names.add(usageInfos.get(i).getAppName());
        }
        return names;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Gonfler le layout principal du Dashboard
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);
        // Initialiser la liste des onglets dynamiquement ici, lorsque le fragment est attaché
        appNames = getTopUsedApps(5);
        pagerAdapter = new DashboardPagerAdapter(this, appNames);
        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(appNames.get(position))
        ).attach();
        return root;
    }
}
