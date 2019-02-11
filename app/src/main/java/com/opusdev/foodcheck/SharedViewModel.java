package com.opusdev.foodcheck;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ClipData;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> selected = new MutableLiveData<String>();

    public void change(String item) {
        selected.setValue(item);
    }

    public LiveData<String> getSelected() {
        return selected;
    }

}
