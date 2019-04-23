package com.example.amin.dictionande.views.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.amin.dictionande.R;

public class ActivityA extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        imageView = (ImageView)findViewById(R.id.iv);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.transition);
            getWindow().setExitTransition(transition);
        }
//
//        Button button = (Button)findViewById(R.id.btn_exit);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(ActivityA.this,null);
//                startActivity(new Intent(ActivityA.this,ActivityB.class),compat.toBundle());
//            }
//        });


        if (Build.VERSION.SDK_INT >= 21) {
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition transition = inflater.inflateTransition(R.transition.shared_element_example);
            getWindow().setSharedElementExitTransition(transition);
        }else{
            showApiSnackbar();
        }

        Button button = (Button)findViewById(R.id.btn_exit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat compat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(ActivityA.this,imageView,"shared");
                startActivity(new Intent(ActivityA.this,ActivityB.class),compat.toBundle());
            }
        });

    }


    private void showApiSnackbar() {
        Toast.makeText(this, "your api is below 21", Toast.LENGTH_SHORT).show();
    }

}
