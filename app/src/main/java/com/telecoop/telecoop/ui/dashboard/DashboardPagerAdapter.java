package com.telecoop.telecoop.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    private List<String> appNames;

    public DashboardPagerAdapter(@NonNull Fragment fragment, List<String> appNames) {
        super(fragment);
        this.appNames = appNames;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String appName = appNames.get(position);
        // Retourne un nouveau AppTabFragment pour cette application
        return AppTabFragment.newInstance(appName);
    }

    @Override
    public int getItemCount() {
        return appNames.size();
    }
}
