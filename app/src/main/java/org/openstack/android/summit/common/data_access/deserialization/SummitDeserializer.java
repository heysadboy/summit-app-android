package org.openstack.android.summit.common.data_access.deserialization;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openstack.android.summit.common.entities.Company;
import org.openstack.android.summit.common.entities.EventType;
import org.openstack.android.summit.common.entities.PresentationSpeaker;
import org.openstack.android.summit.common.entities.Summit;
import org.openstack.android.summit.common.entities.SummitEvent;
import org.openstack.android.summit.common.entities.SummitWIFIConnection;
import org.openstack.android.summit.common.entities.TicketType;
import org.openstack.android.summit.common.entities.Track;
import org.openstack.android.summit.common.entities.TrackGroup;
import org.openstack.android.summit.common.entities.Venue;
import org.openstack.android.summit.common.entities.VenueFloor;
import org.openstack.android.summit.common.entities.VenueRoom;
import org.openstack.android.summit.common.utils.RealmFactory;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created by Claudio Redi on 11/15/2015.
 */
public class SummitDeserializer extends BaseDeserializer implements ISummitDeserializer {

    IGenericDeserializer genericDeserializer;
    IVenueDeserializer venueDeserializer;
    IVenueRoomDeserializer venueRoomDeserializer;
    ISummitEventDeserializer summitEventDeserializer;
    IPresentationSpeakerDeserializer presentationSpeakerDeserializer;
    ITrackGroupDeserializer trackGroupDeserializer;
    ITrackDeserializer trackDeserializer;
    IWifiConnectionDeserializer wifiConnectionDeserializer;
    SummitDeserializerMode mode;

    @Inject
    public SummitDeserializer
            (
                    IGenericDeserializer genericDeserializer,
                    IVenueDeserializer venueDeserializer,
                    IVenueRoomDeserializer venueRoomDeserializer,
                    ISummitEventDeserializer summitEventDeserializer,
                    IPresentationSpeakerDeserializer presentationSpeakerDeserializer,
                    ITrackGroupDeserializer trackGroupDeserializer,
                    ITrackDeserializer trackDeserializer,
                    IWifiConnectionDeserializer wifiConnectionDeserializer
            ) {

        this.genericDeserializer             = genericDeserializer;
        this.venueDeserializer               = venueDeserializer;
        this.venueRoomDeserializer           = venueRoomDeserializer;
        this.summitEventDeserializer         = summitEventDeserializer;
        this.presentationSpeakerDeserializer = presentationSpeakerDeserializer;
        this.trackGroupDeserializer          = trackGroupDeserializer;
        this.trackDeserializer               = trackDeserializer;
        this.wifiConnectionDeserializer      = wifiConnectionDeserializer;
        this.mode                            = SummitDeserializerMode.Header;
    }

    @Override
    public void setMode(SummitDeserializerMode mode) {
        this.mode = mode;
    }

