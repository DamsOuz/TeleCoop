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
            showOverlay("Temps positifs", "Contenu à venir...");
        });

        binding.btnGestionObjectifs.setOnClickListener(v ->
                showOverlay("Gestion des objectifs", "Ici, on peut expliquer comment fixer des objectifs...")
        );

        binding.btnGestionNotifications.setOnClickListener(v ->
                showOverlay("Gestion des notifications", "Ici, on peut expliquer comment fixer des notifications...")
        );

        binding.btnTutorielNotifs.setOnClickListener(v ->
                showOverlay("Tutoriel notifications", "Ici, on explique comment configurer ses notifications...")
        );

        binding.btnTutorielBlocage.setOnClickListener(v ->
                showOverlay("Tutoriel blocage", "Ici, on peut expliquer la fonctionnalité de blocage...")
        );

        binding.btnTutorielFiltresCouleurs.setOnClickListener(v ->
                showOverlay("Tutoriel filtres couleurs", "Ici, on explique les filtres couleurs... (contenu)")
        );

        binding.btnTutorielModeTravail.setOnClickListener(v ->
                showOverlay("Tutoriel mode travail", "Ici, on détaille le mode travail...")
        );

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
    private void showOverlay(String title, String content){
        binding.overlayContainer.setVisibility(View.VISIBLE);
        binding.txtOverlayTitle.setText(title);
        binding.txtOverlayContent.setText(content);
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
