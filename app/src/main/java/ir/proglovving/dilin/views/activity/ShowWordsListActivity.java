package ir.proglovving.dilin.views.activity;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;
import ir.proglovving.dilin.views.fragment.ShowNoteBooksFragment;
import ir.proglovving.dilin.views.fragment.ShowWordsFragment;

public class ShowWordsListActivity extends AppCompatActivity {

    public static final String KEY_NOTEBOOK_ID = "notebook_id";
    public static final String KEY_NOTEBOOK_NAME = "notebook_name";

    private Toolbar toolbar;
    private FloatingActionButton addFab;
    private ActionBarDrawerToggle drawerToggle;
    private FrameLayout containerFrameLayout;

    private ShowWordsFragment showWordsFragment;

    private Dialog addAndEditWordDialog;
    Button verifyButton, cancelButton;
    EditText wordEditText, meaningEditText;
    TextInputLayout wordTextInputLayout, meaningTextInputLayout;
    private LinearLayout dialogContainer;

    private String noteBookName;
    private int notebookId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_words_list);

        Intent intent = getIntent();
        noteBookName = intent.getStringExtra(KEY_NOTEBOOK_NAME);
        notebookId = intent.getExtras().getInt(KEY_NOTEBOOK_ID);


        setupViews();

        showWordsFragment = new ShowWordsFragment(
                false, ShowWordsFragment.REFRESH_TYPE_SETUP, getOnScrollListener(), notebookId
        );

        getSupportFragmentManager().beginTransaction()
                .add(containerFrameLayout.getId(), showWordsFragment)
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utilities.updateShowWordsWidget(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        setupSearchView(searchView);
//        // Retrieve the SearchView and plug it into SearchManager
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
//        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    private void setupSearchView(final SearchView searchView) {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                refreshRecyclerView(s);
//                showWordsFragment.searchRefresh(s);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                showWordsFragment.searchRefresh(s);
                return false;
            }
        });
        searchView.setQueryHint("جست و جو کنید...");

    }

    private void showBrowseAddWordDialog() {

        setupDialog();
        wordEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utilities.showSoftKeyboard(wordEditText, ShowWordsListActivity.this);
            }
        }, 100);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wordEditText.getText().length() == 0) {
                    wordEditText.setError(getString(R.string.no_word_was_entered_text));
                    return;
                } // TODO: 1/29/19 خط زیر اصلاح شود زیرا تکراری است(در پایین تر تکرار شده است)
                else if (new WordsOpenHelper(ShowWordsListActivity.this, notebookId).
                        isThereWord(wordEditText.getText().toString())) {
                    wordEditText.setError(getString(R.string.word_is_repeated));
                    return;
                }

                WordsOpenHelper openHelper = new WordsOpenHelper(ShowWordsListActivity.this, notebookId);
                Word word = new Word();
                word.setWord(wordEditText.getText().toString());
                word.setMeaning(meaningEditText.getText().toString());
                openHelper.addWord(word);
                ShowNoteBooksFragment.updateMeByBroadcast(ShowWordsListActivity.this);


//                showWordsFragment.refreshRecyclerView(ShowWordsFragment.REFRESH_TYPE_END);
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

            addAndEditWordDialog = new Dialog(this);
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

    private void setupViews() {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        addFab = (FloatingActionButton) findViewById(R.id.fab_add);
        addFab.setTag(View.VISIBLE); //  در این جا از تگ ویوی addFab به عنوان نشانه ای برای تشخیص ویزیبل بودن یا نبودن آن استفاده می شود.
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrowseAddWordDialog();
            }
        });

        setupToolbar();

        containerFrameLayout = (FrameLayout) findViewById(R.id.container_frame_layout);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(noteBookName);
        toolbar.setSubtitle(R.string.all_words);
        Utilities.applyFontForAViewGroup(toolbar, this);
        Utilities.applyPaddintBottomForToolbarSubtitle(this, toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public static void start(Context context, int notebookId, String noteBookName) {
        Intent starter = new Intent(context, ShowWordsListActivity.class);
        starter.putExtra(KEY_NOTEBOOK_ID, notebookId);
        starter.putExtra(KEY_NOTEBOOK_NAME, noteBookName);
        context.startActivity(starter);
    }

    private RecyclerView.OnScrollListener getOnScrollListener() {
        return
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) {
                            addFab.hide();
                        } else {
                            if (((int) addFab.getTag()) == View.VISIBLE) {
                                addFab.show();
                            }
                        }
                    }
                };
    }


//    private void refreshInCurrentPosition(int position) {
//        allVocsFragment = new ShowWordsFragment(
//                coordinatorLayout,false,ShowWordsFragment.REFRESH_TYPE_CURRENT,getOnScrollListener(),noteBookName
//        );
//        getSupportFragmentManager().beginTransaction()
//                .replace(containerFrameLayout.getId(),allVocsFragment)
//                .commit();
//        allVocsFragment.refreshRecyclerViewInCurrentPosition(position);
//    }
//
//    private void refresh(String s){ // برای جست و جو
//
//    }
//
//    private void refresh(int refreshPosition){
//
//    }

//    private void setupRecyclerView() {
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//
//        openHelper = new WordsOpenHelper(this, noteBookName);
//        refreshRecyclerView(REFRESH_TYPE_SETUP);
//    }
//
//    private void refreshRecyclerView(int refreshPosition) {
//        WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(this, openHelper.getWordList(), this);
//        recyclerView.setAdapter(adapter);
//        if (refreshPosition == REFRESH_TYPE_END) {
//            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
//        }
//
//    }
//
//    private void refreshRecyclerView(int refreshPosition, int position) {
//        if (refreshPosition == REFRESH_TYPE_CURRENT) {
//            WordsRecyclerViewAdapter adapter = new WordsRecyclerViewAdapter(this, openHelper.getWordList(), this);
//            recyclerView.setAdapter(adapter);
//            recyclerView.scrollToPosition(position);
//        }
//    }
//
//    private void refreshRecyclerView(String search) {
//        recyclerView.setAdapter(
//                new WordsRecyclerViewAdapter(this, openHelper.getSearchedWordList(search), this)
//        );
//    }

}
