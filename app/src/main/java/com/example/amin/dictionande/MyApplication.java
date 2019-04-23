package com.example.amin.dictionande;

import android.app.Application;
import android.graphics.Typeface;

public class MyApplication extends Application {

    // TODO: 2/8/19 فونت برنامه را درست کن

    @Override
    public void onCreate() {
        super.onCreate();

    }

    // بست پرکتیس بودن کد زیر بررسی شود
    @Override
    public void onTerminate() {
        TTSManager.destroyTTS();
        super.onTerminate();
    }

    // TODO: 12/5/18 these typefaces code should be modified

    public Typeface getEnglishTypeface(){


        return Typeface.DEFAULT;
    }

    public Typeface getPersianTypeface(){

        return Typeface.DEFAULT;
    }


}
