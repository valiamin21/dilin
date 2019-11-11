package ir.proglovving.dilin.views.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.adapters.WordsRecyclerViewAdapter;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;

public class BookmarkedWordsFragment extends Fragment implements WordsRecyclerViewAdapter.EventOfWordMeaningRecyclerView {

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
        View view = inflater.inflate(R.layout.fragment_bookmarked_words,container,false);
        recyclerView = view.findViewById(R.id.recycler_view_bookmarked_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(getContext(),WordsOpenHelper.getDetailedWordList(getContext(),true),this);
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onDeleteClick(Word word, int position) {
        Toast.makeText(getContext(), "deleted " + word.getWord(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookmarkClick(Word word) {
        Toast.makeText(getContext(), "bookmarked " + word.getWord(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Word word, int position) {
        Toast.makeText(getContext(), "edited " + word.getWord(), Toast.LENGTH_SHORT).show();
    }
}
