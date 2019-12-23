package ir.proglovving.dilin.views.activity;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
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

    private static final int BACKGROUND_REVEAL_TIME = 800;
    private static final int DILIN_ANIMATION_TIME = 950;
    private static final int STAY_IN_SPLASH_TIME = 0;

    private ImageView dilinImage, backgroundImage;
    private TextView dilinTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_splash);
        initViews();

        // starting animations for activity elements
        backgroundImage.postDelayed(new Runnable() {
            @Override
            public void run() {
                revealShowSplashBackground(BACKGROUND_REVEAL_TIME);
            }
        }, 1);

        showDilinIconAnimation(BACKGROUND_REVEAL_TIME, DILIN_ANIMATION_TIME);
        showDilinTextViewAnimation(BACKGROUND_REVEAL_TIME, DILIN_ANIMATION_TIME);

        // below is a thread for starting next activity
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(BACKGROUND_REVEAL_TIME + DILIN_ANIMATION_TIME + STAY_IN_SPLASH_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(FirstTimeManager.isFirstTime(SplashActivity.this)){
                    AppIntroActivity.start(SplashActivity.this);
                }else{
                    MainActivity.start(SplashActivity.this);
                }
            }
        }).start();

    }

    private void showDilinTextViewAnimation(int delayTime, final int animationTime) {
        new CountDownTimer(delayTime, delayTime) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                dilinTextView.setVisibility(View.VISIBLE);
                AnimationSet animationSet = new AnimationSet(true);

                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1f);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);

                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(scaleAnimation);

                animationSet.setDuration(animationTime);
                animationSet.setInterpolator(new BounceInterpolator());
                dilinTextView.startAnimation(animationSet);
            }
        }.start();

    }

    private void showBackgroundAnimationForLowApis(int animationTime) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 0.9f);
        alphaAnimation.setDuration(animationTime);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        alphaAnimation.setFillAfter(true);
        backgroundImage.startAnimation(alphaAnimation);
    }

    private void showDilinIconAnimation(int delayTime, final int animationTime) {
        new CountDownTimer(delayTime, delayTime) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                dilinImage.setVisibility(View.VISIBLE);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, .5F, Animation.RELATIVE_TO_SELF, .5F);
                scaleAnimation.setDuration(animationTime);
                scaleAnimation.setInterpolator(new BounceInterpolator());
                dilinImage.startAnimation(scaleAnimation);
            }
        }.start();

    }

    private void initViews() {
        dilinImage = findViewById(R.id.img_dilin_icon);
        backgroundImage = findViewById(R.id.img_splash_background);
        dilinTextView = findViewById(R.id.tv_dilin);
    }

    private void makeFullScreen() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    private void revealShowSplashBackground(int animationTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = backgroundImage.getMeasuredWidth() / 2;
            int cy = backgroundImage.getMeasuredHeight() / 2;
            int finalRadius = Math.max(backgroundImage.getWidth(), backgroundImage.getHeight());
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    backgroundImage, cx, cy, 0, finalRadius
            );
            animator.setDuration(animationTime);
            animator.setInterpolator(new AccelerateInterpolator());
            backgroundImage.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            showBackgroundAnimationForLowApis(animationTime);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
