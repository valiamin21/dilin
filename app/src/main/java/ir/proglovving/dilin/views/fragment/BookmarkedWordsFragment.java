package ir.proglovving.dilin.views.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.adapters.WordsRecyclerViewAdapter;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;

public class BookmarkedWordsFragment extends Fragment implements WordsRecyclerViewAdapter.EventOfWordMeaningRecyclerView {

    private static final String TAG = "BookmarkedWordsFragment";

    private static final String ARGS_REFRESH_NEEDED = "refresh_needed";
    private static final String ARGS_CURRENT_POSITION = "current_position";

    public static final int REFRESH_TYPE_SETUP = -1;
    public static final int REFRESH_TYPE_CURRENT = -2;
    public static final int REFRESH_TYPE_END = -3;

    private RecyclerView recyclerView;
    private NestedScrollView emptyMessageContainer;

    private BookmarksReceiver receiver;

    public BookmarkedWordsFragment() {
        // Required empty public constructor
    }

    public static BookmarkedWordsFragment newInstance() {
        BookmarkedWordsFragment fragment = new BookmarkedWordsFragment();

        return fragment;
    }

    public static void updateMebyBroadcast(Context context){
        context.sendBroadcast(new Intent("ir.proglovving.dilin.BookmarkedFragmentRefresh"));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = new Bundle();
        args.putBoolean(ARGS_REFRESH_NEEDED,false);
        args.putInt(ARGS_CURRENT_POSITION,0);
        setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        receiver = new BookmarksReceiver();
        getContext().registerReceiver(receiver, new IntentFilter("ir.proglovving.dilin.BookmarkedFragmentRefresh"));

        View view = inflater.inflate(R.layout.fragment_bookmarked_words, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_bookmarked_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        emptyMessageContainer = view.findViewById(R.id.nested_scroll_view_empty);

        refreshRecyclerView(REFRESH_TYPE_SETUP);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(receiver);
    }

    public void refreshIfNeeded(){

        Bundle args = getArguments();
        if(args != null){
            if (args.getBoolean(ARGS_REFRESH_NEEDED)) {
                refreshRecyclerViewInCurrentPosition(args.getInt(ARGS_CURRENT_POSITION));
            }
        }
    }

    public void refreshRecyclerViewInCurrentPosition(int currentPosition) {
        getArguments().putBoolean(ARGS_REFRESH_NEEDED,false);

        List<Word> words = WordsOpenHelper.getAllWords(getContext(), true);

        if (words.size() == 0) { // اگر کلمه ی نشان شده ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyMessageContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            return;
        }

        emptyMessageContainer.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(getContext(), words, this);

        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(currentPosition);
    }

    public void refreshRecyclerView(int refreshType) {
        getArguments().putBoolean(ARGS_REFRESH_NEEDED,false);

        List<Word> words = WordsOpenHelper.getAllWords(getContext(), true);

        if (words.size() == 0) { // اگر کلمه ی نشان شده ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyMessageContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            return;
        }

        emptyMessageContainer.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(
                getContext(), words, this
        );

        recyclerView.setAdapter(adapter);

        switch (refreshType) {
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
        ShowNoteBooksFragment.updateMeByBroadcast(getContext());
    }

    @Override
    public void onBookmarkClick(Word word,int position) {
        getArguments().putBoolean(ARGS_REFRESH_NEEDED, true);
        getArguments().putInt(ARGS_CURRENT_POSITION,position-1); // because of wrong with deleting last item of recyclerView
        ShowNoteBooksFragment.updateMeByBroadcast(getContext());
    }

    @Override
    public void onWordEdited(int position) {
        refreshRecyclerViewInCurrentPosition(position);
    }

    public class BookmarksReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshRecyclerView(REFRESH_TYPE_SETUP);
        }
    }
}
