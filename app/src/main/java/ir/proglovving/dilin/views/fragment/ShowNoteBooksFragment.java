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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.adapters.NotebookRecyclerAdapter;
import ir.proglovving.dilin.custom_views.MotionableTextView;
import ir.proglovving.dilin.data_model.Notebook;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;

@SuppressLint("ValidFragment")
public class ShowNoteBooksFragment extends Fragment {

    private RecyclerView recyclerView;
    private MotionableTextView emptyTextView;
    private FloatingActionButton fabAddNotebook;
    private CoordinatorLayout coordinatorLayout;
    private SwitchCompat favoriteSwitchButton;

    private NotebookOpenHelper notebookOpenHelper;
    private NotebookRecyclerAdapter recyclerAdapter;

    private UpdateNotebooksBroadcast updateNotebooksReceiver;

    public static void updateMeByBroadcast(Context context) {
        context.sendBroadcast(new Intent("ir.proglovving.dilin.updateNotebooksBroadcast"));
    }

    public ShowNoteBooksFragment(NotebookOpenHelper notebookOpenHelper, CoordinatorLayout coordinatorLayout, FloatingActionButton addFab) {
        this.notebookOpenHelper = notebookOpenHelper;
        this.coordinatorLayout = coordinatorLayout;
        this.fabAddNotebook = addFab;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        updateNotebooksReceiver = new UpdateNotebooksBroadcast();
        getContext().registerReceiver(updateNotebooksReceiver, new IntentFilter("ir.proglovving.dilin.updateNotebooksBroadcast"));

        final View view = inflater.inflate(R.layout.fragment_show_notebooks, container, false);
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
        favoriteSwitchButton = view.findViewById(R.id.switch_favorite);
        favoriteSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                recyclerAdapter.setNotebookList(getSuitableNotebooksList(b));
            }
        });

        setupRecyclerView();

        fabAddNotebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteBookDialog();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(updateNotebooksReceiver);
    }

    public void addNotebook(Notebook notebook) {
        notebookOpenHelper.addNotebook(notebook);
        notebook.setId(notebookOpenHelper.getLastID());
        recyclerAdapter.addNotebook(notebook);
        recyclerView.smoothScrollToPosition(recyclerAdapter.getItemCount() - 1);
    }

    public void setupRecyclerView() {

        List<Notebook> notebooks = getSuitableNotebooksList(false);

        if (notebookOpenHelper.getRawsCount() == 0) { // اگر هیچ دفتری ساخته نشده بود!
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_notebook_has_been_made_yet);
            emptyTextView.setVisibility(View.VISIBLE);
            return;
        } else if (favoriteSwitchButton.isChecked() && notebooks.size() == 0) { // اگر در حالت مورد علاقه بود و دفتر موردعلاقه ای یافت نشد!
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_favorite_notebook_was_found);
            emptyTextView.setVisibility(View.VISIBLE);
            return;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
        }

        if (notebooks.size() == 0) { //  if no notebook was made then
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
            return;
        } else if (recyclerView.getVisibility() == View.INVISIBLE) { // اگر دفتری موجود بود و پیام دفتری موجود نیست در حال نمایش بود
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
        }

        recyclerAdapter = new NotebookRecyclerAdapter(
                getContext(), notebooks, coordinatorLayout, notebookOpenHelper
        );
        recyclerView.setAdapter(recyclerAdapter);

    }


    private List<Notebook> getSuitableNotebooksList(boolean isFavoriteMode) {
        List<Notebook> notebooks;
        if (isFavoriteMode) {
            notebooks = notebookOpenHelper.getFavoriteNotebookList();
        } else {
            notebooks = notebookOpenHelper.getNotebookList();
        }

        return notebooks;
    }

    public void showAddNoteBookDialog() {
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
                Notebook notebook = new Notebook();
                notebook.setNoteBookName(notebookNameEditText.getText().toString());
                notebook.setFavorite(false);

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

    public class UpdateNotebooksBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            recyclerAdapter.setNotebookList(getSuitableNotebooksList(favoriteSwitchButton.isChecked()));
        }
    }
}
