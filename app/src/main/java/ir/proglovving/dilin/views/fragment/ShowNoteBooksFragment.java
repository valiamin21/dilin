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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
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
public class ShowNoteBooksFragment extends Fragment implements NotebookRecyclerAdapter.EventOfNotebookRecyclerAdapter {

    public static final int REFRESH_TYPE_SETUP = -1;
    public static final int REFRESH_TYPE_CURRENT = -2;
    public static final int REFRESH_TYPE_END = -3;

    private boolean isFavoriteMode;
    private RecyclerView recyclerView;
    private MotionableTextView emptyTextView;
    private int refreshType = REFRESH_TYPE_CURRENT;
    private FloatingActionButton fabAddNotebook;
    private CoordinatorLayout coordinatorLayout;

    private int lastRecyclerScrollState = 0;

    private UpdateNotebooksBroadcast updateNotebooksReceiver;

    public static void updateMeByBroadcast(Context context) {
        context.sendBroadcast(new Intent("ir.proglovving.dilin.updateNotebooksBroadcast"));
    }

    public ShowNoteBooksFragment(CoordinatorLayout coordinatorLayout, boolean favoriteMode,
                                 int refreshType, FloatingActionButton addFab) {
        this.coordinatorLayout = coordinatorLayout;
        this.isFavoriteMode = favoriteMode;
        this.refreshType = refreshType;
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
                lastRecyclerScrollState += dy;
                if (dy > 0) {
                    fabAddNotebook.hide();
                } else {
                    if (fabAddNotebook.getVisibility() == View.VISIBLE) {
                        fabAddNotebook.show();
                    }
                }
            }
        });


        emptyTextView = view.findViewById(R.id.tv_empty);
        SwitchCompat favoriteSwitchButton = view.findViewById(R.id.switch_favorite);
        favoriteSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isFavoriteMode = b;
                refreshRecyclerView(refreshType);
            }
        });

        refreshRecyclerView(refreshType);

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

    private void refreshRecyclerViewInCurrentPosition(int currentPosition) {
//        List<Notebook> notebooks = getSuitableNotebooksList(isFavoriteMode);
//
//
//        if(new NotebookOpenHelper(getContext()).getRawsCount() == 0){ // اگر هیچ دفتری ساخته نشده بود!
//            recyclerView.setVisibility(View.INVISIBLE);
//            emptyTextView.changeText(R.string.no_notebook_has_been_made_yet);
//            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
//            return;
//        }else if(isFavoriteMode && notebooks.size() == 0){ // اگر در حالت مورد علاقه بود و دفتر موردعلاقه ای یافت نشد!
//            recyclerView.setVisibility(View.INVISIBLE);
//            emptyTextView.changeText(R.string.no_favorite_notebook_was_found);
//            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
//            return;
//        }else{
//            recyclerView.setVisibility(View.VISIBLE);
//            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
//        }
//
//        NotebookRecyclerAdapter adapter = new NotebookRecyclerAdapter(
//                getContext(), notebooks, coordinatorLayout, this);
//        recyclerView.setAdapter(adapter);

        refreshRecyclerView(REFRESH_TYPE_SETUP);
        recyclerView.scrollToPosition(currentPosition);
    }

    public void refreshRecyclerView(int refreshType) {
        // for saving fabAddNotebook showing status because it will be shown or hidden while recyclerview is scrolling
        boolean fabShowingStatus = fabAddNotebook.isShown();

        List<Notebook> notebooks = getSuitableNotebooksList(isFavoriteMode);

        if (new NotebookOpenHelper(getContext()).getRawsCount() == 0) { // اگر هیچ دفتری ساخته نشده بود!
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_notebook_has_been_made_yet);
            emptyTextView.setVisibility(View.VISIBLE);
            return;
        } else if (isFavoriteMode && notebooks.size() == 0) { // اگر در حالت مورد علاقه بود و دفتر موردعلاقه ای یافت نشد!
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

        NotebookRecyclerAdapter adapter = new NotebookRecyclerAdapter(
                getContext(), notebooks, coordinatorLayout, this);
        recyclerView.setAdapter(adapter);

        if (refreshType == REFRESH_TYPE_SETUP) {

        } else if (refreshType == REFRESH_TYPE_END) {
            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        } else if (refreshType == REFRESH_TYPE_CURRENT) {
            recyclerView.scrollBy(0, lastRecyclerScrollState);
            lastRecyclerScrollState /= 2;
        }

        // for applying saved fabAddNotebook showing status because it will be shown or hidden while recyclerview is scrolling
        if (fabShowingStatus)
            fabAddNotebook.show();
        else
            fabAddNotebook.hide();

    }


    private List<Notebook> getSuitableNotebooksList(boolean isFavoriteMode) {
        List<Notebook> notebooks;
        if (isFavoriteMode) {
            notebooks = new NotebookOpenHelper(getContext()).getFavoriteNotebookList();
        } else {
            notebooks = new NotebookOpenHelper(getContext()).getNotebookList();
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
                } else if (new NotebookOpenHelper(getContext()).
                        isThereNotebook(notebookNameEditText.getText().toString())) {
                    notebookNameEditText.setError(getString(R.string.notebook_is_repeated));
                    return;
                }
                Notebook notebook = new Notebook();
                notebook.setNoteBookName(notebookNameEditText.getText().toString());
                notebook.setFavorite(false);
                new NotebookOpenHelper(getContext()).addNotebook(notebook);

                refreshRecyclerView(REFRESH_TYPE_END);
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

    @Override
    public void onDeleteClick(final Notebook notebook, final int position) {

    }

    @Override
    public void onFavoriteClick(Notebook notebook) {
    }

    @Override
    public void onEditClick(Notebook notebook) {

    }

    @Override
    public void onRefreshInCurrentPosition(int position) {
        // TODO: 2/5/19 از بین دو خط زیر یکیشو بر اساس صلاح برنامه انتخاب کن
//        refreshRecyclerViewInCurrentPosition(position - 1);
        refreshRecyclerViewInCurrentPosition(position);
    }

    public class UpdateNotebooksBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshRecyclerView(REFRESH_TYPE_CURRENT);
        }
    }
}
