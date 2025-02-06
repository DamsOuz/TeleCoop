package com.telecoop.telecoop.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QuizzWelcomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mTextName;
    private final MutableLiveData<String> mTextHintName;
    private final MutableLiveData<String> mTextButton;

    public QuizzWelcomeViewModel() {
        mText = new MutableLiveData<>();
        mTextName = new MutableLiveData<>();
        mTextHintName = new MutableLiveData<>();
        mTextButton = new MutableLiveData<>();

        mText.setValue("Bienvenue sur le quizz TeleCoop !");
        mTextName.setValue("Comment tu t'appelles ?");
        mTextHintName.setValue("Marque ici ton prénom :)");
        mTextButton.setValue("C'est parti pour les questions !");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getTextName() {
        return mTextName;
    }

    public LiveData<String> getTextHintName() {
        return mTextHintName;
    }

    public LiveData<String> getTextButton() {
        return mTextButton;
    }
}