package com.example.amin.dictionande.adapters;

import android.content.Context;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.amin.dictionande.R;
import com.example.amin.dictionande.database_open_helpers.VocabularyOpenHelper;
import com.example.amin.dictionande.data_model.Voc;

import java.util.List;
import java.util.Locale;

public class WordRecyclerViewAdapter extends RecyclerView.Adapter<WordRecyclerViewAdapter.VocMeanRecyclerViewHolder> {


    private Context context;
    private List<Voc> vocs;
    private EventOfVocMeanRecyclerView event;
    private String notebookName;

    private int lastPosition = -1;

    private TextToSpeech textToSpeech;
    private boolean isTTSReady = false;

    public WordRecyclerViewAdapter(Context context, List<Voc> vocs, EventOfVocMeanRecyclerView event, String notebookName) {

        this.context = context;
        this.vocs = vocs;
        this.event = event;
        this.notebookName = notebookName;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    isTTSReady = true;
                }else{
                    isTTSReady = false;
                    // TODO: 4/17/19 show an error text
                }
            }
        });
    }

    @NonNull
    @Override
    public VocMeanRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_word,viewGroup,false);
//        VocMeanRecyclerViewHolder vocMeanRecyclerViewHolder = new VocMeanRecyclerViewHolder(viewGroup);
//        return vocMeanRecyclerViewHolder;
        return new VocMeanRecyclerViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_word, viewGroup, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final VocMeanRecyclerViewHolder vocMeanRecyclerViewHolder, final int i) {
//        Word word = words.get(i);
//        vocMeanRecyclerViewHolder.vocTextView.setText(word.getVoc());
//        vocMeanRecyclerViewHolder.meaningTextView.setText(word.getMeaning());

        final Voc voc = vocs.get(i);

        vocMeanRecyclerViewHolder.vocTextView.setText(voc.getVoc() + " :");

        if (voc.getMeaning().equals("")) { // اگر معنایی وارد نشده بود
            vocMeanRecyclerViewHolder.meaningTextView.setText(R.string.no_meaning_has_been_entered_text);
            vocMeanRecyclerViewHolder.meaningTextView.setTextColor(Color.GRAY);
        } else { // اگر معنا وارد شده بود
            vocMeanRecyclerViewHolder.meaningTextView.setText(voc.getMeaning());
        }

        if (voc.isBookmark()) {
            vocMeanRecyclerViewHolder.bookmarkButton.setImageResource(R.drawable.ic_action_bookmark);
        } else {
            vocMeanRecyclerViewHolder.bookmarkButton.setImageResource(R.drawable.ic_action_bookmark_border);
        }
        vocMeanRecyclerViewHolder.bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.onBookmarkClick(voc);
                if (voc.isBookmark()) {
                    voc.setBookmark(false);
                    vocMeanRecyclerViewHolder.bookmarkButton.setImageResource(R.drawable.ic_action_bookmark_border);
                } else {
                    voc.setBookmark(true);
                    vocMeanRecyclerViewHolder.bookmarkButton.setImageResource(R.drawable.ic_action_bookmark);
                }
                new VocabularyOpenHelper(context,notebookName).update(voc, voc.getId());


            }
        });


        vocMeanRecyclerViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.onDeleteClick(voc, i);
            }
        });

        vocMeanRecyclerViewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.onEditClick(voc, i);
            }
        });

        vocMeanRecyclerViewHolder.speechButtonUK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTTSReady){
                    int ttsLang = textToSpeech.setLanguage(Locale.UK);
                    if(ttsLang == TextToSpeech.LANG_MISSING_DATA
                        || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED){

                        // TODO: 4/17/19 show an error
                    }else{
                        textToSpeech.speak(voc.getVoc(),TextToSpeech.QUEUE_FLUSH,null);
                    }
                }
            }
        });

        vocMeanRecyclerViewHolder.speechButtonUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isTTSReady){
                    int ttsLang = textToSpeech.setLanguage(Locale.US);
                    if(ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED){

                        // TODO: 4/17/19 show an error
                    }else{
                        textToSpeech.speak(voc.getVoc(),TextToSpeech.QUEUE_FLUSH,null);
                    }
                }
            }
        });

//        setAnimation(vocMeanRecyclerViewHolder.itemView, i);
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
        return vocs.size();
    }

    class VocMeanRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView vocTextView;
        private TextView meaningTextView;
        private ImageButton deleteButton;
        private ImageButton bookmarkButton;
        private ImageButton editButton;
        private ImageButton speechButtonUK;
        private ImageButton speechButtonUS;

        private RelativeLayout relativeLayout;


        public VocMeanRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            vocTextView = (TextView) itemView.findViewById(R.id.txt_voc);
            meaningTextView = (TextView) itemView.findViewById(R.id.txt_meaning);
            deleteButton = (ImageButton) itemView.findViewById(R.id.btn_delete);
            bookmarkButton = (ImageButton) itemView.findViewById(R.id.btn_bookmark);
            editButton = (ImageButton) itemView.findViewById(R.id.btn_edit);
            speechButtonUK = (ImageButton)itemView.findViewById(R.id.img_speech_uk);
            speechButtonUS = (ImageButton)itemView.findViewById(R.id.img_speech_us);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_layout);
        }
    }


    public interface EventOfVocMeanRecyclerView {
        void onDeleteClick(Voc voc, int position);

        void onBookmarkClick(Voc voc);

        void onEditClick(Voc voc, int position);
    }
}
