package ir.proglovving.dilin.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;

import ir.proglovving.dilin.FirstTimeManager;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.views.fragment.IntroFragment;

public class AppIntroActivity extends AppIntro {
    private static String INTENT_KEY_START_FROM_NAVIGATION_VIEW = "start_from_navigation_view";

    public static void start(Context context,boolean startFromNavigationView){
        Intent starter = new Intent(context,AppIntroActivity.class);
        starter.putExtra(INTENT_KEY_START_FROM_NAVIGATION_VIEW,startFromNavigationView);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // making full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setDoneText("فهمیدم");

        // hiding skipButton
        skipButton.setEnabled(false);
        skipButton.setAlpha(0);

        IntroFragment introFragment = IntroFragment.newInstance(this,R.string.app_name,R.string.dilin_description,R.drawable.dilin_icon_web,0);
        addSlide(introFragment);

        introFragment = IntroFragment.newInstance(this,R.string.facility,R.string.dilin_intro_tooltip_description,R.drawable.app_intro_image_tooltip,1);
        addSlide(introFragment);

        introFragment = IntroFragment.newInstance(this,R.string.widget,R.string.dilin_intro_widget_description,R.drawable.app_intro_image_widget,2);
        addSlide(introFragment);

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        if(!getIntent().getBooleanExtra(INTENT_KEY_START_FROM_NAVIGATION_VIEW,false)){
            MainActivity.start(this);
            FirstTimeManager.registerAsFirstTime(this);
        }
        finish();
    }
}
