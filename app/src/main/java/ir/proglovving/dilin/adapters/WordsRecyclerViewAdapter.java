package ir.proglovving.dilin.adapters;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import ir.proglovving.dilin.CustomDialogBuilder;
import ir.proglovving.dilin.MyApplication;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.data_model.DetailedWord;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;
import ir.proglovving.dilin.views.activity.ShowWordActivity;

public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.WordMeaningViewHolder> {

    private Context context;
    private List<Word> words;
    private EventOfWordMeaningRecyclerView event;
    private int notebookId;

    private int lastPosition = -1;

    private static TextToSpeech textToSpeech;
    private static boolean isTTSReady = false;

    private Dialog addAndEditWordDialog;
    Button verifyButton, cancelButton;
    EditText wordEditText, meaningEditText;
    TextInputLayout wordTextInputLayout, meaningTextInputLayout;
    private LinearLayout dialogContainer;

    public WordsRecyclerViewAdapter(Context context, List<Word> words, EventOfWordMeaningRecyclerView event, int notebookId) {
        this.context = context;
        this.words = words;
        this.event = event;
        this.notebookId = notebookId;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    isTTSReady = true;
                } else {
                    isTTSReady = false;
                    // TODO: 4/17/19 show an error text
                }
            }
        });
    }

    public WordsRecyclerViewAdapter(Context context, List detailedWords, EventOfWordMeaningRecyclerView event) {
        this.context = context;
        this.words = detailedWords;
        this.event = event;
    }

    @NonNull
    @Override
    public WordMeaningViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WordMeaningViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_word, viewGroup, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final WordMeaningViewHolder wordMeaningViewHolder, final int i) {
        if (words.get(i) instanceof DetailedWord) {
            notebookId = ((DetailedWord) words.get(i)).getNotebookId();
        }

        wordMeaningViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowWordActivity.start(context);
            }
        });

        final Word word = words.get(i);
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
                event.onBookmarkClick(word);
                if (word.isBookmark()) {
                    word.setBookmark(false);
                    wordMeaningViewHolder.bookmarkButton.setImageResource(R.drawable.ic_action_bookmark_border);
                } else {
                    word.setBookmark(true);
                    wordMeaningViewHolder.bookmarkButton.setImageResource(R.drawable.ic_action_bookmark);
                }
                new WordsOpenHelper(context, notebookId).update(word, word.getId());


            }
        });


        wordMeaningViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWord(word,i);
            }
        });

        wordMeaningViewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrowseEditWordDialog(word.getId(), lastPosition);
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

        setAnimation(wordMeaningViewHolder.itemView, i);
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
        return words.size();
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
            dialogContainer = (LinearLayout) addAndEditWordDialog.findViewById(R.id.ll_dialog_add_word);
            dialogContainer.setVisibility(View.INVISIBLE);

            wordEditText = (EditText) addAndEditWordDialog.findViewById(R.id.et_word);
            meaningEditText = (EditText) addAndEditWordDialog.findViewById(R.id.et_meaning);
            wordTextInputLayout = (TextInputLayout) addAndEditWordDialog.findViewById(R.id.text_input_word);
            meaningTextInputLayout = (TextInputLayout) addAndEditWordDialog.findViewById(R.id.text_input_meaning);
            verifyButton = (Button) addAndEditWordDialog.findViewById(R.id.btn_verify);
            cancelButton = (Button) addAndEditWordDialog.findViewById(R.id.btn_cancel);
        } else {
            wordEditText.setText("");
            meaningEditText.setText("");
        }
    }

    private void showBrowseEditWordDialog(final int id, final int position) {
        setupDialog();

        final Word word = new WordsOpenHelper(context, notebookId).getWord(id);
        wordEditText.setText(word.getWord());
        meaningEditText.setText(word.getMeaning());

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wordEditText.getText().length() == 0) {
//                    wordEditText.setError(getString(R.string.no_word_was_entered_text));
                    wordTextInputLayout.setError(context.getString(R.string.no_word_was_entered_text));
                    return;
                }

                word.setWord(wordEditText.getText().toString());
                word.setMeaning(meaningEditText.getText().toString());

                new WordsOpenHelper(context, notebookId).update(word, id);

                event.onWordEdited(position);

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
                        new WordsOpenHelper(context, notebookId).deleteWord(word.getId());
//                        refreshRecyclerView(REFRESH_TYPE_CURRENT, position - 1);
//                        refreshInCurrentPosition(position - 1);
                        event.onDeleted(position);
                    }
                }).setNegative(R.string.no_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).create().show();
    }

    class WordMeaningViewHolder extends RecyclerView.ViewHolder {

        private TextView wordTextView, meaningTextView;
        private ImageButton deleteButton, bookmarkButton, editButton, speechButtonUK, speechButtonUS;
        private RelativeLayout relativeLayout;


        public WordMeaningViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.txt_word);
            meaningTextView = itemView.findViewById(R.id.txt_meaning);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            bookmarkButton = itemView.findViewById(R.id.btn_bookmark);
            editButton = itemView.findViewById(R.id.btn_edit);
            speechButtonUK = itemView.findViewById(R.id.img_speech_uk);
            speechButtonUS = itemView.findViewById(R.id.img_speech_us);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
        }
    }

    public interface EventOfWordMeaningRecyclerView {
        void onDeleted(int position);

        void onBookmarkClick(Word word);

        void onWordEdited(int position);
    }
}
