package ir.proglovving.dilin.views.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.adapters.WordsRecyclerViewAdapter;
import ir.proglovving.dilin.custom_views.MotionableTextView;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;

@SuppressLint("ValidFragment")
public class WordListFragment extends Fragment implements WordsRecyclerViewAdapter.WordsRecyclerViewEvent {

    private RecyclerView recyclerView;
    private NestedScrollView emptyMessageNestedScrollView;
    private MotionableTextView emptyTextView;
    private boolean isBookmarkedMode;
    private RecyclerView.OnScrollListener onScrollListener;
    private int notebookId;
    private WordsRecyclerViewAdapter recyclerViewAdapter;

    public static WordListFragment newInstance(boolean bookmarkedMode, RecyclerView.OnScrollListener onScrollListener, int notebookId) {
        return new WordListFragment(bookmarkedMode, onScrollListener, notebookId);
    }

    public WordListFragment(boolean bookmarkedMode, RecyclerView.OnScrollListener onScrollListener, int notebookId) {
        this.isBookmarkedMode = bookmarkedMode;
        this.onScrollListener = onScrollListener;
        this.notebookId = notebookId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyMessageNestedScrollView = view.findViewById(R.id.nested_scroll_view_empty);
        emptyTextView = view.findViewById(R.id.tv_empty);

        refreshRecyclerView();

        recyclerView.addOnScrollListener(onScrollListener);

        return view;
    }

    public void refreshRecyclerView() {
        List<Word> words = new WordsOpenHelper(getContext(), notebookId).getWordList(false);

        if (recyclerViewAdapter == null) {
            recyclerViewAdapter = new WordsRecyclerViewAdapter(getContext(), words, this);
            recyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    checkWarningVisibilities(new WordsOpenHelper(getContext(), notebookId).getRawsCount());
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    checkWarningVisibilities(new WordsOpenHelper(getContext(), notebookId).getRawsCount());
                }

            });
        }

        checkWarningVisibilities(words.size());

        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(recyclerViewAdapter);
        } else {
            recyclerViewAdapter.setWordList(words);
        }
    }

    private void checkWarningVisibilities(int wordsCount) {
        if (wordsCount == 0) { //  اگر هیچ کلمه ای اضافه نشده باشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_word_was_added);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
        } else if (recyclerView.getVisibility() == View.INVISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }
    }

    public void addWord(Word word) {
        recyclerViewAdapter.addWord(word);
    }

    public void searchRefresh(String search) {
        WordsOpenHelper openHelper = new WordsOpenHelper(getContext(), notebookId);

        if (openHelper.getRawsCount() == 0 // اگر هیچ کلمه ای به دیتابیس اضافه نشده بود
                || (isBookmarkedMode && openHelper.getWordList(true).size() == 0) // یا اگر در حالت نشان شده بود و کلمه ی نشان شده ای نبود
        ) { //  جست و جو نکن و در همان شرایط قبلی بمان
            return;
        }

        List<Word> words = getSuitableWordList(isBookmarkedMode, search);

        if (words.size() == 0) { // اگر کلمه ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.nothing_was_found);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }

        recyclerViewAdapter.setWordList(words);
    }

    private List<Word> getSuitableWordList(boolean isBookmarkedMode, String search) {
        List<Word> result = new ArrayList<>();

        List<Word> words = new WordsOpenHelper(getContext(), notebookId).getWordList(isBookmarkedMode);

        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).getWord().contains(search) || words.get(i).getMeaning().contains(search)) {
                result.add(words.get(i));
            }
        }

        return result;
    }

    @Override
    public void onBookmarked() {
        // sending broadcast for refreshing notebooks fragment
        NotebookListFragment.updateMeByBroadcast(getContext());

        // sending broadcast for refreshing bookmarkedWordFragment
        BookmarkedWordsFragment.updateMebyBroadcast(getContext());
    }

    @Override
    public void onDeleted() {
        // sending broadcast for refreshing notebooks fragment
        NotebookListFragment.updateMeByBroadcast(getContext());

        // sending broadcast for refreshing bookmarkedWordFragment
        BookmarkedWordsFragment.updateMebyBroadcast(getContext());
    }

    @Override
    public void onWordEdited() {
        // sending broadcast for refreshing bookmarkedWordFragment
        BookmarkedWordsFragment.updateMebyBroadcast(getContext());
    }
}
