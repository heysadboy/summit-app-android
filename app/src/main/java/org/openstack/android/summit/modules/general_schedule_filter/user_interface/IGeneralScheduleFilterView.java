package org.openstack.android.summit.modules.general_schedule_filter.user_interface;

import org.openstack.android.summit.common.DTOs.NamedDTO;
import org.openstack.android.summit.common.DTOs.TrackGroupDTO;
import org.openstack.android.summit.common.user_interface.IBaseView;

import java.util.List;

/**
 * Created by Claudio Redi on 2/2/2016.
 */
public interface IGeneralScheduleFilterView extends IBaseView {

    void showTrackGroups(List<TrackGroupDTO> trackGroups);

    void showEventTypes(List<NamedDTO> eventTypes);

    void showVenues(List<NamedDTO> venues);

    void showLevels(List<String> levels);

    void showSummitTypes(List<NamedDTO> summitTypes);

    void toggleShowPastTalks(boolean isChecked);

    void showShowPastTalks(boolean show);
}
