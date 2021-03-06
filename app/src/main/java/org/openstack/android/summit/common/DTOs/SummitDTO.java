package org.openstack.android.summit.common.DTOs;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Claudio Redi on 11/18/2015.
 */
public class SummitDTO extends NamedDTO {

    private Date startDate;
    private Date endDate;
    private String timeZone;
    private Date startShowingVenuesDate;
    private Date scheduleStartDate;
    private String datesLabel;

    private List<WifiListItemDTO> wifiConnections = new ArrayList<WifiListItemDTO>();

    public Date getScheduleStartDate() {
        return scheduleStartDate;
    }

    public void setScheduleStartDate(Date scheduleStartDate) {
        this.scheduleStartDate = scheduleStartDate;
    }

    public int getScheduleStartDay() {
        DateTimeZone summitTimeZone = DateTimeZone.forID(getTimeZone());
        DateTime auxStartSchedule   = new DateTime(scheduleStartDate.getTime(), summitTimeZone).withTime(0, 0, 0, 0);
        return auxStartSchedule.getDayOfMonth();
    }

    public DateTime getFirstEnabledDate(List<DateTime> inactiveDates){
        DateTime start = this.getLocalStartDate();
        DateTime end   = this.getLocalEndDate();
        DateTime first = null;

        while( start.isBefore(end.getMillis())){

          if(inactiveDates.contains(start)){
              start = start.plusDays(1);
              continue;
          }
          first = start;
          break;
        }
        return first;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public DateTime getLocalStartDate() {
        DateTimeZone summitTimeZone = DateTimeZone.forID(getTimeZone());
        return new DateTime(getStartDate(), summitTimeZone);
    }

    public DateTime getLocalEndDate() {
        DateTimeZone summitTimeZone = DateTimeZone.forID(getTimeZone());
        return new DateTime(getEndDate(), summitTimeZone);
    }

    public DateTime convertFromLocalToUTC(DateTime dateTime) {
        DateTimeZone summitTimeZone = DateTimeZone.forID(getTimeZone());
        DateTime localDateTime = new DateTime(dateTime, summitTimeZone);
        return localDateTime.toDateTime(DateTimeZone.UTC);
    }

    public DateTime getLocalScheduleStartDate(){
        DateTimeZone summitTimeZone = DateTimeZone.forID(getTimeZone());
        return new DateTime(getScheduleStartDate().getTime(), summitTimeZone).withTime(0,0,0,0);
    }

    public DateTime convertFromUTCToLocal(DateTime dateTime) {
        DateTimeZone summitTimeZone = DateTimeZone.forID(getTimeZone());
        DateTime utcDateTime = new DateTime(dateTime, DateTimeZone.UTC);
        return utcDateTime.toDateTime(summitTimeZone);
    }

    public DateTime getCurrentLocalTime() {
        return this.convertFromUTCToLocal(new DateTime(DateTimeZone.UTC));
    }

    public boolean isCurrentDateTimeInsideSummitRange() {
        DateTime currentLocal = getCurrentLocalTime();
        return this.startDate.before(currentLocal.toDate()) && this.endDate.after(currentLocal.toDate());
    }

    public ArrayList<DateTime> getPastDates() {
        ArrayList<DateTime> list = new ArrayList<>();
        if (this.isCurrentDateTimeInsideSummitRange()) {
            DateTime currentLocal       = getCurrentLocalTime();
            Calendar c                  = Calendar.getInstance();
            DateTimeZone summitTimeZone = DateTimeZone.forID(getTimeZone());
            do {
                c.setTime(currentLocal.withTime(0, 0, 0, 0).toDate());
                c.add(Calendar.DATE, -1);
                currentLocal = new DateTime(c.getTime(), summitTimeZone);
                if (getLocalStartDate().withTime(0, 0, 0, 0).getMillis() > currentLocal.withTime(0, 0, 0, 0).getMillis())
                    break;
                list.add(currentLocal.withTime(0, 0, 0, 0));

            } while (true);
        }
        return list;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Date getStartShowingVenuesDate() {
        return startShowingVenuesDate;
    }

    public void setStartShowingVenuesDate(Date startShowingVenuesDate) {
        this.startShowingVenuesDate = startShowingVenuesDate;
    }

    public List<WifiListItemDTO> getWifiConnections() {
        return wifiConnections;
    }

    public void setWifiConnections(List<WifiListItemDTO> wifiConnections) {
        this.wifiConnections = wifiConnections;
    }

    public String getDatesLabel() {
        return datesLabel;
    }

    public void setDatesLabel(String datesLabel) {
        this.datesLabel = datesLabel;
    }
}