    @Override
    public Summit deserialize(String jsonString) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);

        String[] missedFields = validateRequiredFields(new String[]
                {
                        "id",
                        "name",
                        "start_date",
                        "end_date",
                        "time_zone",
                }, jsonObject);

        if (missedFields.length > 0) {
            throw new JSONException("Following fields are missed " + TextUtils.join(",", missedFields));
        }

        int summitId  = jsonObject.getInt("id");
        Summit summit = RealmFactory.getSession().where(Summit.class).equalTo("id", summitId).findFirst();

        if (summit == null) {
            summit = RealmFactory.getSession().createObject(Summit.class, summitId);
        }
        // basic summit data
        summit.setName(jsonObject.getString("name"));
        summit.setStartDate(new Date(jsonObject.getInt("start_date") * 1000L));
        summit.setEndDate(new Date(jsonObject.getInt("end_date") * 1000L));
        summit.setScheduleStartDate(!jsonObject.isNull("schedule_start_date")
                ?
                new Date(jsonObject.getInt("schedule_start_date") * 1000L):
                summit.getStartDate()
        );
        summit.setTimeZone(jsonObject.getJSONObject("time_zone").getString("name"));

        if(!jsonObject.isNull("page_url"))
            summit.setPageUrl(jsonObject.getString("page_url"));

        if(!jsonObject.isNull("schedule_page_url"))
            summit.setSchedulePageUrl(jsonObject.getString("schedule_page_url"));

        if(!jsonObject.isNull("schedule_event_detail_url"))
            summit.setScheduleEventDetailUrl(jsonObject.getString("schedule_event_detail_url"));

        if(!jsonObject.isNull("start_showing_venues_date"))
            summit.setStartShowingVenuesDate(new Date(jsonObject.getInt("start_showing_venues_date") * 1000L));

        if(!jsonObject.isNull("dates_label"))
            summit.setDatesLabel(jsonObject.getString("dates_label"));

        if(this.mode == SummitDeserializerMode.Complete) {
            // if its a new summit , then deserialize the entire hierarchy ...
            if (jsonObject.has("sponsors")) {
                summit.getSponsors().clear();
                JSONObject jsonObjectSponsor;
                JSONArray jsonArraySponsors = jsonObject.getJSONArray("sponsors");
                for (int i = 0; i < jsonArraySponsors.length(); i++) {
                    jsonObjectSponsor = jsonArraySponsors.getJSONObject(i);
                    Company sponsor = genericDeserializer.deserialize(jsonObjectSponsor.toString(), Company.class);
                    summit.getSponsors().add(sponsor);
                }
            }

            if (jsonObject.has("ticket_types")) {
                summit.getTicketTypes().clear();
                TicketType ticketType;
                JSONObject jsonObjectTicketType;
                JSONArray jsonArrayTicketTypes = jsonObject.getJSONArray("ticket_types");
                for (int i = 0; i < jsonArrayTicketTypes.length(); i++) {
                    jsonObjectTicketType = jsonArrayTicketTypes.getJSONObject(i);
                    ticketType = genericDeserializer.deserialize(jsonObjectTicketType.toString(), TicketType.class);
                    summit.getTicketTypes().add(ticketType);
                    ticketType.setSummit(summit);
                }
            }

            if (jsonObject.has("event_types")) {
                summit.getEventTypes().clear();
                EventType eventType;
                JSONObject jsonObjectEventType;
                JSONArray jsonArrayEventTypes = jsonObject.getJSONArray("event_types");
                for (int i = 0; i < jsonArrayEventTypes.length(); i++) {
                    jsonObjectEventType = jsonArrayEventTypes.getJSONObject(i);
                    eventType = genericDeserializer.deserialize(jsonObjectEventType.toString(), EventType.class);
                    summit.getEventTypes().add(eventType);
                    eventType.setSummit(summit);
                }
            }

            if (jsonObject.has("tracks")) {
                summit.getTracks().clear();
                ;
                Track track;
                JSONObject jsonObjectTrack;
                trackDeserializer.setShouldDeserializeTrackGroups(false);
                JSONArray jsonArrayTracks = jsonObject.getJSONArray("tracks");
                for (int i = 0; i < jsonArrayTracks.length(); i++) {
                    jsonObjectTrack = jsonArrayTracks.getJSONObject(i);
                    track = trackDeserializer.deserialize(jsonObjectTrack.toString());
                    summit.getTracks().add(track);
                    track.setSummit(summit);
                }
            }

            if (jsonObject.has("track_groups")) {
                summit.getTrackGroups().clear();
                TrackGroup trackGroup;
                JSONObject jsonObjectTrackGroup;
                JSONArray jsonArrayTrackGroups = jsonObject.getJSONArray("track_groups");
                for (int i = 0; i < jsonArrayTrackGroups.length(); i++) {
                    jsonObjectTrackGroup = jsonArrayTrackGroups.getJSONObject(i);
                    trackGroup = trackGroupDeserializer.deserialize(jsonObjectTrackGroup.toString());
                    summit.getTrackGroups().add(trackGroup);
                    trackGroup.setSummit(summit);
                }
            }

            if (jsonObject.has("locations")) {
                Venue venue;
                JSONObject jsonObjectVenue;
                JSONArray jsonArrayVenues = jsonObject.getJSONArray("locations");

                for (int i = 0; i < jsonArrayVenues.length(); i++) {
                    jsonObjectVenue = jsonArrayVenues.getJSONObject(i);
                    if (isVenue(jsonObjectVenue)) {
                        venue = venueDeserializer.deserialize(jsonObjectVenue.toString());
                        summit.getVenues().add(venue);
                        venue.setSummit(summit);
                    }
                }

                VenueRoom venueRoom;
                JSONObject jsonObjectVenueRoom;
                JSONArray jsonArrayVenueRooms = jsonObject.getJSONArray("locations");

                for (int i = 0; i < jsonArrayVenueRooms.length(); i++) {
                    jsonObjectVenueRoom = jsonArrayVenueRooms.getJSONObject(i);
                    if (isVenueRoom(jsonObjectVenueRoom)) {

                        Integer floorId = jsonObjectVenueRoom.optInt("floor_id");
                        venueRoom = venueRoomDeserializer.deserialize(jsonObjectVenueRoom.toString());
                        summit.getVenueRooms().add(venueRoom);

                        if (floorId != null) {
                            VenueFloor venueFloor = RealmFactory.getSession().where(VenueFloor.class).equalTo("id", floorId).findFirst();
                            if (venueFloor != null)
                                venueFloor.getRooms().add(venueRoom);
                        }
                    }
                }
            }

            if (jsonObject.has("speakers")) {
                PresentationSpeaker presentationSpeaker = null;
                summit.getSpeakers().clear();
                JSONObject jsonObjectPresentationSpeaker;
                JSONArray jsonArrayPresentationSpeakers = jsonObject.getJSONArray("speakers");
                for (int i = 0; i < jsonArrayPresentationSpeakers.length(); i++) {
                    jsonObjectPresentationSpeaker = jsonArrayPresentationSpeakers.getJSONObject(i);
                    presentationSpeaker = presentationSpeakerDeserializer.deserialize(jsonObjectPresentationSpeaker.toString());
                    summit.getSpeakers().add(presentationSpeaker);
                }
            }

            if (jsonObject.has("schedule") && !summit.isScheduleLoaded()) {
                summit.setInitialDataLoadDate(jsonObject.has("timestamp") ? new Date(jsonObject.getInt("timestamp") * 1000L) : null);
                summit.getEvents().clear();
                SummitEvent summitEvent;
                JSONObject jsonObjectSummitEvent;
                JSONArray jsonArraySummitEvents = jsonObject.getJSONArray("schedule");
                for (int i = 0; i < jsonArraySummitEvents.length(); i++) {
                    jsonObjectSummitEvent = jsonArraySummitEvents.getJSONObject(i);
                    summitEvent = summitEventDeserializer.deserialize(jsonObjectSummitEvent.toString());
                    summit.getEvents().add(summitEvent);
                }
                summit.setScheduleLoaded(true);
            }

            if (jsonObject.has("wifi_connections")) {
                SummitWIFIConnection wifiConnection;
                JSONObject jsonObjectWifiConnection;
                summit.getWifiConnections().clear();
                JSONArray jsonArrayWifiConnections = jsonObject.getJSONArray("wifi_connections");
                for (int i = 0; i < jsonArrayWifiConnections.length(); i++) {
                    jsonObjectWifiConnection = jsonArrayWifiConnections.getJSONObject(i);
                    wifiConnection = wifiConnectionDeserializer.deserialize(jsonObjectWifiConnection.toString());
                    summit.getWifiConnections().add(wifiConnection);
                }
            }
        }
        return summit;
    }

    private boolean isVenue(JSONObject jsonObjectVenue) throws JSONException {
        return jsonObjectVenue.getString("class_name").equals("SummitVenue") || jsonObjectVenue.getString("class_name").equals("SummitExternalLocation");
    }

    private boolean isVenueRoom(JSONObject jsonObjectVenueRoom) throws JSONException {
        return jsonObjectVenueRoom.getString("class_name").equals("SummitVenueRoom");
    }
}
