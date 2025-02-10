package com.telecoop.telecoop.ui.quizz;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import java.util.Arrays;
import java.util.List;

public class QuizzContentFragment extends Fragment {

    private QuizzViewModel viewModel;
    private FragmentQuizzContentBinding binding;
    private int defaultButtonColor;

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
        defaultButtonColor = binding.answer1.getBackgroundTintList().getDefaultColor();

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
                    answers[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateAnswer(answers[finalI]);
                        }
                    });
                }
            }
        });
    }

    private void updateQuestion(Question question){

        android.widget.Button[] answers = {binding.answer1, binding.answer2, binding.answer3,
                binding.answer4, binding.answer5};

        binding.question.setText(question.getQuestion());
        for(int i = 0; i < question.getChoiceList().size(); i++){
            answers[i].setBackgroundColor(defaultButtonColor);
            answers[i].setText(question.getChoiceList().get(i));
        }

        for(int i = answers.length; i > question.getChoiceList().size(); i--){
            answers[i-1].setVisibility(View.GONE);
        }

        binding.next.setText("NEXT");
        binding.next.setEnabled(false);
    }

    private void updateAnswer(android.widget.Button button){

        if (button.getBackgroundTintList() != null) {
            int currentColor = button.getBackgroundTintList().getDefaultColor();

            if (currentColor == defaultButtonColor) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#000000")));
            } else {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultButtonColor));
            }
        }
        checkIfNextShouldBeEnabled();
        enableAllAnswers(true);
    }

    private void enableAllAnswers(Boolean enable){
        List<Button> allAnswers = Arrays.asList(binding.answer1, binding.answer2, binding.answer3,
                binding.answer4, binding.answer5);
        allAnswers.forEach( answer -> {
            answer.setEnabled(enable);
        });
    }

    private void checkIfNextShouldBeEnabled() {
        boolean isAnySelected = false;
        List<Button> allAnswers = Arrays.asList(binding.answer1, binding.answer2, binding.answer3,
                binding.answer4, binding.answer5);

        for (Button button : allAnswers) {
            if (button.getVisibility() == View.VISIBLE &&
                    button.getBackgroundTintList() != null &&
                    button.getBackgroundTintList().getDefaultColor() == Color.parseColor("#000000")) {
                isAnySelected = true;
                break;
            }
        }

        if (isAnySelected){
            binding.next.setTextColor(Color.WHITE);
        } else {
            binding.next.setTextColor(Color.GRAY);
        }

        binding.next.setEnabled(isAnySelected);
    }

}