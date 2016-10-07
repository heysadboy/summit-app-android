package org.openstack.android.summit.modules.main.user_interface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;

import org.openstack.android.summit.InitialDataLoadingActivity;
import org.openstack.android.summit.OpenStackSummitApplication;
import org.openstack.android.summit.R;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.network.IReachability;
import org.openstack.android.summit.common.security.ISecurityManager;
import org.openstack.android.summit.common.user_interface.BadgeCounterMenuItemDecorator;
import org.openstack.android.summit.common.utils.RealmFactory;
import org.openstack.android.summit.dagger.components.ApplicationComponent;
import org.openstack.android.summit.dagger.modules.ActivityModule;
import org.openstack.android.summit.modules.main.exceptions.MissingMemberException;

import javax.inject.Inject;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IMainView {

    @Inject
    IMainPresenter presenter;

    @Inject
    // TODO: this should be moved to interactor. It's necessary to know how to deal with the Activiy parameter on login
            ISecurityManager securityManager;

    @Inject
    IReachability reachability;

    private ACProgressFlower progressDialog;
    private Button loginButton;
    private TextView memberNameTextView;
    private SimpleDraweeView memberProfileImageView;
    private ActionBarDrawerToggle toggle;
    private boolean userClickedLogout;
    private int selectedMenuItemId;
    private NavigationView navigationView;
    private boolean onLoginProcess = false;
    private boolean onDataLoading  = false;

    private void cancelLoginProcess(){
        Log.d(Constants.LOG_TAG, "MainActivity.cancelLoginProcess");
        this.onLoginProcess = false;
    }

    private void initiateLoginProcess(){
        Log.d(Constants.LOG_TAG, "MainActivity.initiateLoginProcess");
        this.onLoginProcess = true;
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                if (intent.getAction() == Constants.WIPE_DATE_EVENT) {
                    Log.d(Constants.LOG_TAG, "WIPE_DATE_EVENT");
                    launchInitialDataLoadingActivity();
                }

                if (intent.getAction().contains(Constants.PUSH_NOTIFICATION_RECEIVED)) {
                    Log.d(Constants.LOG_TAG, "PUSH_NOTIFICATION_RECEIVED");
                    presenter.updateNotificationCounter();
                    return;
                }

                if (intent.getAction().contains(Constants.PUSH_NOTIFICATION_DELETED)) {
                    Log.d(Constants.LOG_TAG, "PUSH_NOTIFICATION_DELETED");
                    presenter.updateNotificationCounter();
                    return;
                }

                if (intent.getAction().contains(Constants.PUSH_NOTIFICATION_OPENED)) {
                    Log.d(Constants.LOG_TAG, "PUSH_NOTIFICATION_OPENED");
                    presenter.updateNotificationCounter();
                    return;
                }

                if (intent.getAction().contains(Constants.START_LOG_IN_EVENT)) {
                    Log.d(Constants.LOG_TAG, "START_LOG_IN_EVENT");
                    showActivityIndicator();
                    initiateLoginProcess();
                    return;
                }

                if (intent.getAction().contains(Constants.LOG_IN_ERROR_EVENT)) {
                    Log.d(Constants.LOG_TAG, "LOG_IN_ERROR_EVENT");
                    hideActivityIndicator();
                    showErrorMessage(intent.getExtras().getString(Constants.LOG_IN_ERROR_MESSAGE, Constants.GENERIC_ERROR_MSG));
                    cancelLoginProcess();
                    presenter.enableDataUpdateService();
                    return;
                }

                if (intent.getAction().contains(Constants.LOGGED_IN_EVENT)) {
                    try {
                        Log.d(Constants.LOG_TAG, "LOGGED_IN_EVENT");
                        presenter.onLoggedIn();
                        //show my profile tab
                        navigationView.getMenu().findItem(R.id.nav_my_profile).setVisible(true);
                        // set events tab ...
                        navigationView.getMenu().findItem(R.id.nav_events).setChecked(true);
                        presenter.showEventsView();
                    } catch (MissingMemberException ex1) {
                        Crashlytics.logException(ex1);
                        Log.w(Constants.LOG_TAG, ex1.getMessage());
                        showErrorMessage(getResources().getString(R.string.login_error_message));
                    } finally {
                        presenter.enableDataUpdateService();
                        hideActivityIndicator();
                        cancelLoginProcess();
                    }
                    return;
                }

                if (intent.getAction().contains(Constants.LOGGED_OUT_EVENT)) {
                    try {
                        Log.d(Constants.LOG_TAG, "LOGGED_OUT_EVENT");
                        onLoggedOut();
                        if (!userClickedLogout) {
                            showInfoMessage(getResources().getString(R.string.session_expired_message));
                        }
                        userClickedLogout = false;
                        presenter.showEventsView();
                    }
                    finally {
                        if(intent.getBooleanExtra(Constants.EXTRA_ENABLE_DATA_UPDATES_AFTER_LOGOUT, false))
                            presenter.enableDataUpdateService();
                        cancelLoginProcess();
                    }
                    return;
                }

                if (intent.getAction().contains(Constants.LOG_IN_CANCELLED_EVENT)) {
                    Log.d(Constants.LOG_TAG, "LOG_IN_CANCELLED_EVENT");
                    hideActivityIndicator();
                    cancelLoginProcess();
                    return;
                }

            } catch (Exception ex) {
                Crashlytics.logException(new Exception(String.format("Action %s", intent.getAction()), ex));
            }
        }
    };

    private static final int DATA_LOAD_REQUEST = 1;  // The request code

    private void launchInitialDataLoadingActivity() {
        if (!presenter.isSummitDataLoaded() && !onDataLoading) {
            onDataLoading = true;
            // disable data updates ...
            presenter.disableDataUpdateService();
            Intent intent = new Intent(MainActivity.this, InitialDataLoadingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            Log.i(Constants.LOG_TAG, "starting InitialDataLoadingActivity ...");
            startActivityForResult(intent, DATA_LOAD_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == DATA_LOAD_REQUEST) {
            onDataLoading = false;
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i(Constants.LOG_TAG, "MainActivity.onActivityResult: Summit Data Loaded!");
                //re enable data update service
                presenter.enableDataUpdateService();
                presenter.shouldShowMainView();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(Constants.LOG_TAG, "MainActivity.onCreate");
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
        presenter.setView(this);

        // bind local broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.START_LOG_IN_EVENT);
        intentFilter.addAction(Constants.LOG_IN_CANCELLED_EVENT);
        intentFilter.addAction(Constants.LOG_IN_ERROR_EVENT);
        intentFilter.addAction(Constants.LOGGED_IN_EVENT);
        intentFilter.addAction(Constants.LOGGED_OUT_EVENT);
        intentFilter.addAction(Constants.PUSH_NOTIFICATION_RECEIVED);
        intentFilter.addAction(Constants.PUSH_NOTIFICATION_DELETED);
        intentFilter.addAction(Constants.PUSH_NOTIFICATION_OPENED);
        intentFilter.addAction(Constants.WIPE_DATE_EVENT);
        LocalBroadcastManager.getInstance(OpenStackSummitApplication.context).registerReceiver(messageReceiver, intentFilter);

        presenter.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        securityManager.init();
        ActionBarSetup();
        NavigationMenuSetup(savedInstanceState);
        if (savedInstanceState == null) {
            Log.d(Constants.LOG_TAG, "MainActivity.onCreate - savedInstanceState == null");
            presenter.shouldShowMainView();
        } else {
            onLoginProcess = savedInstanceState.getBoolean(Constants.ON_LOGGING_PROCESS, false);
        }

        if (onLoginProcess) {
            Log.d(Constants.LOG_TAG, "MainActivity.onCreate - its on logging process ...");
            cancelLoginProcess();
            showActivityIndicator();
            return;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        try {
            Log.d(Constants.LOG_TAG, "MainActivity.onResume");
            toggleMenuLogo(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
            super.onResume();
            presenter.onResume();
            launchInitialDataLoadingActivity();
            setupNavigationIcons();
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
    }

    @Override
    protected void onPause() {
        Log.d(Constants.LOG_TAG, "MainActivity.onPause");
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(Constants.LOG_TAG, "MainActivity.onDestroy");

        super.onDestroy();
        presenter.onDestroy();
        // unbind local broadcast receiver
        LocalBroadcastManager.getInstance(OpenStackSummitApplication.context).unregisterReceiver(messageReceiver);
        hideActivityIndicator();
        //close realm session for current thread
        RealmFactory.closeSession();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.onConfigurationChanged(newConfig);
        setupNavigationIcons();
    }

    public void toggleMenuLogo(boolean show) {
        ImageView footerLogo = (ImageView) findViewById(R.id.footer_logo);
        footerLogo.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void ActionBarSetup() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                presenter.onOpenedNavigationMenu();
            }
        };

        drawer.setDrawerListener(toggle);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        //calling sync state is neccesary or else your hamburger icon wont show up
        toggle.syncState();

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    onBackPressed();
                }
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                setupNavigationIcons();
            }
        });
    }

    private void setupNavigationIcons() {
        toggle.setDrawerIndicatorEnabled(getSupportFragmentManager().getBackStackEntryCount() == 0);
        // Enable Up button only  if there are entries in the back stack ( back arrow )
        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
        toggle.syncState();
    }

    private void NavigationMenuSetup(Bundle savedInstanceState) {
        try {
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().getItem(0).setChecked(true);
            LinearLayout headerView = (LinearLayout) navigationView.inflateHeaderView(R.layout.nav_header_main);
            loginButton             = (Button) headerView.findViewById(R.id.login_button);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!reachability.isNetworkingAvailable(MainActivity.this)) {
                        showErrorMessage(getResources().getString(R.string.login_disallowed_no_connectivity));
                        return;
                    }

                    presenter.disableDataUpdateService();

                    if (!presenter.isSummitDataLoaded()) {
                        showInfoMessage(getResources().getString(R.string.login_disallowed_no_data));
                        launchInitialDataLoadingActivity();
                        return;
                    }

                    // LOGIN
                    if (!securityManager.isLoggedIn()) {
                        securityManager.login(MainActivity.this);
                        return;
                    }

                    // LOGOUT
                    userClickedLogout = true;

                    securityManager.logout(true);
                }
            });

            memberNameTextView = (TextView) headerView.findViewById(R.id.member_name_textview);
            memberProfileImageView = (SimpleDraweeView) headerView.findViewById(R.id.member_profile_pic_imageview);

            EditText searchText = (EditText) headerView.findViewById(R.id.nav_header_search_edittext);
            searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String searchTerm = v.getText().toString();
                    if (actionId == EditorInfo.IME_ACTION_DONE && !searchTerm.isEmpty()) {
                        presenter.showSearchView(searchTerm);
                    }
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    v.setText("");
                    return false;
                }
            });

            if (securityManager.isLoggedIn()) {

                presenter.onLoggedIn();

            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            if (!drawer.isDrawerOpen(GravityCompat.START) && getFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            }

            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                return;
            }

            drawer.closeDrawer(GravityCompat.START);
            getFragmentManager().popBackStack();
        }
        catch (Exception ex){
            Log.e(Constants.LOG_TAG, ex.getMessage(), ex);
            Crashlytics.logException(ex);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
        Log.d(Constants.LOG_TAG, "MainActivity.onSaveInstanceState");
        if (onLoginProcess)
            outState.putBoolean(Constants.ON_LOGGING_PROCESS, onLoginProcess);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        selectedMenuItemId = item.getItemId();

        if (selectedMenuItemId == R.id.nav_events) {
            presenter.showEventsView();
        } else if (selectedMenuItemId == R.id.nav_speakers) {
            presenter.showSpeakerListView();
        } else if (selectedMenuItemId == R.id.nav_venues) {
            presenter.showVenuesView();
        } else if (selectedMenuItemId == R.id.nav_my_profile) {
            presenter.showMyProfileView();
        } else if (selectedMenuItemId == R.id.nav_notifications) {
            presenter.showNotificationView();
        } else if (selectedMenuItemId == R.id.nav_settings) {
            presenter.showSettingsView();
        } else if (selectedMenuItemId == R.id.nav_about) {
            presenter.showAboutView();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public ApplicationComponent getApplicationComponent() {
        return ((OpenStackSummitApplication) getApplication()).getApplicationComponent();
    }

    public ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    public void setLoginButtonText(String text) {
        loginButton.setText(text);
    }

    @Override
    public void setMemberName(String text) {
        memberNameTextView.setText(text);
    }

    @Override
    public void setProfilePic(Uri uri) {
        memberProfileImageView.setImageURI(uri);
    }

    public void toggleMyProfileMenuItem(boolean show) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_my_profile).setVisible(show);
    }

    private void onLoggedOut() {
        Log.i(Constants.LOG_TAG, "doing log out");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_my_profile).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_events).setChecked(true);
        presenter.onLoggedOut();
    }

    private void onError(String message) {
        showErrorMessage(message);
        hideActivityIndicator();
    }

    public void setMenuItemChecked(int menuItemId) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView == null) return;
        MenuItem menuItem = navigationView.getMenu().findItem(menuItemId);
        if (menuItem == null) return;
        menuItem.setChecked(true);
    }

    public void showErrorMessage(String message) {
       new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
       .setTitleText("Oops...")
       .setContentText(message)
       .show();
    }

    @Override
    public void showInfoMessage(String message) {
        showInfoMessage(message, "info");
    }

    @Override
    public void showInfoMessage(String message, String title) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();
    }

    @Override
    public void toggleMenu(boolean show) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (show) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void updateNotificationCounter(Long value) {
        BadgeCounterMenuItemDecorator badge = new BadgeCounterMenuItemDecorator
                (
                        navigationView.getMenu().findItem(R.id.nav_notifications),
                        R.id.txt_counter
                );
        if (value > 0) {
            badge.updateCounter(value.toString());
            return;
        }
        badge.hideCounter();
    }

    @Override
    public void setTitle(String title) {
        setTitle(title);
    }

    @Override
    public void showActivityIndicator(int delay) {
        showActivityIndicator();
    }

    public void showActivityIndicator() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    hideActivityIndicator();
                }
                Log.d(Constants.LOG_TAG, "MainActivity.showActivityIndicator");
                progressDialog = new ACProgressFlower.Builder(MainActivity.this)
                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                        .themeColor(Color.WHITE)
                        .text(getResources().getString(R.string.please_wait))
                        .fadeColor(Color.DKGRAY).build();
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        });
    }

    public void hideActivityIndicator() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    Log.d(Constants.LOG_TAG, "MainActivity.hideActivityIndicator");
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });

    }
}