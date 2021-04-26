package ir.proglovving.dilin.adapters;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

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

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.data_model.Notebook;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;
import ir.proglovving.dilin.views.CustomDialogBuilder;
import ir.proglovving.dilin.views.activity.WordsListActivity;
import ir.proglovving.dilin.views.fragment.SavedWordsFragment;

public class NotebookRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnLongClickListener {
    private Context context;
    private List<Notebook> notebookList;
    private NotebookOpenHelper notebookOpenHelper;

    private int lastPosition = -1;

    public NotebookRecyclerAdapter(
            Context context,
            List<Notebook> notebookList,
            NotebookOpenHelper notebookOpenHelper) {

        this.context = context;
        this.notebookList = notebookList;
        this.notebookOpenHelper = notebookOpenHelper;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotebookViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_notebook, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder mViewHolder, int position) {
        final NotebookViewHolder notebookViewHolder = (NotebookViewHolder) mViewHolder;
        final Notebook notebook = notebookList.get(position);

        notebookViewHolder.noteBookNameTextView.setText(notebook.getNoteBookName());
        notebookViewHolder.wordsCountTextView.setText(context.getString(R.string.words_count) + ": " + Utilities.convertNumberToPersian(notebook.getWordsCount()));

        mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordsListActivity.start(context, notebook.getId(), notebook.getNoteBookName());

            }
        });

        notebookViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomDialogBuilder(context, R.color.primary_text, R.color.colorAccent)
                        .setTitle(R.string.delete_notebook)
                        .setMessage(R.string.do_you_want_to_delete_this_notebook)
                        .setPositive(R.string.yes, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int currentPosition = mViewHolder.getAdapterPosition();

                                notebookOpenHelper.deleteNotebook(notebook.getId());
                                notebookList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);

                                final boolean[] isReturned = {false};
                                Snackbar.make(mViewHolder.itemView, R.string.was_deleted, Snackbar.LENGTH_LONG)
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
                                            SavedWordsFragment.updateMebyBroadcast(context);
                                        }
                                    }
                                }.start();
                            }
                        }).setNegative(R.string.no, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).create().show();
            }
        });

        notebookViewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditNoteBookDialog(notebook, mViewHolder.getAdapterPosition());
            }
        });

        notebookViewHolder.deleteButton.setOnLongClickListener(this);
        notebookViewHolder.editButton.setOnLongClickListener(this);

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
        notebookList.add(0, notebook);
        notifyItemInserted(0);
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
            case R.id.btn_edit:
                ToolTip.show(context, context.getString(R.string.showing_in_app_widget), v);
                break;
        }
        return true;
    }

    static class NotebookViewHolder extends RecyclerView.ViewHolder {

        private TextView noteBookNameTextView;
        private TextView wordsCountTextView;

        private ImageButton deleteButton, editButton;

        private LinearLayout linearImages;

        public NotebookViewHolder(@NonNull View itemView) {
            super(itemView);
            noteBookNameTextView = itemView.findViewById(R.id.txt_notebook_name);
            wordsCountTextView = itemView.findViewById(R.id.txt_words_count);

            deleteButton = itemView.findViewById(R.id.btn_delete);
            editButton = itemView.findViewById(R.id.btn_edit);

            linearImages = itemView.findViewById(R.id.linear_imgs);
        }
    }
}
