package ir.proglovving.dilin.adapters;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import ir.proglovving.dilin.custom_views.CustomDialogBuilder;
import ir.proglovving.dilin.MyApplication;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;

public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.WordMeaningViewHolder> implements View.OnLongClickListener {

    private Context context;
    private List<Word> wordList;

    private int lastPosition = -1;

    private Dialog addAndEditWordDialog;
    private Button verifyButton, cancelButton;
    private EditText wordEditText, meaningEditText;
    private TextInputLayout wordTextInputLayout;
    private LinearLayout dialogContainer;

    private WordsRecyclerViewEvent mEvent;

    public WordsRecyclerViewAdapter(Context context, List<Word> wordList,WordsRecyclerViewEvent event) {
        this.context = context;
        this.wordList = wordList;
        this.mEvent = event;
    }

    @NonNull
    @Override
    public WordMeaningViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WordMeaningViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_word, viewGroup, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final WordMeaningViewHolder wordMeaningViewHolder, int position) {

        final Word word = wordList.get(position);
        wordMeaningViewHolder.wordTextView.setText(word.getWord() + " :");
        if (word.getMeaning().equals("")) { // اگر معنایی وارد نشده بود
            wordMeaningViewHolder.meaningTextView.setText(R.string.no_meaning_has_been_entered_text);
            wordMeaningViewHolder.meaningTextView.setTextColor(ContextCompat.getColor(context, R.color.meaningless_persian_words_color));
        } else { // اگر معنا وارد شده بود
            wordMeaningViewHolder.meaningTextView.setText(word.getMeaning());
            wordMeaningViewHolder.meaningTextView.setTextColor(ContextCompat.getColor(context, R.color.persian_words_color));
        }

        if (word.isBookmark()) {
            wordMeaningViewHolder.bookmarkButton.setImageResource(R.drawable.ic_action_bookmark);
        } else {
            wordMeaningViewHolder.bookmarkButton.setImageResource(R.drawable.ic_action_bookmark_border);
        }
        wordMeaningViewHolder.bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (word.isBookmark()) {
                    word.setBookmark(false);
                    wordMeaningViewHolder.bookmarkButton.setImageResource(R.drawable.ic_action_bookmark_border);
                } else {
                    word.setBookmark(true);
                    wordMeaningViewHolder.bookmarkButton.setImageResource(R.drawable.ic_action_bookmark);
                }
                new WordsOpenHelper(context, word.getNotebookId()).update(word, word.getId());

                mEvent.onBookmarked();
            }
        });


        wordMeaningViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWord(word, wordMeaningViewHolder.getAdapterPosition());
            }
        });

        wordMeaningViewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrowseEditWordDialog(word, wordMeaningViewHolder.getAdapterPosition());
            }
        });

        wordMeaningViewHolder.speechButtonUK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.speechWord(word.getWord(), Locale.UK, context);
            }
        });

        wordMeaningViewHolder.speechButtonUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.speechWord(word.getWord(), Locale.US, context);
            }
        });

        wordMeaningViewHolder.deleteButton.setOnLongClickListener(this);
        wordMeaningViewHolder.bookmarkButton.setOnLongClickListener(this);
        wordMeaningViewHolder.editButton.setOnLongClickListener(this);
        wordMeaningViewHolder.speechButtonUK.setOnLongClickListener(this);
        wordMeaningViewHolder.speechButtonUS.setOnLongClickListener(this);


        setAnimation(wordMeaningViewHolder.itemView, position);
    }

    private void setAnimation(View viewToAnimation, int position) {
        if (position > lastPosition) {
            lastPosition = position;
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            animation.setDuration(800);
            animation.setInterpolator(new OvershootInterpolator());
            viewToAnimation.startAnimation(animation);
        }

    }

    @Override
    public int getItemCount() {
        return wordList.size();
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

    private void setupDialog() {
        // TODO: 1/14/19 value 'true' in below code have to be improved later !
        if (true
//                ||
//                addAndEditWordDialog == null ||
//                wordEditText == null ||
//                meaningEditText == null ||
//                verifyButton == null ||
//                cancelButton == null
        ) {

            addAndEditWordDialog = new Dialog(context);
            addAndEditWordDialog.setContentView(R.layout.dialog_add_word);
            addAndEditWordDialog.setTitle(R.string.adding_word_text);
            dialogContainer = addAndEditWordDialog.findViewById(R.id.ll_dialog_add_word);
            dialogContainer.setVisibility(View.INVISIBLE);

            wordEditText = addAndEditWordDialog.findViewById(R.id.et_word);
            meaningEditText = addAndEditWordDialog.findViewById(R.id.et_meaning);
            wordTextInputLayout = addAndEditWordDialog.findViewById(R.id.text_input_word);
            verifyButton = addAndEditWordDialog.findViewById(R.id.btn_verify);
            cancelButton = addAndEditWordDialog.findViewById(R.id.btn_cancel);
        } else {
            wordEditText.setText("");
            meaningEditText.setText("");
        }
    }

    private void showBrowseEditWordDialog(final Word word, final int position) {
        setupDialog();
        wordEditText.setText(word.getWord());
        meaningEditText.setText(word.getMeaning());

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wordEditText.getText().length() == 0) {
                    wordTextInputLayout.setError(context.getString(R.string.no_word_was_entered_text));
                    return;
                }

                word.setWord(wordEditText.getText().toString());
                word.setMeaning(meaningEditText.getText().toString());

                new WordsOpenHelper(context,word.getNotebookId()).update(word, word.getId());
                notifyItemChanged(position);
                mEvent.onWordEdited();

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

    private void deleteWord(final Word word, final int position) {
        new CustomDialogBuilder(context)
                .setTitle(R.string.delete_word_text)
                .setMessage(R.string.do_you_want_to_delete_this_word_text)
                .setPositive(R.string.yes_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new WordsOpenHelper(context, word.getNotebookId()).deleteWord(word.getId());
                        wordList.remove(position);
                        notifyItemRemoved(position);
                        mEvent.onDeleted();
                    }
                }).setNegative(R.string.no_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).create().show();
    }

    public void setWordList(List<Word> wordList){
        this.wordList = wordList;
        notifyDataSetChanged();
    }

    public void addWord(Word word){
        wordList.add(word);
        notifyItemInserted(wordList.size() - 1);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete:
                ToolTip.show(context, context.getString(R.string.delete_word_text), v);
                break;
            case R.id.btn_bookmark:
                ToolTip.show(context, context.getString(R.string.bookmarking), v);
                break;
            case R.id.btn_edit:
                ToolTip.show(context, context.getString(R.string.edit), v);
                break;
            case R.id.img_speech_us:
                ToolTip.show(context, context.getString(R.string.american_speech), v);
                break;
            case R.id.img_speech_uk:
                ToolTip.show(context, context.getString(R.string.english_pronunciation), v);
                break;
        }
        return true;
    }

    static class WordMeaningViewHolder extends RecyclerView.ViewHolder {

        private TextView wordTextView, meaningTextView;
        private ImageButton deleteButton, bookmarkButton, editButton, speechButtonUK, speechButtonUS;

        public WordMeaningViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.txt_word);
            meaningTextView = itemView.findViewById(R.id.txt_meaning);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            bookmarkButton = itemView.findViewById(R.id.btn_bookmark);
            editButton = itemView.findViewById(R.id.btn_edit);
            speechButtonUK = itemView.findViewById(R.id.img_speech_uk);
            speechButtonUS = itemView.findViewById(R.id.img_speech_us);
        }
    }

    public interface WordsRecyclerViewEvent{
        void onBookmarked();
        void onDeleted();
        void onWordEdited();
    }
}
