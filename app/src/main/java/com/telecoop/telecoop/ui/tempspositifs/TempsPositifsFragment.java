package com.telecoop.telecoop.ui.tempspositifs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.telecoop.telecoop.R;
import com.telecoop.telecoop.data.TempsPositif;
import com.telecoop.telecoop.databinding.FragmentTempsPositifsBinding;

import java.util.ArrayList;
import java.util.List;

public class TempsPositifsFragment extends Fragment {

    private FragmentTempsPositifsBinding binding;

    // Liste en mémoire
    private List<TempsPositif> tempsPositifsList = new ArrayList<>();
    private TempsPositifsAdapter adapter;

    // Timer en cours
    private CountDownTimer activeTimer;
    private TempsPositif currentTimerTempsPositif;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTempsPositifsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Charger la liste sauvegardée
        loadTempsPositifsList();

        // Initialisation de la RecyclerView
        adapter = new TempsPositifsAdapter(tempsPositifsList, (tempsPositif, position) -> {
            // Ouvrir l’overlay détail
            showDetailOverlay(tempsPositif);
        });
        binding.recyclerTempsPositifs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTempsPositifs.setAdapter(adapter);

        // Bouton "Ajouter un temps positif"
        binding.btnAddTempsPositif.setOnClickListener(v -> showAddOverlay());

        // Gestion de l’overlay d’ajout
        binding.btnSaveTempsPositif.setOnClickListener(v -> saveNewTempsPositif());
        binding.btnCancelAdd.setOnClickListener(v -> closeAddOverlay());

        // Gestion de l’overlay de détail
        binding.btnCloseDetail.setOnClickListener(v -> closeDetailOverlay());
        binding.btnPlayPause.setOnClickListener(v -> {
            if (currentTimerTempsPositif != null) {
                if (currentTimerTempsPositif.isRunning()) {
                    pauseTimer();
                } else {
                    startTimer(currentTimerTempsPositif);
                }
            }
        });

