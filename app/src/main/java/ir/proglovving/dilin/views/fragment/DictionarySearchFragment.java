package ir.proglovving.dilin.views.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
import ir.proglovving.dilin.adapters.DictionaryRecyclerAdapter;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.data_model.DictionaryWord;
import ir.proglovving.dilin.database_open_helpers.DictionaryOpenHelper;

public class DictionarySearchFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private View toolbarView;
    private ImageButton eraseButton, searchButton;
    private EditText searchEditText;
    private TextView guideTextView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private Thread searchingThread;
    private DictionaryOpenHelper dictionaryOpenHelper;
    private List<DictionaryWord> dictionaryWordList;

    private int recyclerViewPaddingTop;

    public DictionarySearchFragment() {
        // Required empty public constructor
    }

    public static DictionarySearchFragment newInstance() {
        DictionarySearchFragment fragment = new DictionarySearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: 8/10/19 یه نگاهی به کدای کامنت شده ی زیر بنداز
//        searchingThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                dictionaryOpenHelper = new DictionaryOpenHelper(getContext());
//
//                final RecyclerView.Adapter adapter = new DictionaryRecyclerAdapter(
//                        getContext(),
//                        dictionaryOpenHelper.getWordList(searchText),
//                        recyclerViewPaddingTop
//                );
//
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        recyclerView.setVisibility(View.VISIBLE);
//                        recyclerView.setAdapter(adapter);
//                        progressBar.setVisibility(View.INVISIBLE);
//                    }
//                });
//
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_dictionary_search, container, false);
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
                guideTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
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
        guideTextView = view.findViewById(R.id.tv_dictionary_guide);
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
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
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
                performSearch();
                break;
        }
    }

    private void performSearch(){
        guideTextView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                dictionaryOpenHelper = new DictionaryOpenHelper(getContext());
                dictionaryWordList = dictionaryOpenHelper.getDictionaryWordList(searchEditText.getText().toString());
                final boolean wordFoundInSearch = dictionaryWordList.size() != 0;

                final RecyclerView.Adapter adapter = new DictionaryRecyclerAdapter(
                        getContext(),
                        dictionaryWordList,
                        recyclerViewPaddingTop
                );

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (wordFoundInSearch) {
                            guideTextView.setVisibility(View.INVISIBLE);
                        } else {
                            guideTextView.setVisibility(View.VISIBLE);
                        }
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

            }
        }).start();
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_erase:
                ToolTip.show(getContext(),getContext().getString(R.string.erase),v);
                break;
            case R.id.btn_search:
                ToolTip.show(getContext(),getContext().getString(R.string.search),v);
                break;
        }
        return true;
    }
}
