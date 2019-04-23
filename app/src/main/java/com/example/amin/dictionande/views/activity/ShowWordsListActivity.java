package com.example.amin.dictionande.views.activity;

import android.animation.Animator;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.amin.dictionande.R;
import com.example.amin.dictionande.TTSManager;
import com.example.amin.dictionande.Utilities;
import com.example.amin.dictionande.database_open_helpers.VocabularyOpenHelper;
import com.example.amin.dictionande.data_model.Voc;
import com.example.amin.dictionande.views.fragment.ShowWordsFragment;

public class ShowWordsListActivity extends AppCompatActivity {

    public static final String KEY_NOTEBOOK_NAME = "notebook_name";

    private Toolbar toolbar;
    private FloatingActionButton addFab;
    private ActionBarDrawerToggle drawerToggle;
    private CoordinatorLayout coordinatorLayout;
    private NavigationView navigationView;
    private FrameLayout containerFrameLayout;

    private ShowWordsFragment showWordsFragment;

    private VocabularyOpenHelper openHelper;

    private boolean shouldBeFinished = false;

    private Dialog addAndEditWordDialog;
    Button verifyButton, cancelButton;
    EditText wordEditText, meaningEditText;
    TextInputLayout wordTextInputLayout, meaningTextInputLayout;
    private LinearLayout dialogContainer;

    private String noteBookName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_words_list);

        Intent intent = getIntent();
        noteBookName = intent.getExtras().getString(KEY_NOTEBOOK_NAME);

        setupViews();
        setupEnterTransition();
        setupSharedElementEnterTransition();
        setupExitTransition();

        showWordsFragment = new ShowWordsFragment(
                coordinatorLayout, false, ShowWordsFragment.REFRESH_TYPE_SETUP, getOnScrollListener(), noteBookName
        );

        getSupportFragmentManager().beginTransaction()
                .add(containerFrameLayout.getId(), showWordsFragment)
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utilities.updageShowWordsWidget(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTSManager.destroyTTS();
    }

    private void setupExitTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(500);
            fade.setInterpolator(new DecelerateInterpolator());
            getWindow().setExitTransition(fade);
        }
    }

    private void setupSharedElementEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(500);
            changeBounds.setInterpolator(new OvershootInterpolator());
            getWindow().setSharedElementEnterTransition(changeBounds);
        }
    }

    private void setupEnterTransition() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setDuration(700);
//            slide.setInterpolator(new OvershootInterpolator());
            getWindow().setEnterTransition(slide);
//            Fade fade = new Fade();
//            fade.setDuration(700);
//            getWindow().setEnterTransition(fade);
        }

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
                showWordsFragment.searchRefresh(s);
                searchView.clearFocus();

                if (s.equals("goToTabLayoutActivity();")) {
                    startActivity(new Intent(ShowWordsListActivity.this, TabLayoutActivity.class));
                }

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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case R.id.search:
//                startActivity(new Intent(ShowWordsListActivity.this, TabLayoutActivity.class));
//                break;
//            case R.id.rate:
//                Toast.makeText(this, "rate clicked", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.about:
//                Toast.makeText(this, "about clicked", Toast.LENGTH_SHORT).show();
//
//        }
//        return true;
//    }

    private void showBrowseAddVocabularyDialog() {

        setupDialog();

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wordEditText.getText().length() == 0) {
                    wordEditText.setError(getString(R.string.no_word_was_entered_text));
                    return;
                } // TODO: 1/29/19 خط زیر اصلاح شود زیرا تکراری است(در پایین تر تکرار شده است)
                else if (new VocabularyOpenHelper(ShowWordsListActivity.this, noteBookName).
                        isThereWord(wordEditText.getText().toString())) {
                    wordEditText.setError(getString(R.string.word_is_repeated));
                    return;
                }

                VocabularyOpenHelper openHelper = new VocabularyOpenHelper(ShowWordsListActivity.this, noteBookName);
                Voc voc = new Voc();
                voc.setVoc(wordEditText.getText().toString());
                voc.setMeaning(meaningEditText.getText().toString());
                openHelper.addVoc(voc);


                showWordsFragment.refreshRecyclerView(ShowWordsFragment.REFRESH_TYPE_END);
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

    private void setupViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        addFab = (FloatingActionButton) findViewById(R.id.fab_add);
        addFab.setTag(View.VISIBLE); //  در این جا از تگ ویوی addFab به عنوان نشانه ای برای تشخیص ویزیبل بودن یا نبودن آن استفاده می شود.
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrowseAddVocabularyDialog();
            }
        });

        setupToolbar();

        containerFrameLayout = (FrameLayout) findViewById(R.id.container_frame_layout);
    }

    private void setupToolbar() {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(noteBookName);
        toolbar.setSubtitle(R.string.all_words);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });

