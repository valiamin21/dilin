package com.example.amin.dictionande.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.transition.Fade;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.amin.dictionande.R;
import com.example.amin.dictionande.Utilities;
import com.example.amin.dictionande.data_model.Notebook;
import com.example.amin.dictionande.database_open_helpers.NotebookOpenHelper;
import com.example.amin.dictionande.views.activity.ShowNoteBooksListActivity;
import com.example.amin.dictionande.views.activity.ShowWordsListActivity;

import java.util.List;

public class NotebookRecyclerAdapter extends RecyclerView.Adapter<NotebookRecyclerAdapter.NotebookViewHolder> {


    private Context context;
    private List<Notebook> notebooks;
    private CoordinatorLayout coordinatorLayout;
    private EventOfNotebookRecyclerAdapter event;
    private int lastPosition = -1;

    public NotebookRecyclerAdapter(Context context, List<Notebook> notebooks, CoordinatorLayout coordinatorLayout, EventOfNotebookRecyclerAdapter event) {

        this.context = context;
        this.notebooks = notebooks;
        this.coordinatorLayout = coordinatorLayout;
        this.event = event;
    }

    @NonNull
    @Override
    public NotebookViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return
                new NotebookViewHolder(
                        LayoutInflater.from(context).inflate(R.layout.item_notebook, viewGroup, false)
                );
    }

    @Override
    public void onBindViewHolder(@NonNull final NotebookViewHolder mViewHolder, final int position) {
        final Notebook notebook = notebooks.get(position);

        mViewHolder.noteBookNameTextView.setText(notebook.getNoteBookName());
        // TODO: 2/5/19 فارسی شده ی کلمات زیر در فایل استرینگ ها نوشته شود و اینجا استفاده شود
        mViewHolder.wordsCountTextView.setText(context.getString(R.string.words_count)+ ": " + notebook.getWordsCount());
        mViewHolder.bookmarkedCountTextView.setText(context.getString(R.string.bookmarked_count)+ ": " + notebook.getBookmarkedCount());

        setFavoriteImage(mViewHolder.favoriteButton,notebook.isFavorite()); // TODO: 2/5/19 اگر لیست دفتر های محبوب در حال نمایش بود و کاربر همانجا دکمه ی لغو محبوبیت را زد باید از آن لیست حذف شود. این مورد اصلاح شود.

        setPlayingImage(mViewHolder.playButton,notebook.isPlaying());

        mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat compat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,((Activity)context).findViewById(R.id.fab_add),context.getString(R.string.fab_transition_name));

                Intent intent = new Intent(context, ShowWordsListActivity.class);
                intent.putExtra(ShowWordsListActivity.KEY_NOTEBOOK_NAME, notebook.getNoteBookName());
//                context.startActivity(intent,compat.toBundle());
                ((Activity) context).startActivityForResult(intent, ShowNoteBooksListActivity.RESULT_BACK_FROM_NOTEBOOKS_ACTIVITY,compat.toBundle());
            }
        });

        mViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.delete_notebook)
                        .setMessage(R.string.do_you_want_to_delete_this_notebook)
                        .setPositiveButton(R.string.yes_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final NotebookOpenHelper notebookOpenHelper = new NotebookOpenHelper(context);
                                notebookOpenHelper.deleteNotebook(notebook.getId());
                                event.onRefreshInCurrentPosition(position);

                                final boolean[] isReturned = {false};
                                Snackbar.make(coordinatorLayout, R.string.was_deleted, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.retrieve, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                isReturned[0] = true;
                                                notebookOpenHelper.returnNotebook(notebook);
                                                event.onRefreshInCurrentPosition(position);
                                            }
                                        })
                                        .show();

                                new CountDownTimer(4000,4000){

                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        if(!isReturned[0]){
                                            notebookOpenHelper.deleteDatabase(notebook.getNoteBookName());
                                        }
                                    }
                                }.start();
                            }
                        })
                        .setNegativeButton(R.string.no_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.create().show();
                event.onDeleteClick(notebook, position);
            }
        });

        mViewHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavoriteAnimation(mViewHolder.linearImages, mViewHolder.favoriteButton, !notebook.isFavorite());
                notebook.setFavorite(!notebook.isFavorite());
                new NotebookOpenHelper(context).update(notebook);
                event.onFavoriteClick(notebook);
            }
        });

        mViewHolder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayingAnimation(mViewHolder.linearImages,mViewHolder.playButton , !notebook.isPlaying());
                notebook.setPlaying(!notebook.isPlaying());
                new NotebookOpenHelper(context).update(notebook);
                event.onPlayClick(notebook);
            }
        });

        // TODO: 4/21/19 یه فکری برا این انیمیشنا بکن
