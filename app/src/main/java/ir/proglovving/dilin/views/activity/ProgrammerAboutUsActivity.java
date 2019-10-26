package ir.proglovving.dilin.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.animation.OvershootInterpolator;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;

public class ProgrammerAboutUsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmer_about_us);

        initViews();

        setupEnterTransition();
    }

    private void initViews() {
        setupToolbar();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_about_us);
        Utilities.applyFontForToolbar(toolbar,this);
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

    public static void start(Context context,Bundle optionsCompat){
        context.startActivity(new Intent(context, ProgrammerAboutUsActivity.class),optionsCompat);
    }
}