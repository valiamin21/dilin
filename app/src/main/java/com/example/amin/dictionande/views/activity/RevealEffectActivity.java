package com.example.amin.dictionande.views.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.amin.dictionande.R;

public class RevealEffectActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private LinearLayout ll;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveal_effect);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        ll = (LinearLayout) findViewById(R.id.show);
        image = (ImageView) findViewById(R.id.image);

        fab.setOnClickListener(this);
        image.postDelayed(new Runnable() {
            @Override
            public void run() {
                revealEffectImage();
            }
        }, 1000);
        fab.postDelayed(new Runnable() {
            @Override
            public void run() {
                revealEffectFab();
            }
        }, 1500);


    }

    @SuppressLint("RestrictedApi")
    private void revealEffectFab() {
        if (Build.VERSION.SDK_INT > 20) {
            int cx = fab.getMeasuredWidth() / 2;
            int cy = fab.getMeasuredHeight() / 2;
            int finalRadis = Math.max(fab.getWidth(),
                    fab.getHeight());
            Animator fabReveal =
                    ViewAnimationUtils.createCircularReveal(fab,
                            cx,cy,0,finalRadis);
            fabReveal.setDuration(1000);
            fabReveal.setInterpolator(new DecelerateInterpolator());
            fab.setVisibility(View.VISIBLE);
            fabReveal.start();
        }
    }

    private void revealEffectImage() {
        if (Build.VERSION.SDK_INT > 20) {
            int cx = (image.getLeft() + image.getRight()) / 2;
            int cy = (image.getTop() + image.getBottom()) / 2;
            int finalRadius = (int) Math.hypot(image.getWidth(),
                    image.getHeight());
            Animator imageReveal =
                    ViewAnimationUtils.createCircularReveal(image,
                            cx, cy, 0, finalRadius);
            imageReveal.setDuration(1000);
            imageReveal.setInterpolator(new DecelerateInterpolator());
            image.setVisibility(View.VISIBLE);
            imageReveal.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:

                if (Build.VERSION.SDK_INT > 20) {
                    int cx = image.getRight();
                    int cy = image.getBottom();
                    int finalRadius = (int) Math.hypot(image.getWidth(),
                            image.getHeight());
                    Animator animator =
                            ViewAnimationUtils.createCircularReveal(ll,
                                    cx, cy, 0, finalRadius);
                    animator.setDuration(500);
                    animator.setInterpolator(new DecelerateInterpolator());
                    ll.setVisibility(View.VISIBLE);
                    animator.start();
                }

                break;
        }
    }

//    @SuppressLint("RestrictedApi")
//    private void revealEffect() {
//        if (Build.VERSION.SDK_INT > 20) {
//            int cx = fab.getMeasuredWidth() / 2;
//            int cy = fab.getMeasuredHeight() / 2;
//            int finalRadius = Math.max(fab.getWidth(), fab.getHeight());
//            Animator animator =
//                    ViewAnimationUtils.createCircularReveal(fab,
//                            cx,cy,0,finalRadius);
//            animator.setDuration(1000);
//            animator.setInterpolator(new OvershootInterpolator());
//            fab.setVisibility(View.VISIBLE);
//            animator.start();
//        }
//    }
}
