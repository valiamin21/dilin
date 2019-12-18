package ir.proglovving.dilin.views.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class ShowWordsFragment extends Fragment implements WordsRecyclerViewAdapter.EventOfWordMeaningRecyclerView {

    public static final int REFRESH_TYPE_SETUP = -1;
    public static final int REFRESH_TYPE_CURRENT = -2;
    public static final int REFRESH_TYPE_END = -3;

    private RecyclerView recyclerView;
    private NestedScrollView emptyMessageNestedScrollView;
    private MotionableTextView emptyTextView;
    private boolean isBookmarkedMode;
    private int refreshType = REFRESH_TYPE_CURRENT;
    private RecyclerView.OnScrollListener onScrollListener;
    private int notebookId;

    public ShowWordsFragment(boolean bookmarkedMode, int refreshType, RecyclerView.OnScrollListener onScrollListener, int notebookId) {
        this.isBookmarkedMode = bookmarkedMode;
        this.refreshType = refreshType;
        this.onScrollListener = onScrollListener;
        this.notebookId = notebookId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_words, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        emptyMessageNestedScrollView = (NestedScrollView) view.findViewById(R.id.nested_scroll_view_empty);
        emptyTextView = (MotionableTextView) view.findViewById(R.id.tv_empty);

        refreshRecyclerView(refreshType);

        recyclerView.addOnScrollListener(onScrollListener);

        return view;
    }

    public boolean isBookmarkedMode() {
        return isBookmarkedMode;
    }

    public void refreshRecyclerViewInCurrentPosition(int currentPosition) {
        List<Word> words = getSuitableNotebooksList(isBookmarkedMode);

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

        WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(
                getContext(), words, this
        );

        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(currentPosition);
    }

    public void refreshRecyclerView(int refreshType) {
        List<Word> words = getSuitableNotebooksList(isBookmarkedMode);

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

        WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(
                getContext(), words, this
        );

        recyclerView.setAdapter(adapter);
        if (refreshType == REFRESH_TYPE_SETUP) {

        } else if (refreshType == REFRESH_TYPE_END) {
            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        } else if (refreshType == REFRESH_TYPE_CURRENT) {

        }

    }

    public void searchRefresh(String search) {
        WordsOpenHelper openHelper = new WordsOpenHelper(getContext(), notebookId);

        if (openHelper.getRawsCount() == 0 // اگر هیچ کلمه ای به دیتابیس اضافه نشده بود
                || (isBookmarkedMode && openHelper.getWordList(true).size() == 0) // یا اگر در حالت نشان شده بود و کلمه ی نشان شده ای نبود
        ) { //  جست و جو نکن و در همان شرایط قبلی بمان
            return;
        }

//        if(search.length() == 0){
//            refreshRecyclerView(REFRESH_TYPE_SETUP);
//            return;
//        }

        List<Word> words = getSuitableNotebooksList(isBookmarkedMode, search);

        if (words.size() == 0) { // اگر کلمه ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.nothing_was_found);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }
        WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(
                getContext(), words, this
        );

        recyclerView.setAdapter(adapter);
    }

    private List<Word> getSuitableNotebooksList(boolean isBookmarkedMode) {
        return new WordsOpenHelper(getContext(), notebookId).getWordList(isBookmarkedMode);
    }

    private List<Word> getSuitableNotebooksList(boolean isBookmarkedMode, String search) {
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
    public void onDeleted(int position) {
        refreshRecyclerViewInCurrentPosition(position - 1);

        // sending broadcast for refreshing bookmarkedWordFragment
        getActivity().sendBroadcast(new Intent("BookmarkedFragmentRefresh"));
    }

    @Override
    public void onBookmarkClick(Word word,int position) {
        // TODO: 12/20/18  نمایش متن <<به لیست علاقه مندی ها افزوده شد>> و << از لیست علاقه مندی ها حذف شد>> اضافه شود

        // sending broadcast for refreshing bookmarkedWordFragment
        getActivity().sendBroadcast(new Intent("BookmarkedFragmentRefresh"));
    }

    @Override
    public void onWordEdited(int position) {
        refreshRecyclerViewInCurrentPosition(position);

        // sending broadcast for refreshing bookmarkedWordFragment
        getActivity().sendBroadcast(new Intent("BookmarkedFragmentRefresh"));

    }


}
