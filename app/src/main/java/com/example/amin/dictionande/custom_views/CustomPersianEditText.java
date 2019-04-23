package com.example.amin.dictionande.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.example.amin.dictionande.MyApplication;

public class CustomPersianEditText extends android.support.v7.widget.AppCompatEditText {


    public CustomPersianEditText(Context context) {
        super(context);
        setTypeface();
    }

    public CustomPersianEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public CustomPersianEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface();
    }

    private void setTypeface(){
        if(!isInEditMode()){
            MyApplication application = (MyApplication) getContext().getApplicationContext();
            setTypeface(application.getPersianTypeface());
        }
    }
}
