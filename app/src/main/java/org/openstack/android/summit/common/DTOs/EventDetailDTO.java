package org.openstack.android.summit.common.DTOs;

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
    private int headCount;
    private String eventDescription;
    private String tags;
    private List<PersonListItemDTO> speakers = new ArrayList<PersonListItemDTO>();
    private PersonListItemDTO moderator;
    private String level;
    private Double averageRate;
    private String attachmentUrl;

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

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

    public boolean getAllowRsvp() {
        return this.rsvpLink != null;
    }

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

}
