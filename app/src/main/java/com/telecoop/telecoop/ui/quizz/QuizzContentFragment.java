package com.telecoop.telecoop.ui.quizz;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.telecoop.telecoop.data.Question;
import com.telecoop.telecoop.databinding.FragmentQuizzContentBinding;
import com.telecoop.telecoop.injection.ViewModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

public class QuizzContentFragment extends Fragment {

    private QuizzViewModel viewModel;
    private FragmentQuizzContentBinding binding;
    private final int defaultColor = Color.WHITE;
    private final int selectedColor = Color.BLACK;
    private Set<Button> selectedButtons = new HashSet<>();
    private Stack<Question> questionHistory = new Stack<>();
    private Map<Question, Set<Button>> selectedAnswersMap = new HashMap<>();
    private Map<Question, Integer> questionChoicesCount = new HashMap<>();
    private List<Question> questionOrder = new ArrayList<>();
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
        viewModel.startQuizz();

        if (getActivity() != null) {
            ((androidx.appcompat.app.AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);

        binding.next.setTextColor(Color.GRAY);
        binding.next.setEnabled(false);

        Button[] answers = {binding.answer1, binding.answer2, binding.answer3, binding.answer4, binding.answer5};

        viewModel.currentQuestion.observe(getViewLifecycleOwner(), question -> {
            if (question != null) {
                updateQuestion(question);
            }

            for (Button button : answers) {
                button.setVisibility(View.GONE);
                button.setOnClickListener(null);
            }

            List<String> choices = question.getChoiceList();
            for (int i = 0; i < choices.size(); i++) {
                int finalI = i;
                answers[i].setText(choices.get(i));
                answers[i].setVisibility(View.VISIBLE);
                answers[i].setOnClickListener(v -> updateAnswer(answers[finalI]));
            }
        });

        binding.next.setOnClickListener(v -> {
            boolean isCurrentlyLastQuestion = (currentQuestionIndex == questionOrder.size() - 1)
                    && Boolean.TRUE.equals(viewModel.isLastQuestion.getValue());
            if (isCurrentlyLastQuestion) {
                displayResultDialog();
            } else {
                selectedAnswersMap.put(questionOrder.get(currentQuestionIndex), new HashSet<>(selectedButtons));

                if (currentQuestionIndex < questionOrder.size() - 1) {
                    currentQuestionIndex++;
                    Question nextQuestion = questionOrder.get(currentQuestionIndex);
                    updateQuestion(nextQuestion);
                } else {
                    viewModel.nextQuestion();
                }

                resetQuestion();
                binding.next.setTextColor(Color.GRAY);
            }

            updateNextButtonText();
        });

        viewModel.isLastQuestion.observe(getViewLifecycleOwner(), isLastQuestion -> {
            binding.next.setText(Boolean.TRUE.equals(isLastQuestion) ? "FINISH" : "NEXT");
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackNavigation();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBackNavigation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleBackNavigation() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            Question previousQuestion = questionOrder.get(currentQuestionIndex);
            restorePreviousQuestion(previousQuestion);
            updateNextButtonText();
        } else {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Quitter le Quiz ?")
                    .setMessage("Voulez-vous vraiment quitter le quiz ?")
                    .setPositiveButton("Oui", (dialog, which) -> requireActivity().finish())
                    .setNegativeButton("Non", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }


    private void updateQuestion(Question question) {
        Button[] answers = {binding.answer1, binding.answer2, binding.answer3, binding.answer4, binding.answer5};

        if (!questionOrder.contains(question)) {
            questionOrder.add(question);
        }
        currentQuestionIndex = questionOrder.indexOf(question);
        selectedButtons.clear();
        binding.question.setText(question.getQuestion());

        if (!questionChoicesCount.containsKey(question)) {
            questionChoicesCount.put(question, question.getChoiceList().size());
        }

        int numChoices = questionChoicesCount.get(question);
        for (int i = 0; i < numChoices; i++) {
            answers[i].setText(question.getChoiceList().get(i));
            answers[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
            answers[i].setVisibility(View.VISIBLE);
        }

        for (int i = numChoices; i < answers.length; i++) {
            answers[i].setVisibility(View.GONE);
        }

        binding.next.setEnabled(false);
        binding.next.setTextColor(Color.GRAY);

        restoreSelectedAnswers(question);
        updateNextButtonText();
    }

    private void restoreSelectedAnswers(Question question) {
        if (selectedAnswersMap.containsKey(question)) {
            selectedButtons = new HashSet<>(Objects.requireNonNull(selectedAnswersMap.get(question)));
            for (Button btn : selectedButtons) {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedColor));
            }
            checkIfNextShouldBeEnabled();
        }
    }

    private void restorePreviousQuestion(Question question) {
        updateQuestion(question);

        if (selectedAnswersMap.containsKey(question)) {
            selectedButtons = new HashSet<>(selectedAnswersMap.get(question));
            for (Button button : selectedButtons) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedColor));
            }
        }
        checkIfNextShouldBeEnabled();
    }

    private void updateAnswer(Button button) {
        if (selectedButtons.contains(button)) {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
            selectedButtons.remove(button);
        } else {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedColor));
            selectedButtons.add(button);
        }
        checkIfNextShouldBeEnabled();
    }

    private void checkIfNextShouldBeEnabled() {
        boolean isAnySelected = !selectedButtons.isEmpty();
        binding.next.setEnabled(isAnySelected);
        binding.next.setTextColor(isAnySelected ? Color.WHITE : Color.GRAY);
    }

    private void resetQuestion() {
        List<Button> allAnswers = List.of(binding.answer1, binding.answer2, binding.answer3, binding.answer4, binding.answer5);

        for (Button button : allAnswers) {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
        }

        selectedButtons.clear();
        binding.next.setEnabled(false);
        binding.next.setTextColor(Color.GRAY);
    }

    private void displayResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Terminé !");
        builder.setMessage("Merci d'avoir répondu aux questions :)");
        builder.setPositiveButton("Quit", (dialog, id) -> requireActivity().finish());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateNextButtonText() {
        if (currentQuestionIndex == questionOrder.size() - 1 && Boolean.TRUE.equals(viewModel.isLastQuestion.getValue())) {
            binding.next.setText("FINISH");
        } else {
            binding.next.setText("NEXT");
        }
    }
}
