package org.openstack.android.summit.common.data_access.data_polling;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.openstack.android.summit.OpenStackSummitApplication;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.api.ISummitSelector;
import org.openstack.android.summit.common.data_access.repositories.ISummitAttendeeDataStore;
import org.openstack.android.summit.common.entities.DataUpdate;
import org.openstack.android.summit.common.entities.Member;
import org.openstack.android.summit.common.entities.SummitEvent;
import org.openstack.android.summit.common.security.ISecurityManager;

/**
 * Created by Claudio Redi on 2/8/2016.
 */
public class MyScheduleDataUpdateStrategy extends DataUpdateStrategy {
    ISecurityManager securityManager;
    ISummitAttendeeDataStore summitAttendeeDataStore;

    public MyScheduleDataUpdateStrategy(ISummitAttendeeDataStore summitAttendeeDataStore, ISecurityManager securityManager, ISummitSelector summitSelector) {
        super(summitSelector);
        this.summitAttendeeDataStore = summitAttendeeDataStore;
        this.securityManager         = securityManager;
    }

    @Override
    public void process(DataUpdate dataUpdate) {
        Member currentMember = securityManager.getCurrentMember();

        if(currentMember == null) return;
        if(currentMember.getAttendeeRole() == null) return;
        Intent intent = null;
        switch (dataUpdate.getOperation()) {
            case DataOperation.Insert:
            case DataOperation.Update:


                summitAttendeeDataStore.addEventToMemberScheduleLocal(currentMember.getAttendeeRole(), (SummitEvent)dataUpdate.getEntity());
                intent = new Intent(Constants.DATA_UPDATE_MY_SCHEDULE_EVENT_ADDED);
                intent.putExtra(Constants.DATA_UPDATE_ENTITY_ID, dataUpdate.getEntityId());
                intent.putExtra(Constants.DATA_UPDATE_ENTITY_CLASS, dataUpdate.getEntityClassName());
                LocalBroadcastManager.getInstance(OpenStackSummitApplication.context).sendBroadcast(intent);
                break;
            case DataOperation.Delete:

                summitAttendeeDataStore.removeEventFromMemberScheduleLocal(currentMember.getAttendeeRole(), (SummitEvent)dataUpdate.getEntity());
                intent = new Intent(Constants.DATA_UPDATE_MY_SCHEDULE_EVENT_DELETED);
                intent.putExtra(Constants.DATA_UPDATE_ENTITY_ID, dataUpdate.getEntityId());
                intent.putExtra(Constants.DATA_UPDATE_ENTITY_CLASS, dataUpdate.getEntityClassName());
                LocalBroadcastManager.getInstance(OpenStackSummitApplication.context).sendBroadcast(intent);
                break;
        }
    }
}
