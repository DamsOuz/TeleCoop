package com.telecoop.telecoop.ui.tempspositifs;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.telecoop.telecoop.R;
import com.telecoop.telecoop.data.TempsPositif;

import java.util.List;

public class TempsPositifsAdapter extends RecyclerView.Adapter<TempsPositifsAdapter.TempsViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(TempsPositif tempsPositif, int position);
    }

    private List<TempsPositif> data;
    private OnItemClickListener listener;

    public TempsPositifsAdapter(List<TempsPositif> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TempsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_temps_positif, parent, false);
        return new TempsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TempsViewHolder holder, int position) {
        TempsPositif tp = data.get(position);
        holder.bind(tp, listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class TempsViewHolder extends RecyclerView.ViewHolder {
        View viewColorState;
        TextView txtTitle, txtDuration;
        ProgressBar progressBar;

        public TempsViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColorState = itemView.findViewById(R.id.viewColorState);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public void bind(TempsPositif tempsPositif, OnItemClickListener listener) {
            txtTitle.setText(tempsPositif.getTitle());

            // Durée en minutes
            long totalMinutes = tempsPositif.getDurationMs() / 60000;
            txtDuration.setText(totalMinutes + " mins");

            // Couleur d'état
            viewColorState.setBackgroundColor(tempsPositif.getStateColor());

            // Progression en pourcentage
            float fraction = tempsPositif.getProgressFraction();
            int percent = (int) (fraction * 100);
            progressBar.setProgress(percent);

            // Clic sur l'item -> callback
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(tempsPositif, getAdapterPosition());
                }
            });
        }
    }
}
