package ir.proglovving.cfviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class CfEditText extends AppCompatEditText {
    public CfEditText(Context context) {
        super(context);
    }

    public CfEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView(attrs);
    }

    public CfEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView(attrs);
    }

    private void setupView(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.CfViewsAttributes);

        try {
            int font = attributes.getInteger(R.styleable.CfViewsAttributes_cusfont, CTypefaceProvider.VAZIR_NORMAL);
            switch (font) {
                case CTypefaceProvider.VAZIR_NORMAL:
                    setTypeface(CTypefaceProvider.getVazir(getContext()));
                    break;
                case CTypefaceProvider.VAZIR_LIGHT:
                    setTypeface(CTypefaceProvider.getVazirLight(getContext()));
                    break;
                case CTypefaceProvider.VAZIR_BOLD:
                    setTypeface(CTypefaceProvider.getVazirBold(getContext()));
                    break;
            }
        } finally {
            invalidate();
            requestLayout();
            attributes.recycle();
        }
    }
}
