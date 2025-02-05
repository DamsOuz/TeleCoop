package com.telecoop.telecoop.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.telecoop.telecoop.databinding.FragmentQuizzBinding;

public class QuizzFragment extends Fragment {

    private FragmentQuizzBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        QuizzViewModel quizzViewModel =
                new ViewModelProvider(this).get(QuizzViewModel.class);

        binding = FragmentQuizzBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textQuizz;
        quizzViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}