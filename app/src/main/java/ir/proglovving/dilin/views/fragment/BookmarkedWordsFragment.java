package ir.proglovving.dilin.views.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.adapters.WordsRecyclerViewAdapter;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;

public class BookmarkedWordsFragment extends Fragment implements WordsRecyclerViewAdapter.WordsRecyclerViewEvent {

    private static final String ARG_REFRESH_REQUIRED = "refresh_required";

    private RecyclerView recyclerView;
    private NestedScrollView emptyMessageContainer;

    private WordsRecyclerViewAdapter recyclerAdapter;
    private NotebookOpenHelper notebookOpenHelper;

    private BookmarksReceiver receiver;

    public BookmarkedWordsFragment() {
        // Required empty public constructor
    }

    public static BookmarkedWordsFragment newInstance() {
        return new BookmarkedWordsFragment();
    }

    public static void updateMebyBroadcast(Context context) {
        context.sendBroadcast(new Intent("ir.proglovving.dilin.BookmarkedFragmentRefresh"));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notebookOpenHelper = new NotebookOpenHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        receiver = new BookmarksReceiver();
        getContext().registerReceiver(receiver, new IntentFilter("ir.proglovving.dilin.BookmarkedFragmentRefresh"));

        View view = inflater.inflate(R.layout.fragment_bookmarked_words, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_bookmarked_fragment);
        emptyMessageContainer = view.findViewById(R.id.nested_scroll_view_empty);

        refreshRecyclerView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(receiver);
    }

    public void refreshRecyclerView() {
        List<Word> words = WordsOpenHelper.getAllWords(getContext(), notebookOpenHelper, true);

        if (words.size() == 0) { // اگر کلمه ی نشان شده ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyMessageContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            return;
        }

        emptyMessageContainer.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        if (recyclerAdapter == null) {
            recyclerAdapter = new WordsRecyclerViewAdapter(
                    getContext(), words, this
            );
            recyclerView.setAdapter(recyclerAdapter);
        } else {
            recyclerAdapter.setWordList(words);
        }

        setRefreshRequirement(false);
    }

    public void refreshIfRequired() {
        if (isRefreshRequired()) {
            refreshRecyclerView();
            setRefreshRequirement(false);
        }
    }

    private void setRefreshRequirement(boolean refreshRequirement) {
        getArguments().putBoolean(ARG_REFRESH_REQUIRED, refreshRequirement);
    }

    private boolean isRefreshRequired() {
        if (getArguments() == null) {
            Bundle args = new Bundle();
            args.putBoolean(ARG_REFRESH_REQUIRED, false);
            setArguments(args);
            return false;
        } else {
            return getArguments().getBoolean(ARG_REFRESH_REQUIRED);
        }
    }

    @Override
    public void onBookmarked() {
        NotebookListFragment.updateMeByBroadcast(getContext());
        setRefreshRequirement(true);
    }

    @Override
    public void onDeleted() {
        NotebookListFragment.updateMeByBroadcast(getContext());
    }

    @Override
    public void onWordEdited() {
        NotebookListFragment.updateMeByBroadcast(getContext());
    }

    public class BookmarksReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshRecyclerView();
        }
    }
}
