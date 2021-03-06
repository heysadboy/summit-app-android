package org.openstack.android.summit.dagger.modules;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.IScheduleFilter;
import org.openstack.android.summit.common.api.ISummitSelector;
import org.openstack.android.summit.common.data_access.repositories.ISummitDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitEventDataStore;
import org.openstack.android.summit.common.data_access.repositories.ITrackDataStore;
import org.openstack.android.summit.common.security.ISecurityManager;
import org.openstack.android.summit.modules.track_list.ITrackListWireframe;
import org.openstack.android.summit.modules.track_list.TrackListWireframe;
import org.openstack.android.summit.modules.track_list.business_logic.ITrackListInteractor;
import org.openstack.android.summit.modules.track_list.business_logic.TrackListInteractor;
import org.openstack.android.summit.modules.track_list.user_interface.ITrackListPresenter;
import org.openstack.android.summit.modules.track_list.user_interface.TrackListFragment;
import org.openstack.android.summit.modules.track_list.user_interface.TrackListPresenter;
import org.openstack.android.summit.modules.track_schedule.ITrackScheduleWireframe;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Claudio Redi on 12/22/2015.
 */
@Module
public class TrackListModule {
    @Provides
    TrackListFragment providesTrackListFragment() {
        return new TrackListFragment();
    }
    
    @Provides
    ITrackListWireframe providesTrackListWireframe(ITrackScheduleWireframe trackScheduleWireframe) {
        return new TrackListWireframe(trackScheduleWireframe);
    }

    @Provides
    ITrackListInteractor providesTrackListInteractor
    (
        ISecurityManager securityManager,
        IDTOAssembler dtoAssembler,
        ISummitEventDataStore summitEventDataStore,
        ITrackDataStore trackDataStore,
        ISummitDataStore summitDataStore,
        ISummitSelector summitSelector
    ) {
        return new TrackListInteractor(securityManager, dtoAssembler, summitEventDataStore, trackDataStore, summitDataStore, summitSelector);
    }

    @Provides
    ITrackListPresenter providesTrackListPresenter(ITrackListInteractor trackListInteractor, ITrackListWireframe trackListWireframe, IScheduleFilter scheduleFilter) {
        return new TrackListPresenter(trackListInteractor, trackListWireframe, scheduleFilter);
    }    
}
