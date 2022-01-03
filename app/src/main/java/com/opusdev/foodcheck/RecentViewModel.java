package com.opusdev.foodcheck;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
