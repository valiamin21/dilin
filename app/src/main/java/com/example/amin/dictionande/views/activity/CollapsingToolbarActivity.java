package com.example.amin.dictionande.views.activity;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.example.amin.dictionande.R;

public class CollapsingToolbarActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private CollapsingToolbarLayout ctl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collapsing_toolbar);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        ctl = (CollapsingToolbarLayout)findViewById(R.id.ctl);

        setSupportActionBar(toolbar);
        ctl.setTitle("The title");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_menu,menu);

        return true;
    }
}
