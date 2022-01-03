package com.opusdev.foodcheck;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> selected = new MutableLiveData<String>();

    public void change(String item) {
        selected.setValue(item);
    }

    public LiveData<String> getSelected() {
        return selected;
    }

}
