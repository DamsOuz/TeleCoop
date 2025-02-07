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

        mText.setValue("Bienvenue dans ta nouvelle vie !");
        mTextName.setValue("Comment t'appelles-tu ?");
        mTextHintName.setValue("Marque ici ton prénom :)");
        mTextButton.setValue("NEXT");
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