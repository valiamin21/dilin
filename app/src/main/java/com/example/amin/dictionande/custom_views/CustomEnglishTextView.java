package com.example.amin.dictionande.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.example.amin.dictionande.MyApplication;

public class CustomEnglishTextView extends android.support.v7.widget.AppCompatTextView {


    public CustomEnglishTextView(Context context) {
        super(context);
        setTypeface();
    }

    public CustomEnglishTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public CustomEnglishTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface();
    }

    private void setTypeface(){
        if(!isInEditMode()){
            MyApplication application = (MyApplication) getContext().getApplicationContext();
            setTypeface(application.getEnglishTypeface());
        }
    }
}
