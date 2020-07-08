package ir.proglovving.cfviews;

import android.content.Context;
import android.graphics.Typeface;

public class CTypefaceProvider {
    public static final int VAZIR_NORMAL = 0;
    public static final int VAZIR_LIGHT = 1;
    public static final int VAZIR_BOLD = 2;

    private static Typeface vazir;
    private static Typeface vazirLight;
    private static Typeface vazirBold;

    public static Typeface getVazir(Context context) {
        if (vazir == null) {
            vazir = Typeface.createFromAsset(context.getAssets(), "fonts/Vazir.ttf");
        }
        return vazir;
    }

    public static Typeface getVazirLight(Context context) {
        if (vazirLight == null) {
            vazirLight = Typeface.createFromAsset(context.getAssets(), "fonts/Vazir-Light.ttf");
        }
        return vazirLight;
    }

    public static Typeface getVazirBold(Context context) {
        if (vazirBold == null) {
            vazirBold = Typeface.createFromAsset(context.getAssets(), "fonts/Vazir-Bold.ttf");
        }
        return vazirBold;
    }

}
