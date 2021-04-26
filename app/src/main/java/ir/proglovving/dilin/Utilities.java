package ir.proglovving.dilin;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityOptionsCompat;
import androidx.appcompat.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import ir.proglovving.dilin.views.activity.MainActivity;
import ir.proglovving.dilin.views.activity.SplashActivity;
import ir.proglovving.dilin.widgets.WordsWidget;

public class Utilities {

    public static String convertNumberToPersian(int input) {
        return String.valueOf(input)
                .replace('0', '۰')
                .replace('1', '۱')
                .replace('2', '۲')
                .replace('3', '۳')
                .replace('4', '۴')
                .replace('5', '۵')
                .replace('6', '۶')
                .replace('7', '۷')
                .replace('8', '۸')
                .replace('9', '۹');
    }

    public static boolean existsInArray(String[] ss, String input) {
        for (String s : ss) {
            if (s.equals(input)) {
                return true;
            }
        }

        return false;
    }

    public static void applyPaddintBottomForToolbarSubtitle(Context context, Toolbar toolbar) {
        View view;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                if (((TextView) view).getText().equals(toolbar.getSubtitle())) {
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(),
                            view.getPaddingBottom() + (int) context.getResources().getDimension(R.dimen.app_toolbar_bottom_padding)
                    );
                }
            }
        }
    }

    public static void updateShowWordsWidget(Context context) {
        Intent intent = new Intent(context, WordsWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, WordsWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    // TODO: 4/21/19 برای همه ی اکتیویتی ها از فانکشن های زیر استفاده شود(در آپدیت های بعدی)
    public static void setupExitTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Transition transition = new Fade();

            if (activity instanceof SplashActivity) {
                transition = new Slide();
                transition.setDuration(1000);
                transition.setInterpolator(new DecelerateInterpolator());
            } else if (activity instanceof MainActivity) {
                transition = new Fade();
                transition.setDuration(500);

                activity.getWindow().setReturnTransition(transition);
            }


            activity.getWindow().setExitTransition(transition);
        }
    }

    public static Bundle getActivityCompatToBundle(Activity activity) {
        return
                ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, null)
                        .toBundle();
    }

    public static void hideSoftKeyboard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void showSoftKeyboard(View view, Context context) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
