package org.openstack.android.summit.modules.member_profile;

import org.openstack.android.summit.common.IBaseWireframe;
import org.openstack.android.summit.common.user_interface.IBaseView;

/**
 * Created by Claudio Redi on 1/26/2016.
 */
public interface IMemberProfileWireframe extends IBaseWireframe{

    void presentOtherSpeakerProfileView(int speakerId, IBaseView context);

    void presentMyProfileView(IBaseView context, String defaultTabTitle);

    void presentMyProfileView(IBaseView context);

    void showEventsView(IBaseView context);
}
