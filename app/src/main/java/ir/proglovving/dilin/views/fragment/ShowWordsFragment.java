package ir.proglovving.dilin.views.fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ir.proglovving.dilin.CustomDialogBuilder;
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
    private CoordinatorLayout coordinatorLayout;
    private boolean isBookmarkedMode;
    private int refreshType = REFRESH_TYPE_CURRENT;
    private RecyclerView.OnScrollListener onScrollListener;
    private int notebookId;

    private Dialog addAndEditWordDialog;
    Button verifyButton, cancelButton;
    EditText wordEditText, meaningEditText;
    TextInputLayout wordTextInputLayout, meaningTextInputLayout;
    private LinearLayout dialogContainer;


    public ShowWordsFragment(CoordinatorLayout coordinatorLayout, boolean bookmarkedMode, int refreshType, RecyclerView.OnScrollListener onScrollListener, int notebookId) {
        this.coordinatorLayout = coordinatorLayout;
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
                getContext(), words, this, notebookId
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
                getContext(), words, this, notebookId
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
                || (isBookmarkedMode && openHelper.getBookmarkedWords().size() == 0) // یا اگر در حالت نشان شده بود و کلمه ی نشان شده ای نبود
        ) { //  جست و جو نکن و در همان شرایط قبلی بمان
            return;
        }

