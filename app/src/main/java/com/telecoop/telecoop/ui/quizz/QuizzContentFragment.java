package com.telecoop.telecoop.ui.quizz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telecoop.telecoop.data.Question;
import com.telecoop.telecoop.databinding.FragmentQuizzContentBinding;
import com.telecoop.telecoop.injection.ViewModelFactory;

public class QuizzContentFragment extends Fragment {

    private QuizzViewModel viewModel;
    private FragmentQuizzContentBinding binding;

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
        viewModel.currentQuestion.observe(getViewLifecycleOwner(), new Observer<Question>() {
            @Override
            public void onChanged(Question question) {
                updateQuestion(question);
            }
        });
    }

    private void updateQuestion(Question question){

        android.widget.Button[] answers = {binding.answer1, binding.answer2, binding.answer3,
                binding.answer4, binding.answer5};

        binding.question.setText(question.getQuestion());
        for(int i = 0; i < question.getChoiceList().size(); i++){
            answers[i].setText(question.getChoiceList().get(i));
        }

        for(int i = answers.length; i > question.getChoiceList().size(); i--){
            answers[i-1].setVisibility(View.GONE);
        }

        binding.next.setText("NEXT");
    }

}