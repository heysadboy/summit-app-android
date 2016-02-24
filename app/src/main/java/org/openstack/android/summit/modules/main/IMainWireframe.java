package org.openstack.android.summit.modules.main;

import org.openstack.android.summit.common.user_interface.IBaseView;

/**
 * Created by Claudio Redi on 2/12/2016.
 */
public interface IMainWireframe {
    void showEventsView(IBaseView context);

    void showMyProfileView(IBaseView context);

    void showSpeakerListView(IBaseView context);

    void showSearchView(String searchTerm, IBaseView context);

    void showVenuesView(IBaseView context);
}