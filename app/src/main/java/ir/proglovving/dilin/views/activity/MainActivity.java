package ir.proglovving.dilin.views.activity;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import ir.proglovving.dilin.CustomDialogBuilder;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.data_model.Notebook;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;
import ir.proglovving.dilin.views.fragment.BookmarkedWordsFragment;
import ir.proglovving.dilin.views.fragment.DictionarySearchFragment;
import ir.proglovving.dilin.views.fragment.ShowNoteBooksFragment;

public class MainActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
//        private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout coordinatorLayout;
    private NavigationView navigationView;
    private FloatingActionButton fabAddNotebook;
    private FrameLayout containerFrameLayout;
    private BottomNavigationView bottomNavigationView;

    private ShowNoteBooksFragment showNoteBooksFragment;
    private DictionarySearchFragment dictionarySearchFragment;
    private BookmarkedWordsFragment bookmarkedWordsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActivityTransitions();
        setupViews();

        Utilities.setupExitTransition(this);

        showNoteBooksFragment = new ShowNoteBooksFragment(coordinatorLayout, false, ShowNoteBooksFragment.REFRESH_TYPE_SETUP, (FloatingActionButton)findViewById(R.id.fab_add));

        getSupportFragmentManager().beginTransaction()
                .add(containerFrameLayout.getId(), showNoteBooksFragment)
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utilities.updateShowWordsWidget(this);
    }

    @Override
    public void onBackPressed() {
        // in below if statement we check if navigationView was opened, we'll close that first.
        if(drawerLayout.isDrawerOpen(Gravity.START)){
            drawerLayout.closeDrawer(Gravity.START);
            return;
        }

        new CustomDialogBuilder(this)
                .setTitle(getString(R.string.exit))
                .setMessage(getString(R.string.exit_message))
                .setPositive(getString(R.string.yes_text), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegative(getString(R.string.no_text), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}})
                .create().show();
    }

    private void setupViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        toolbar = (Toolbar) findViewById(R.id.toolbar_show_note_activity);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.notebooks));
        Utilities.applyFontForToolbar(toolbar,this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        drawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Utilities.applyFontForAViewGroup(navigationView, MainActivity.this);

                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, null);

                switch (menuItem.getItemId()) {
                    /*
                    case R.id.favorite:

                        showNoteBooksFragment = new ShowNoteBooksFragment(
                                coordinatorLayout, !showNoteBooksFragment.isFavoriteMode(), ShowNoteBooksFragment.REFRESH_TYPE_SETUP, getOnScrollListener()
                        );
                        getSupportFragmentManager().beginTransaction()
                                .replace(containerFrameLayout.getId(), showNoteBooksFragment)
                                .commit();

                        if (showNoteBooksFragment.isFavoriteMode()) { // اگر در حال نمایش مورد علاقه بود
                            menuItem.setTitle(R.string.all_notebooks);
                            menuItem.setIcon(R.drawable.ic_action_all_books);
//                            oolbarLayout.setTitle(getString(R.string.favorite_text));

                            fabAddNotebook.hide();
                            fabAddNotebook.setTag(View.INVISIBLE);
                        } else { // اگر در حال نمایش همه ی دفتر ها بود

                            menuItem.setTitle(R.string.favorite_text);
                            menuItem.setIcon(R.drawable.ic_action_favorite);
//                            collapsingToolbarLayout.setTitle(getString(R.string.all_notebooks));

                            fabAddNotebook.show();
                            fabAddNotebook.setTag(View.VISIBLE);
                        }

                        drawerLayout.closeDrawers();

                        break;
                        */
                    case R.id.guidencec:
                        AppIntroActivity.start(MainActivity.this,true);
                        break;
                    case R.id.rate:
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("myket://comment?id=ir.proglovving.dilin")));
                        break;
                    case R.id.other_apps:
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("myket://developer/ir.proglovving.dilin")));
                        break;
                    case R.id.about:
                        startActivity(new Intent(MainActivity.this, ProgrammerAboutUsActivity.class), compat.toBundle());
                        break;
                    case R.id.protect:
                        startActivity(new Intent(MainActivity.this, DonateActivity.class), compat.toBundle());
                        break;
                }
                return true;
            }
        });

        // adding custom font for navigationView
        navigationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utilities.applyFontForAViewGroup(navigationView, MainActivity.this);
            }
        },1);


        containerFrameLayout = (FrameLayout) findViewById(R.id.container_frame_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utilities.applyFontForAViewGroup(bottomNavigationView, MainActivity.this);
            }
        },1);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() != R.id.notebooks){
                    fabAddNotebook.hide();
                }

                hideAFragmentForBottomNavigation(dictionarySearchFragment,getSupportFragmentManager());
                hideAFragmentForBottomNavigation(showNoteBooksFragment,getSupportFragmentManager());
                hideAFragmentForBottomNavigation(bookmarkedWordsFragment,getSupportFragmentManager());

                switch (menuItem.getItemId()){
                    case R.id.notebooks:
                        getSupportFragmentManager().beginTransaction().show(showNoteBooksFragment).commit();
                        fabAddNotebook.show();
                        break;
                    case R.id.bookmark:
                        if(bookmarkedWordsFragment == null){
                            bookmarkedWordsFragment = BookmarkedWordsFragment.newInstance();
                            getSupportFragmentManager().beginTransaction().add(containerFrameLayout.getId(),bookmarkedWordsFragment).commit();
                        }
                        getSupportFragmentManager().beginTransaction().show(bookmarkedWordsFragment).commit();
                        break;
                    case R.id.dictionary:
                        if(dictionarySearchFragment == null){
                            dictionarySearchFragment = new DictionarySearchFragment();
                            getSupportFragmentManager().beginTransaction().add(containerFrameLayout.getId(),dictionarySearchFragment).commit();
                        }
                        getSupportFragmentManager().beginTransaction().show(dictionarySearchFragment).commit();
                        break;
                }

                if(bookmarkedWordsFragment != null){
                    bookmarkedWordsFragment.refreshIfNeeded();
                }
                return true;
            }
        });

        fabAddNotebook = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAddNotebook.setTag(View.VISIBLE); // در این جا از تگ ویوی fabAddNotebook به عنوان نشانه ای برای تشخیص ویزیبل بودن یا نبودن آن استفاده می شود.
        fabAddNotebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteBookDialog();
            }
        });

        fabAddNotebook.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ToolTip.show(MainActivity.this,getString(R.string.creating_notebook),v);
                return true;
            }
        });

    }

    private void hideAFragmentForBottomNavigation(@Nullable Fragment fragment, FragmentManager fm){
        if(fragment != null){
            fm.beginTransaction().hide(fragment).commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setActivityTransitions() {
        Fade fade = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fade = new Fade();
            fade.setDuration(1000);
            fade.setInterpolator(new DecelerateInterpolator());
            getWindow().setEnterTransition(fade);
        }

    }

    private void showAddNoteBookDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_notebook);
        final LinearLayout dialogContainer = dialog.findViewById(R.id.ll_dialog_add_notebook);
        final EditText notebookNameEditText = dialog.findViewById(R.id.et_notebook);
        Button verifyButton = dialog.findViewById(R.id.btn_verify), cancelButton = dialog.findViewById(R.id.btn_cancel);

        // کد زیر کیبورد گوشی را برای ادیت تکست نمایش می دهد
        notebookNameEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utilities.showSoftKeyboard(notebookNameEditText, MainActivity.this);
            }
        },100);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notebookNameEditText.getText().length() == 0) {
                    notebookNameEditText.setError(getString(R.string.no_name_has_been_entered));
                    return;
                } else if (new NotebookOpenHelper(MainActivity.this).
                        isThereNotebook(notebookNameEditText.getText().toString())) {
                    notebookNameEditText.setError(getString(R.string.notebook_is_repeated));
                    return;
                }
                Notebook notebook = new Notebook();
                notebook.setNoteBookName(notebookNameEditText.getText().toString());
                notebook.setFavorite(false);
                notebook.setPlaying(false);
                new NotebookOpenHelper(MainActivity.this).addNotebook(notebook);

                showNoteBooksFragment.refreshRecyclerView(ShowNoteBooksFragment.REFRESH_TYPE_END);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogContainer.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Animator animator = ViewAnimationUtils.createCircularReveal(
                            dialogContainer, dialogContainer.getWidth(), dialogContainer.getHeight(), 0,
                            Math.max(dialogContainer.getWidth(), dialogContainer.getHeight()));
                    dialogContainer.setVisibility(View.VISIBLE);
                    animator.start();
                }


            }, 200);
        } else {
            dialogContainer.setVisibility(View.VISIBLE);
        }

    }

    public static void start(Context context){
        Intent starter = new Intent(context, MainActivity.class);
        // starter.putSomething!

        context.startActivity(starter);
    }

}
