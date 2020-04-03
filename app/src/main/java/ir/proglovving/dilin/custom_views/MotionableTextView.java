package ir.proglovving.dilin.custom_views;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;

import ir.proglovving.dilin.R;

public class MotionableTextView extends MagicTextView {
    private static final int ADDING_NEXT_CHAR_TIME = 115;
    private static final int REFRESHING_IGNORE_TIME = 5000;
    private static final int REFRESHING_IGNORE_COUNT = REFRESHING_IGNORE_TIME / ADDING_NEXT_CHAR_TIME;
    private static final int[] colors = new int[]{Color.RED, Color.parseColor("#FF198D"), Color.BLUE};

    private String text;
    private int currentCharPosition = 0;
    private int keepTextCounter = 5000;
    // TODO: 8/5/19 disable this timer after destroying view
    CountDownTimer addCharTimer;


    public MotionableTextView(Context context) {
        super(context);
        initializeWhenConstruct();
    }

    public MotionableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeWhenConstruct();
    }

    public MotionableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeWhenConstruct();
    }

    private void initializeWhenConstruct() {
        setStroke(1, ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
//        setShadowLayer(1,2,2,ContextCompat.getColor(getContext(),R.color.colorPrimary));

        setTextColor(getColor());
        text = getText().toString();
        refreshText();

        addCharTimer = new CountDownTimer(999999999, ADDING_NEXT_CHAR_TIME) {

            @Override
            public void onTick(long millisUntilFinished) {
                refreshText();
            }

            @Override
            public void onFinish() {

            }
        };
        addCharTimer.start();
    }

    public void changeText(String text) {
        this.text = text;
        currentCharPosition = 0;
        refreshText();
    }

    public void changeText(@StringRes int resid) {
        changeText(getContext().getString(resid));
    }

    private void refreshText() {
        if (getText().toString().equals(text)) {
            if (keepTextCounter <= REFRESHING_IGNORE_COUNT) { // در واقع با این کار سیزده بار از فراخوانده شدن تایمر صرف نظر می کنیم و متن به طور ثابت می ماند(در صورت کامل شدن متن)
                keepTextCounter++;
                return;
            } else {
                keepTextCounter = 0;
            }
        }

        if (currentCharPosition == 0) {
            setTextColor(getColor());
        }
        setText(text.substring(0, currentCharPosition));
        currentCharPosition++;

        if (currentCharPosition == text.length() + 1) {
            currentCharPosition = 0;
        }
    }

    private int colorCounter = 0;

    private int getColor() {
        if (colorCounter == colors.length) {
            colorCounter = 0;
        }
        int color = colors[colorCounter];
        colorCounter++;
        return color;
    }

}
