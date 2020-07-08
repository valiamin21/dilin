package ir.proglovving.cfviews;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

public class CustomDialogBuilder {

    private Dialog dialog;

    private TextView titleTextView;
    private TextView messageTextView;
    private Button positiveButton, cancelButton, negativeButton;

    private Context context;

    public CustomDialogBuilder(Context context, @ColorRes int textViewsColor, @ColorRes int buttonsColor) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_app_dialog);

        titleTextView = dialog.findViewById(R.id.tv_cusDialog_title);
        messageTextView = dialog.findViewById(R.id.tv_cusDialog_message);
        positiveButton = dialog.findViewById(R.id.btn_cusDialog_positive);
        cancelButton = dialog.findViewById(R.id.btn_cusDialog_cancel);
        negativeButton = dialog.findViewById(R.id.btn_cusDialog_negative);

        positiveButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        negativeButton.setVisibility(View.GONE);


        titleTextView.setTextColor(ContextCompat.getColor(context, textViewsColor));
        messageTextView.setTextColor(ContextCompat.getColor(context, textViewsColor));

        positiveButton.setTextColor(ContextCompat.getColor(context, buttonsColor));
        cancelButton.setTextColor(ContextCompat.getColor(context, buttonsColor));
        negativeButton.setTextColor(ContextCompat.getColor(context, buttonsColor));
    }

    public CustomDialogBuilder setTitle(String title) {
        titleTextView.setText(title);
        return this;
    }

    public CustomDialogBuilder setMessage(String message) {
        messageTextView.setText(message);
        return this;
    }

    public CustomDialogBuilder setPositive(String positiveText, final View.OnClickListener onPositiveClickListener) {
        positiveButton.setText(positiveText);
        positiveButton.setVisibility(View.VISIBLE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onPositiveClickListener.onClick(view);
            }
        });
        return this;
    }

    public CustomDialogBuilder setCancel(String cancelText, final View.OnClickListener onCancelClickListener) {
        cancelButton.setText(cancelText);
        cancelButton.setVisibility(View.VISIBLE);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onCancelClickListener.onClick(view);
            }
        });
        return this;
    }

    public CustomDialogBuilder setNegative(String negativeText, final View.OnClickListener onNegativeClickListener) {
        negativeButton.setText(negativeText);
        negativeButton.setVisibility(View.VISIBLE);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onNegativeClickListener.onClick(view);
            }
        });
        return this;
    }

    public CustomDialogBuilder setTitle(@StringRes int titleId) {
        titleTextView.setText(context.getString(titleId));
        return this;
    }

    public CustomDialogBuilder setMessage(@StringRes int messageId) {
        messageTextView.setText(context.getString(messageId));
        return this;
    }

    public CustomDialogBuilder setPositive(@StringRes int positiveTextId, View.OnClickListener onPositiveClickListener) {
        setPositive(context.getString(positiveTextId), onPositiveClickListener);
        return this;
    }

    public CustomDialogBuilder setCancel(@StringRes int cancelTextId, View.OnClickListener onClickListener) {
        setCancel(context.getString(cancelTextId), onClickListener);
        return this;
    }

    public CustomDialogBuilder setNegative(@StringRes int negativeTextId, View.OnClickListener onNegativeClickListener) {
        setNegative(context.getString(negativeTextId), onNegativeClickListener);
        return this;
    }

    public Dialog create() {
        return dialog;
    }
}
