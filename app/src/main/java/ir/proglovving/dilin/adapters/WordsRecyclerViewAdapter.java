package ir.proglovving.dilin.adapters;

import android.content.Context;

import androidx.annotation.NonNull;


import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import ir.proglovving.cfviews.CustomDialogBuilder;
import ir.proglovving.dilin.MyApplication;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.custom_views.WordsInputDialog;
import ir.proglovving.dilin.data_model.NotebookWord;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;

public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.WordMeaningViewHolder> implements View.OnLongClickListener {

    private Context context;
    private List<NotebookWord> wordList;

    private int lastPosition = -1;

    private WordsRecyclerViewEvent mEvent;

    public WordsRecyclerViewAdapter(Context context, List<NotebookWord> wordList, WordsRecyclerViewEvent event) {
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

        final NotebookWord word = wordList.get(position);
        wordMeaningViewHolder.wordTextView.setText(word.getWord() + " :");
        if (word.getMeaning().equals("")) { // اگر معنایی وارد نشده بود
            wordMeaningViewHolder.meaningTextView.setText(R.string.no_meaning_has_been_entered);
            wordMeaningViewHolder.meaningTextView.setTextColor(ContextCompat.getColor(context, R.color.meaningless_persian_words_color));
        } else { // اگر معنا وارد شده بود
            wordMeaningViewHolder.meaningTextView.setText(word.getMeaning());
            wordMeaningViewHolder.meaningTextView.setTextColor(ContextCompat.getColor(context, R.color.persian_words_color));
        }

        wordMeaningViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWord(word, wordMeaningViewHolder.getAdapterPosition());
            }
        });

        wordMeaningViewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordsInputDialog.getInstance(context).showBrowseAddWordDialog(word.getWord(), word.getMeaning(), new WordsInputDialog.OnAddWord() {
                    @Override
                    public void onAdd(String w, String meaning) {
                        word.setWord(w);
                        word.setMeaning(meaning);

                        new WordsOpenHelper(context, word.getNotebookId()).update(word, word.getId());

                        notifyItemChanged(wordMeaningViewHolder.getAdapterPosition());
                        mEvent.onWordEdited();
                    }

                    @Override
                    public boolean onAlreadyExists(String w) {
                            return !word.getWord().equals(w) && new WordsOpenHelper(context,word.getNotebookId()).isThereWord(w);
                    }
                });
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

    private void deleteWord(final NotebookWord word, final int position) {
        new CustomDialogBuilder(context,R.color.primary_text,R.color.colorAccent)
                .setTitle(R.string.delete_word)
                .setMessage(R.string.do_you_want_to_delete_this_word)
                .setPositive(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new WordsOpenHelper(context, word.getNotebookId()).deleteWord(word.getId());
                        wordList.remove(position);
                        notifyItemRemoved(position);
                        mEvent.onDeleted();
                    }
                }).setNegative(R.string.no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).create().show();
    }

    public void setWordList(List<NotebookWord> wordList) {
        this.wordList = wordList;
        notifyDataSetChanged();
    }

    public void addWord(NotebookWord word) {
        wordList.add(word);
        notifyItemInserted(wordList.size() - 1);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete:
                ToolTip.show(context, context.getString(R.string.delete_word), v);
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
        private ImageButton deleteButton, editButton, speechButtonUK, speechButtonUS;

        public WordMeaningViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.txt_word);
            meaningTextView = itemView.findViewById(R.id.txt_meaning);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            editButton = itemView.findViewById(R.id.btn_edit);
            speechButtonUK = itemView.findViewById(R.id.img_speech_uk);
            speechButtonUS = itemView.findViewById(R.id.img_speech_us);
        }
    }

    public interface WordsRecyclerViewEvent {
        void onDeleted();

        void onWordEdited();
    }
}
