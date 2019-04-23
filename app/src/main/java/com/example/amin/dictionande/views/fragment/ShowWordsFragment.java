package com.example.amin.dictionande.views.fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import com.example.amin.dictionande.R;
import com.example.amin.dictionande.adapters.WordRecyclerViewAdapter;
import com.example.amin.dictionande.custom_views.MotionableTextView;
import com.example.amin.dictionande.data_model.Voc;
import com.example.amin.dictionande.database_open_helpers.VocabularyOpenHelper;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ShowWordsFragment extends Fragment implements WordRecyclerViewAdapter.EventOfVocMeanRecyclerView {

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
    private String notebookName;

    private Dialog addAndEditWordDialog;
    Button verifyButton, cancelButton;
    EditText wordEditText, meaningEditText;
    TextInputLayout wordTextInputLayout, meaningTextInputLayout;
    private LinearLayout dialogContainer;


    public ShowWordsFragment(CoordinatorLayout coordinatorLayout, boolean bookmarkedMode, int refreshType, RecyclerView.OnScrollListener onScrollListener, String notebookName) {
        this.coordinatorLayout = coordinatorLayout;
        this.isBookmarkedMode = bookmarkedMode;
        this.refreshType = refreshType;
        this.onScrollListener = onScrollListener;
        this.notebookName = notebookName;
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

    public boolean isBookmarkedMode(){
        return isBookmarkedMode;
    }

    public void refreshRecyclerViewInCurrentPosition(int currentPosition) {
        List<Voc> vocs = getSuitableNotebooksList(isBookmarkedMode);

        if(new VocabularyOpenHelper(getContext(),notebookName).getRawsCount() == 0){ //  اگر هیچ کلمه ای اضافه نشده باشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_word_was_added);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        }else if(recyclerView.getVisibility() == View.INVISIBLE){
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }

        if (vocs.size() == 0 && isBookmarkedMode) { // اگر کلمه ی نشان شده ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_bookmarked_word_was_found);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        }

        WordRecyclerViewAdapter adapter = new WordRecyclerViewAdapter(
                getContext(), vocs, this,notebookName
        );

        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(currentPosition);
    }

    public void refreshRecyclerView(int refreshType) {
        List<Voc> vocs = getSuitableNotebooksList(isBookmarkedMode);

        if(new VocabularyOpenHelper(getContext(),notebookName).getRawsCount() == 0){ //  اگر هیچ کلمه ای اضافه نشده باشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_word_was_added);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        } else if(recyclerView.getVisibility() == View.INVISIBLE){
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }

        if (vocs.size() == 0 && isBookmarkedMode) { // اگر کلمه ی نشان شده ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.no_bookmarked_word_was_found);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        }

        WordRecyclerViewAdapter adapter = new WordRecyclerViewAdapter(
                getContext(), vocs, this,notebookName
        );

        recyclerView.setAdapter(adapter);
        if (refreshType == REFRESH_TYPE_SETUP) {

        } else if (refreshType == REFRESH_TYPE_END) {
            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        } else if (refreshType == REFRESH_TYPE_CURRENT) {

        }

    }

    public void searchRefresh(String search){
        VocabularyOpenHelper openHelper = new VocabularyOpenHelper(getContext(),notebookName);

        if(openHelper.getRawsCount() == 0 // اگر هیچ کلمه ای به دیتابیس اضافه نشده بود
            || (isBookmarkedMode && openHelper.getBookmarkedVocs().size()==0) // یا اگر در حالت نشان شده بود و کلمه ی نشان شده ای نبود
        ){ //  جست و جو نکن و در همان شرایط قبلی بمان
            return;
        }

//        if(search.length() == 0){
//            refreshRecyclerView(REFRESH_TYPE_SETUP);
//            return;
//        }

        List<Voc> vocs = getSuitableNotebooksList(isBookmarkedMode,search);

        if(vocs.size() == 0){ // اگر کلمه ای یافت نشد
            // TODO: 2/3/19 اصلاح پیام کلمه ای اضافه نشده است به کلمه ای یافت نشد
            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.changeText(R.string.nothing_was_found);
            emptyMessageNestedScrollView.setVisibility(View.VISIBLE);
            return;
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessageNestedScrollView.setVisibility(View.INVISIBLE);
        }
        WordRecyclerViewAdapter adapter = new WordRecyclerViewAdapter(
                getContext(), vocs, this, notebookName
        );

        recyclerView.setAdapter(adapter);
    }

    private List<Voc> getSuitableNotebooksList(boolean isBookmarkedMode) {
        List<Voc> vocs;
        if (isBookmarkedMode) {
            vocs = new VocabularyOpenHelper(getContext(), notebookName).getBookmarkedVocs();
        }else{
            vocs = new VocabularyOpenHelper(getContext(),notebookName).getVocList();
        }

        return vocs;
    }

    private List<Voc> getSuitableNotebooksList(boolean isBookmarkedMode, String search) {
        List<Voc> result = new ArrayList<>();

        List<Voc> vocs;
        if (isBookmarkedMode) {
            vocs = new VocabularyOpenHelper(getContext(), notebookName).getBookmarkedVocs();
        }else{
            vocs = new VocabularyOpenHelper(getContext(),notebookName).getVocList();
        }

        for (int i = 0; i < vocs.size(); i++) {
            if(vocs.get(i).getVoc().contains(search) || vocs.get(i).getMeaning().contains(search)){
                result.add(vocs.get(i));
            }
        }

        return result;
    }

    private void showBrowseAddVocabularyDialog() {

        setupDialog();
//        VocabularyOpenHelper openHelper = new VocabularyOpenHelper(this);
//
//        Voc voc = new Voc();
//        voc.setVoc("hello");
//        voc.setMeaning("سلام");
//
//        openHelper.addVoc(voc);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wordEditText.getText().length() == 0) {
                    wordEditText.setError(getString(R.string.no_word_was_entered_text));
                    return;
                } // TODO: 1/29/19 خط زیر اصلاح شود زیرا تکراری است(در پایین تر تکرار شده است)
                else if(new VocabularyOpenHelper(getContext(),notebookName).
                        isThereWord(wordEditText.getText().toString())){
                    wordEditText.setError(getString(R.string.word_is_repeated));
                    return;
                }

                VocabularyOpenHelper openHelper = new VocabularyOpenHelper(getContext(), notebookName);
                Voc voc = new Voc();
                voc.setVoc(wordEditText.getText().toString());
                voc.setMeaning(meaningEditText.getText().toString());
                openHelper.addVoc(voc);

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

    private void showBrowseEditVocabularyDialog(final int id, final int position) {
        setupDialog();

        Voc voc = new VocabularyOpenHelper(getContext(),notebookName).getVoc(id);
        wordEditText.setText(voc.getVoc());
        meaningEditText.setText(voc.getMeaning());

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wordEditText.getText().length() == 0) {
//                    wordEditText.setError(getString(R.string.no_word_was_entered_text));
                    wordTextInputLayout.setError(getString(R.string.no_word_was_entered_text));
                    return;
                }

                VocabularyOpenHelper openHelper = new VocabularyOpenHelper(getContext(), notebookName);
                Voc voc = new Voc();
                voc.setVoc(wordEditText.getText().toString());
                voc.setMeaning(meaningEditText.getText().toString());
                openHelper.update(voc, id);

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

    private void dialogContainerRevealEffectShow(){
        dialogContainer.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    Animator animator =
                            ViewAnimationUtils.createCircularReveal(dialogContainer,
                                    dialogContainer.getWidth(), dialogContainer.getHeight(), 0,
                                    (float) Math.hypot(dialogContainer.getWidth(), dialogContainer.getHeight()));
                    dialogContainer.setVisibility(View.VISIBLE);
                    animator.start();
                }else{
                    dialogContainer.setVisibility(View.VISIBLE);
                }

            }
        },100);

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
            addAndEditWordDialog.setContentView(R.layout.dialog_add_voc);
            addAndEditWordDialog.setTitle(R.string.adding_word_text);
            dialogContainer = (LinearLayout) addAndEditWordDialog.findViewById(R.id.ll);
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
    public void onDeleteClick(final Voc voc, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete_word_text)
                .setMessage(R.string.do_you_want_to_delete_this_word_text)
                .setPositiveButton(R.string.yes_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new VocabularyOpenHelper(getContext(),notebookName).deleteVoc(voc.getId());
//                        refreshRecyclerView(REFRESH_TYPE_CURRENT, position - 1);
//                        refreshInCurrentPosition(position - 1);
                        refreshRecyclerViewInCurrentPosition(position - 1);
                    }
                })
                .setNegativeButton(R.string.no_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    @Override
    public void onBookmarkClick(Voc voc) {
        // TODO: 12/20/18  نمایش متن <<به لیست علاقه مندی ها افزوده شد>> و << از لیست علاقه مندی ها حذف شد>> اضافه شود
    }

    @Override
    public void onEditClick(Voc voc, int position) {
        showBrowseEditVocabularyDialog(voc.getId(), position);
    }


}
