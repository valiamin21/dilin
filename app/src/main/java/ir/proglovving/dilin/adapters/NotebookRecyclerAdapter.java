package ir.proglovving.dilin.adapters;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.transition.Fade;
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

import ir.proglovving.dilin.CustomDialogBuilder;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.data_model.Notebook;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;
import ir.proglovving.dilin.views.activity.WordsListActivity;

public class NotebookRecyclerAdapter extends RecyclerView.Adapter<NotebookRecyclerAdapter.NotebookViewHolder> implements View.OnLongClickListener {
    private Context context;
    private List<Notebook> notebookList;
    private CoordinatorLayout coordinatorLayout;
    private NotebookOpenHelper notebookOpenHelper;

    private static final int VALUELESS = -23;
    private static int favoriteColorTint = VALUELESS;
    private static int non_FavoriteColorTint = VALUELESS;

    private int lastPosition = -1;

    public NotebookRecyclerAdapter(
            Context context,
            List<Notebook> notebookList,
            CoordinatorLayout coordinatorLayout,
            NotebookOpenHelper notebookOpenHelper) {

        this.context = context;
        this.notebookList = notebookList;
        this.coordinatorLayout = coordinatorLayout;
        this.notebookOpenHelper = notebookOpenHelper;
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
    public void onBindViewHolder(@NonNull final NotebookViewHolder mViewHolder, int position) {
        final Notebook notebook = notebookList.get(position);

        mViewHolder.noteBookNameTextView.setText(notebook.getNoteBookName());
        mViewHolder.wordsCountTextView.setText(context.getString(R.string.words_count) + ": " + Utilities.convertNumberToPersian(notebook.getWordsCount()));
        mViewHolder.bookmarkedCountTextView.setText(context.getString(R.string.bookmarked_count) + ": " + Utilities.convertNumberToPersian(notebook.getBookmarkedCount()));

        setFavoriteImage(mViewHolder.favoriteButton, notebook.isFavorite()); // TODO: 2/5/19 اگر لیست دفتر های محبوب در حال نمایش بود و کاربر همانجا دکمه ی لغو محبوبیت را زد باید از آن لیست حذف شود. این مورد اصلاح شود.

        mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordsListActivity.start(context, notebook.getId(), notebook.getNoteBookName());

            }
        });

        mViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomDialogBuilder(context)
                        .setTitle(R.string.delete_notebook)
                        .setMessage(R.string.do_you_want_to_delete_this_notebook)
                        .setPositive(R.string.yes_text, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int currentPosition = mViewHolder.getAdapterPosition();

                                notebookOpenHelper.deleteNotebook(notebook.getId());
                                notebookList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);

                                final boolean[] isReturned = {false};
                                Snackbar.make(coordinatorLayout, R.string.was_deleted, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.retrieve, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                isReturned[0] = true;
                                                notebookOpenHelper.returnNotebook(notebook);
                                                notebookList.add(currentPosition, notebook);
                                                notifyItemInserted(currentPosition);
                                            }
                                        })
                                        .show();

                                new CountDownTimer(4000, 4000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        if (!isReturned[0]) {
                                            notebookOpenHelper.deleteDatabase(notebook.getNoteBookName());
                                        }
                                    }
                                }.start();
                            }
                        }).setNegative(R.string.no_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).create().show();
            }
        });

        mViewHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavoriteAnimation(mViewHolder.linearImages, mViewHolder.favoriteButton, !notebook.isFavorite());
                notebook.setFavorite(!notebook.isFavorite());
               notebookOpenHelper.update(notebook);
            }
        });

        mViewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditNoteBookDialog(notebook, mViewHolder.getAdapterPosition());
            }
        });

        mViewHolder.deleteButton.setOnLongClickListener(this);
        mViewHolder.favoriteButton.setOnLongClickListener(this);
        mViewHolder.editButton.setOnLongClickListener(this);

        setAnimation(mViewHolder.itemView, position);
    }

    private void showEditNoteBookDialog(final Notebook notebook, final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_notebook);
        final LinearLayout dialogContainer = dialog.findViewById(R.id.ll_dialog_add_notebook);
        final EditText notebookNameEditText = dialog.findViewById(R.id.et_notebook);
        Button verifyButton = dialog.findViewById(R.id.btn_verify), cancelButton = dialog.findViewById(R.id.btn_cancel);
        notebookNameEditText.setText(notebook.getNoteBookName());

        // کد زیر کیبورد گوشی را برای ادیت تکست نمایش می دهد
        notebookNameEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utilities.showSoftKeyboard(notebookNameEditText, context);
            }
        }, 100);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notebookNameEditText.getText().length() == 0) {
                    notebookNameEditText.setError(context.getString(R.string.no_name_has_been_entered));
                    return;
                }

                notebook.setNoteBookName(notebookNameEditText.getText().toString());
                notebookOpenHelper.update(notebook);
                notifyItemChanged(position);

                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogContainer.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Animator animator = ViewAnimationUtils.createCircularReveal(
                            dialogContainer, dialogContainer.getWidth(), dialogContainer.getHeight(), 0,
                            Math.max(dialogContainer.getWidth(), dialogContainer.getHeight()));
                    dialogContainer.setVisibility(View.VISIBLE);
                    animator.start();
                }


            }, 200);
        } else {
            dialogContainer.setVisibility(View.VISIBLE);
        }

    }

    private void setFavoriteAnimation(LinearLayout containerLinear, ImageButton imageButton, boolean isFavorite) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


            imageButton.setVisibility(View.INVISIBLE);
            setFavoriteImage(imageButton, isFavorite);

            Fade fade = new Fade();
            fade.setDuration(500);
            fade.setInterpolator(new OvershootInterpolator());

            androidx.transition.TransitionManager.beginDelayedTransition(containerLinear, fade);

            imageButton.setVisibility(View.VISIBLE);
        } else {
            setFavoriteImage(imageButton, isFavorite);
        }

    }

    private void setFavoriteImage(ImageButton imageButton, boolean isFavorite) {
        imageButton.setColorFilter(getFavoriteImageButtonColorTint(isFavorite));

        if (isFavorite) {
            imageButton.setImageResource(R.drawable.ic_action_favorite);
        } else {
            imageButton.setImageResource(R.drawable.ic_action_favorite_border);
        }
    }

    // returns suitable color for imageButton in mode favorite or non-favorite
    private int getFavoriteImageButtonColorTint(boolean isFavorite) {
        if (isFavorite) {

            if (favoriteColorTint == VALUELESS) {
                favoriteColorTint = ContextCompat.getColor(context, R.color.heart_icon_color);
            }
            return favoriteColorTint;

        } else {

            if (non_FavoriteColorTint == VALUELESS) {
                non_FavoriteColorTint = ContextCompat.getColor(context, R.color.icon_color);
            }
            return non_FavoriteColorTint;
        }
    }

    private void setAnimation(View viewToAnimation, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            animation.setDuration(4000);
            animation.setInterpolator(new OvershootInterpolator());
            viewToAnimation.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void addNotebook(Notebook notebook) {
        notebookList.add(notebook);
        notifyItemInserted(notebookList.size() - 1);
    }

    public void setNotebookList(List<Notebook> notebookList) {
        this.notebookList = notebookList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notebookList.size();
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete:
                ToolTip.show(context, context.getString(R.string.delete_notebook), v);
                break;
            case R.id.btn_favorite:
                ToolTip.show(context, context.getString(R.string.adding_to_favorite), v);
                break;
            case R.id.btn_edit:
                ToolTip.show(context, context.getString(R.string.showing_in_app_widget), v);
                break;
        }
        return true;
    }

    static class NotebookViewHolder extends RecyclerView.ViewHolder {

        private TextView noteBookNameTextView;
        private TextView wordsCountTextView;
        private TextView bookmarkedCountTextView;

        private ImageButton deleteButton, favoriteButton, editButton;

        private LinearLayout linearImages;

        public NotebookViewHolder(@NonNull View itemView) {
            super(itemView);
            noteBookNameTextView = itemView.findViewById(R.id.txt_notebook_name);
            wordsCountTextView = itemView.findViewById(R.id.txt_words_count);
            bookmarkedCountTextView = itemView.findViewById(R.id.txt_bookmarked_count);

            deleteButton = itemView.findViewById(R.id.btn_delete);
            favoriteButton = itemView.findViewById(R.id.btn_favorite);
            editButton = itemView.findViewById(R.id.btn_edit);

            linearImages = itemView.findViewById(R.id.linear_imgs);
        }
    }
}
