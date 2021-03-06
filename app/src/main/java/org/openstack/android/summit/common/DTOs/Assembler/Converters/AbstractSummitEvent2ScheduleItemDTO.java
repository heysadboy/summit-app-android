package org.openstack.android.summit.common.DTOs.Assembler.Converters;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.joda.time.DateTime;
import org.modelmapper.AbstractConverter;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.DTOs.ScheduleItemDTO;
import org.openstack.android.summit.common.entities.Company;
import org.openstack.android.summit.common.entities.Summit;
import org.openstack.android.summit.common.entities.SummitEvent;
import org.openstack.android.summit.common.entities.Venue;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Claudio Redi on 12/28/2015.
 */

public class AbstractSummitEvent2ScheduleItemDTO<S extends SummitEvent, T extends ScheduleItemDTO> extends AbstractConverter<S, T> {

    protected void convertInternal(S source, ScheduleItemDTO scheduleItemDTO) {

        scheduleItemDTO.setId(source.getId());
        scheduleItemDTO.setName(source.getName());
        String time = getTime(source);
        scheduleItemDTO.setTime(time);
        String dateTime = getDateTime(source);
        scheduleItemDTO.setDateTime(dateTime);
        scheduleItemDTO.setStartDate( new DateTime(source.getStart()));
        scheduleItemDTO.setEndDate( new DateTime(source.getEnd()));
        scheduleItemDTO.setStarted(isStarted(source));
        scheduleItemDTO.setLocation(getLocation(source));
        scheduleItemDTO.setLocationAddress(getLocationAddress(source));
        scheduleItemDTO.setRoom(source.getVenueRoom() != null ?source.getVenueRoom().getName() : null);
        scheduleItemDTO.setEventType(source.getType() != null ? source.getType().getName() : "");
        scheduleItemDTO.setAllowFeedback(source.getAllowFeedback());
        String sponsors = getSponsors(source);
        scheduleItemDTO.setSponsors(sponsors);
        scheduleItemDTO.setTrack(source.getTrack() != null
                        ? source.getTrack().getName()
                        : null
        );

        if (source.getTrack() != null && source.getTrack().getTrackGroups().size() > 0) {
            scheduleItemDTO.setColor(source.getTrack().getTrackGroups().get(0).getColor());
        }
        else if(source.getType() != null ){
            // use the color of the event type
            scheduleItemDTO.setColor(source.getType().getColor());
        }

        scheduleItemDTO.setSummitId(source.getSummit().getId());
        scheduleItemDTO.setRSVPLink(source.getRsvpLink());
        scheduleItemDTO.setExternalRSVP(source.isRsvpExternal());
        scheduleItemDTO.setSocialSummary(source.getSocialDescription());
        Summit summit = source.getSummit();

        if (summit != null) {
            String templateUrl = summit.getScheduleEventDetailUrl();
            String eventUrl    = templateUrl
                    .replace(":event_id", Integer.toString(source.getId()))
                    .replace(":event_title", scheduleItemDTO.getSlug());

            scheduleItemDTO.setEventUrl(eventUrl);
        }

        if (source.getPresentation() != null) {
            scheduleItemDTO.setPresentation(true);
            scheduleItemDTO.setToRecord(source.getPresentation().isToRecord());
        }


    }

    protected Boolean isStarted(S summitEvent){
        return summitEvent.getStart().getTime() <= System.currentTimeMillis();
    }

    protected String getSponsors(S summitEvent) {
        String sponsors = "";

        if (summitEvent.getSponsors().size() > 0) {
            sponsors = "Sponsored by ";
            String separator = "";
            for(Company company: summitEvent.getSponsors()) {
                sponsors += separator + company.getName();
                separator = ", ";
            }
        }

        return sponsors;
    }

