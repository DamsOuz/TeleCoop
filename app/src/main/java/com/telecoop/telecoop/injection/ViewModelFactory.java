package com.telecoop.telecoop.injection;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.telecoop.telecoop.data.QuestionRepository;
import com.telecoop.telecoop.data.QuestionBank;
import com.telecoop.telecoop.ui.quizz.QuizzViewModel;

import org.jetbrains.annotations.NotNull;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final QuestionRepository questionRepository;
    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                }
            }
        }
        return factory;
    }

    private ViewModelFactory() {
        QuestionBank questionBank = QuestionBank.getInstance();
        this.questionRepository = new QuestionRepository(questionBank);
    }

    @Override
    @NotNull
    public <T extends ViewModel>  T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QuizzViewModel.class)) {
            return (T) new QuizzViewModel(questionRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}