        // Nouveau listener pour le bouton "Supprimer"
        binding.btnDeleteTemps.setOnClickListener(v -> {
            if (currentTimerTempsPositif != null) {
                // Trouver l'index de l'élément courant dans la liste
                int index = tempsPositifsList.indexOf(currentTimerTempsPositif);
                if (index != -1) {
                    tempsPositifsList.remove(index);
                    adapter.notifyItemRemoved(index);
                    saveTempsPositifsList();
                }
                // Fermer l'overlay de détail après suppression
                closeDetailOverlay();
            }
        });
    }

    // Overlay d’ajout
    private void showAddOverlay() {
        binding.overlayAddContainer.setVisibility(View.VISIBLE);
    }

    private void closeAddOverlay() {
        binding.overlayAddContainer.setVisibility(View.GONE);
    }

    private void saveNewTempsPositif() {
        EditText edtTitle = binding.edtTitle;
        EditText edtDuration = binding.edtDuration;

        String title = edtTitle.getText().toString().trim();
        String durationStr = edtDuration.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(durationStr)) {
            Toast.makeText(requireContext(), "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }

        long minutes = Long.parseLong(durationStr);
        long durationMs = minutes * 60_000;

        TempsPositif tp = new TempsPositif(title, durationMs);
        tempsPositifsList.add(tp);
        adapter.notifyItemInserted(tempsPositifsList.size() - 1);

        // Sauvegarder la liste après ajout
        saveTempsPositifsList();

        // Reset du formulaire
        edtTitle.setText("");
        edtDuration.setText("");

        closeAddOverlay();
    }

    // Sauvegarder liste des temps positifs dans les SharedPreferences
    private void saveTempsPositifsList() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("TempsPositifsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        // Stocker le nombre total de temps positifs
        editor.putInt("temps_count", tempsPositifsList.size());
        for (int i = 0; i < tempsPositifsList.size(); i++) {
            TempsPositif tp = tempsPositifsList.get(i);
            editor.putString("tp_" + i + "_title", tp.getTitle());
            editor.putLong("tp_" + i + "_duration", tp.getDurationMs());
            editor.putLong("tp_" + i + "_timeSpent", tp.getTimeSpentMs());
            editor.putBoolean("tp_" + i + "_isRunning", tp.isRunning());
        }
        editor.apply();
    }

    // Restaurer la liste depuis les SharedPreferences
    private void loadTempsPositifsList() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("TempsPositifsPrefs", Context.MODE_PRIVATE);
        int count = prefs.getInt("temps_count", 0);
        tempsPositifsList.clear();
        for (int i = 0; i < count; i++) {
            String title = prefs.getString("tp_" + i + "_title", "");
            long duration = prefs.getLong("tp_" + i + "_duration", 0);
            long timeSpent = prefs.getLong("tp_" + i + "_timeSpent", 0);
            boolean isRunning = prefs.getBoolean("tp_" + i + "_isRunning", false);
            if (!TextUtils.isEmpty(title) && duration > 0) {
                TempsPositif tp = new TempsPositif(title, duration);
                tp.setTimeSpentMs(timeSpent);
                tp.setRunning(isRunning);
                tempsPositifsList.add(tp);
            }
        }
    }

    // Overlay de détail
    private void showDetailOverlay(TempsPositif tempsPositif) {
        currentTimerTempsPositif = tempsPositif;
        binding.overlayDetailContainer.setVisibility(View.VISIBLE);

        // Met à jour l’UI
        binding.txtDetailTitle.setText(tempsPositif.getTitle());
        updateDetailUI();
    }

    private void closeDetailOverlay() {
        binding.overlayDetailContainer.setVisibility(View.GONE);
        // Stop le timer en cours (sinon on a un timer qui tourne en arrière-plan)
        if (activeTimer != null) {
            activeTimer.cancel();
            activeTimer = null;
        }
        currentTimerTempsPositif = null;
    }

    private void updateDetailUI() {
        if (currentTimerTempsPositif == null) return;

        // Calcul de la progression
        float fraction = currentTimerTempsPositif.getProgressFraction();
        int percent = (int) (fraction * 100);

        // Mise à jour du "camembert"
        ProgressBar progressBar = binding.progressCamembert;
        progressBar.setProgress(percent);

        // Calcul du temps restant
        long timeLeftMs = currentTimerTempsPositif.getDurationMs() - currentTimerTempsPositif.getTimeSpentMs();
        if (timeLeftMs < 0) timeLeftMs = 0;

        // Affichage mm:ss
        long sec = timeLeftMs / 1000;
        long mm = sec / 60;
        long ss = sec % 60;
        String timeLeftStr = String.format("%02d:%02d", mm, ss);
        binding.txtTimeRemaining.setText(timeLeftStr);

        // Bouton play/pause
        if (currentTimerTempsPositif.isRunning()) {
            binding.btnPlayPause.setText("Pause");
        } else {
            binding.btnPlayPause.setText("Play");
        }
    }

    // Gestion du timer
    private void startTimer(TempsPositif tempsPositif) {
        // S’il est déjà terminé, on ne fait rien
        if (tempsPositif.getTimeSpentMs() >= tempsPositif.getDurationMs()) {
            Toast.makeText(requireContext(), "Déjà terminé !", Toast.LENGTH_SHORT).show();
            return;
        }

        tempsPositif.setRunning(true);
        long timeLeftMs = tempsPositif.getDurationMs() - tempsPositif.getTimeSpentMs();

        activeTimer = new CountDownTimer(timeLeftMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Incrémente timeSpentMs
                long alreadySpent = tempsPositif.getTimeSpentMs();
                tempsPositif.setTimeSpentMs(alreadySpent + 1000);
                updateDetailUI();
                // Met à jour la liste (couleur, barre de progression)
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                // Timer terminé
                tempsPositif.setTimeSpentMs(tempsPositif.getDurationMs());
                tempsPositif.setRunning(false);
                updateDetailUI();
                adapter.notifyDataSetChanged();
            }
        };
        activeTimer.start();
        updateDetailUI();
    }

    private void pauseTimer() {
        if (activeTimer != null) {
            activeTimer.cancel();
            activeTimer = null;
        }
        if (currentTimerTempsPositif != null) {
            currentTimerTempsPositif.setRunning(false);
            updateDetailUI();
            adapter.notifyDataSetChanged();
        }
    }

    // Override methode onPause() pour sauvegarder la progression des temps positifs
    @Override
    public void onPause() {
        super.onPause();
        // Si un timer est actif, on l'arrête et on fige la progression
        if (activeTimer != null) {
            activeTimer.cancel();
            activeTimer = null;
        }
        if (currentTimerTempsPositif != null) {
            // On met isRunning à false pour indiquer qu'on est en pause
            currentTimerTempsPositif.setRunning(false);
        }
        // Ici, on enregistre la liste pour que timeSpentMs soit sauvegardé
        saveTempsPositifsList();
    }

    // Nettoyage
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (activeTimer != null) {
            activeTimer.cancel();
            activeTimer = null;
        }
        binding = null;
    }
}
