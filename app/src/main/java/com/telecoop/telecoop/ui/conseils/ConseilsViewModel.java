package com.telecoop.telecoop.ui.conseils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConseilsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ConseilsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is conseils fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}