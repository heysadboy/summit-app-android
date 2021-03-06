package org.openstack.android.summit.modules.splash.user_interface;

import org.openstack.android.summit.OpenStackSummitApplication;
import org.openstack.android.summit.R;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.entities.Member;
import org.openstack.android.summit.common.entities.notifications.IPushNotification;
import org.openstack.android.summit.common.entities.notifications.IPushNotificationFactory;
import org.openstack.android.summit.common.entities.notifications.PushNotification;
import org.openstack.android.summit.common.security.ISecurityManager;
import org.openstack.android.summit.common.utils.DeepLinkInfo;
import org.openstack.android.summit.common.utils.IAppLinkRouter;
import org.openstack.android.summit.dagger.components.ApplicationComponent;
import org.openstack.android.summit.modules.push_notifications_inbox.business_logic.IPushNotificationInteractor;
import org.openstack.android.summit.modules.push_notifications_inbox.business_logic.ISettingsInteractor;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by smarcet on 2/3/17.
 */

public class SplashActivity extends AppCompatActivity implements ISplashView {

    @Inject
    ISplashPresenter presenter;

    @Inject
    ISettingsInteractor settings;

    private Button loginButton;
    private Button guestButton;
    private LinearLayout summitInfoContainer;
    private TextView summitDates;
    private TextView summitName;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // todo: move to butterkniffe
        loginButton         = (Button) this.findViewById(R.id.btn_splash_login);
        guestButton         = (Button) this.findViewById(R.id.btn_splash_guest);
        summitInfoContainer = (LinearLayout) this.findViewById(R.id.splash_summit_info_container);
        summitDates         = (TextView) this.findViewById(R.id.splash_summit_dates);
        summitName          = (TextView) this.findViewById(R.id.splash_summit_name );
        getApplicationComponent().inject(this);
        StartAnimations();
        presenter.setView(this);
        presenter.onCreate(savedInstanceState);

        loginButton.setOnClickListener(v -> presenter.loginClicked(v));

        guestButton.setOnClickListener(v -> presenter.guestClicked(v));

    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.splash_main_container);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash_logo);
        iv.clearAnimation();
        iv.startAnimation(anim);

    }

    public ApplicationComponent getApplicationComponent() {
        return ((OpenStackSummitApplication) getApplication()).getApplicationComponent();
    }

    @Override
    protected void onResume() {
        try {
            Log.d(Constants.LOG_TAG, "SplashActivity.onResume");
            super.onResume();
            presenter.onResume();

        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        presenter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void showActivityIndicator(int delay) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void showActivityIndicator() {

    }

    @Override
    public void hideActivityIndicator() {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public FragmentActivity getFragmentActivity() {
        return null;
    }

    @Override
    public void setLoginButtonVisibility(boolean visible) {
        loginButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setGuestButtonVisibility(boolean visible) {
        guestButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSummitDates(String summitDates) {
        this.summitDates.setText(summitDates);
    }

    @Override
    public void setSummitName(String summitName) {
        this.summitName.setText(summitName);
    }

    @Override
    public void setSummitInfoContainerVisibility(boolean visible) {
        summitInfoContainer.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}
