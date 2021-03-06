package org.openstack.android.summit.modules.push_notifications_inbox;

import android.support.v4.app.FragmentManager;

import org.openstack.android.summit.R;
import org.openstack.android.summit.common.BaseWireframe;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.INavigationParametersStore;
import org.openstack.android.summit.common.user_interface.IBaseView;
import org.openstack.android.summit.modules.push_notifications_inbox.user_interface.PushNotificationDetailFragment;
import org.openstack.android.summit.modules.push_notifications_inbox.user_interface.PushPushNotificationsListFragment;

/**
 * Created by sebastian on 8/19/2016.
 */
final public class PushPushNotificationsWireframe extends BaseWireframe implements IPushNotificationsWireframe {

    public PushPushNotificationsWireframe(INavigationParametersStore navigationParametersStore) {
        super(navigationParametersStore);
    }

    @Override
    public void presentNotificationsListView(IBaseView context) {
        PushPushNotificationsListFragment notificationsListFragment = new PushPushNotificationsListFragment();
        FragmentManager fragmentManager = context.getSupportFragmentManager();

        fragmentManager
                .beginTransaction()
                .setCustomAnimations
                        (
                                R.anim.slide_in_left,
                                R.anim.slide_out_left,
                                R.anim.slide_out_right,
                                R.anim.slide_in_right
                        )
                    .replace(R.id.frame_layout_content, notificationsListFragment, "nav_notifications")
                    .addToBackStack("nav_notifications")
                .commitAllowingStateLoss();
    }

    @Override
    public void showNotification(int notificationId, IBaseView context) {
        navigationParametersStore.put(Constants.NAVIGATION_PARAMETER_NOTIFICATION_ID, notificationId);
        PushNotificationDetailFragment detailFragment = new PushNotificationDetailFragment();
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                    .replace(R.id.frame_layout_content, detailFragment)
                    .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}
