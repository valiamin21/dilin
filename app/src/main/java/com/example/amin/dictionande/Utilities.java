package com.example.amin.dictionande;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.animation.DecelerateInterpolator;

import com.example.amin.dictionande.views.activity.ShowNoteBooksListActivity;
import com.example.amin.dictionande.views.activity.SplashActivity;
import com.example.amin.dictionande.widgets.ShowWordsWidget;

public class Utilities {
    // TODO: 2/5/19 متد های کاربردی پر تکرار در این کلاس نوشته شود(به صورت استاتیک)ـ

    public static void updageShowWordsWidget(Context context) {
        Intent intent = new Intent(context, ShowWordsWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, ShowWordsWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    // TODO: 4/21/19 برای همه ی اکتیویتی ها از فانکشن های زیر استفاده شود
    public static void setupExitTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Transition transition = new Fade();

            if (activity instanceof SplashActivity) {
                transition = new Slide();
                transition.setDuration(1000);
                transition.setInterpolator(new DecelerateInterpolator());
            } else if (activity instanceof ShowNoteBooksListActivity) {
                transition = new Fade();
                transition.setDuration(500);

                activity.getWindow().setReturnTransition(transition);
            }


            activity.getWindow().setExitTransition(transition);
        }
    }

    public static void setupEnterTransition(Activity activity){

    }

    public static Bundle getActivityCompatToBundle(Activity activity) {
        return
                ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, null)
                        .toBundle();
    }
}
