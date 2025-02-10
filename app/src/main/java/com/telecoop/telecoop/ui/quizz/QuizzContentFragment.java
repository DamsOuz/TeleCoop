package com.telecoop.telecoop.ui.quizz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.telecoop.telecoop.data.Question;
import com.telecoop.telecoop.databinding.FragmentQuizzContentBinding;
import com.telecoop.telecoop.injection.ViewModelFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuizzContentFragment extends Fragment {

    private QuizzViewModel viewModel;
    private FragmentQuizzContentBinding binding;
    private final int defaultColor = Color.WHITE;
    private final int selectedColor = Color.BLACK;
    private Set<Button> selectedButtons = new HashSet<>();

    public static QuizzContentFragment newInstance() {
        return new QuizzContentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(
                this, ViewModelFactory.getInstance()).get(QuizzViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQuizzContentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        viewModel.startQuizz();
        binding.next.setTextColor(Color.GRAY);
        binding.next.setEnabled(false);

        android.widget.Button[] answers = {binding.answer1, binding.answer2, binding.answer3,
                binding.answer4, binding.answer5};

        viewModel.currentQuestion.observe(getViewLifecycleOwner(), new Observer<Question>() {
            @Override
            public void onChanged(Question question) {
                if (question != null){
                    updateQuestion(question);
                }

                for (Button button : answers){
                    button.setVisibility(View.GONE);
                    button.setOnClickListener(null);
                }

                List<String> choices = question.getChoiceList();
                for(int i = 0; i < choices.size(); i++){
                    int finalI = i;
                    answers[i].setText(choices.get(i));
                    answers[i].setVisibility(View.VISIBLE);
                    answers[i].setOnClickListener(v -> updateAnswer(answers[finalI]));
                }
            }
        });

        binding.next.setOnClickListener(v -> {
                Boolean isLastQuestion = viewModel.isLastQuestion.getValue();
                if (isLastQuestion != null && isLastQuestion){
                    displayResultDialog();
                } else {
                    viewModel.nextQuestion();
                    resetQuestion();
                    binding.next.setTextColor(Color.GRAY);
                }

                if (isLastQuestion != null && isLastQuestion){
                    binding.next.setText("FINISH");
                } else {
                    binding.next.setText("NEXT");
                }
        });

        viewModel.isLastQuestion.observe(getViewLifecycleOwner(), isLastQuestion -> {
            if (Boolean.TRUE.equals(isLastQuestion)) {
                binding.next.setText("FINISH");
            } else {
                binding.next.setText("NEXT");
            }
        });
    }

    private void updateQuestion(Question question){

        android.widget.Button[] answers = {binding.answer1, binding.answer2, binding.answer3,
                binding.answer4, binding.answer5};

        binding.question.setText(question.getQuestion());

        for(int i = 0; i < question.getChoiceList().size(); i++){
            answers[i].setText(question.getChoiceList().get(i));
            answers[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
        }

        for(int i = answers.length; i > question.getChoiceList().size(); i--){
            answers[i-1].setVisibility(View.GONE);
        }

        selectedButtons.clear();
        binding.next.setEnabled(false);
        binding.next.setTextColor(Color.GRAY);
    }

    private void updateAnswer(android.widget.Button button){
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

    private void resetQuestion(){
        List<Button> allAnswers = List.of(binding.answer1, binding.answer2, binding.answer3,
                binding.answer4, binding.answer5);

        for (Button button : allAnswers){
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
        builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //goToWelcomeFragment();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}