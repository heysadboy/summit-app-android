package org.openstack.android.summit.common.data_access.deserialization;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openstack.android.summit.common.entities.Company;
import org.openstack.android.summit.common.entities.EventType;
import org.openstack.android.summit.common.entities.ISummitEventType;
import org.openstack.android.summit.common.entities.Presentation;
import org.openstack.android.summit.common.entities.Summit;
import org.openstack.android.summit.common.entities.SummitEvent;
import org.openstack.android.summit.common.entities.SummitEventWithFile;
import org.openstack.android.summit.common.entities.SummitGroupEvent;
import org.openstack.android.summit.common.entities.Tag;
import org.openstack.android.summit.common.entities.Track;
import org.openstack.android.summit.common.entities.Venue;
import org.openstack.android.summit.common.entities.VenueRoom;
import org.openstack.android.summit.common.utils.RealmFactory;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created by Claudio Redi on 11/13/2015.
 */
public class SummitEventDeserializer extends BaseDeserializer implements ISummitEventDeserializer {

    IGenericDeserializer genericDeserializer;
    IPresentationDeserializer presentationDeserializer;
    IGroupEventDeserializer  groupEventDeserializer;
    ISummitEventWithFileDeserializer summitEventWithFileDeserializer;

    @Inject
    public SummitEventDeserializer
    (
        IGenericDeserializer genericDeserializer,
        IPresentationDeserializer presentationDeserializer,
        IGroupEventDeserializer  groupEventDeserializer,
        ISummitEventWithFileDeserializer summitEventWithFileDeserializer
    )
    {
        this.genericDeserializer             = genericDeserializer;
        this.presentationDeserializer        = presentationDeserializer;
        this.groupEventDeserializer          = groupEventDeserializer;
        this.summitEventWithFileDeserializer = summitEventWithFileDeserializer;
    }

