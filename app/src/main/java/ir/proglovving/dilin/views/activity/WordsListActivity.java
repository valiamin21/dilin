package ir.proglovving.dilin.views.activity;

import android.animation.Animator;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;
import ir.proglovving.dilin.views.fragment.ShowNoteBooksFragment;
import ir.proglovving.dilin.views.fragment.ShowWordsFragment;

public class WordsListActivity extends AppCompatActivity {

    public static final String KEY_NOTEBOOK_ID = "notebook_id";
    public static final String KEY_NOTEBOOK_NAME = "notebook_name";

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
                false, getOnScrollListener(), notebookId
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
        return true;
    }

    private void setupSearchView(final SearchView searchView) {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                showWordsFragment.searchRefresh(s);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                showWordsFragment.searchRefresh(s);
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
                Utilities.showSoftKeyboard(wordEditText, WordsListActivity.this);
            }
        }, 100);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wordEditText.getText().length() == 0) {
                    wordEditText.setError(getString(R.string.no_word_was_entered_text));
                    return;
                }
                else if (new WordsOpenHelper(WordsListActivity.this, notebookId).
                        isThereWord(wordEditText.getText().toString())) {
                    wordEditText.setError(getString(R.string.word_is_repeated));
                    return;
                }

                WordsOpenHelper openHelper = new WordsOpenHelper(WordsListActivity.this, notebookId);
                Word word = new Word();
                word.setWord(wordEditText.getText().toString());
                word.setMeaning(meaningEditText.getText().toString());
                openHelper.addWord(word);

                word.setId(openHelper.getLastID());
                word.setNotebookId(notebookId);
                showWordsFragment.addWord(word);

                ShowNoteBooksFragment.updateMeByBroadcast(WordsListActivity.this);

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
            dialogContainer = addAndEditWordDialog.findViewById(R.id.ll_dialog_add_word);
            dialogContainer.setVisibility(View.INVISIBLE);

            wordEditText = addAndEditWordDialog.findViewById(R.id.et_word);
            meaningEditText = addAndEditWordDialog.findViewById(R.id.et_meaning);
            wordTextInputLayout = addAndEditWordDialog.findViewById(R.id.text_input_word);
            meaningTextInputLayout = addAndEditWordDialog.findViewById(R.id.text_input_meaning);
            verifyButton = addAndEditWordDialog.findViewById(R.id.btn_verify);
            cancelButton = addAndEditWordDialog.findViewById(R.id.btn_cancel);
        } else {
            wordEditText.setText("");
            meaningEditText.setText("");
        }
    }

    private void setupViews() {
        addFab = findViewById(R.id.fab_add);
        addFab.setTag(View.VISIBLE); //  در این جا از تگ ویوی addFab به عنوان نشانه ای برای تشخیص ویزیبل بودن یا نبودن آن استفاده می شود.
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrowseAddWordDialog();
            }
        });
        addFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ToolTip.show(WordsListActivity.this, getString(R.string.adding_word_text), v);
                return true;
            }
        });

        setupToolbar();

        containerFrameLayout = findViewById(R.id.container_frame_layout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        Intent starter = new Intent(context, WordsListActivity.class);
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
}
