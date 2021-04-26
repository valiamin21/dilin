package ir.proglovving.dilin.views.activity;

import android.content.ActivityNotFoundException;
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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import ir.proglovving.dilin.BuildConfig;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;
import ir.proglovving.dilin.views.CustomDialogBuilder;
import ir.proglovving.dilin.views.fragment.SavedWordsFragment;
import ir.proglovving.dilin.views.fragment.DictionaryFragment;
import ir.proglovving.dilin.views.fragment.NotebookListFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CoordinatorLayout coordinatorLayout;
    private NavigationView navigationView;
    private ExtendedFloatingActionButton fabAddNotebook;
    private FrameLayout containerFrameLayout;
    private BottomNavigationView bottomNavigationView;

    private NotebookListFragment notebookListFragment;
    private DictionaryFragment dictionaryFragment;
    private SavedWordsFragment savedWordsFragment;

    private NotebookOpenHelper notebookOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActivityTransitions();
        setupViews();

        Utilities.setupExitTransition(this);

        notebookOpenHelper = new NotebookOpenHelper(this);
        dictionaryFragment = DictionaryFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .add(containerFrameLayout.getId(), dictionaryFragment)
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
        if (bottomNavigationView.getSelectedItemId() == R.id.notebooks) {
            fabAddNotebook.show();
        } else {
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

        new CustomDialogBuilder(this, R.color.primary_text, R.color.colorAccent)
                .setTitle(getString(R.string.exit))
                .setMessage(getString(R.string.exit_message))
                .setPositive(getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegative(R.string.rate_in_bazar, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rate(MainActivity.this);
                    }
                })
                .setCancel(getString(R.string.no), new View.OnClickListener() {
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

        drawerLayout = findViewById(R.id.drawable_layout);
        drawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, null);

                switch (menuItem.getItemId()) {
                    case R.id.guidance:
                        AppIntroActivity.start(MainActivity.this, true);
                        break;
                    case R.id.rate:
                        rate(MainActivity.this);
                        break;
                    case R.id.other_apps:
                        openDeveloperApps(MainActivity.this);
                        break;
                    case R.id.about:
                        startActivity(new Intent(MainActivity.this, AboutProgrammerActivity.class), compat.toBundle());
                        break;
                }
                return true;
            }
        });

        // the textView in navigation header for showing app name along with version
        TextView appIntroductionTextView = navigationView.getHeaderView(0).findViewById(R.id.tv_app_introduction);
        appIntroductionTextView.setText(getString(R.string.app_name) + "، " + getString(R.string.version) + " " + BuildConfig.VERSION_NAME);

        containerFrameLayout = findViewById(R.id.container_frame_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.notebooks) {
                    fabAddNotebook.show();
                } else {
                    fabAddNotebook.hide();
                }

                hideAFragmentForBottomNavigation(dictionaryFragment, getSupportFragmentManager());
                hideAFragmentForBottomNavigation(notebookListFragment, getSupportFragmentManager());
                hideAFragmentForBottomNavigation(savedWordsFragment, getSupportFragmentManager());

                switch (menuItem.getItemId()) {
                    case R.id.notebooks:
                        if (notebookListFragment == null) {
                            notebookListFragment = NotebookListFragment.getInstance(notebookOpenHelper, coordinatorLayout, (ExtendedFloatingActionButton) findViewById(R.id.fab_add));
                            fabAddNotebook.setOnClickListener(notebookListFragment.getFabAddNotebookOnClickListener());
                            getSupportFragmentManager().beginTransaction().add(containerFrameLayout.getId(), notebookListFragment).commit();
                        }
                        getSupportFragmentManager().beginTransaction().show(notebookListFragment).commit();
                        break;
                    case R.id.bookmark:
                        if (savedWordsFragment == null) {
                            savedWordsFragment = SavedWordsFragment.newInstance();
                            getSupportFragmentManager().beginTransaction().add(containerFrameLayout.getId(), savedWordsFragment).commit();
                        }
                        getSupportFragmentManager().beginTransaction().show(savedWordsFragment).commit();
                        break;
                    case R.id.dictionary:
                        if (dictionaryFragment == null) {
                            dictionaryFragment = DictionaryFragment.newInstance();
                            getSupportFragmentManager().beginTransaction().add(containerFrameLayout.getId(), dictionaryFragment).commit();
                        }
                        getSupportFragmentManager().beginTransaction().show(dictionaryFragment).commit();
                        break;
                }

                if (savedWordsFragment != null) {
                    savedWordsFragment.refreshIfRequired();
                }
                return true;
            }
        });

        fabAddNotebook = findViewById(R.id.fab_add);
        fabAddNotebook.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ToolTip.show(MainActivity.this, getString(R.string.creating_notebook), v);
                return true;
            }
        });

    }

    public static void rate(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setData(Uri.parse("bazaar://details?id=" + context.getPackageName()));
            intent.setPackage("com.farsitel.bazaar");
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.please_install_bazar, Toast.LENGTH_SHORT).show();
        }
    }

    public static void openDeveloperApps(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("bazaar://collection?slug=by_author&aid=" + context.getString(R.string.developer_id)));
            intent.setPackage("com.farsitel.bazaar");
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.please_install_bazar, Toast.LENGTH_SHORT).show();
        }
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

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

}
