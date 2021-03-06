package org.openstack.android.summit.modules.member_profile;

import android.support.v4.app.FragmentManager;

import org.openstack.android.summit.R;
import org.openstack.android.summit.common.BaseWireframe;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.INavigationParametersStore;
import org.openstack.android.summit.common.user_interface.IBaseView;
import org.openstack.android.summit.modules.events.IEventsWireframe;
import org.openstack.android.summit.modules.member_profile.user_interface.MemberProfileFragment;

import javax.inject.Inject;

/**
 * Created by Claudio Redi on 1/26/2016.
 */
public class MemberProfileWireframe extends BaseWireframe implements IMemberProfileWireframe {
    IEventsWireframe eventsWireframe;

    @Inject
    public MemberProfileWireframe(INavigationParametersStore navigationParametersStore, IEventsWireframe eventsWireframe) {
        super(navigationParametersStore);
        this.eventsWireframe = eventsWireframe;
    }

    @Override
    public void presentOtherSpeakerProfileView(int speakerId, IBaseView context) {
        navigationParametersStore.put(Constants.NAVIGATION_PARAMETER_IS_MY_PROFILE, false);
        navigationParametersStore.put(Constants.NAVIGATION_PARAMETER_SPEAKER, speakerId);
        presentMemberProfileView(context);
    }

    @Override
    public void presentMyProfileView(IBaseView context){
        presentMyProfileView(context, null);
    }

    @Override
    public void presentMyProfileView(IBaseView context, String defaultTabTitle) {
        navigationParametersStore.put(Constants.NAVIGATION_PARAMETER_IS_MY_PROFILE, true);
        navigationParametersStore.put(Constants.NAVIGATION_PARAMETER_MY_PROFILE_DEFAULT_TAB, defaultTabTitle);
        navigationParametersStore.put(Constants.NAVIGATION_PARAMETER_DAY, 0);

        MemberProfileFragment memberProfileFragment = new MemberProfileFragment();
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
                    .replace(R.id.frame_layout_content, memberProfileFragment, "nav_my_profile")
                    .addToBackStack("nav_my_profile")
                .commitAllowingStateLoss();
    }

    private void presentMemberProfileView(IBaseView context) {
        MemberProfileFragment memberProfileFragment = new MemberProfileFragment();
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
                    .replace(R.id.frame_layout_content, memberProfileFragment)
                    .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void showEventsView(IBaseView context) {
        eventsWireframe.presentEventsView(context);
    }
}