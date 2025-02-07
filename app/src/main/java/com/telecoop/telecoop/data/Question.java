package com.telecoop.telecoop.data;
import java.util.List;

public class Question {
    private final String question;
    private final List<String> choiceList;

    public Question(String question, List<String> choiceList) {
        this.question = question;
        this.choiceList = choiceList;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getChoiceList() {
        return choiceList;
    }
}
