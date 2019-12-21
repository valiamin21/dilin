package ir.proglovving.dilin.views.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import ir.proglovving.dilin.R;

public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("my slider");
        sliderPage.setDescription("This is dilin a wonderful app");
        sliderPage.setImageDrawable(R.drawable.programmer_profile_picture);
        sliderPage.setBgColor(ContextCompat.getColor(this,R.color.primary_light));
        addSlide(AppIntroFragment.newInstance(sliderPage));

    }
}
