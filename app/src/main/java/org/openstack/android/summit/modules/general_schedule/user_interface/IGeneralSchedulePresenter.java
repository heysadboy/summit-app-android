package org.openstack.android.summit.modules.general_schedule.user_interface;

import org.openstack.android.summit.common.user_interface.IPresenter;
import org.openstack.android.summit.common.user_interface.IScheduleItem;

/**
 * Created by Claudio Redi on 12/21/2015.
 */
public interface IGeneralSchedulePresenter extends IPresenter<GeneralScheduleFragment> {
    void buildItem(IScheduleItem item, int position);
}
