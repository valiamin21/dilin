package com.example.amin.dictionande.custom_views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.example.amin.dictionande.MyApplication;

public class CustomPersianTextView extends AppCompatTextView {
    public CustomPersianTextView(Context context) {
        super(context);
        setTypeface();
    }

    public CustomPersianTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public CustomPersianTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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
