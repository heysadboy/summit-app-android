package org.openstack.android.summit.modules.personal_schedule.user_interface;

import org.joda.time.DateTime;
import org.openstack.android.summit.common.DTOs.ScheduleItemDTO;
import org.openstack.android.summit.common.IScheduleFilter;
import org.openstack.android.summit.common.user_interface.IScheduleItemView;
import org.openstack.android.summit.common.user_interface.IScheduleItemViewBuilder;
import org.openstack.android.summit.common.user_interface.IScheduleablePresenter;
import org.openstack.android.summit.common.user_interface.SchedulePresenter;
import org.openstack.android.summit.modules.personal_schedule.IPersonalScheduleWireframe;
import org.openstack.android.summit.modules.personal_schedule.business_logic.IPersonalScheduleInteractor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claudio Redi on 1/27/2016.
 */
public class PersonalSchedulePresenter
        extends SchedulePresenter<IPersonalScheduleView, IPersonalScheduleInteractor, IPersonalScheduleWireframe>
        implements IPersonalSchedulePresenter {

    public PersonalSchedulePresenter(IPersonalScheduleInteractor interactor, IPersonalScheduleWireframe wireframe, IScheduleablePresenter scheduleablePresenter, IScheduleItemViewBuilder scheduleItemViewBuilder, IScheduleFilter scheduleFilter) {
        super(interactor, wireframe, scheduleablePresenter, scheduleItemViewBuilder, scheduleFilter);

        this.toggleScheduleStatusListener = (position, formerState, viewItem) -> {
            if(formerState && !viewItem.getFavorite()) {
                removeItem(position);
            }
        };

        this.toggleFavoriteStatusListener = (position, formerState, viewItem) -> {
            if(formerState && !viewItem.getScheduled()) {
                removeItem(position);
            }
        };
    }

    @Override
    public void onResume() {
        this.shouldShowNow = false;
        super.onResume();
    }

    @Override
    protected List<ScheduleItemDTO> getScheduleEvents(DateTime startDate, DateTime endDate, IPersonalScheduleInteractor interactor) {
        if(!interactor.isMemberLoggedInAndConfirmedAttendee()) return new ArrayList<>();
        return interactor.getCurrentMemberScheduledEvents(startDate.toDate(), endDate.toDate());
    }

    @Override
    protected List<DateTime> getDatesWithoutEvents(DateTime startDate, DateTime endDate) {
        return interactor.isMemberLoggedInAndConfirmedAttendee()?
                interactor.getCurrentMemberScheduleDatesWithoutEvents(startDate, endDate):
                new ArrayList<>();
    }

    @Override
    public void toggleScheduleStatus(IScheduleItemView scheduleItemView, final int position) {
        _toggleScheduleStatus(scheduleItemView, position);
    }

    @Override
    protected ScheduleItemDTO getCurrentItem(int position) {
        if (dayEvents.size() - 1 < position || dayEvents.size() == 0 || position < 0) return null;
        return dayEvents.get(position);
    }

    private void removeItem(int position){
        if (dayEvents.size() - 1 < position || dayEvents.size() == 0 || position < 0) return;
        dayEvents.remove(position);
        view.removeItem(position);
    }

    @Override
    public void toggleFavoriteStatus(IScheduleItemView scheduleItemView, int position) {
       _toggleFavoriteStatus(scheduleItemView, position);
    }

    @Override
    public void buildItem(IScheduleItemView scheduleItemView, int position) {
        if (dayEvents.size() - 1 < position || dayEvents.size() == 0 || position < 0) return;
        ScheduleItemDTO scheduleItemDTO = dayEvents.get(position);
        scheduleItemViewBuilder.build
                (
                        scheduleItemView,
                        scheduleItemDTO,
                        isMemberLogged,
                        isMemberAttendee,
                        scheduleItemDTO.getScheduled(),
                        scheduleItemDTO.getFavorite(),
                        false,
                        shouldShowVenues,
                        scheduleItemDTO.getRSVPLink() ,
                        scheduleItemDTO.isExternalRSVP(),
                        scheduleItemDTO.getAllowFeedback(),
                        scheduleItemDTO.isToRecord(),
                        true,
                        false
                );
    }
}
