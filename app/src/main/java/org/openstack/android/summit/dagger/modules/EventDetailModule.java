package org.openstack.android.summit.dagger.modules;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.INavigationParametersStore;
import org.openstack.android.summit.common.api.ISummitSelector;
import org.openstack.android.summit.common.data_access.repositories.IMemberDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitAttendeeDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitEventDataStore;
import org.openstack.android.summit.common.network.Reachability;
import org.openstack.android.summit.common.push_notifications.IPushNotificationsManager;
import org.openstack.android.summit.common.security.ISecurityManager;
import org.openstack.android.summit.common.user_interface.IScheduleablePresenter;
import org.openstack.android.summit.modules.event_detail.EventDetailWireframe;
import org.openstack.android.summit.modules.event_detail.IEventDetailWireframe;
import org.openstack.android.summit.modules.event_detail.business_logic.EventDetailInteractor;
import org.openstack.android.summit.modules.event_detail.business_logic.IEventDetailInteractor;
import org.openstack.android.summit.modules.event_detail.user_interface.EventDetailFragment;
import org.openstack.android.summit.modules.event_detail.user_interface.EventDetailPresenter;
import org.openstack.android.summit.modules.event_detail.user_interface.IEventDetailPresenter;
import org.openstack.android.summit.modules.feedback_edit.IFeedbackEditWireframe;
import org.openstack.android.summit.modules.member_profile.IMemberProfileWireframe;
import org.openstack.android.summit.modules.rsvp.IRSVPWireframe;
import org.openstack.android.summit.modules.venue_detail.IVenueDetailWireframe;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Claudio Redi on 11/17/2015.
 */
@Module
public class EventDetailModule {

    @Provides
    EventDetailFragment providesEventDetailFragment() {
        return new EventDetailFragment();
    }

    @Provides
    IEventDetailPresenter providesEventDetailPresenter(IEventDetailInteractor eventDetailInteractor, IEventDetailWireframe eventDetailWireframe, IScheduleablePresenter scheduleablePresenter) {
        return new EventDetailPresenter(eventDetailInteractor, eventDetailWireframe, scheduleablePresenter);
    }

    @Provides
    IEventDetailInteractor providesEventDetailInteractor(IMemberDataStore memberDataStore, ISummitEventDataStore summitEventDataStore, ISummitAttendeeDataStore summitAttendeeDataStore, ISummitDataStore summitDataStore, IDTOAssembler dtoAssembler, ISecurityManager securityManager, IPushNotificationsManager pushNotificationsManager, ISummitSelector summitSelector) {
        return new EventDetailInteractor(summitEventDataStore, summitAttendeeDataStore, summitDataStore, memberDataStore, new Reachability(), dtoAssembler, securityManager, pushNotificationsManager, summitSelector);
    }

    @Provides
    IEventDetailWireframe providesEventDetailWireframe
    (
            IMemberProfileWireframe memberProfileWireframe,
            IFeedbackEditWireframe feedbackEditWireframe,
            IVenueDetailWireframe venueDetailWireframe,
            IRSVPWireframe rsvpWireframe,
            INavigationParametersStore navigationParametersStore
    )
    {
        return new EventDetailWireframe
        (
            memberProfileWireframe,
            feedbackEditWireframe,
            venueDetailWireframe,
            rsvpWireframe,
            navigationParametersStore
        );
    }
}
