package org.openstack.android.summit.common.DTOs;

import org.openstack.android.summit.common.utils.Slugifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claudio Redi on 11/18/2015.
 */
public class EventDetailDTO extends ScheduleItemDTO {

    private int venueId;
    private int venueRoomId;
    private int venueFloorId;
    private VideoDTO video;

    public VideoDTO getVideo() {
        return video;
    }

    public void setVideo(VideoDTO video) {
        this.video = video;
    }

    public int getVenueFloorId() {
        return venueFloorId;
    }

    public void setVenueFloorId(int venueFloorId) {
        this.venueFloorId = venueFloorId;
    }

    private String rsvpLink;

    public boolean getAllowRsvp() {
        return this.rsvpLink != null;
    }

    private int headCount;

    public String getRsvpLink() {
        return rsvpLink;
    }

    public void setRsvpLink(String rsvpLink) {
        this.rsvpLink = rsvpLink;
    }

    public int getHeadCount() {
        return headCount;
    }

    public void setHeadCount(int headCount) {
        this.headCount = headCount;
    }

    private String eventDescription;
    private String tags;
    private List<PersonListItemDTO> speakers = new ArrayList<PersonListItemDTO>();

    private Boolean started;
    private Boolean allowFeedback;
    private PersonListItemDTO moderator;
    private String level;
    private String eventUrl;
    private Double averageRate;

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public int getVenueRoomId() {
        return venueRoomId;
    }

    public void setVenueRoomId(int venueRoomId) {
        this.venueRoomId = venueRoomId;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<PersonListItemDTO> getModeratorAndSpeakers() {
        // speakers ...
        List<PersonListItemDTO> speakersList = new ArrayList<>();
        if(getModerator() != null )
            speakersList.add(getModerator());
        if(getSpeakers() != null && getSpeakers().size() > 0)
            speakersList.addAll(getSpeakers());
        return speakersList;
    }

    public List<PersonListItemDTO> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<PersonListItemDTO> speakers) {
        this.speakers = speakers;
    }

    public Boolean isStarted() {
        return started;
    }

    public void setStarted(Boolean started) {
        this.started = started;
    }

    public Boolean getAllowFeedback() {
        return allowFeedback;
    }

    public void setAllowFeedback(Boolean allowFeedback) {
        this.allowFeedback = allowFeedback;
    }

    public PersonListItemDTO getModerator() {
        return moderator;
    }

    public void setModerator(PersonListItemDTO moderator) {
        this.moderator = moderator;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Double getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(Double averageRate) {
        this.averageRate = averageRate;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getSlug() {
        return Slugifier.toSlug(getName());
    }
}
