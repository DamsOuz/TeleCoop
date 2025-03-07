package com.telecoop.telecoop.data;
import java.util.List;

public class Question {
    private final String question;
    private final List<AnswerChoice> answerChoices;

    public Question(String question, List<AnswerChoice> answerChoices) {
        this.question = question;
        this.answerChoices = answerChoices;
    }

    public String getQuestion() {
        return question;
    }

    public List<AnswerChoice> getAnswerChoices() {
        return answerChoices;
    }
}
