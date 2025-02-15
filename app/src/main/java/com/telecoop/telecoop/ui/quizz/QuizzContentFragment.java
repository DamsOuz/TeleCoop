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

import com.telecoop.telecoop.R;
import com.telecoop.telecoop.data.Question;
import com.telecoop.telecoop.databinding.FragmentQuizzContentBinding;
import com.telecoop.telecoop.injection.ViewModelFactory;

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
    // Pour chaque question (identifiée par son indice dans la liste), on sauvegarde l'ensemble des indices de réponses sélectionnées
    private Map<Integer, Set<Integer>> selectedAnswersIndicesMap = new HashMap<>();
    // Liste des questions (issue du ViewModel)
    private List<Question> questionOrder = new ArrayList<>();
    // Index de la question courante (géré dans le fragment)
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

        // Démarre le quiz et initialise la liste des questions
        viewModel.startQuizz();

        // Récupère la liste des questions depuis le ViewModel
        questionOrder = new ArrayList<>(viewModel.getQuestions());

        // Restaure l'état sauvegardé (index courant et réponses sélectionnées)
        restoreQuizState();

        // Si un état sauvegardé existe, on utilise currentQuestionIndex sauvegardé (s'il est valide)
        if (currentQuestionIndex < questionOrder.size()) {
            viewModel.currentQuestion.postValue(questionOrder.get(currentQuestionIndex));
        } else {
            currentQuestionIndex = 0;
            viewModel.currentQuestion.postValue(questionOrder.get(0));
        }

        updateNextButtonText();

        // Active le bouton "retour" de la barre d'action
        if (getActivity() != null) {
            ((androidx.appcompat.app.AppCompatActivity) getActivity())
                    .getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);

        binding.next.setTextColor(Color.GRAY);
        binding.next.setEnabled(false);

        // Tableau des boutons de réponses (déclarés dans le layout)
        Button[] answers = {binding.answer1, binding.answer2, binding.answer3, binding.answer4, binding.answer5};

        // Observer la question courante pour mettre à jour l'interface
        viewModel.currentQuestion.observe(getViewLifecycleOwner(), question -> {
            if (question != null) {
                updateQuestion(question);
            }
            // Réinitialise tous les boutons
            for (Button button : answers) {
                button.setVisibility(View.GONE);
                button.setOnClickListener(null);
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
            }
            if (question != null) {
                // Affiche les choix de réponse
                List<String> choices = question.getChoiceList();
                for (int i = 0; i < choices.size(); i++) {
                    int index = i;
                    answers[i].setText(choices.get(i));
                    answers[i].setVisibility(View.VISIBLE);
                    answers[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
                    answers[i].setOnClickListener(v -> updateAnswer(answers[index], index));
                }
            }
            // Restaure la sélection déjà effectuée pour la question courante
            restoreSelectedAnswers();
            checkIfNextShouldBeEnabled();
        });

        // Gestion du clic sur le bouton "NEXT"/"FINISH"
        binding.next.setOnClickListener(v -> {
            if (currentQuestionIndex < questionOrder.size() - 1) {
                currentQuestionIndex++;
                viewModel.currentQuestion.postValue(questionOrder.get(currentQuestionIndex));
                updateNextButtonText();
            } else {
                // Dernière question : on sauvegarde et on affiche le résultat
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

    // Gère la sélection d'un item du menu (ici, le bouton "retour" de l'action bar)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBackNavigation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Gère la navigation vers la question précédente ou la demande de confirmation pour quitter le quiz
    private void handleBackNavigation() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            viewModel.currentQuestion.postValue(questionOrder.get(currentQuestionIndex));
            restoreSelectedAnswers();
            updateNextButtonText();
            saveQuizState();
        } else {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Quitter le Quiz ?")
                    .setMessage("Voulez-vous vraiment quitter le quiz ?")
                    .setPositiveButton("Oui", (dialog, id) -> {
                        NavController navController = NavHostFragment.findNavController(QuizzContentFragment.this);
                        navController.navigate(R.id.action_quizzContentFragment_to_homeFragment);
                    })
                    .setNegativeButton("Non", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    // Met à jour l'intitulé de la question dans l'interface
    private void updateQuestion(Question question) {
        binding.question.setText(question.getQuestion());
        binding.next.setEnabled(false);
        binding.next.setTextColor(Color.GRAY);
    }

    // Gère la sélection/désélection d'une réponse
    private void updateAnswer(Button button, int answerIndex) {
        Set<Integer> selectedSet = selectedAnswersIndicesMap.get(currentQuestionIndex);
        if (selectedSet == null) {
            selectedSet = new HashSet<>();
        }
        if (selectedSet.contains(answerIndex)) {
            selectedSet.remove(answerIndex);
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
        } else {
            selectedSet.add(answerIndex);
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedColor));
        }
        selectedAnswersIndicesMap.put(currentQuestionIndex, selectedSet);
        checkIfNextShouldBeEnabled();
    }

    // Restaure l'apparence des boutons selon les réponses déjà sélectionnées
    private void restoreSelectedAnswers() {
        Button[] answers = {binding.answer1, binding.answer2, binding.answer3, binding.answer4, binding.answer5};
        Set<Integer> selectedIndices = selectedAnswersIndicesMap.get(currentQuestionIndex);
        if (selectedIndices != null) {
            for (Integer index : selectedIndices) {
                if (index >= 0 && index < answers.length) {
                    answers[index].setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedColor));
                }
            }
        }
    }

    // Active/désactive le bouton NEXT en fonction d'une sélection
    private void checkIfNextShouldBeEnabled() {
        Set<Integer> selectedSet = selectedAnswersIndicesMap.get(currentQuestionIndex);
        boolean isAnySelected = selectedSet != null && !selectedSet.isEmpty();
        binding.next.setEnabled(isAnySelected);
        binding.next.setTextColor(isAnySelected ? Color.WHITE : Color.GRAY);
    }

    // Change le libellé du bouton selon que l'on est à la dernière question ou non
    private void updateNextButtonText() {
        if (currentQuestionIndex == questionOrder.size() - 1) {
            binding.next.setText("FINISH");
        } else {
            binding.next.setText("NEXT");
        }
    }

    // Affiche une boîte de dialogue de résultat en fin de quiz
    private void displayResultDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Terminé !")
                .setMessage("Merci d'avoir répondu aux questions :)")
                .setPositiveButton("Quit", (dialog, id) -> {
                    NavController navController = NavHostFragment.findNavController(QuizzContentFragment.this);
                    navController.navigate(R.id.action_quizzContentFragment_to_homeFragment);
                })
                .setNegativeButton("Annuler", (dialog, id) -> dialog.dismiss())
                .show();
    }

    // Sauvegarde l'état (index courant et réponses sélectionnées) dans les SharedPreferences
    private void saveQuizState() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("currentQuestionIndex", currentQuestionIndex);
        for (Map.Entry<Integer, Set<Integer>> entry : selectedAnswersIndicesMap.entrySet()) {
            int qIndex = entry.getKey();
            Set<Integer> answersSet = entry.getValue();
            StringBuilder sb = new StringBuilder();
            for (Integer ansIndex : answersSet) {
                sb.append(ansIndex).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            editor.putString("question_" + qIndex, sb.toString());
        }
        editor.apply();
    }

    // Restaure l'état sauvegardé depuis les SharedPreferences
    private void restoreQuizState() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
        currentQuestionIndex = prefs.getInt("currentQuestionIndex", 0);
        selectedAnswersIndicesMap.clear();

        int totalQuestions = questionOrder.size();
        for (int i = 0; i < totalQuestions; i++) {
            String key = "question_" + i;
            String value = prefs.getString(key, "");
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
                selectedAnswersIndicesMap.put(i, set);
            }
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
