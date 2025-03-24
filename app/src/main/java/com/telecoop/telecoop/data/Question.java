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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Question other = (Question) obj;
        return question != null ? question.equals(other.question) : other.question == null;
    }

    @Override
    public int hashCode() {
        return question != null ? question.hashCode() : 0;
    }
}
