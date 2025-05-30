package com.telecoop.telecoop.ui.quizz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import android.util.Log;
import android.widget.TextView;

import com.telecoop.telecoop.R;
import com.telecoop.telecoop.data.AnswerChoice;
import com.telecoop.telecoop.data.Profile;
import com.telecoop.telecoop.data.Question;
import com.telecoop.telecoop.injection.ViewModelFactory;
import com.telecoop.telecoop.databinding.FragmentQuizzContentBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuizzContentFragment extends Fragment {

    private QuizzViewModel viewModel;
    private FragmentQuizzContentBinding binding;
    private final int defaultColor = Color.WHITE;
    private final int selectedColor = Color.BLACK;

    // Pour chaque question, on enregistre l'ensemble des indices de réponses sélectionnées
    private Map<Integer, Set<Integer>> selectedAnswerIndicesMap = new HashMap<>();
    // Liste des questions (issue du ViewModel)
    private ArrayList<Question> questionOrder = new ArrayList<>();
    // Index de la question courante
    private int currentQuestionIndex = 0;

    public static QuizzContentFragment newInstance() {
        return new QuizzContentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(QuizzViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuizzContentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Démarre le quiz et charge la liste de questions enrichies
        viewModel.startQuizz();
        questionOrder = new ArrayList<>(viewModel.getQuestions());

        // Restaure l'état sauvegardé (réponses sélectionnées et question courante)
        restoreQuizState();

        // Réintégrer les scores à partir des réponses déjà sélectionnées
        restoreProfileScores();

        if (currentQuestionIndex < questionOrder.size()) {
            viewModel.currentQuestion.postValue(questionOrder.get(currentQuestionIndex));
        } else {
            currentQuestionIndex = 0;
            viewModel.currentQuestion.postValue(questionOrder.get(0));
        }
        updateQuestionCounter();
        updateNextButtonText();

        // Active le bouton "retour" dans la barre d'action
        if(getActivity() != null) {
            ((androidx.appcompat.app.AppCompatActivity)getActivity())
                    .getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);

        binding.next.setTextColor(Color.GRAY);
        binding.next.setEnabled(false);

        // Tableau des boutons de réponses (fixé à 6 maximum)
        Button[] answers = {binding.answer1, binding.answer2, binding.answer3, binding.answer4, binding.answer5, binding.answer6};

        // Observer la question courante pour mettre à jour l'affichage
        viewModel.currentQuestion.observe(getViewLifecycleOwner(), question -> {
            if(question != null) {
                updateQuestion(question);
            }

            // Afficher l'état des scores dans la console
            Log.d("ProfileScores", viewModel.getProfileScores().toString());

            // Réinitialise tous les boutons
            for(Button button : answers) {
                button.setVisibility(View.GONE);
                button.setOnClickListener(null);
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
            }
            if(question != null) {
                // On récupère la liste d'AnswerChoice
                int nb = Math.min(question.getAnswerChoices().size(), answers.length);
                for(int i = 0; i < nb; i++) {
                    int index = i;
                    AnswerChoice choice = question.getAnswerChoices().get(i);
                    answers[i].setText(choice.getText());
                    answers[i].setVisibility(View.VISIBLE);
                    answers[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
                    answers[i].setOnClickListener(v -> {
                        updateAnswer(answers[index], index, choice);
                    });
                }
            }
            restoreSelectedAnswers();
            checkIfNextShouldBeEnabled();
        });

        // Gestion du clic sur le bouton "NEXT"/"FINISH"
        binding.next.setOnClickListener(v -> {
            if(currentQuestionIndex < questionOrder.size() - 1) {
                currentQuestionIndex++;
                viewModel.currentQuestion.postValue(questionOrder.get(currentQuestionIndex));
                updateQuestionCounter();
                updateNextButtonText();
            } else {
                saveQuizState();
                displayResultDialog();
            }
            saveQuizState();
        });

        // Gestion du bouton "retour" matériel
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackNavigation();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            handleBackNavigation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Navigation vers la question précédente ou confirmation de quitter le quiz
    private void handleBackNavigation() {
        if(currentQuestionIndex > 0) {
            currentQuestionIndex--;
            viewModel.currentQuestion.postValue(questionOrder.get(currentQuestionIndex));
            restoreSelectedAnswers();
            updateQuestionCounter();
            updateNextButtonText();
            saveQuizState();
        } else {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Quitter le Quizz ?")
                    .setMessage("Veux-tu vraiment quitter le quizz ?")
                    .setPositiveButton("Oui", (dialog, id) -> {
                        NavController navController = NavHostFragment.findNavController(QuizzContentFragment.this);
                        navController.navigate(R.id.action_quizzContentFragment_to_homeFragment);
                    })
                    .setNegativeButton("Non", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    // Met à jour l'intitulé de la question affichée
    private void updateQuestion(Question question) {
        binding.question.setText(question.getQuestion());
        binding.next.setEnabled(false);
        binding.next.setTextColor(Color.GRAY);
    }

    /**
     * Gère la sélection/désélection d'une réponse
     * Permet de sélectionner plusieurs réponses par question
     */
    private void updateAnswer(Button button, int answerIndex, AnswerChoice choice) {
        Set<Integer> selectedSet = selectedAnswerIndicesMap.get(currentQuestionIndex);
        if(selectedSet == null) {
            selectedSet = new HashSet<>();
        }
        if(selectedSet.contains(answerIndex)) {
            // Désélectionner : retirer l'indice et décrémenter les profils associés
            selectedSet.remove(answerIndex);
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
            viewModel.decrementProfiles(choice.getAssociatedProfiles());
        } else {
            // Sélectionner : ajouter l'indice et incrémenter les profils associés
            selectedSet.add(answerIndex);
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedColor));
            viewModel.incrementProfiles(choice.getAssociatedProfiles());
        }
        selectedAnswerIndicesMap.put(currentQuestionIndex, selectedSet);
        checkIfNextShouldBeEnabled();
    }

    // Méthode utilitaire pour obtenir le bouton correspondant à un index
    private Button getButtonForIndex(int index) {
        Button[] answers = {binding.answer1, binding.answer2, binding.answer3, binding.answer4, binding.answer5, binding.answer6};
        if(index >= 0 && index < answers.length) {
            return answers[index];
        }
        return null;
    }

    // Méthode utilitaire pour restaurer l'apparence des boutons sélectionnés
    private void restoreSelectedAnswers() {
        Button[] answers = {binding.answer1, binding.answer2, binding.answer3, binding.answer4, binding.answer5, binding.answer6};
        Set<Integer> selectedSet = selectedAnswerIndicesMap.get(currentQuestionIndex);
        if(selectedSet != null) {
            for (Integer index : selectedSet) {
                if(index >= 0 && index < answers.length) {
                    answers[index].setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedColor));
                }
            }
        }
    }

    // Active ou désactive le bouton NEXT en fonction de si une réponse est sélectionnée
    private void checkIfNextShouldBeEnabled() {

        if (currentQuestionIndex == 0) {
            binding.next.setEnabled(true);
            binding.next.setTextColor(Color.WHITE);
            return;
        }

        Set<Integer> selectedSet = selectedAnswerIndicesMap.get(currentQuestionIndex);
        boolean isAnySelected = (selectedSet != null && !selectedSet.isEmpty());
        binding.next.setEnabled(isAnySelected);
        binding.next.setTextColor(isAnySelected ? Color.WHITE : Color.GRAY);
    }

    // Met à jour le texte du bouton NEXT ("NEXT" ou "FINISH")
    private void updateNextButtonText() {
        if(currentQuestionIndex == questionOrder.size() - 1) {
            binding.next.setText("FINISH");
        } else {
            binding.next.setText("NEXT");
        }
    }

    // Affiche un dialog avec les résultats du quiz (profils dominants, etc.)
    private void displayResultDialog() {
        // Calculer et publier le résultat final dans le ViewModel
        viewModel.computeFinalProfiles();

        // Observer le LiveData finalProfiles une seule fois
        viewModel.getFinalProfiles().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<List<Profile>>() {
            @Override
            public void onChanged(List<Profile> profiles) {
                // Dès que la valeur est disponible, on la sauvegarde
                if (profiles != null && !profiles.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Profile p : profiles) {
                        sb.append(p.name()).append(",");
                    }
                    String csv = sb.toString();
                    if (csv.endsWith(",")) {
                        csv = csv.substring(0, csv.length() - 1);
                    }
                    SharedPreferences prefs = requireActivity().getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
                    prefs.edit().putString("finalProfilesCsv", csv).apply();
                }
                // Supprimer cet observateur pour ne pas le déclencher à nouveau
                viewModel.getFinalProfiles().removeObserver(this);

                // Afficher le dialog des résultats
                StringBuilder sbResult = new StringBuilder("Merci de ta participation ! Voici ton/tes profil(s) :\n");
                List<Profile> topProfiles = viewModel.getTopProfiles();
                for (Profile p : topProfiles) {
                    sbResult.append("- ").append(p.name()).append("\n");
                }

                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("Terminé !")
                        .setMessage(sbResult.toString())
                        .setPositiveButton("Quit", (dialog, id) -> {
                            NavController navController = androidx.navigation.fragment.NavHostFragment.findNavController(QuizzContentFragment.this);
                            navController.navigate(R.id.action_quizzContentFragment_to_homeFragment);
                        })
                        .setNegativeButton("Annuler", (dialog, id) -> dialog.dismiss())
                        .show();
            }
        });
    }

    // Sauvegarde l'état actuel du quiz (question et réponse sélectionnée) dans SharedPreferences
    private void saveQuizState() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("currentQuestionIndex", currentQuestionIndex);
        for(Map.Entry<Integer, Set<Integer>> entry : selectedAnswerIndicesMap.entrySet()){
            int qIndex = entry.getKey();
            Set<Integer> answersSet = entry.getValue();
            StringBuilder sb = new StringBuilder();
            for(Integer ansIndex : answersSet) {
                sb.append(ansIndex).append(",");
            }
            if(sb.length() > 0) {
                sb.deleteCharAt(sb.length()-1);
            }
            editor.putString("question_" + qIndex, sb.toString());
        }
        editor.apply();
    }

    // Restaure l'état du quiz depuis SharedPreferences
    private void restoreQuizState() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
        currentQuestionIndex = prefs.getInt("currentQuestionIndex", 0);
        selectedAnswerIndicesMap.clear();
        int totalQuestions = questionOrder.size();
        // Récupérer toutes les entrées pour éviter les problèmes de type
        Map<String, ?> allPrefs = prefs.getAll();
        for (int i = 0; i < totalQuestions; i++) {
            Object rawValue = allPrefs.get("question_" + i);
            if (rawValue != null) {
                String value;
                if (rawValue instanceof String) {
                    value = (String) rawValue;
                } else {
                    // Convertit en chaîne si ce n'est pas déjà une String
                    value = String.valueOf(rawValue);
                }
                if (!value.isEmpty()) {
                    String[] parts = value.split(",");
                    Set<Integer> set = new HashSet<>();
                    for (String part : parts) {
                        try {
                            set.add(Integer.parseInt(part));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    selectedAnswerIndicesMap.put(i, set);
                }
            }
        }
    }

    /**
     * Après avoir restauré les réponses sélectionnées, parcourt les réponses restaurées
     * et incrémente les scores des profils correspondants dans le ViewModel
     */
    private void restoreProfileScores() {
        // Parcourir chaque entrée (clé: questionIndex, valeur: set d'indices de réponses sélectionnées)
        for (Map.Entry<Integer, Set<Integer>> entry : selectedAnswerIndicesMap.entrySet()) {
            int questionIndex = entry.getKey();
            // Vérifier que l'index de question est valide
            if (questionIndex < 0 || questionIndex >= questionOrder.size()) {
                Log.e("restoreProfileScores", "Question index invalide: " + questionIndex);
                continue;
            }
            List<AnswerChoice> choices = questionOrder.get(questionIndex).getAnswerChoices();
            Set<Integer> answerSet = entry.getValue();
            for (Integer answerIndex : answerSet) {
                // Vérifier que l'indice de réponse est dans les limites de la liste des choix pour la question
                if (answerIndex < 0 || answerIndex >= choices.size()) {
                    Log.e("restoreProfileScores", "Invalid answer index " + answerIndex + " for question index " + questionIndex +
                            " (nombre de réponses: " + choices.size() + ")");
                    continue;
                }
                AnswerChoice choice = choices.get(answerIndex);
                viewModel.incrementProfiles(choice.getAssociatedProfiles());
            }
        }
    }

    private void updateQuestionCounter() {
        TextView txtQuestionIndex = getView().findViewById(R.id.txtQuestionIndex);

        if (currentQuestionIndex == 0){
            txtQuestionIndex.setVisibility(View.GONE);
        } else if (txtQuestionIndex != null && questionOrder != null && !questionOrder.isEmpty()) {
            txtQuestionIndex.setVisibility(View.VISIBLE);
            // Affiche la question actuelle sur le nombre total en décalant tout de 1 (car on ne compte pas la 1ere page comme une question)
            txtQuestionIndex.setText(currentQuestionIndex + "/" + (questionOrder.size()-1));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveQuizState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
