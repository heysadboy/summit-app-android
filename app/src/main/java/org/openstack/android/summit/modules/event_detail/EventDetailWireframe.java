package org.openstack.android.summit.modules.event_detail;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.openstack.android.summit.R;
import org.openstack.android.summit.common.BaseWireframe;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.DTOs.NamedDTO;
import org.openstack.android.summit.common.INavigationParametersStore;
import org.openstack.android.summit.common.user_interface.IBaseView;
import org.openstack.android.summit.modules.event_detail.user_interface.EventDetailFragment;
import org.openstack.android.summit.modules.feedback_edit.IFeedbackEditWireframe;
import org.openstack.android.summit.modules.level_schedule.user_interface.LevelScheduleFragment;
import org.openstack.android.summit.modules.member_profile.IMemberProfileWireframe;
import org.openstack.android.summit.modules.rsvp.IRSVPWireframe;
import org.openstack.android.summit.modules.venue_detail.IVenueDetailWireframe;

import javax.inject.Inject;

/**
 * Created by claudio on 11/2/2015.
 */
public class EventDetailWireframe extends BaseWireframe implements IEventDetailWireframe {

    private IMemberProfileWireframe memberProfileWireframe;
    private IFeedbackEditWireframe feedbackEditWireframe;
    private IVenueDetailWireframe venueDetailWireframe;
    private IRSVPWireframe rsvpWireframe;

    @Inject
    public EventDetailWireframe
    (
            IMemberProfileWireframe memberProfileWireframe,
            IFeedbackEditWireframe feedbackEditWireframe,
            IVenueDetailWireframe venueDetailWireframe,
            IRSVPWireframe rsvpWireframe,
            INavigationParametersStore navigationParametersStore
    )
    {
        super(navigationParametersStore);

        this.memberProfileWireframe = memberProfileWireframe;
        this.feedbackEditWireframe  = feedbackEditWireframe;
        this.venueDetailWireframe   = venueDetailWireframe;
        this.rsvpWireframe          = rsvpWireframe;
    }

    @Override
    public void showEventDetail(int eventId, IBaseView context) {
        try {
            navigationParametersStore.put(Constants.NAVIGATION_PARAMETER_EVENT_ID, eventId);

            EventDetailFragment eventDetailFragment = new EventDetailFragment();
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
                    .replace(R.id.frame_layout_content, eventDetailFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            // Swallowing exception "Can not perform this action after onSaveInstanceState" until we figure out what's wrong
            Crashlytics.logException(e);
            Log.e(Constants.LOG_TAG, e.getMessage(), e);
        }
    }

    @Override
    public void presentEventRsvpView(String rsvpLink, IBaseView context) {
        rsvpWireframe.presentEventRsvpView(rsvpLink, context);
    }

    @Override
    public void showSpeakerProfile(int speakerId, IBaseView context) {
        memberProfileWireframe.presentOtherSpeakerProfileView(speakerId, context);
    }

    @Override
    public void showFeedbackEditView(int eventId, String eventName, int rate, IBaseView view) {
        feedbackEditWireframe.presentFeedbackEditView(eventId, eventName, rate, view);
    }

    @Override
    public void showEventVenueDetailView(int venueId, IBaseView view) {
        NamedDTO venue = new NamedDTO();
        venue.setId(venueId);
        venueDetailWireframe.presentVenueDetailView(venue, view);
    }

    @Override
    public void showEventLocationDetailView(int locationId, IBaseView view) {
        NamedDTO location = new NamedDTO();
        location.setId(locationId);
        venueDetailWireframe.presentLocationDetailView(location, view);
    }

    @Override
    public void presentLevelScheduleView(String level,  IBaseView view) {
        try {
            LevelScheduleFragment levelScheduleFragment = new LevelScheduleFragment();
            navigationParametersStore.put(Constants.NAVIGATION_PARAMETER_LEVEL, level);
            FragmentManager fragmentManager = view.getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations
                            (
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_left,
                                    R.anim.slide_out_right,
                                    R.anim.slide_in_right
                            )
                    .replace(R.id.frame_layout_content, levelScheduleFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
        catch(Exception ex){
            Crashlytics.logException(ex);
        }
    }
}
