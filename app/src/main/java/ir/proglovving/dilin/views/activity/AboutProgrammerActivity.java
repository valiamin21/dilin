package ir.proglovving.dilin.views.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.transition.Slide;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.views.RainbowParticleView;

public class AboutProgrammerActivity extends AppCompatActivity {

    private TextView otherAppsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmer_about_us);

        initViews();
        otherAppsTextView.setOnClickListener(v -> MainActivity.openDeveloperApps(this));
        setupEnterTransition();
    }

    private void initViews() {
        setupToolbar();
        RainbowParticleView rainbowParticleView = findViewById(R.id.particle_view);
        rainbowParticleView.setColors(new int[]{ContextCompat.getColor(this, R.color.colorPrimary), ContextCompat.getColor(this, R.color.colorAccent)});
        otherAppsTextView = findViewById(R.id.other_apps);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_about_us);
        Utilities.applyFontForToolbar(toolbar, this);
        setSupportActionBar(toolbar);
    }

    private void setupEnterTransition() {
        Slide slide;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide();
            slide.setDuration(700);
            slide.setInterpolator(new OvershootInterpolator());
            getWindow().setEnterTransition(slide);
        }
    }

    public static void start(Context context, Bundle optionsCompat) {
        context.startActivity(new Intent(context, AboutProgrammerActivity.class), optionsCompat);
    }
}