    @Override
    public SummitEvent deserialize(String jsonString) throws JSONException {JSONObject jsonObject = new JSONObject(jsonString);

        String[] missedFields = validateRequiredFields(new String[] {"id","summit_id"},  jsonObject);
        handleMissedFieldsIfAny(missedFields);

        int eventId             = jsonObject.optInt("id");
        int summitId            = jsonObject.optInt("summit_id");
        SummitEvent summitEvent = RealmFactory.getSession().where(SummitEvent.class).equalTo("id", eventId).findFirst();

        if(summitEvent == null)
            summitEvent = RealmFactory.getSession().createObject(SummitEvent.class, eventId);

        summitEvent.setName(jsonObject.optString("title"));
        summitEvent.setAllowFeedback(jsonObject.optBoolean("allow_feedback"));
        summitEvent.setStart(new Date(jsonObject.optLong("start_date") * 1000L));
        summitEvent.setEnd(new Date(jsonObject.optLong("end_date") * 1000L));
        summitEvent.setDescription(!jsonObject.isNull("description") ? jsonObject.getString("description") : "");
        summitEvent.setSocialDescription(!jsonObject.isNull("social_description") ? jsonObject.getString("social_description") : "");
        summitEvent.setAverageRate(!jsonObject.isNull("avg_feedback_rate") ? jsonObject.getDouble("avg_feedback_rate") : 0);
        summitEvent.setRsvpLink(!jsonObject.isNull("rsvp_link") ? jsonObject.getString("rsvp_link") : null);
        summitEvent.setRsvpExternal(!jsonObject.isNull("rsvp_external") ? jsonObject.getBoolean("rsvp_external") : false);
        summitEvent.setHeadCount(!jsonObject.isNull("head_count") ? jsonObject.getInt("head_count") : 0);

        //first check db, and then cache storage
        Summit summit  =  RealmFactory.getSession().where(Summit.class).equalTo("id", summitId).findFirst();

        if(summit == null)
            throw new JSONException(String.format("Can't deserialize event id %d (summit not found)!", eventId));

        summitEvent.setSummit(summit);
        summitEvent.setClassName(ISummitEventType.Type.SummitEvent.toString());

        if (jsonObject.has("type_id")) {
            EventType eventType = RealmFactory.getSession().where(EventType.class).equalTo("id", jsonObject.getInt("type_id")).findFirst();
            summitEvent.setType(eventType);
        }

        if (jsonObject.has("sponsors")) {
            Company company;
            int sponsorId;
            JSONArray jsonArraySponsors = jsonObject.getJSONArray("sponsors");
            summitEvent.getSponsors().clear();
            for (int i = 0; i < jsonArraySponsors.length(); i++) {
                sponsorId = jsonArraySponsors.optInt(i);
                company = (sponsorId > 0) ?
                        RealmFactory.getSession().where(Company.class).equalTo("id", sponsorId).findFirst():
                        genericDeserializer.deserialize(jsonArraySponsors.getJSONObject(i).toString(), Company.class);
                if(company == null) continue;
                summitEvent.getSponsors().add(company);
                summit.getSponsors().add(company);
            }
        }

        if (jsonObject.has("location_id") && !jsonObject.isNull("location_id")) {
            int locationId = jsonObject.getInt("location_id");
            Venue venue    = RealmFactory.getSession().where(Venue.class).equalTo("id", locationId).findFirst();
            if (venue != null){
                summitEvent.setVenue(venue);
                venue.setSummit(summit);
            }
            else {
                VenueRoom venueRoom = RealmFactory.getSession().where(VenueRoom.class).equalTo("id", locationId).findFirst();
                if(venueRoom != null)
                    summitEvent.setVenueRoom(venueRoom);
            }
        }

        int trackId = jsonObject.has("track_id") ? jsonObject.getInt("track_id") : 0;

        if(trackId > 0) {
            Track track = RealmFactory.getSession().where(Track.class).equalTo("id", trackId).findFirst();
            if (track != null) {
                summitEvent.setTrack(track);
                track.setSummit(summit);
            }
        }

        if (jsonObject.has("class_name") &&
                !jsonObject.isNull("class_name") &&
                jsonObject.getString("class_name").startsWith("Presentation")) {
            Presentation presentation = presentationDeserializer.deserialize(jsonString);
            summitEvent.setPresentation(presentation);
            presentation.setEvent(summitEvent);
            presentation.setClassName(ISummitEventType.Type.Presentation.toString());
        }

        if (jsonObject.has("class_name") &&
                !jsonObject.isNull("class_name") &&
                jsonObject.getString("class_name").startsWith("SummitGroupEvent")) {
            SummitGroupEvent groupEvent = groupEventDeserializer.deserialize(jsonString);
            summitEvent.setGroupEvent(groupEvent);
            groupEvent.setEvent(summitEvent);
            groupEvent.setClassName(ISummitEventType.Type.SummitGroupEvent.toString());
        }

        if (jsonObject.has("class_name") &&
                !jsonObject.isNull("class_name") &&
                jsonObject.getString("class_name").startsWith("SummitEventWithFile")) {
            SummitEventWithFile eventWithFile = summitEventWithFileDeserializer.deserialize(jsonString);
            summitEvent.setSummitEventWithFile(eventWithFile);
            eventWithFile.setEvent(summitEvent);
            eventWithFile.setClassName(ISummitEventType.Type.SummitEventWithFile.toString());
        }

        if (jsonObject.has("tags")) {
            Tag tag;
            JSONObject jsonObjectTag;
            JSONArray jsonArrayTags = jsonObject.getJSONArray("tags");
            summitEvent.getTags().clear();
            for (int i = 0; i < jsonArrayTags.length(); i++) {
                jsonObjectTag = jsonArrayTags.getJSONObject(i);
                tag = genericDeserializer.deserialize(jsonObjectTag.toString(), Tag.class);
                if(tag == null) continue;
                summitEvent.getTags().add(tag);
            }
        }

        return summitEvent;
    }
}