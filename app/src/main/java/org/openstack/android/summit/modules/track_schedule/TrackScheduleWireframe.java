package org.openstack.android.summit.modules.track_schedule;

import android.support.v4.app.FragmentManager;

import org.openstack.android.summit.R;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.DTOs.NamedDTO;
import org.openstack.android.summit.common.INavigationParametersStore;
import org.openstack.android.summit.common.ScheduleWireframe;
import org.openstack.android.summit.common.user_interface.IBaseView;
import org.openstack.android.summit.modules.event_detail.IEventDetailWireframe;
import org.openstack.android.summit.modules.track_schedule.user_interface.TrackScheduleFragment;

/**
 * Created by Claudio Redi on 1/12/2016.
 */
public class TrackScheduleWireframe extends ScheduleWireframe implements ITrackScheduleWireframe {
    public TrackScheduleWireframe(IEventDetailWireframe eventDetailWireframe, INavigationParametersStore navigationParametersStore) {
        super(eventDetailWireframe, navigationParametersStore);
    }

    @Override
    public void presentTrackScheduleView(NamedDTO track, IBaseView context) {
        TrackScheduleFragment trackScheduleFragment = new TrackScheduleFragment();
        navigationParametersStore.put(Constants.NAVIGATION_PARAMETER_TRACK, track.getId());
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout_content, trackScheduleFragment)
                .addToBackStack(null)
                .commit();

    }
}

