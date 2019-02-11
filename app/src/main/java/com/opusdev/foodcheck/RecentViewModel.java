package com.opusdev.foodcheck;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

public class RecentViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<String>> array = new MutableLiveData<ArrayList<String>>();
    public void add(ArrayList<String> s){
        array.postValue(s);
    }
    public LiveData<ArrayList<String>> get(){
        return array;
    }
}
