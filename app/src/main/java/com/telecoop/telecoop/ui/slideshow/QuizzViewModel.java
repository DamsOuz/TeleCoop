package com.telecoop.telecoop.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QuizzViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public QuizzViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is quizz fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}