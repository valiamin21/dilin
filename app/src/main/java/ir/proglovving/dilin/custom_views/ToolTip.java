package ir.proglovving.dilin.custom_views;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ir.proglovving.dilin.R;

public class ToolTip {

    public static void show(Context context, String text, View view) {
        final Toast toast = new Toast(context);

        final View toastView = LayoutInflater.from(context).inflate(R.layout.layout_tooltip, null);

        TextView tooltipTextView = toastView.findViewById(R.id.tv_tooltip);
        tooltipTextView.setText(text);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xOffset, yOffset;
        xOffset = location[0] - view.getMeasuredWidth() / 2;
        yOffset = location[1];
        toast.setGravity(Gravity.TOP | Gravity.START, xOffset, yOffset);

        toast.show();

    }


}
