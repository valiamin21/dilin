package ir.proglovving.dilin.views.activity;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.TextView;

import ir.proglovving.dilin.BuildConfig;
import ir.proglovving.dilin.CustomDialogBuilder;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.data_model.Notebook;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;
import ir.proglovving.dilin.views.fragment.BookmarkedWordsFragment;
import ir.proglovving.dilin.views.fragment.DictionarySearchFragment;
import ir.proglovving.dilin.views.fragment.ShowNoteBooksFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
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

        showNoteBooksFragment = new ShowNoteBooksFragment(coordinatorLayout, false, ShowNoteBooksFragment.REFRESH_TYPE_SETUP, (FloatingActionButton) findViewById(R.id.fab_add));

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
    protected void onResume() {
        super.onResume();
        if(bottomNavigationView.getSelectedItemId() == R.id.notebooks){
            fabAddNotebook.show();
        }else{
            fabAddNotebook.hide();
        }
    }

    @Override
    public void onBackPressed() {
        // in below if statement we check if navigationView was opened, we'll close that first.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
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
                    public void onClick(View v) {
                    }
                })
                .create().show();
    }

    private void setupViews() {
        coordinatorLayout = findViewById(R.id.coordinator);

        Toolbar toolbar = findViewById(R.id.toolbar_show_note_activity);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.notebooks));
        Utilities.applyFontForToolbar(toolbar, this);

        drawerLayout = findViewById(R.id.drawable_layout);
        drawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Utilities.applyFontForAViewGroup(navigationView, MainActivity.this);

                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, null);

                switch (menuItem.getItemId()) {
                    case R.id.guidencec:
                        AppIntroActivity.start(MainActivity.this, true);
                        break;
                    case R.id.rate:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("myket://comment?id=ir.proglovving.dilin")));
                        break;
                    case R.id.other_apps:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("myket://developer/ir.proglovving.dilin")));
                        break;
                    case R.id.about:
                        startActivity(new Intent(MainActivity.this, AboutProgrammerActivity.class), compat.toBundle());
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
        }, 1);

        // the textView in navigation header for showing app name along with version
        TextView appIntroductionTextView = navigationView.getHeaderView(0).findViewById(R.id.tv_app_introduction);
        appIntroductionTextView.setText(getString(R.string.app_name) + "، " + getString(R.string.version) + " " + BuildConfig.VERSION_NAME);

        containerFrameLayout = findViewById(R.id.container_frame_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utilities.applyFontForAViewGroup(bottomNavigationView, MainActivity.this);
            }
        }, 1);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.notebooks) {
                    fabAddNotebook.show();
                }else{
                    fabAddNotebook.hide();
                }

                hideAFragmentForBottomNavigation(dictionarySearchFragment, getSupportFragmentManager());
                hideAFragmentForBottomNavigation(showNoteBooksFragment, getSupportFragmentManager());
                hideAFragmentForBottomNavigation(bookmarkedWordsFragment, getSupportFragmentManager());

                switch (menuItem.getItemId()) {
                    case R.id.notebooks:
                        getSupportFragmentManager().beginTransaction().show(showNoteBooksFragment).commit();
                        break;
                    case R.id.bookmark:
                        if (bookmarkedWordsFragment == null) {
                            bookmarkedWordsFragment = BookmarkedWordsFragment.newInstance();
                            getSupportFragmentManager().beginTransaction().add(containerFrameLayout.getId(), bookmarkedWordsFragment).commit();
                        }
                        getSupportFragmentManager().beginTransaction().show(bookmarkedWordsFragment).commit();
                        break;
                    case R.id.dictionary:
                        if (dictionarySearchFragment == null) {
                            dictionarySearchFragment = DictionarySearchFragment.newInstance();
                            getSupportFragmentManager().beginTransaction().add(containerFrameLayout.getId(), dictionarySearchFragment).commit();
                        }
                        getSupportFragmentManager().beginTransaction().show(dictionarySearchFragment).commit();
                        break;
                }

                if (bookmarkedWordsFragment != null) {
                    bookmarkedWordsFragment.refreshIfNeeded();
                }
                return true;
            }
        });

        fabAddNotebook = findViewById(R.id.fab_add);
        fabAddNotebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteBookDialog();
            }
        });

        fabAddNotebook.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ToolTip.show(MainActivity.this, getString(R.string.creating_notebook), v);
                return true;
            }
        });

    }

    private void hideAFragmentForBottomNavigation(@Nullable Fragment fragment, FragmentManager fm) {
        if (fragment != null) {
            fm.beginTransaction().hide(fragment).commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
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
        }, 100);

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

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

}
