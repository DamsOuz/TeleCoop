package com.telecoop.telecoop.ui.conseils; // ou .ui.conseils, si vous préférez

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.telecoop.telecoop.R;
import com.telecoop.telecoop.data.Content;
import com.telecoop.telecoop.databinding.FragmentConseilsBinding;

/**
 * Page "Conseils"
 * Affiche une liste de boutons. Chaque bouton ouvre un overlay
 * (une zone blanche) contenant du texte ou des images, que l'on peut fermer
 */
public class ConseilsFragment extends Fragment {

    private FragmentConseilsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentConseilsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Masque l'overlay au démarrage
        closeOverlay();

        // Exemples de boutons : "Temps positifs", "Gestion des objectifs", ...
        binding.btnTempsPositifs.setOnClickListener(v -> {
            // Récupérer le tutoriel "filtres_couleurs" depuis le provider
            Content temps_positifs = com.telecoop.telecoop.data.ContentProvider.getContent("temps_positifs");
            showOverlay(temps_positifs.getTitle(), temps_positifs.getContent(), temps_positifs.getImageResId());
        });

        binding.btnGestionObjectifs.setOnClickListener(v -> {
            Content gestion_objectifs = com.telecoop.telecoop.data.ContentProvider.getContent("gestion_objectifs");
            showOverlay(gestion_objectifs.getTitle(), gestion_objectifs.getContent(), gestion_objectifs.getImageResId());
        });

        binding.btnGestionNotifications.setOnClickListener(v -> {
            Content gestion_notifications = com.telecoop.telecoop.data.ContentProvider.getContent("gestion_notifications");
            showOverlay(gestion_notifications.getTitle(), gestion_notifications.getContent(), gestion_notifications.getImageResId());
        });

        binding.btnTutorielNotifs.setOnClickListener(v -> {
            Content tutoriel_notifications = com.telecoop.telecoop.data.ContentProvider.getContent("tutoriel_notifications");
            showOverlay(tutoriel_notifications.getTitle(), tutoriel_notifications.getContent(), tutoriel_notifications.getImageResId());
        });

        binding.btnTutorielBlocage.setOnClickListener(v -> {
            Content tutoriel_blocage = com.telecoop.telecoop.data.ContentProvider.getContent("tutoriel_blocage");
            showOverlay(tutoriel_blocage.getTitle(), tutoriel_blocage.getContent(), tutoriel_blocage.getImageResId());
        });

        binding.btnTutorielFiltresCouleurs.setOnClickListener(v -> {
            Content tutoriel_filtres_couleurs = com.telecoop.telecoop.data.ContentProvider.getContent("tutoriel_filtres_couleurs");
            showOverlay(tutoriel_filtres_couleurs.getTitle(), tutoriel_filtres_couleurs.getContent(), tutoriel_filtres_couleurs.getImageResId());
        });

        binding.btnTutorielModeTravail.setOnClickListener(v -> {
            Content tutoriel_mode_travail = com.telecoop.telecoop.data.ContentProvider.getContent("tutoriel_mode_travail");
            showOverlay(tutoriel_mode_travail.getTitle(), tutoriel_mode_travail.getContent(), tutoriel_mode_travail.getImageResId());
        });

        // "Refaire le quizz" : effacer les données du quizz et naviguer vers l'écran d'accueil du quizz
        binding.btnRefaireQuizz.setOnClickListener(v -> {
            // Efface les données stockées dans "QuizPrefs" (prénom, état du quizz, réponses...)
            SharedPreferences prefs = requireActivity().getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();

            // Vérifier que les préférences ont bien été effacées
            Log.d("QuizzReset", "Toutes les données du quiz ont été effacées.");

            // Navigation vers l'écran d'accueil du quizz (QuizzWelcomeFragment)
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_nav_conseils_to_quizzWelcomeFragment);
        });

        // Gère le bouton "fermer" dans l'overlay
        binding.btnCloseOverlay.setOnClickListener(v -> closeOverlay());
    }

    // Affiche l'overlay en lui assignant un titre et un contenu (texte, image...)
    private void showOverlay(String title, String content, Integer imageResId){
        binding.overlayContainer.setVisibility(View.VISIBLE);
        binding.txtOverlayTitle.setText(title);
        binding.txtOverlayContent.setText(content);
        if (imageResId != null) {
            binding.imgOverlay.setVisibility(View.VISIBLE);
            binding.imgOverlay.setImageResource(imageResId);
        } else {
            binding.imgOverlay.setVisibility(View.GONE);
        }
    }

    // Ferme (cache) l'overlay pour revenir à la liste de boutons
    private void closeOverlay(){
        binding.overlayContainer.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
