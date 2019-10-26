package ir.proglovving.dilin.custom_views;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import ir.proglovving.dilin.Utilities;

public class CusEditText extends AppCompatEditText {
    public CusEditText(Context context) {
        super(context);
        setTypeface();
    }

    public CusEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public CusEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface();
    }

    private void setTypeface() {
        if (!isInEditMode()) {
            setTypeface(Utilities.getAppTypeFace(getContext()));
        }
    }
}
