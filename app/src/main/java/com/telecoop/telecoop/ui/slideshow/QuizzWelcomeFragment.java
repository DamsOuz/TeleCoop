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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.telecoop.telecoop.R;
import com.telecoop.telecoop.databinding.FragmentWelcomequizzBinding;

public class QuizzWelcomeFragment extends Fragment {

    private FragmentWelcomequizzBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWelcomequizzBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        QuizzWelcomeViewModel quizzViewModel =
                new ViewModelProvider(this).get(QuizzWelcomeViewModel.class);

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
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_quizzWelcomeFragment_to_quizzContentFragment);
                Log.d("Damien", "Navigation vers QuizzContentFragment !");
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