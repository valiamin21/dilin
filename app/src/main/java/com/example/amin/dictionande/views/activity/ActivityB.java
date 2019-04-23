package com.example.amin.dictionande.views.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.example.amin.dictionande.R;

public class ActivityB extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);


        if (Build.VERSION.SDK_INT >= 21) {
            Slide slide = new Slide();
            slide.setDuration(700);
            slide.setInterpolator(new OvershootInterpolator());
            getWindow().setEnterTransition(slide);

            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.shared_element_example);
            getWindow().setSharedElementEnterTransition(transition);
        }

    }
}
