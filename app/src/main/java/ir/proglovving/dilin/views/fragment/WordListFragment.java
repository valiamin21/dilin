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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.adapters.WordsRecyclerViewAdapter;
import ir.proglovving.dilin.data_model.NotebookWord;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;

@SuppressLint("ValidFragment")
public class WordListFragment extends Fragment implements WordsRecyclerViewAdapter.WordsRecyclerViewEvent {

    private RecyclerView recyclerView;
    private NestedScrollView emptyMessageNestedScrollView;
    private TextView emptyTextView;
    private RecyclerView.OnScrollListener onScrollListener;
    private int notebookId;
    private WordsRecyclerViewAdapter recyclerViewAdapter;

    public static WordListFragment newInstance(RecyclerView.OnScrollListener onScrollListener, int notebookId, WordsOpenHelper wordsOpenHelper) {
        return new WordListFragment(onScrollListener, notebookId, wordsOpenHelper);
    }

    public WordListFragment(RecyclerView.OnScrollListener onScrollListener, int notebookId, WordsOpenHelper wordsOpenHelper) {
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
        List<NotebookWord> words = new WordsOpenHelper(getContext(), notebookId).getWordList();

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
            emptyTextView.setText(R.string.no_word_was_added);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
        } else if (recyclerView.getVisibility() == View.INVISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }
    }

    public void addWord(NotebookWord word) {
        recyclerViewAdapter.addWord(word);
    }

    public void searchRefresh(String search) {
        WordsOpenHelper openHelper = new WordsOpenHelper(getContext(), notebookId);

        if (openHelper.getRawsCount() == 0 // اگر هیچ کلمه ای به دیتابیس اضافه نشده بود
        ) { //  جست و جو نکن و در همان شرایط قبلی بمان
            return;
        }

        List<NotebookWord> words = getSuitableWordList(search);

        if (words.size() == 0) { // اگر کلمه ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.setText(R.string.nothing_was_found);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }

        recyclerViewAdapter.setWordList(words);
    }

    private List<NotebookWord> getSuitableWordList(String search) {
        List<NotebookWord> result = new ArrayList<>();

        List<NotebookWord> words = new WordsOpenHelper(getContext(), notebookId).getWordList();

        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).getWord().contains(search) || words.get(i).getMeaning().contains(search)) {
                result.add(words.get(i));
            }
        }

        return result;
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
