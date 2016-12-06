package com.textfield.json.outabus;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

/**
 * Created by Jason on 28/04/2016.
 */
public class MyTextWatcher implements TextWatcher {
    ArrayAdapter<?> arrayAdapter;
    public MyTextWatcher(ArrayAdapter<?> arrayAdapter){
        this.arrayAdapter = arrayAdapter;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        arrayAdapter.getFilter().filter(s.toString());
    }
}
