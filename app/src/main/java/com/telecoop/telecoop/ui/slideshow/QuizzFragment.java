package com.telecoop.telecoop.ui.slideshow;

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

import com.telecoop.telecoop.databinding.FragmentQuizzBinding;

public class QuizzFragment extends Fragment {

    private FragmentQuizzBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentQuizzBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        QuizzViewModel quizzViewModel =
                new ViewModelProvider(this).get(QuizzViewModel.class);

        final TextView textView = binding.textWelcomequizz;
        final TextView textViewName = binding.textFirstname;
        final EditText textViewHintName = binding.textHintname;
        final TextView textButton = binding.textButtonFirstpageQuizz;

        quizzViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        quizzViewModel.getTextName().observe(getViewLifecycleOwner(), textViewName::setText);
        quizzViewModel.getTextHintName().observe(getViewLifecycleOwner(), textViewHintName::setHint);
        quizzViewModel.getTextButton().observe(getViewLifecycleOwner(), textButton::setText);

        binding.textHintname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = s.toString().isEmpty();
                binding.textButtonFirstpageQuizz.setEnabled(!isEmpty);
            }
        });

        binding.textButtonFirstpageQuizz.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO: naviguer vers le fragment de quizz
                Log.d("Damien", "Clic !");
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        binding.textButtonFirstpageQuizz.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}