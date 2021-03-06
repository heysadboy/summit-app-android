package org.openstack.android.summit.modules.level_schedule.business_logic;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.api.ISummitSelector;
import org.openstack.android.summit.common.data_access.repositories.IMemberDataStore;
import org.openstack.android.summit.common.push_notifications.IPushNotificationsManager;
import org.openstack.android.summit.common.ISession;
import org.openstack.android.summit.common.business_logic.ScheduleInteractor;
import org.openstack.android.summit.common.data_access.repositories.ISummitAttendeeDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitEventDataStore;
import org.openstack.android.summit.common.security.ISecurityManager;

import javax.inject.Inject;

/**
 * Created by Claudio Redi on 1/11/2016.
 */
public class LevelScheduleInteractor extends ScheduleInteractor implements ILevelScheduleInteractor {
    @Inject
    public LevelScheduleInteractor(IMemberDataStore memberDataStore, ISummitEventDataStore summitEventDataStore, ISummitDataStore summitDataStore, ISummitAttendeeDataStore summitAttendeeDataStore, IDTOAssembler dtoAssembler, ISecurityManager securityManager, IPushNotificationsManager pushNotificationsManager, ISession session, ISummitSelector summitSelector) {
        super(summitEventDataStore, summitDataStore, summitAttendeeDataStore, memberDataStore, dtoAssembler, securityManager, pushNotificationsManager, session, summitSelector);
    }
}
