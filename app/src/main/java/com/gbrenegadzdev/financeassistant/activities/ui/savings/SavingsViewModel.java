package com.gbrenegadzdev.financeassistant.activities.ui.savings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SavingsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SavingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is savings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
