package ir.proglovving.dilin.views.fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.adapters.NotebookRecyclerAdapter;
import ir.proglovving.dilin.data_model.Notebook;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;

@SuppressLint("ValidFragment")
public class NotebookListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ExtendedFloatingActionButton fabAddNotebook;

    private NotebookOpenHelper notebookOpenHelper;
    private NotebookRecyclerAdapter recyclerAdapter;

    private UpdateNotebooksBroadcast updateNotebooksReceiver;

    public static void updateMeByBroadcast(Context context) {
        context.sendBroadcast(new Intent("ir.proglovving.dilin.updateNotebooksBroadcast"));
    }

    private static NotebookListFragment instance;

    public static NotebookListFragment getInstance(NotebookOpenHelper notebookOpenHelper, CoordinatorLayout coordinatorLayout, ExtendedFloatingActionButton addFab) {
        if (instance == null) {
            instance = new NotebookListFragment(notebookOpenHelper, addFab);
        }
        return instance;
    }

    public NotebookListFragment(NotebookOpenHelper notebookOpenHelper, ExtendedFloatingActionButton addFab) {
        this.notebookOpenHelper = notebookOpenHelper;
        this.fabAddNotebook = addFab;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        updateNotebooksReceiver = new UpdateNotebooksBroadcast();
        getContext().registerReceiver(updateNotebooksReceiver, new IntentFilter("ir.proglovving.dilin.updateNotebooksBroadcast"));

        final View view = inflater.inflate(R.layout.fragment_notebook_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fabAddNotebook.hide();
                } else {
                    fabAddNotebook.show();
                }
            }
        });


        emptyTextView = view.findViewById(R.id.tv_empty);

        setupRecyclerView();
        return view;
    }

    public View.OnClickListener getFabAddNotebookOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteBookDialog();
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(updateNotebooksReceiver);
    }

    private void addNotebook(Notebook notebook) {
        showNotebookRecyclerView();
        notebookOpenHelper.addNotebook(notebook);
        notebook.setId(notebookOpenHelper.getLastID());
        recyclerAdapter.addNotebook(notebook);
        recyclerView.smoothScrollToPosition(0);
    }

    private void setupRecyclerView() {

        List<Notebook> notebookList = notebookOpenHelper.getNotebookList();

        recyclerAdapter = new NotebookRecyclerAdapter(
                getContext(), notebookList, notebookOpenHelper
        );

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(recyclerAdapter);

        validateEmptyDataSet(notebookList);

    }

    private void showNotebookRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.INVISIBLE);
    }

    private void hideNotebookRecyclerView() {
        recyclerView.setVisibility(View.INVISIBLE);
        emptyTextView.setVisibility(View.VISIBLE);
    }

    private void validateEmptyDataSet(List<Notebook> notebookList) {

        if (notebookOpenHelper.getRawsCount() == 0) { // اگر هیچ دفتری ساخته نشده بود!
            emptyTextView.setText(R.string.no_notebook_has_been_made_yet);
            hideNotebookRecyclerView();
            startShakeAnimation();

            return;
        } else {
            showNotebookRecyclerView();
        }

        if (notebookList.size() == 0) { //  if no notebook was made then
            hideNotebookRecyclerView();
        } else if (recyclerView.getVisibility() == View.INVISIBLE) { // اگر دفتری موجود بود و پیام دفتری موجود نیست در حال نمایش بود
            showNotebookRecyclerView();
        }
    }

    private void showAddNoteBookDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_notebook);
        final LinearLayout dialogContainer = dialog.findViewById(R.id.ll_dialog_add_notebook);
        final EditText notebookNameEditText = dialog.findViewById(R.id.et_notebook);
        Button verifyButton = dialog.findViewById(R.id.btn_verify), cancelButton = dialog.findViewById(R.id.btn_cancel);

        // کد زیر کیبورد گوشی را برای ادیت تکست نمایش می دهد
        notebookNameEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utilities.showSoftKeyboard(notebookNameEditText, getContext());
            }
        }, 100);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notebookNameEditText.getText().length() == 0) {
                    notebookNameEditText.setError(getString(R.string.no_name_has_been_entered));
                    return;
                } else if (notebookOpenHelper.isThereNotebook(notebookNameEditText.getText().toString())) {
                    notebookNameEditText.setError(getString(R.string.notebook_is_repeated));
                    return;
                }
                fabAddNotebook.clearAnimation();
                Notebook notebook = new Notebook();
                notebook.setNoteBookName(notebookNameEditText.getText().toString());

                addNotebook(notebook);
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

    private void startShakeAnimation() {
        ScaleAnimation shakeAnimation = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shakeAnimation.setDuration(400);
        shakeAnimation.setRepeatMode(Animation.REVERSE);
        shakeAnimation.setRepeatCount(Animation.INFINITE);
        shakeAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        fabAddNotebook.startAnimation(shakeAnimation);
    }

    public class UpdateNotebooksBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            recyclerAdapter.setNotebookList(notebookOpenHelper.getNotebookList());
        }
    }
}