    protected String getLocation(S summitEvent) {
        String location = "";
        if (summitEvent.getVenueRoom() != null && summitEvent.getVenueRoom().getVenue() != null) {
            location = summitEvent.getVenueRoom().getVenue().getName();
            if(summitEvent.getVenueRoom().getFloor() != null)
            {
                location +=" - " + summitEvent.getVenueRoom().getFloor().getName();
            }
            location +=" - " + summitEvent.getVenueRoom().getName();
        }
        else if (summitEvent.getVenue() != null){
            location = summitEvent.getVenue().getName();
        }
        return location;
    }

    protected String getLocationAddress(S summitEvent){
        String address = "";
        Venue venue    = summitEvent.getVenue() != null ? summitEvent.getVenue():(summitEvent.getVenueRoom() != null ? summitEvent.getVenueRoom().getVenue() : null) ;

        if(venue != null){
            address = venue.getName();
            if(venue.getAddress() != null && !venue.getAddress().isEmpty()){
                address += ", " + venue.getAddress();
            }
            if(venue.getZipCode() != null && !venue.getZipCode().isEmpty()){
                address += ", " + venue.getZipCode();
            }
            if(venue.getCity() != null && !venue.getCity().isEmpty()){
                address += ", " + venue.getCity();
            }
            if(venue.getState() != null && !venue.getState().isEmpty()){
                address += ", " + venue.getState();
            }
            if(venue.getCountry() != null && !venue.getCountry().isEmpty()){
                address += ", " + venue.getCountry();
            }
        }

        if (summitEvent.getVenueRoom() != null) {
            if(summitEvent.getVenueRoom().getFloor() != null)
            {
                address +=", " + summitEvent.getVenueRoom().getFloor().getName();
            }
            address += ", " + summitEvent.getVenueRoom().getName();
        }
        return address;
    }

    protected String getDateTime(S summitEvent) {
        Summit summit = null;
        try {

            summit = summitEvent.getSummit();

            if(summit == null)
                throw new InvalidParameterException("missing summit on event id "+ summitEvent.getId());

            if(summit.getTimeZone() == null)
                throw new InvalidParameterException("summit timezone id is not set for summit id "+ summit.getId());

            TimeZone timeZone = TimeZone.getTimeZone(summit.getTimeZone());

            DateFormat formatterFrom = new SimpleDateFormat("EEEE dd MMMM hh:mm a");
            formatterFrom.setTimeZone(timeZone);

            DateFormat formatterTo = new SimpleDateFormat("hh:mm a");
            formatterTo.setTimeZone(timeZone);

            return String.format("%s / %s", formatterFrom.format(summitEvent.getStart()), formatterTo.format(summitEvent.getEnd()));

        }
        catch (Exception ex){
            Log.w(Constants.LOG_TAG, ex);
            Crashlytics.logException(ex);
        }
        return "INVALID";
    }

    protected String getTime(S summitEvent) {
        Summit summit = null;
        try {
            DateFormat formatterFrom = new SimpleDateFormat("hh:mm a");
            summit                   = summitEvent.getSummit();

            if(summit == null)
                throw new InvalidParameterException("missing summit on event id "+ summitEvent.getId());

            if(summit.getTimeZone() == null)
                throw new InvalidParameterException("summit timezone id is not set for summit id "+ summit.getId());

            TimeZone timeZone = TimeZone.getTimeZone(summit.getTimeZone());
            formatterFrom.setTimeZone(timeZone);
            DateFormat formatterTo = new SimpleDateFormat("hh:mm a");
            formatterTo.setTimeZone(timeZone);
            String timeRange = String.format("%s / %s", formatterFrom.format(summitEvent.getStart()), formatterTo.format(summitEvent.getEnd()));
            return timeRange.toLowerCase();
        }
        catch (Exception ex){
            Log.w(Constants.LOG_TAG, ex);
            Crashlytics.logException(ex);
        }
        return "INVALID";
    }

    @Override
    protected T convert(S source) {
        ScheduleItemDTO scheduleItemDTO = new ScheduleItemDTO();
        try {
            convertInternal(source, scheduleItemDTO);
        }
        catch (Exception e) {
            Crashlytics.logException(e);
            Log.e(Constants.LOG_TAG, e.getMessage(), e);
            throw e;
        }
        return (T)scheduleItemDTO;
    }
}
