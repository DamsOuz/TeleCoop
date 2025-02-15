package com.telecoop.telecoop.ui.quizz;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.telecoop.telecoop.data.Question;
import com.telecoop.telecoop.data.QuestionRepository;

import java.util.List;

public class QuizzViewModel extends ViewModel {
    private final QuestionRepository questionRepository;
    private List<Question> questions;

    // LiveData qui contiendra la question courante
    public MutableLiveData<Question> currentQuestion = new MutableLiveData<>();

    public QuizzViewModel(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public void startQuizz() {
        questions = questionRepository.getQuestions();
        if (questions != null && !questions.isEmpty()) {
            currentQuestion.postValue(questions.get(0));
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
