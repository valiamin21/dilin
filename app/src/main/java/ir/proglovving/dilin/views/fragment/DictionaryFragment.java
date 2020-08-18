package ir.proglovving.dilin.views.fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.adapters.DictionaryRecyclerAdapter;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.DictionaryOpenHelper;

public class DictionaryFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private View toolbarView;
    private ImageButton eraseButton, searchButton;
    private EditText searchEditText;
    private View guideContainer;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private Thread searchingThread;
    private DictionaryOpenHelper dictionaryOpenHelper;
    private DictionaryRecyclerAdapter recyclerAdapter;
    private List<Word> wordList;

    private int recyclerViewPaddingTop;

    public DictionaryFragment() {
        // Required empty public constructor
    }

    public static DictionaryFragment newInstance() {
        return new DictionaryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dictionaryOpenHelper = DictionaryOpenHelper.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_dictionary, container, false);
        setupViews(fragmentView);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.length() != 0) {
                    performSearch();
                } else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    guideContainer.setVisibility(View.VISIBLE);
                }

            }

        });


        return fragmentView;
    }

    private void setupViews(View view) {
        toolbarView = view.findViewById(R.id.card_toolbar);

        eraseButton = view.findViewById(R.id.btn_erase);
        searchButton = view.findViewById(R.id.btn_search);
        eraseButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        eraseButton.setOnLongClickListener(this);
        searchButton.setOnLongClickListener(this);

        searchEditText = view.findViewById(R.id.ed_search);
        guideContainer = view.findViewById(R.id.dictionary_guide_container);
        recyclerView = view.findViewById(R.id.recyclerView_dictionary);
        progressBar = view.findViewById(R.id.progressbar_dictionary);

        // below code computes toolbar height and vertical margin's sum for recyclerView top padding
        toolbarView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout.LayoutParams toolbarViewLayoutParams = (ConstraintLayout.LayoutParams) toolbarView.getLayoutParams();
                recyclerViewPaddingTop = toolbarView.getHeight() + toolbarViewLayoutParams.topMargin + toolbarViewLayoutParams.bottomMargin;
            }
        }, 1);

        // when user clicks search button of mobile keyboard following code does searching operation
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_erase:
                searchEditText.setText("");
                break;
            case R.id.btn_search:
                Utilities.showSoftKeyboard(searchEditText, getContext());
                break;
        }
    }

    private void performSearch() {
        guideContainer.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        if (searchingThread == null) {
            searchingThread = new SearchThread();
        }

        try {
            searchingThread.start();
        } catch (Exception e) {
            searchingThread.interrupt();
            searchingThread = new SearchThread();
            searchingThread.start();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_erase:
                ToolTip.show(getContext(), getContext().getString(R.string.erase), v);
                break;
            case R.id.btn_search:
                ToolTip.show(getContext(), getContext().getString(R.string.search), v);
                break;
        }
        return true;
    }

    class SearchThread extends Thread {

        private boolean isInterrupted = false;

        private DictionaryOpenHelper.OnIterationListener onIterationListener =
                new DictionaryOpenHelper.OnIterationListener() {
                    @Override
                    public boolean onInterrupted() {
                        return isInterrupted;
                    }
                };

        public void interrupt() {
            isInterrupted = true;
        }

        @Override
        public void run() {
            wordList = dictionaryOpenHelper.getDictionaryWordList(searchEditText.getText().toString(), onIterationListener);

            if (isInterrupted) return;

            if (recyclerAdapter == null) {
                recyclerAdapter = new DictionaryRecyclerAdapter(
                        getContext(),
                        wordList,
                        recyclerViewPaddingTop
                );
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (wordList.size() != 0) {
                        guideContainer.setVisibility(View.INVISIBLE);
                    } else {
                        guideContainer.setVisibility(View.VISIBLE);
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    if (recyclerView.getAdapter() == null) {
                        recyclerView.setAdapter(recyclerAdapter);
                    } else {
                        recyclerAdapter.setWordList(wordList);
                        recyclerView.scrollToPosition(0);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        }
    }
}