//        setAnimation(mViewHolder.itemView);
    }

    private void setPlayingAnimation(LinearLayout containerLinear, ImageButton imageButton, boolean isPlaying){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){

            imageButton.setVisibility(View.INVISIBLE);
            setPlayingImage(imageButton, isPlaying);

            Fade fade = new Fade();
            fade.setDuration(500);
            fade.setInterpolator(new DecelerateInterpolator());

            android.support.transition.TransitionManager.beginDelayedTransition(containerLinear, fade);

            imageButton.setVisibility(View.VISIBLE);
        }else{
            setPlayingImage(imageButton, isPlaying);
        }
    }

    private void setPlayingImage(ImageButton imageButton, boolean isPlaying) {
        if(isPlaying){
            imageButton.setImageResource(R.drawable.ic_action_pause);
        }else{
            imageButton.setImageResource(R.drawable.ic_action_play);
        }
    }

    private void setFavoriteAnimation(LinearLayout containerLinear, ImageButton imageButton, boolean isFavorite) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


            imageButton.setVisibility(View.INVISIBLE);
            setFavoriteImage(imageButton, isFavorite);

            Fade fade = new Fade();
            fade.setDuration(500);
            fade.setInterpolator(new OvershootInterpolator());

            android.support.transition.TransitionManager.beginDelayedTransition(containerLinear, fade);

            imageButton.setVisibility(View.VISIBLE);
        } else {
            setFavoriteImage(imageButton, isFavorite);
        }

    }

    private void setFavoriteImage(ImageButton imageButton, boolean isFavorite) {
        if (isFavorite) {
            imageButton.setImageResource(R.drawable.ic_action_favorite);
        } else {
            imageButton.setImageResource(R.drawable.ic_action_favorite_border);
        }
    }

    private void setAnimation(View viewToAnimation) {
//        if(position > lastPosition){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animation.setDuration(4000);
        animation.setInterpolator(new OvershootInterpolator());
        viewToAnimation.startAnimation(animation);
//        }
    }

    @Override
    public int getItemCount() {
        return notebooks.size();
    }

    public class NotebookViewHolder extends RecyclerView.ViewHolder {

        private TextView noteBookNameTextView;
        private TextView wordsCountTextView;
        private TextView bookmarkedCountTextView;

        private ImageButton deleteButton, favoriteButton, playButton;

        private LinearLayout linearImages;

        public NotebookViewHolder(@NonNull View itemView) {
            super(itemView);
            noteBookNameTextView = (TextView) itemView.findViewById(R.id.txt_notebook_name);
            wordsCountTextView = (TextView) itemView.findViewById(R.id.txt_words_count);
            bookmarkedCountTextView = (TextView) itemView.findViewById(R.id.txt_bookmarked_count);

            deleteButton = (ImageButton) itemView.findViewById(R.id.btn_delete);
            favoriteButton = (ImageButton) itemView.findViewById(R.id.btn_favorite);
            playButton = (ImageButton) itemView.findViewById(R.id.btn_play);

            linearImages = (LinearLayout) itemView.findViewById(R.id.linear_imgs);
        }
    }

    public interface EventOfNotebookRecyclerAdapter {
        void onDeleteClick(Notebook notebook, int position);

        void onFavoriteClick(Notebook notebook);

        void onPlayClick(Notebook notebook);

        void onRefreshInCurrentPosition(int position);
    }
}
