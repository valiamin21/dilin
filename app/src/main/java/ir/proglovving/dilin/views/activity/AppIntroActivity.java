package ir.proglovving.dilin.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;

import ir.proglovving.dilin.FirstTimeManager;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.views.fragment.IntroFragment;

public class AppIntroActivity extends AppIntro {

    public static void start(Context context){
        Intent starter = new Intent(context,AppIntroActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDoneText("فهمیدم");

        // hiding skipButton
        skipButton.setEnabled(false);
        skipButton.setAlpha(0);

        // changing doneButton Font
        doneButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utilities.applyFontForAView(doneButton, AppIntroActivity.this);
            }
        }, 1);

        IntroFragment introFragment = IntroFragment.newInstance(this,R.string.app_name,R.string.dilin_description,R.drawable.dilin_icon_web);
        addSlide(introFragment);

        introFragment = IntroFragment.newInstance(this,R.string.dilin_intro_tooltip_description,R.drawable.app_intro_image_tooltip);
        addSlide(introFragment);

        introFragment = IntroFragment.newInstance(this,R.string.widget,R.string.dilin_intro_widget_description,R.drawable.app_intro_image_widget);
        addSlide(introFragment);

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        MainActivity.start(this);
        FirstTimeManager.registerAsFirstTime(this);
        finish();
    }
}
