package com.telecoop.telecoop.ui.home;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.telecoop.telecoop.R;

import java.util.List;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.AppUsageViewHolder> {

    private List<AppUsageInfo> usageList;
    private OnItemClickListener listener;

    // Constructeur incluant le listener pour les clics
    public AppUsageAdapter(List<AppUsageInfo> usageList, OnItemClickListener listener) {
        this.usageList = usageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppUsageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_usage, parent, false);
        return new AppUsageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppUsageViewHolder holder, int position) {
        AppUsageInfo info = usageList.get(position);

        // Nom de l'appli
        holder.appName.setText(info.getAppName());
        // Temps d'utilisation
        holder.appUsageTime.setText("Temps d'utilisation : " + info.getUsageTimeText());
        // Barre de progression (0-100)
        holder.appUsageBar.setProgress(info.getUsagePercent());
        // Icône de l'appli
        holder.appIcon.setImageDrawable(info.getAppIcon());

        // Utiliser holder.getAdapterPosition() au clic
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(usageList.get(currentPosition), currentPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usageList.size();
    }

    static class AppUsageViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appUsageTime;
        ProgressBar appUsageBar;

        public AppUsageViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            appUsageTime = itemView.findViewById(R.id.appUsageTime);
            appUsageBar = itemView.findViewById(R.id.appUsageBar);
        }
    }

    // Interface pour gérer le clic sur un item
    public interface OnItemClickListener {
        void onItemClick(AppUsageInfo usageInfo, int position);
    }

    // Classe représentant les informations d'utilisation d'une application
    public static class AppUsageInfo {
        private String appName;
        private Drawable appIcon;
        private long usageTime; // en millisecondes
        private int usagePercent; // pour la barre de progression (0-100)

        public AppUsageInfo(String appName, Drawable appIcon, long usageTime, int usagePercent) {
            this.appName = appName;
            this.appIcon = appIcon;
            this.usageTime = usageTime;
            this.usagePercent = usagePercent;
        }

        public String getAppName() {
            return appName;
        }

        public Drawable getAppIcon() {
            return appIcon;
        }

        public long getUsageTime() {
            return usageTime;
        }

        // Retourne le temps d'utilisation formaté en heures, minutes et secondes
        public String getUsageTimeText() {
            long totalSeconds = usageTime / 1000;
            long heures = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;
            return heures + " h " + minutes + " min " + seconds + " s";
        }

        public int getUsagePercent() {
            return usagePercent;
        }
    }
}
