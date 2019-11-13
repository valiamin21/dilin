package ir.proglovving.dilin.views.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.adapters.WordsRecyclerViewAdapter;
import ir.proglovving.dilin.data_model.DetailedWord;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;

public class BookmarkedWordsFragment extends Fragment implements WordsRecyclerViewAdapter.EventOfWordMeaningRecyclerView {

    public static final int REFRESH_TYPE_SETUP = -1;
    public static final int REFRESH_TYPE_CURRENT = -2;
    public static final int REFRESH_TYPE_END = -3;

    private RecyclerView recyclerView;

    public BookmarkedWordsFragment() {
        // Required empty public constructor
    }

    public static BookmarkedWordsFragment newInstance() {
        BookmarkedWordsFragment fragment = new BookmarkedWordsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarked_words, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_bookmarked_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(getContext(), WordsOpenHelper.getDetailedWordList(getContext(), true), this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void refreshRecyclerViewInCurrentPosition(int currentPosition) {
        List<DetailedWord> words = WordsOpenHelper.getDetailedWordList(getContext(), true);

        /*
        if (new WordsOpenHelper(getContext(), notebookId).getRawsCount() == 0) { //  اگر هیچ کلمه ای اضافه نشده باشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_word_was_added);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        } else if (recyclerView.getVisibility() == View.INVISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }

        if (words.size() == 0 && isBookmarkedMode) { // اگر کلمه ی نشان شده ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_bookmarked_word_was_found);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        }
         */

        WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(getContext(), words, this);

        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(currentPosition);
    }

    public void refreshRecyclerView(int refreshType) {
        List<DetailedWord> words = WordsOpenHelper.getDetailedWordList(getContext(),true);

        /*
        if (new WordsOpenHelper(getContext(), notebookId).getRawsCount() == 0) { //  اگر هیچ کلمه ای اضافه نشده باشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_word_was_added);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        } else if (recyclerView.getVisibility() == View.INVISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }

        if (words.size() == 0 && isBookmarkedMode) { // اگر کلمه ی نشان شده ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_bookmarked_word_was_found);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        }

         */
        WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(
                getContext(), words, this
        );

        recyclerView.setAdapter(adapter);

        switch (refreshType){
            case REFRESH_TYPE_SETUP:

                break;
            case REFRESH_TYPE_END:
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                break;
            case REFRESH_TYPE_CURRENT:

                break;
        }

    }


    @Override
    public void onDeleted(int position) {
        refreshRecyclerViewInCurrentPosition(position - 1);
    }

    @Override
    public void onBookmarkClick(Word word) {

    }

    @Override
    public void onWordEdited(int position) {
        refreshRecyclerViewInCurrentPosition(position);
    }
}
