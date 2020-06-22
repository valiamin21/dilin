package ir.proglovving.dilin.views.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.custom_views.WordsInputDialog;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;
import ir.proglovving.dilin.views.fragment.NotebookListFragment;
import ir.proglovving.dilin.views.fragment.WordListFragment;

public class WordsListActivity extends AppCompatActivity {

    public static final String KEY_NOTEBOOK_ID = "notebook_id";
    public static final String KEY_NOTEBOOK_NAME = "notebook_name";

    private Toolbar toolbar;
    private ViewGroup searchBoxContainer;
    private EditText searchEditText;
    private FloatingActionButton addFab;
    private ActionBarDrawerToggle drawerToggle;
    private FrameLayout containerFrameLayout;

    private WordListFragment wordListFragment;

    private String noteBookName;
    private int notebookId;

    private WordsOpenHelper wordsOpenHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_list);

        Intent intent = getIntent();
        noteBookName = intent.getStringExtra(KEY_NOTEBOOK_NAME);
        notebookId = intent.getExtras().getInt(KEY_NOTEBOOK_ID);


        setupViews();

        wordListFragment = WordListFragment.newInstance(false, getOnScrollListener(), notebookId, wordsOpenHelper);

        getSupportFragmentManager().beginTransaction()
                .add(containerFrameLayout.getId(), wordListFragment)
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            showSearchBox();
        }
        return true;
    }

    private void showSearchBox() {
        toolbar.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = ViewAnimationUtils.createCircularReveal(searchBoxContainer,
                    searchBoxContainer.getWidth(), searchBoxContainer.getHeight(), 0,
                    (float) Math.hypot(searchBoxContainer.getWidth(), searchBoxContainer.getHeight()));
            animator.setDuration(550);
            searchBoxContainer.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            searchBoxContainer.setVisibility(View.VISIBLE);
        }

        Utilities.showSoftKeyboard(searchEditText, this);
    }

    private void hideSearchBox() {
        searchEditText.setText("");
        searchBoxContainer.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = ViewAnimationUtils.createCircularReveal(toolbar,
                    toolbar.getWidth(), toolbar.getHeight(), 0,
                    (float) Math.hypot(toolbar.getWidth(), toolbar.getHeight()));
            animator.setDuration(400);
            toolbar.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }

        Utilities.hideSoftKeyboard(searchEditText, this);
    }

    private void setupViews() {
        addFab = findViewById(R.id.fab_add);
        addFab.setTag(View.VISIBLE); //  در این جا از تگ ویوی addFab به عنوان نشانه ای برای تشخیص ویزیبل بودن یا نبودن آن استفاده می شود.
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordsOpenHelper == null) {
                    wordsOpenHelper = new WordsOpenHelper(WordsListActivity.this, notebookId);
                }
                WordsInputDialog.getInstance(WordsListActivity.this).showBrowseAddWordDialog(
                        new WordsInputDialog.OnAddWord() {
                            @Override
                            public void onAdd(String word, String meaning) {
                                Word w = new Word();
                                w.setWord(word);
                                w.setMeaning(meaning);
                                wordsOpenHelper.addWord(w);

                                w.setId(wordsOpenHelper.getLastID());
                                w.setNotebookId(notebookId);
                                wordListFragment.addWord(w);

                                NotebookListFragment.updateMeByBroadcast(WordsListActivity.this);
                            }

                            @Override
                            public boolean onAlreadyExists(String w) {
                                return wordsOpenHelper.isThereWord(w);
                            }
                        });
            }
        });
        addFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ToolTip.show(WordsListActivity.this, getString(R.string.adding_word), v);
                return true;
            }
        });

        setupToolbar();
        setupSearchBox();

        containerFrameLayout = findViewById(R.id.container_frame_layout);
    }

    private void setupSearchBox() {
        searchBoxContainer = findViewById(R.id.search_box_container);
        searchEditText = findViewById(R.id.ed_search);
        ImageButton closeSearchBoxButton = findViewById(R.id.btn_search_close);

        closeSearchBoxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSearchBox();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                wordListFragment.searchRefresh(editable.toString());
            }
        });
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
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
