package com.example.amin.dictionande.views.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amin.dictionande.R;
import com.example.amin.dictionande.adapters.NotebookRecyclerAdapter;
import com.example.amin.dictionande.custom_views.MotionableTextView;
import com.example.amin.dictionande.data_model.Notebook;
import com.example.amin.dictionande.database_open_helpers.NotebookOpenHelper;

import java.util.List;

@SuppressLint("ValidFragment")
public class ShowNoteBooksFragment extends Fragment implements NotebookRecyclerAdapter.EventOfNotebookRecyclerAdapter {

    public static final int REFRESH_TYPE_SETUP = -1;
    public static final int REFRESH_TYPE_CURRENT = -2;
    public static final int REFRESH_TYPE_END = -3;

    private boolean isFavoriteMode;
    private RecyclerView recyclerView;
    private NestedScrollView emptyMessageNestedScrollView;
    private MotionableTextView emptyTextView;
    private int refreshType = REFRESH_TYPE_CURRENT;
    private RecyclerView.OnScrollListener onScrollListener;
    private CoordinatorLayout coordinatorLayout;

    private int lastRecyclerScrollState = 0;

    @SuppressLint("ValidFragment")
    public ShowNoteBooksFragment(CoordinatorLayout coordinatorLayout, boolean favoriteMode) {
        this.coordinatorLayout = coordinatorLayout;
        this.isFavoriteMode = favoriteMode;
    }

    public ShowNoteBooksFragment(CoordinatorLayout coordinatorLayout, boolean favoriteMode, int refreshType, RecyclerView.OnScrollListener onScrollListener) {
        this.coordinatorLayout = coordinatorLayout;
        this.isFavoriteMode = favoriteMode;
        this.refreshType = refreshType;
        this.onScrollListener = onScrollListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_show_notebooks, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastRecyclerScrollState += dy;
            }
        });


        emptyMessageNestedScrollView = (NestedScrollView) view.findViewById(R.id.nested_scroll_view_empty);
        emptyTextView = (MotionableTextView)view.findViewById(R.id.tv_empty);

        refreshRecyclerView(refreshType);

        recyclerView.addOnScrollListener(onScrollListener);

        return view;
    }

    public boolean isFavoriteMode() {
        return isFavoriteMode;
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

        List<Notebook> notebooks = getSuitableNotebooksList(isFavoriteMode);

        if(new NotebookOpenHelper(getContext()).getRawsCount() == 0){ // اگر هیچ دفتری ساخته نشده بود!
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_notebook_has_been_made_yet);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        }else if(isFavoriteMode && notebooks.size() == 0){ // اگر در حالت مورد علاقه بود و دفتر موردعلاقه ای یافت نشد!
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_favorite_notebook_was_found);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }

        if (notebooks.size() == 0) { //  if no notebook was made then
            recyclerView.setVisibility(View.INVISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        }else if (recyclerView.getVisibility() == View.INVISIBLE) { // اگر دفتری موجود بود و پیام دفتری موجود نیست در حال نمایش بود
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }

        NotebookRecyclerAdapter adapter = new NotebookRecyclerAdapter(
                getContext(), notebooks, coordinatorLayout, this);
        recyclerView.setAdapter(adapter);

        if (refreshType == REFRESH_TYPE_SETUP) {

        } else if (refreshType == REFRESH_TYPE_END) {
            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        } else if (refreshType == REFRESH_TYPE_CURRENT) {
//            recyclerView.scrollTo(0,scrollState);
//            recyclerView.scrollBy(0,scrollState);
//            recyclerView.smoothScrollBy(0,50);
            recyclerView.scrollBy(0,lastRecyclerScrollState);
            lastRecyclerScrollState /= 2;
        }

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

    @Override
    public void onDeleteClick(final Notebook notebook, final int position) {

    }

    @Override
    public void onFavoriteClick(Notebook notebook) {
//        if(notebook.isFavorite()){
//            notebook.setFavorite(false);
//        }else{
//            notebook.setFavorite(true);
//        }
//
//        new NotebookOpenHelper(getContext()).update(notebook);
    }

    @Override
    public void onPlayClick(Notebook notebook) {

    }

    @Override
    public void onRefreshInCurrentPosition(int position) {
        // TODO: 2/5/19 از بین دو خط زیر یکیشو بر اساس صلاح برنامه انتخاب کن
//        refreshRecyclerViewInCurrentPosition(position - 1);
        refreshRecyclerViewInCurrentPosition(position);
    }

}
