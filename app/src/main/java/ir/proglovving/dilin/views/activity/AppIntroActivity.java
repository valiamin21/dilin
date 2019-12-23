package ir.proglovving.dilin.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

import ir.proglovving.dilin.FirstTimeManager;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.views.fragment.IntroFragment;

public class AppIntroActivity extends AppIntro {

    public static void start(Context context){
        Intent starter = new Intent(context,AppIntroActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_app_intro);

        setDoneText("فهمیدم");

//        SliderPage sliderPage = new SliderPage();
//        sliderPage.setTitle("my slider");
//        sliderPage.setDescription("This is dilin a wonderful app");
//        sliderPage.setImageDrawable(R.drawable.programmer_profile_picture);
//        sliderPage.setBgColor(ContextCompat.getColor(this,R.color.primary_light));
//        addSlide(IntroFragment.newInstance(sliderPage));
//
//        sliderPage = new SliderPage();
//        sliderPage.setTitle("second title");
//        sliderPage.setDescription("it is second descriptions");
//        sliderPage.setImageDrawable(R.drawable.programmer_profile_picture1);
//        sliderPage.setBgColor(ContextCompat.getColor(this,R.color.colorAccent));
//        addSlide(IntroFragment.newInstance(sliderPage));

        IntroFragment introFragment = IntroFragment.newInstance("this my fucking title","this is my fucking description",R.drawable.programmer_profile_picture1);
        addSlide(introFragment);

        introFragment = IntroFragment.newInstance("second fucking title","second description",R.drawable.navigation_header_image);
        addSlide(introFragment);

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        MainActivity.start(this);
        FirstTimeManager.registerAsFirstTime(this);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        MainActivity.start(this);
        FirstTimeManager.registerAsFirstTime(this);
        finish();
    }
}
