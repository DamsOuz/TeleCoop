package com.telecoop.telecoop.ui.quizz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.telecoop.telecoop.R;
import com.telecoop.telecoop.data.AnswerChoice;
import com.telecoop.telecoop.data.Question;
import com.telecoop.telecoop.data.QuestionBank;
import com.telecoop.telecoop.databinding.FragmentWelcomequizzBinding;

import java.util.ArrayList;

public class QuizzWelcomeFragment extends Fragment {

    private FragmentWelcomequizzBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWelcomequizzBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        QuizzWelcomeViewModel quizzViewModel =
                new ViewModelProvider(this).get(QuizzWelcomeViewModel.class);

        final TextView textView = binding.textWelcomequizz;
        final TextView textViewName = binding.textFirstname;
        final EditText textViewHintName = binding.textHintname;
        final TextView textButton = binding.textButtonFirstpageQuizz;
        final TextView textBasPageButton = binding.textBasWelcomequizz;

        quizzViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        quizzViewModel.getTextName().observe(getViewLifecycleOwner(), textViewName::setText);
        quizzViewModel.getTextHintName().observe(getViewLifecycleOwner(), textViewHintName::setHint);
        quizzViewModel.getTextButton().observe(getViewLifecycleOwner(), textButton::setText);
        quizzViewModel.getTextBasDePage().observe(getViewLifecycleOwner(), textBasPageButton::setText);

        // Restaure le nom de l'utilisateur s'il a été sauvegardé
        SharedPreferences prefs = requireActivity().getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
        String savedName = prefs.getString("userName", "");
        if (!savedName.isEmpty()) {
            binding.textHintname.setText(savedName);
            binding.textFirstname.setText(savedName);
            binding.textButtonFirstpageQuizz.setEnabled(true);
        }

        binding.textHintname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Rien */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { /* Rien */ }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = s.toString().isEmpty();
                binding.textButtonFirstpageQuizz.setEnabled(!isEmpty);
                saveUserName(s.toString());
            }
        });

        binding.textButtonFirstpageQuizz.setOnClickListener(v -> {
            String userName = textViewHintName.getText().toString().trim();

            QuestionBank qb = QuestionBank.getInstance();
            // Ajouter la question personnalisée dans le QuestionBank
            Question addNameQuestion = qb.getAddNameQuestion("", userName,
                    ",\n\nNous allons te poser" + qb.getBaseQuestionCount() + "questions afin de mieux connaître tes besoins et tes priorités.\n\nN'hésite pas à cocher plusieurs réponses si elles te semblent toutes pertinentes !",
                    new ArrayList<AnswerChoice>());


            if (!qb.getQuestions().contains(addNameQuestion)){
                qb.addNameQuestion("", userName,
                        ",\n\nNous allons te poser " + qb.getBaseQuestionCount() + " questions afin de mieux connaître tes besoins et tes priorités.\n\nN'hésite pas à cocher plusieurs réponses si elles te semblent toutes pertinentes !",
                        0,
                        new ArrayList<AnswerChoice>());
            }

            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_quizzWelcomeFragment_to_quizzContentFragment);
            Log.d("Damien", "Navigation vers QuizzContentFragment !");
        });
    }

    private void saveUserName(String userName) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userName", userName);
        editor.apply();
    }

    @Override
    public void onStart(){
        super.onStart();
        String currentName = binding.textHintname.getText().toString();
        binding.textButtonFirstpageQuizz.setEnabled(!currentName.isEmpty());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