//        if(search.length() == 0){
//            refreshRecyclerView(REFRESH_TYPE_SETUP);
//            return;
//        }

        List<Word> words = getSuitableNotebooksList(isBookmarkedMode, search);

        if (words.size() == 0) { // اگر کلمه ای یافت نشد
            // TODO: 2/3/19 اصلاح پیام کلمه ای اضافه نشده است به کلمه ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.nothing_was_found);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }
        WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(
                getContext(), words, this, notebookId
        );

        recyclerView.setAdapter(adapter);
    }

    private List<Word> getSuitableNotebooksList(boolean isBookmarkedMode) {
        List<Word> words;
        if (isBookmarkedMode) {
            words = new WordsOpenHelper(getContext(), notebookId).getBookmarkedWords();
        } else {
            words = new WordsOpenHelper(getContext(), notebookId).getWordList();
        }

        return words;
    }

    private List<Word> getSuitableNotebooksList(boolean isBookmarkedMode, String search) {
        List<Word> result = new ArrayList<>();

        List<Word> words;
        if (isBookmarkedMode) {
            words = new WordsOpenHelper(getContext(), notebookId).getBookmarkedWords();
        } else {
            words = new WordsOpenHelper(getContext(), notebookId).getWordList();
        }

        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).getWord().contains(search) || words.get(i).getMeaning().contains(search)) {
                result.add(words.get(i));
            }
        }

        return result;
    }

    private void showBrowseAddWordDialog() {

        setupDialog();
//        WordsOpenHelper openHelper = new WordsOpenHelper(this);
//
//        Word voc = new Word();
//        voc.setWord("hello");
//        voc.setMeaning("سلام");
//
//        openHelper.addWord(voc);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wordEditText.getText().length() == 0) {
                    wordEditText.setError(getString(R.string.no_word_was_entered_text));
                    return;
                } // TODO: 1/29/19 خط زیر اصلاح شود زیرا تکراری است(در پایین تر تکرار شده است)
                else if (new WordsOpenHelper(getContext(), notebookId).
                        isThereWord(wordEditText.getText().toString())) {
                    wordEditText.setError(getString(R.string.word_is_repeated));
                    return;
                }

                WordsOpenHelper openHelper = new WordsOpenHelper(getContext(), notebookId);
                Word word = new Word();
                word.setWord(wordEditText.getText().toString());
                word.setMeaning(meaningEditText.getText().toString());
                openHelper.addWord(word);

                refreshRecyclerView(REFRESH_TYPE_END);

                addAndEditWordDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAndEditWordDialog.dismiss();
            }
        });
        addAndEditWordDialog.show();

        dialogContainerRevealEffectShow();

    }

    private void showBrowseEditWordDialog(final int id, final int position) {
        setupDialog();

        Word word = new WordsOpenHelper(getContext(), notebookId).getWord(id);
        wordEditText.setText(word.getWord());
        meaningEditText.setText(word.getMeaning());

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wordEditText.getText().length() == 0) {
//                    wordEditText.setError(getString(R.string.no_word_was_entered_text));
                    wordTextInputLayout.setError(getString(R.string.no_word_was_entered_text));
                    return;
                }

                WordsOpenHelper openHelper = new WordsOpenHelper(getContext(), notebookId);
                Word word = new Word();
                word.setWord(wordEditText.getText().toString());
                word.setMeaning(meaningEditText.getText().toString());
                openHelper.update(word, id);

                refreshRecyclerViewInCurrentPosition(position);

                addAndEditWordDialog.dismiss();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAndEditWordDialog.dismiss();
            }
        });

        addAndEditWordDialog.show();

        dialogContainerRevealEffectShow();

    }

    private void dialogContainerRevealEffectShow() {
        dialogContainer.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Animator animator =
                            ViewAnimationUtils.createCircularReveal(dialogContainer,
                                    dialogContainer.getWidth(), dialogContainer.getHeight(), 0,
                                    (float) Math.hypot(dialogContainer.getWidth(), dialogContainer.getHeight()));
                    dialogContainer.setVisibility(View.VISIBLE);
                    animator.start();
                } else {
                    dialogContainer.setVisibility(View.VISIBLE);
                }

            }
        }, 100);

    }

    private void setupDialog() {
        // TODO: 1/14/19 value 'true' in below code have to be improved later !
        if (true
//                ||
//                addAndEditWordDialog == null ||
//                wordEditText == null ||
//                meaningEditText == null ||
//                verifyButton == null ||
//                cancelButton == null
        ) {

            addAndEditWordDialog = new Dialog(getContext());
            addAndEditWordDialog.setContentView(R.layout.dialog_add_word);
            addAndEditWordDialog.setTitle(R.string.adding_word_text);
            dialogContainer = (LinearLayout) addAndEditWordDialog.findViewById(R.id.ll_dialog_add_word);
            dialogContainer.setVisibility(View.INVISIBLE);

            wordEditText = (EditText) addAndEditWordDialog.findViewById(R.id.et_word);
            meaningEditText = (EditText) addAndEditWordDialog.findViewById(R.id.et_meaning);
            wordTextInputLayout = (TextInputLayout) addAndEditWordDialog.findViewById(R.id.text_input_word);
            meaningTextInputLayout = (TextInputLayout) addAndEditWordDialog.findViewById(R.id.text_input_meaning);
            verifyButton = (Button) addAndEditWordDialog.findViewById(R.id.btn_verify);
            cancelButton = (Button) addAndEditWordDialog.findViewById(R.id.btn_cancel);
        } else {
            wordEditText.setText("");
            meaningEditText.setText("");
        }
    }


    @Override
    public void onDeleteClick(final Word word, final int position) {
        new CustomDialogBuilder(getContext())
                .setTitle(R.string.delete_word_text)
                .setMessage(R.string.do_you_want_to_delete_this_word_text)
                .setPositive(R.string.yes_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new WordsOpenHelper(getContext(), notebookId).deleteWord(word.getId());
//                        refreshRecyclerView(REFRESH_TYPE_CURRENT, position - 1);
//                        refreshInCurrentPosition(position - 1);
                        refreshRecyclerViewInCurrentPosition(position - 1);
                    }
                }).setNegative(R.string.no_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).create().show();
    }

    @Override
    public void onBookmarkClick(Word word) {
        // TODO: 12/20/18  نمایش متن <<به لیست علاقه مندی ها افزوده شد>> و << از لیست علاقه مندی ها حذف شد>> اضافه شود
    }

    @Override
    public void onEditClick(Word word, int position) {
        showBrowseEditWordDialog(word.getId(), position);
    }


}
