package ir.proglovving.dilin;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class MyApplication extends Application {
    /*
    ideas for app:
        converting html pages to pdf and make a beauty html page and put vocs values in that and get pdf export for backup from notebooks
        قرار دادن امکان مرور لغات دفترها با کارت‌ها
        ساختن لیست کلمات آماده و لود کردن آن از سرور
        قابلیت لاگین کردن کاربران و ذخیره‌ی فعالیت کاربران در برنامه
        قرار دادن امکان انتشار دفترهای زبان توسط کاربران
     */

    // releasing process
    // TODO: 3/1/20 prevent adding a word from dictionary to notebook if the word already existed in notebook
    // TODO: 9/7/19 releasing app in markets


    // TODO: 9/7/19 replacing landscape images with abstract images
    // TODO: 7/20/19 bug fix: اصلاح بخش جست و جو. زمانی که کاربر در بین کلمات سرچ می کند و در همانجا کلمه ای را ویرایش می کند مشکل ایجاد می شود.
    // TODO: 8/2/19 modify motionable textView
    // TODO: 7/24/19 اضافه کردن قابلیت تغییر نام دفترها
    // TODO: 8/10/19 modifying fragments of dilin(best practice)
    // TODO: 12/18/19 making a custom recyclerView for app that saves last position
    // TODO: 8/5/19 علی بدیعی: در کل نباید اسم برنامه‌رو مستقیم بنویسی(توی صفحه‌ی اسپلش) بهتره که با یه فونت قشنگ بنویسی* عکس بگیری* بعد بچسبونی رو عکس والپیپر
    // TODO: 8/2/19 modifying text color of dialogs
    // TODO: 7/28/19  Color of app decreasing transparency of toolbar
    // TODO: 7/28/19  Changing icon colors
    // TODO: 7/28/19 دوزبانه کردن برنامه
    // TODO: 7/28/19  Putting listView in dilin widget
    // TODO: 7/28/19  Using notification for reviewing words
    // TODO: 7/28/19  Using notification for reminding user to review his/her words
    // TODO: 7/27/19 modifying transitions of app (between activities or inside activities)
    // TODO: 7/24/19 modifying toolbars height because subtitles are overflowed
    // TODO: 7/24/19 قرار دادن قابلیت دکمه‌ی کنسل در customDialog
    // TODO: 7/24/19 اصلاح صفحه‌ی کمک مالی
    // TODO: 7/24/19 اصلاح و سبک‌تر کردن آیتم‌های ریسایکلرویوها، همچنین قرار دادن پیجینیشن در ریسایکلرویوها
    // TODO: 7/19/19 making custom toast for this app
    // TODO: 7/24/19 modify transitions between activities of app

    private static Typeface appTypeface;

    private static TextToSpeech textToSpeech;
    private static boolean isTTSReady;

    @Override
    public void onCreate() {
        super.onCreate();
        initTTS();
    }

    // TODO: 8/9/19   بست پرکتیس بودن کد زیر بررسی شود
    @Override
    public void onTerminate() {
        clearTTS();
        TTSManager.destroyTTS();
        super.onTerminate();
    }


    private void initTTS() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    isTTSReady = true;
                } else {
                    isTTSReady = false;
                    // TODO: 4/17/19 show an error text
                }
            }
        });
    }

    public static void clearTTS() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            isTTSReady = false;
        }
    }

    public static void speechWord(String word, Locale loc, Context context) {
        if (textToSpeech == null) {
            ((MyApplication) context.getApplicationContext()).initTTS();
        }

        if (isTTSReady) {
            int ttsLang = textToSpeech.setLanguage(loc);
            if (ttsLang == TextToSpeech.LANG_MISSING_DATA ||
                    ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                // TODO: 8/9/19 show an error like : زبان انگلیسی پشتیبانی نمی شود.
            } else {
                // TODO: 8/9/19 modify this deprecated method
                textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    public Typeface getAppTypeface() {
        if (appTypeface == null) {
            appTypeface = Typeface.createFromAsset(getAssets(), "fonts/Vazir.ttf");
        }

        return appTypeface;
    }
}