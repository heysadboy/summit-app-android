package org.openstack.android.summit.dagger.modules;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.business_logic.IScheduleableInteractor;
import org.openstack.android.summit.common.business_logic.ScheduleableInteractor;
import org.openstack.android.summit.common.data_access.ISummitAttendeeDataStore;
import org.openstack.android.summit.common.data_access.ISummitDataStore;
import org.openstack.android.summit.common.data_access.ISummitEventDataStore;
import org.openstack.android.summit.common.data_access.data_polling.IDataUpdatePoller;
import org.openstack.android.summit.common.push_notifications.IPushNotificationsManager;
import org.openstack.android.summit.common.security.ISecurityManager;
import org.openstack.android.summit.common.user_interface.IScheduleablePresenter;
import org.openstack.android.summit.common.user_interface.ScheduleablePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Claudio Redi on 1/15/2016.
 */
@Module
public class ScheduleableModule {
    @Provides
    IScheduleableInteractor providesScheduleableInteractor(ISummitEventDataStore summitEventDataStore, ISummitAttendeeDataStore summitAttendeeDataStore, ISummitDataStore summitDataStore, IDTOAssembler dtoAssembler, ISecurityManager securityManager, IDataUpdatePoller dataUpdatePoller, IPushNotificationsManager pushNotificationsManager) {
        return new ScheduleableInteractor(summitEventDataStore, summitAttendeeDataStore, summitDataStore, dtoAssembler, securityManager, dataUpdatePoller, pushNotificationsManager);
    }

    @Provides
    IScheduleablePresenter providesScheduleablePresenter() {
        return new ScheduleablePresenter();
    }
}
