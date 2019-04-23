package com.example.amin.dictionande.views.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.amin.dictionande.R;

public class BottomSheetActivity extends AppCompatActivity {

    private static final String TEXT_OPEN_BOTTOM_SHEET = "open bottom sheet";
    private static final String TEXT_CLOSE_BOTTOM_SHEET = "close bottom sheet";


    Button b, bHalf;
    BottomSheetBehavior behavior;
    NestedScrollView nestedView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet);

        setupViews();

        behavior = BottomSheetBehavior.from(nestedView);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        bHalf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            }
        });


        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        b.setText(TEXT_CLOSE_BOTTOM_SHEET);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        b.setText(TEXT_OPEN_BOTTOM_SHEET);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

    }

    private void setupViews() {
        b = (Button) findViewById(R.id.btn);
        b.setText(TEXT_OPEN_BOTTOM_SHEET);
        bHalf = (Button) findViewById(R.id.btn_half);
        nestedView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
