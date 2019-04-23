package com.example.amin.dictionande.views.activity;

import android.os.Build;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.transition.Explode;
import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.amin.dictionande.R;

public class TransitionActivity extends AppCompatActivity {

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);

        linearLayout = findViewById(R.id.linear_layout);

        final Button
                hideButton = findViewById(R.id.btn_hide),
                showButton = findViewById(R.id.btn_show),
                changeSizeButton = findViewById(R.id.btn_change_size);
        final ImageView iv = findViewById(R.id.iv);

        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 19) {
//                    Slide s = new Slide();
//                    s.setDuration(1500);
//                    TransitionManager.beginDelayedTransition(linearLayout,s);
                    Explode explode = new Explode();
                    TransitionManager.beginDelayedTransition(linearLayout,explode);
                    if (iv.getVisibility() == View.VISIBLE) {
                        iv.setVisibility(View.INVISIBLE);
                    }
                } else {
                    showApiSnack();
                }
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 19) {
//                    Fade f = new Fade();
//                    f.setDuration(1000);
                    Explode e = new Explode();
                    e.setDuration(700);
                    TransitionManager.beginDelayedTransition(linearLayout,e);
                    if (iv.getVisibility() == View.INVISIBLE) {
                        iv.setVisibility(View.VISIBLE);
                    }
                } else {
                    showApiSnack();
                }
            }
        });

        changeSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 19) {
                    TransitionManager.beginDelayedTransition(linearLayout);
                    ViewGroup.LayoutParams params = iv.getLayoutParams();
                    int h = iv.getHeight();
                    int w = iv.getWidth();
                    params.height = h / 2;
                    params.width = w / 2;
                    iv.setLayoutParams(params);
                }else{
                    showApiSnack();
                }
            }
        });
    }

    private void showApiSnack() {
        Snackbar.make(linearLayout, "Seems like the api is below 190", BaseTransientBottomBar.LENGTH_SHORT).show();
    }
}
