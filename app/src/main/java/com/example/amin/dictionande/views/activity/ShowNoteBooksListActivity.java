package com.example.amin.dictionande.views.activity;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Slide;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.amin.dictionande.R;
import com.example.amin.dictionande.Utilities;
import com.example.amin.dictionande.data_model.Notebook;
import com.example.amin.dictionande.database_open_helpers.NotebookOpenHelper;
import com.example.amin.dictionande.views.fragment.ShowNoteBooksFragment;

public class ShowNoteBooksListActivity extends AppCompatActivity {

    public static final int RESULT_BACK_FROM_NOTEBOOKS_ACTIVITY = 52;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
        private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout coordinatorLayout;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private FrameLayout containerFrameLayout;

    private ShowNoteBooksFragment showNoteBooksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note_books);
        setActivityTransitions();
        setupViews();

        setupEnterTransition();
        Utilities.setupExitTransition(this);
        setupSharedElementExitTransition();

        showNoteBooksFragment = new ShowNoteBooksFragment(coordinatorLayout, false, ShowNoteBooksFragment.REFRESH_TYPE_SETUP, getOnScrollListener());

        getSupportFragmentManager().beginTransaction()
                .add(containerFrameLayout.getId(), showNoteBooksFragment)
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utilities.updageShowWordsWidget(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_BACK_FROM_NOTEBOOKS_ACTIVITY){
            showNoteBooksFragment.refreshRecyclerView(ShowNoteBooksFragment.REFRESH_TYPE_CURRENT);
        }
    }

    private void setupEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setDuration(1000);
            slide.setInterpolator(new OvershootInterpolator());
            getWindow().setEnterTransition(slide);
        }
    }

    private void setupSharedElementExitTransition() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(500);
            changeBounds.setInterpolator(new OvershootInterpolator());
            getWindow().setSharedElementExitTransition(changeBounds);
        }
    }


    private void setupViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.ctl);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.notebooks));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        drawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(ShowNoteBooksListActivity.this, null);

                switch (menuItem.getItemId()) {
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
                            collapsingToolbarLayout.setTitle(getString(R.string.favorite_text));

                            fab.hide();
                            fab.setTag(View.INVISIBLE);
                        } else { // اگر در حال نمایش همه ی دفتر ها بود

                            menuItem.setTitle(R.string.favorite_text);
                            menuItem.setIcon(R.drawable.ic_action_favorite);
                            collapsingToolbarLayout.setTitle(getString(R.string.all_notebooks));

                            fab.show();
                            fab.setTag(View.VISIBLE);
                        }

                        drawerLayout.closeDrawers();

                        break;
                    case R.id.rate:
                        Toast.makeText(ShowNoteBooksListActivity.this, "rate was clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.other_apps:
                        Toast.makeText(ShowNoteBooksListActivity.this, "other apps was clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about:
//                        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(ShowNoteBooksListActivity.this, null);
                        startActivity(new Intent(ShowNoteBooksListActivity.this, About_Us_Programmer.class), compat.toBundle());
                        break;
                    // TODO: 2/7/19  بخش حمایت از ما اضافه شود.
                    case R.id.protect:
//                        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(ShowNoteBooksListActivity.this, null);
                        startActivity(new Intent(ShowNoteBooksListActivity.this, DonateActivity.class), compat.toBundle());
                        break;
                }
                return true;
            }
        });

        containerFrameLayout = (FrameLayout) findViewById(R.id.container_frame_layout);

        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setTag(View.VISIBLE); // در این جا از تگ ویوی fab به عنوان نشانه ای برای تشخیص ویزیبل بودن یا نبودن آن استفاده می شود.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteBookDialog();
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setActivityTransitions() {
        Fade fade = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fade = new Fade();
            fade.setDuration(1000);
            fade.setInterpolator(new DecelerateInterpolator());
            getWindow().setEnterTransition(fade);
        }

    }


    private void showAddNoteBookDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_notebook);
        final LinearLayout dialogContainer = dialog.findViewById(R.id.ll);
        final EditText notebookNameEditText = dialog.findViewById(R.id.et_notebook);
        Button verifyButton = dialog.findViewById(R.id.btn_verify), cancelButton = dialog.findViewById(R.id.btn_cancel);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notebookNameEditText.getText().length() == 0) {
                    notebookNameEditText.setError(getString(R.string.no_name_has_been_entered));
                    return;
                } else if (new NotebookOpenHelper(ShowNoteBooksListActivity.this).
                        isThereNotebook(notebookNameEditText.getText().toString())) {
                    notebookNameEditText.setError(getString(R.string.notebook_is_repeated));
                    return;
                }
                Notebook notebook = new Notebook();
                notebook.setNoteBookName(notebookNameEditText.getText().toString());
                notebook.setFavorite(false);
                notebook.setPlaying(false);
                NotebookOpenHelper openHelper = new NotebookOpenHelper(ShowNoteBooksListActivity.this);
                openHelper.addNotebook(notebook);

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

    private RecyclerView.OnScrollListener getOnScrollListener() {
        return
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) {
                            fab.hide();
                        } else {
                            if(((int)fab.getTag()) == View.VISIBLE){
                                fab.show();
                            }
                        }
                    }
                };
    }
}
