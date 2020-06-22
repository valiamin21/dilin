package ir.proglovving.dilin.custom_views;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;

public class WordsInputDialog {

    private Dialog addAndEditWordDialog;
    private Button verifyButton, cancelButton;
    private EditText wordEditText, meaningEditText;
    private LinearLayout dialogContainer;

    public static WordsInputDialog getInstance(Context context) {
        return new WordsInputDialog(context);
    }

    private Context context;

    private WordsInputDialog(Context context) {
        this.context = context;
    }

    public void showBrowseAddWordDialog(OnAddWord onAddWord) {
        showBrowseAddWordDialog("", "", onAddWord);
    }

    public void showBrowseAddWordDialog(final String word, String meaning, final OnAddWord onAddWord) {
        setupDialog(context);
        wordEditText.setText(word);
        meaningEditText.setText(meaning);

        wordEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utilities.showSoftKeyboard(wordEditText, context);
            }
        }, 100);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wordEditText.getText().length() == 0) {
                    wordEditText.setError(context.getString(R.string.no_word_was_entered));
                    return;
                } else if (onAddWord.onAlreadyExists(wordEditText.getText().toString())) {
                    wordEditText.setError(context.getString(R.string.word_is_repeated));
                    return;
                }

                onAddWord.onAdd(wordEditText.getText().toString(), meaningEditText.getText().toString());
                addAndEditWordDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAndEditWordDialog.dismiss();
            }
        });
        addAndEditWordDialog.show();

        dialogContainerRevealEffectShow();

    }


    private void dialogContainerRevealEffectShow() {
        dialogContainer.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Animator animator =
                            ViewAnimationUtils.createCircularReveal(dialogContainer,
                                    dialogContainer.getWidth(), dialogContainer.getHeight(), 0,
                                    (float) Math.hypot(dialogContainer.getWidth(), dialogContainer.getHeight()));
                    dialogContainer.setVisibility(View.VISIBLE);
                    animator.start();
                } else {
                    dialogContainer.setVisibility(View.VISIBLE);
                }

            }
        }, 100);

    }

    private void setupDialog(Context context) {
        if (addAndEditWordDialog == null || wordEditText == null || meaningEditText == null || verifyButton == null || cancelButton == null) {

            addAndEditWordDialog = new Dialog(context);
            addAndEditWordDialog.setContentView(R.layout.dialog_add_word);
            addAndEditWordDialog.setTitle(R.string.adding_word);
            dialogContainer = addAndEditWordDialog.findViewById(R.id.ll_dialog_add_word);
            dialogContainer.setVisibility(View.INVISIBLE);

            wordEditText = addAndEditWordDialog.findViewById(R.id.et_word);
            meaningEditText = addAndEditWordDialog.findViewById(R.id.et_meaning);
            verifyButton = addAndEditWordDialog.findViewById(R.id.btn_verify);
            cancelButton = addAndEditWordDialog.findViewById(R.id.btn_cancel);
        } else {
            wordEditText.setText("");
            meaningEditText.setText("");
        }
    }

    public interface OnAddWord {
        void onAdd(String word, String meaning);

        boolean onAlreadyExists(String word);
    }
}
