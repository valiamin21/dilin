package ir.proglovving.dilin.views.activity;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import ir.proglovving.dilin.FirstTimeManager;
import ir.proglovving.dilin.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirstTimeManager.isFirstTime(SplashActivity.this)) {
            AppIntroActivity.start(SplashActivity.this, false);
        } else {
            MainActivity.start(SplashActivity.this);
        }

        finish();
    }
}