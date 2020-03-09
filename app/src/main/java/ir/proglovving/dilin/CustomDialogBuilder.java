package ir.proglovving.dilin;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogBuilder {
    // TODO: 7/24/19 add cancel button in your customDialog
    private String title;
    private String message;
    private String positiveText;
    private String negativeText;
    private Context context;
    private View.OnClickListener onPositiveClickListener;
    private View.OnClickListener onNegativeClickListener;

    public CustomDialogBuilder(Context context) {
        this.context = context;
    }

    public CustomDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public CustomDialogBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public CustomDialogBuilder setPositive(String positiveText, View.OnClickListener onPositiveClickListener) {
        this.positiveText = positiveText;
        this.onPositiveClickListener = onPositiveClickListener;
        return this;
    }

    public CustomDialogBuilder setNegative(String negativeText, View.OnClickListener onNegativeClickListener) {
        this.negativeText = negativeText;
        this.onNegativeClickListener = onNegativeClickListener;
        return this;
    }


    public CustomDialogBuilder setTitle(@StringRes int titleId) {
        this.title = context.getString(titleId);
        return this;
    }

    public CustomDialogBuilder setMessage(@StringRes int messageId) {
        this.message = context.getString(messageId);
        return this;
    }

    public CustomDialogBuilder setPositive(@StringRes int positiveTextId, View.OnClickListener onPositiveClickListener) {
        this.positiveText = context.getString(positiveTextId);
        this.onPositiveClickListener = onPositiveClickListener;
        return this;
    }

    public CustomDialogBuilder setNegative(@StringRes int negativeTextId, View.OnClickListener onNegativeClickListener) {
        this.negativeText = context.getString(negativeTextId);
        this.onNegativeClickListener = onNegativeClickListener;
        return this;
    }

    public Dialog create() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_app_dialog);
        ((TextView) dialog.findViewById(R.id.tv_cusDialog_title)).setText(title);
        ((TextView) dialog.findViewById(R.id.tv_cusDialog_message)).setText(message);

        // TODO: 7/24/19 modify onclick events for positive and negative button. current code is so hopeless D:

        ((Button) dialog.findViewById(R.id.btn_cusDialog_positive)).setText(positiveText);
        dialog.findViewById(R.id.btn_cusDialog_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onPositiveClickListener.onClick(v);
            }
        });

        ((Button) dialog.findViewById(R.id.btn_cusDialog_negative)).setText(negativeText);
        dialog.findViewById(R.id.btn_cusDialog_negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onNegativeClickListener.onClick(v);
            }
        });

        return dialog;
    }
}
