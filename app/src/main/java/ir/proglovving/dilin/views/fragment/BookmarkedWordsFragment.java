package ir.proglovving.dilin.views.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.proglovving.dilin.R;

public class BookmarkedWordsFragment extends Fragment {

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmarked_words, container, false);
    }


}
