package com.telecoop.telecoop.data;

import java.util.List;

public class AnswerChoice {
    private final String text;
    private final List<Profile> associatedProfiles;

    public AnswerChoice(String text, List<Profile> associatedProfiles) {
        this.text = text;
        this.associatedProfiles = associatedProfiles;
    }

    public String getText() {
        return text;
    }

    public List<Profile> getAssociatedProfiles() {
        return associatedProfiles;
    }
}