//        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
//           this,
//           drawerLayout,toolbar,   0,0);
        drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, R.string.open, R.string.close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                ActivityOptionsCompat compat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(ShowWordsListActivity.this, null);

                switch (menuItem.getItemId()) {

                    case R.id.bookmark:

                        showWordsFragment = new ShowWordsFragment(coordinatorLayout
                                , !showWordsFragment.isBookmarkedMode(), ShowWordsFragment.REFRESH_TYPE_SETUP, getOnScrollListener(), noteBookName);
                        getSupportFragmentManager().beginTransaction()
                                .replace(containerFrameLayout.getId(), showWordsFragment)
                                .commit();

                        if (showWordsFragment.isBookmarkedMode()) {
                            menuItem.setTitle(R.string.all_words);
                            // TODO: 2/3/19 آیکون زیر اصلاح شود
                            menuItem.setIcon(R.drawable.ic_action_all_books);
                            toolbar.setSubtitle(R.string.bookmarked_words_text);
                            addFab.hide();
                            addFab.setTag(View.INVISIBLE);
                        } else {
                            menuItem.setTitle(R.string.bookmarked_text);
                            menuItem.setIcon(R.drawable.ic_action_bookmark);
                            toolbar.setSubtitle(R.string.all_words);
                            addFab.show();
                            addFab.setTag(View.VISIBLE);
                        }

                        drawerLayout.closeDrawers();

                        break;

                    case R.id.main_page:
                        finish();
                        break;

                    case R.id.about:

                        startActivity(new Intent(ShowWordsListActivity.this, About_Us_Programmer.class), compat.toBundle());
                        break;
                    case R.id.protect:
                        startActivity(new Intent(ShowWordsListActivity.this, DonateActivity.class), compat.toBundle());
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

//    @Override
//    public void finish() {
//        if (shouldBeFinished) {
//            super.finish();
//        } else {
//            shouldBeFinished = true;
//            final Snackbar snackbar = Snackbar
//                    .make(coordinatorLayout,
//                            "برای خروج دوباره کلیک کنید!",
//                            Snackbar.LENGTH_LONG);
//            snackbar.setAction("مخفی کردن", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    snackbar.dismiss();
//                }
//            });
//            snackbar.show();
//            new CountDownTimer(1500, 1500) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//
//                }
//
//                @Override
//                public void onFinish() {
//                    shouldBeFinished = false;
//                }
//            }.start();
//        }
//    }

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
//        // TODO: 2/3/19
//    }
//
//    private void refresh(int refreshPosition){
//        // TODO: 2/3/19
//    }

//    private void setupRecyclerView() {
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//
//        openHelper = new VocabularyOpenHelper(this, noteBookName);
//        refreshRecyclerView(REFRESH_TYPE_SETUP);
//    }
//
//    private void refreshRecyclerView(int refreshPosition) {
//        WordRecyclerViewAdapter adapter = new WordRecyclerViewAdapter(this, openHelper.getVocList(), this);
//        recyclerView.setAdapter(adapter);
//        if (refreshPosition == REFRESH_TYPE_END) {
//            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
//        }
//
//    }
//
//    private void refreshRecyclerView(int refreshPosition, int position) {
//        if (refreshPosition == REFRESH_TYPE_CURRENT) {
//            WordRecyclerViewAdapter adapter = new WordRecyclerViewAdapter(this, openHelper.getVocList(), this);
//            recyclerView.setAdapter(adapter);
//            recyclerView.scrollToPosition(position);
//        }
//    }
//
//    private void refreshRecyclerView(String search) {
//        recyclerView.setAdapter(
//                new WordRecyclerViewAdapter(this, openHelper.getSearchedVocList(search), this)
//        );
//    }

}
