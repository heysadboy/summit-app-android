package org.openstack.android.summit.dagger.modules;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.INavigationParametersStore;
import org.openstack.android.summit.common.IScheduleFilter;
import org.openstack.android.summit.common.ISession;
import org.openstack.android.summit.common.api.ISummitSelector;
import org.openstack.android.summit.common.data_access.repositories.IMemberDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitAttendeeDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitEventDataStore;
import org.openstack.android.summit.common.network.IReachability;
import org.openstack.android.summit.common.push_notifications.IPushNotificationsManager;
import org.openstack.android.summit.common.security.ISecurityManager;
import org.openstack.android.summit.common.user_interface.IScheduleablePresenter;
import org.openstack.android.summit.common.user_interface.ScheduleItemViewBuilder;
import org.openstack.android.summit.modules.event_detail.IEventDetailWireframe;
import org.openstack.android.summit.modules.general_schedule.GeneralScheduleWireframe;
import org.openstack.android.summit.modules.general_schedule.IGeneralScheduleWireframe;
import org.openstack.android.summit.modules.general_schedule.business_logic.GeneralScheduleInteractor;
import org.openstack.android.summit.modules.general_schedule.business_logic.IGeneralScheduleInteractor;
import org.openstack.android.summit.modules.general_schedule.user_interface.GeneralScheduleFragment;
import org.openstack.android.summit.modules.general_schedule.user_interface.GeneralSchedulePresenter;
import org.openstack.android.summit.modules.general_schedule.user_interface.IGeneralSchedulePresenter;
import org.openstack.android.summit.modules.general_schedule_filter.IGeneralScheduleFilterWireframe;
import org.openstack.android.summit.modules.rsvp.IRSVPWireframe;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Claudio Redi on 12/21/2015.
 */
@Module
public class GeneralScheduleModule {

    @Provides
    GeneralScheduleFragment providesGeneralScheduleFragment() {
        return new GeneralScheduleFragment();
    }

    @Provides
    IGeneralScheduleWireframe providesGeneralScheduleWireframe
    (
            IEventDetailWireframe eventDetailWireframe,
            IRSVPWireframe rsvpWireframe,
            IGeneralScheduleFilterWireframe generalScheduleFilterWireframe,
            INavigationParametersStore navigationParametersStore
    )
    {
        return new GeneralScheduleWireframe(rsvpWireframe, eventDetailWireframe, generalScheduleFilterWireframe, navigationParametersStore);
    }

    @Provides
    IGeneralScheduleInteractor providesGeneralScheduleInteractor(IMemberDataStore memberDataStore, ISummitEventDataStore summitEventDataStore, ISummitDataStore summitDataStore, ISummitAttendeeDataStore summitAttendeeDataStore, IDTOAssembler dtoAssembler, ISecurityManager securityManager, IPushNotificationsManager pushNotificationsManager, ISession session, IReachability reachability, ISummitSelector summitSelector) {
        return new GeneralScheduleInteractor
        (
                summitEventDataStore,
                memberDataStore,
                summitDataStore,
                summitAttendeeDataStore,
                dtoAssembler,
                securityManager,
                pushNotificationsManager,
                session,
                reachability,
                summitSelector
        );
    }

    @Provides
    IGeneralSchedulePresenter providesGeneralSchedulePresenter(IGeneralScheduleInteractor generalScheduleInteractor, IGeneralScheduleWireframe generalScheduleWireframe, IScheduleablePresenter scheduleablePresenter, IScheduleFilter scheduleFilter) {
        return new GeneralSchedulePresenter(generalScheduleInteractor, generalScheduleWireframe, scheduleablePresenter, new ScheduleItemViewBuilder(), scheduleFilter);
    }
}
