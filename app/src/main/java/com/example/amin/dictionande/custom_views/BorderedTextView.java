package com.example.amin.dictionande.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class BorderedTextView extends android.support.v7.widget.AppCompatTextView {
    private Paint paint = new Paint();
    public static final int BORDER_TOP = 0x00000001;
    public static final int BORDER_RIGHT = 0x00000002;
    public static final int BORDER_BOTTOM = 0x00000004;
    public static final int BORDER_LEFT = 0x00000008;

    private Border[] borders;

    public BorderedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BorderedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BorderedTextView(Context context) {
        super(context);
        init();
    }
    private void init(){
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(borders == null) return;

        for(Border border : borders){
            paint.setColor(border.getColor());
            paint.setStrokeWidth(border.getWidth());

            if(border.getStyle() == BORDER_TOP){
                canvas.drawLine(0, 0, getWidth(), 0, paint);
            } else
            if(border.getStyle() == BORDER_RIGHT){
                canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), paint);
            } else
            if(border.getStyle() == BORDER_BOTTOM){
                canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paint);
            } else
            if(border.getStyle() == BORDER_LEFT){
                canvas.drawLine(0, 0, 0, getHeight(), paint);
            }
        }
    }

    public Border[] getBorders() {
        return borders;
    }

    public void setBorders(Border[] borders) {
        this.borders = borders;
    }
}
