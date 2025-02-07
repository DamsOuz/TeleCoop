package com.telecoop.telecoop.ui.quizz;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.telecoop.telecoop.data.Question;
import com.telecoop.telecoop.data.QuestionRepository;

import java.util.List;

public class QuizzViewModel extends ViewModel {

    private QuestionRepository questionRepository;
    private List<Question> questions;
    private Integer currentQuestionIndex = 0;

    public QuizzViewModel(QuestionRepository questionRepository){
        this.questionRepository = questionRepository;
    }

    MutableLiveData<Question> currentQuestion = new MutableLiveData<Question>();
    MutableLiveData<Boolean> isLastQuestion = new MutableLiveData<Boolean>(false);

    public void startQuizz(){
        questions = questionRepository.getQuestions();
        currentQuestion.postValue(questions.get(0));
    }

    public void nextQuestion(){
        Integer nextIndex = currentQuestionIndex + 1;
        if(nextIndex >= questions.size()){
            return;
        } else if (nextIndex == questions.size() - 1){
            isLastQuestion.postValue(true);
        }
        currentQuestionIndex = nextIndex;
        currentQuestion.postValue(questions.get(nextIndex));
    }
}
