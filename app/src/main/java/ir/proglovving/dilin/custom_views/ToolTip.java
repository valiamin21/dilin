package ir.proglovving.dilin.custom_views;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ir.proglovving.dilin.R;

public class ToolTip {
    private Context context;
    private String text;
    private View view;
    private int xOffset;
    private int yOffset;

    public ToolTip(Context context, String text, View view) {
        this.context = context;
        this.text = text;
        this.view = view;
    }

    public void show() {
        final Toast toast = new Toast(context);

        final View toastView = LayoutInflater.from(context).inflate(R.layout.layout_tooltip, null);

        TextView tooltipTextView = toastView.findViewById(R.id.tv_tooltip);
        tooltipTextView.setText(text);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        xOffset = location[0] - view.getMeasuredWidth() / 2;
        yOffset = location[1];
        toast.setGravity(Gravity.TOP | Gravity.START, xOffset, yOffset);

        toast.show();

    }


}
