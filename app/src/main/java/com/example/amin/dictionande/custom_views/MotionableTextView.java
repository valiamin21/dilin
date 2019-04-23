package com.example.amin.dictionande.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.example.amin.dictionande.R;

public class MotionableTextView extends MagicTextView {
    private String text;
    private int currentPosition = 0;
    private int keepTextCounter = 0;
    CountDownTimer addCharTimer;



    public MotionableTextView(Context context) {
        super(context);
        initializeWhenCustruct();
    }

    public MotionableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeWhenCustruct();
    }

    public MotionableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeWhenCustruct();
    }

    public void changeText(String text){
        this.text = text;
        currentPosition = 0;
        refreshText();
    }

    public void changeText(@StringRes int resid){
        this.text = getContext().getString(resid);
        currentPosition = 0;
        refreshText();
    }

    private void initializeWhenCustruct(){

        setStroke(1,ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));
//        setShadowLayer(1,2,2,ContextCompat.getColor(getContext(),R.color.colorPrimary));

        setTextColor(getColor());
        text = getText().toString();
        refreshText();

        addCharTimer = new CountDownTimer(999999999, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                refreshText();
            }

            @Override
            public void onFinish() {

            }
        };
        addCharTimer.start();
    }

    private void refreshText() {
        if(getText().toString().equals(text)){
            if(keepTextCounter <= 13){ // در واقع با این کار سیزده بار از فراخوانده شدن تایمر صرف نظر می کنیم و متن به طور ثابت می ماند(در صورت کامل شدن متن)
                keepTextCounter ++;
                return;
            }else{
                keepTextCounter = 0;
            }
        }


        if(currentPosition == 0){
            setTextColor(getColor());
        }
        setText(text.substring(0, currentPosition));
        currentPosition++;

        if (currentPosition == text.length() + 1) {
            currentPosition = 0;
        }
    }

    private int colorCounter = 0;

    private int getColor() {
        colorCounter++;
        if(colorCounter == 5){
            colorCounter = 1;
        }

        switch (colorCounter) {
            case 1:
                return Color.parseColor("#FFAB22");

            case 2:
                return Color.RED;

            case 3:
                return Color.parseColor("#FF198D");

            case 4:
                return Color.BLUE;

                default:
                    return Color.BLACK;
        }
    }


}
