package com.telecoop.telecoop.ui.conseils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.telecoop.telecoop.R;
import com.telecoop.telecoop.data.Content;
import com.telecoop.telecoop.data.ContentProvider;
import com.telecoop.telecoop.data.Profile;
import com.telecoop.telecoop.databinding.FragmentConseilsBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Page "Conseils"
 * Affiche une liste de boutons. Chaque bouton ouvre un overlay
 * contenant du texte ou des images, que l'on peut fermer
 */
public class ConseilsFragment extends Fragment {

    private FragmentConseilsBinding binding;

    private static final List<ConseilAction> ALL_ACTIONS = Arrays.asList(
            new ConseilAction("tutoriel_blocage", "Tutoriel : limiter l'utilisation d'application par un blocage", "tutoriel_blocage"),
            new ConseilAction("temps_positifs", "Programmer des temps positifs quotidiens", "temps_positifs"),
            new ConseilAction("gestion_objectifs", "Fixer des horaires d'utilisation par application", "gestion_objectifs"),
            new ConseilAction("gestion_notifications", "Conscientiser mon utilisation du téléphone via des notifications", "gestion_notifications"),
            new ConseilAction("tutoriel_notifications", "Tutoriel : mieux gérer mes notifications", "tutoriel_notifications"),
            new ConseilAction("tutoriel_filtres_couleurs", "Tutoriel : instaurer des filtres de couleurs", "tutoriel_filtres_couleurs"),
            new ConseilAction("tutoriel_mode_travail", "Tutoriel : instaurer des temps de concentration", "tutoriel_mode_travail")
    );

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

        // Récupérer les profils depuis SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
        String csv = prefs.getString("finalProfilesCsv", "");
        // Parser la chaîne CSV pour reconstruire la liste de profils
        List<Profile> userProfiles = parseProfilesFromCsv(csv);

        // Si la liste est vide, on considère le profil par défaut
        if (userProfiles.isEmpty()) {
            userProfiles = Collections.singletonList(Profile.PROFIL_PAR_DEFAUT);
        }

        // Déterminer la liste des ID d'actions "recommandées"
        List<String> recommendedIds = getRecommendedActionsForUserProfile(userProfiles);

        binding.layoutActionsSuggerees.removeAllViews();
        binding.layoutAutresActions.removeAllViews();

        // LayoutParams pour les boutons
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // Ajouter des marges entre boutons
        params.setMargins(0, 24, 0, 24);

        int nbSuggerees = 0;
        int nbAutres = 0;

        for (ConseilAction action : ALL_ACTIONS) {
            Button btn = new Button(requireContext());
            btn.setBackgroundResource(R.drawable.rounded_button_non_recommended);
            btn.setText(action.getButtonLabel());
            btn.setLayoutParams(params);

            // Ajouter un padding interne au bouton
            // 12dp tout autour par exemple
            int paddingInPx = (int) (12 * getResources().getDisplayMetrics().density);
            btn.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

            // Forcer des couleurs et une taille pour éviter un éventuel problème de thème
            btn.setTextColor(getResources().getColor(R.color.textContentButton));
            btn.setTextSize(20);

            // Si l'action est recommandée et qu'on n'a pas déjà 3 actions suggérées
            if (recommendedIds.contains(action.getActionId()) && nbSuggerees < 3) {
                btn.setTextColor(Color.WHITE);
                btn.setBackgroundResource(R.drawable.rounded_button_recommended);
                binding.layoutActionsSuggerees.addView(btn);
                nbSuggerees++;
            } else {
                binding.layoutAutresActions.addView(btn);
                nbAutres++;
            }

            // Au clic => overlay ou redirection vers autre page
            btn.setOnClickListener(v2 -> {
                if (action.getActionId().equals("temps_positifs")) {
                    // Naviguer vers la page "Temps positifs"
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_nav_conseils_to_tempspositifs);
                } else {
                    // Sinon, afficher l'overlay
                    Content c = ContentProvider.getContent(action.getContentKey());
                    showOverlay(c.getTitle(), c.getContent(), c.getImageResId());
                }
            });

        }

        // Afficher ou masquer les sections
        binding.containerActionsSuggerees.setVisibility(nbSuggerees > 0 ? View.VISIBLE : View.GONE);
        binding.containerAutresActions.setVisibility(nbAutres > 0 ? View.VISIBLE : View.GONE);

        // "Refaire le quizz" : effacer les données du quizz et naviguer vers l'écran d'accueil du quizz
        binding.btnRefaireQuizz.setOnClickListener(v -> {
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

    // Convertit une chaîne CSV en liste de profils
    private List<Profile> parseProfilesFromCsv(String csv) {
        List<Profile> list = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) {
            return list;
        }
        // Exemple de CSV: "FOMO,DOOMSCROLL"
        String[] tokens = csv.split(",");
        for (String t : tokens) {
            try {
                Profile p = Profile.valueOf(t.trim());
                list.add(p);
            } catch (IllegalArgumentException e) {
                // Si on ne reconnaît pas le profil, on l'ignore
            }
        }
        return list;
    }

    // Ferme l'overlay pour revenir à la liste de boutons
    private void closeOverlay(){
        binding.overlayContainer.setVisibility(View.GONE);
    }

    // Obtenir entre 0 et 3 actions maximum recommandées en fonction du profil utilisateur
    private List<String> getRecommendedActionsForUserProfile(List<Profile> profiles) {
        List<String> recommended = new ArrayList<>();
        // Si l'utilisateur n'a obtenu que le profil par défaut, aucune suggestion n'est proposée
        if (profiles.size() == 1 && profiles.get(0) == Profile.PROFIL_PAR_DEFAUT) {
            return recommended;
        }

        for (Profile userProfile: profiles){
            switch (userProfile) {
                case AUTOMATISME:
                    recommended.add("gestion_notifications");
                    break;
                case DOOMSCROLL:
                    recommended.add("tutoriel_blocage");
                    recommended.add("gestion_notifications");
                    recommended.add("tutoriel_filtres_couleurs");
                    break;
                case SOMMEIL:
                    recommended.add("gestion_objectifs");
                    recommended.add("tutoriel_filtres_couleurs");
                case MANQUE_PRODUCTIVITE:
                    recommended.add("gestion_objectifs");
                    recommended.add("tutoriel_filtres_couleurs");
                    recommended.add("tutoriel_notifications");
                    recommended.add("tutoriel_mode_travail");
                case FOMO:
                    recommended.add("tutoriel_notifications");
                    break;
                case PERTE_TEMPS_ENNUI:
                    recommended.add("temps_positifs");
                    recommended.add("tutoriel_blocage");
                    recommended.add("gestion_notifications");
                    break;
                default:
                    // Profil inconnu => rien
                    break;
            }
        }

        // Supprimer les doublons en conservant l'ordre
        recommended = new ArrayList<>(new LinkedHashSet<>(recommended));

        // On tronque la liste à 3 maximum
        if (recommended.size() > 3) {
            recommended = recommended.subList(0, 3);
        }

        return recommended;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
