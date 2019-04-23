package com.example.amin.dictionande.views.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.animation.OvershootInterpolator;

import com.example.amin.dictionande.R;

public class About_Us_Programmer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about__us__programmer);

        setupEnterTransition();
    }

    private void setupEnterTransition() {
        Slide slide = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide();
            slide.setDuration(700);
            slide.setInterpolator(new OvershootInterpolator());
            getWindow().setEnterTransition(slide);
        }
    }
}
