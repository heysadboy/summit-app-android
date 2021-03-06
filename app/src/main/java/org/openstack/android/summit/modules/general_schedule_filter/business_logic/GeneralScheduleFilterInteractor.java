package org.openstack.android.summit.modules.general_schedule_filter.business_logic;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.DTOs.NamedDTO;
import org.openstack.android.summit.common.DTOs.TrackGroupDTO;
import org.openstack.android.summit.common.api.ISummitSelector;
import org.openstack.android.summit.common.business_logic.BaseInteractor;
import org.openstack.android.summit.common.data_access.repositories.IEventTypeDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitEventDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitTypeDataStore;
import org.openstack.android.summit.common.data_access.repositories.ITagDataStore;
import org.openstack.android.summit.common.data_access.repositories.ITrackGroupDataStore;
import org.openstack.android.summit.common.data_access.repositories.IVenueDataStore;
import org.openstack.android.summit.common.entities.EventType;
import org.openstack.android.summit.common.entities.SummitType;
import org.openstack.android.summit.common.entities.Tag;
import org.openstack.android.summit.common.entities.TrackGroup;
import org.openstack.android.summit.common.entities.Venue;
import org.openstack.android.summit.common.security.ISecurityManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Sort;

/**
 * Created by Claudio Redi on 2/1/2016.
 */
public class GeneralScheduleFilterInteractor extends BaseInteractor implements IGeneralScheduleFilterInteractor {

    private ISummitTypeDataStore summitTypeDataStore;
    private ITagDataStore        tagDataStore;
    private IEventTypeDataStore  eventTypeDataStore;
    private ISummitEventDataStore summitEventDataStore;
    private IVenueDataStore venueDataStore;
    private ITrackGroupDataStore trackGroupDataStore;

    public GeneralScheduleFilterInteractor
    (
        ISecurityManager securityManager,
        ISummitDataStore summitDataStore,
        ISummitEventDataStore summitEventDataStore,
        IVenueDataStore venueDataStore,
        ITrackGroupDataStore trackGroupDataStore,
        ISummitTypeDataStore summitTypeDataStore,
        IEventTypeDataStore eventTypeDataStore,
        ITagDataStore tagDataStore,
        IDTOAssembler dtoAssembler,
        ISummitSelector summitSelector
    )
    {
        super(securityManager, dtoAssembler, summitSelector, summitDataStore);

        this.venueDataStore       = venueDataStore;
        this.summitEventDataStore = summitEventDataStore;
        this.summitTypeDataStore  = summitTypeDataStore;
        this.eventTypeDataStore   = eventTypeDataStore;
        this.tagDataStore         = tagDataStore;
        this.trackGroupDataStore  = trackGroupDataStore;
    }

    @Override
    public List<NamedDTO> getSummitTypes() {
        List<SummitType> summitTypes = summitTypeDataStore.getAll(new String[] { "name"}, new Sort[]{ Sort.ASCENDING });
        List<SummitType> results     =  new ArrayList<>();
        for(SummitType summitType: summitTypes){
            if ( summitEventDataStore.countBySummitType(summitType.getId()) > 0)
                results.add(summitType);
        }
        return createDTOList(results, NamedDTO.class);
    }

    @Override
    public List<NamedDTO> getEventTypes() {
        List<EventType> eventTypes = eventTypeDataStore.getAll(new String[] { "name"}, new Sort[]{ Sort.ASCENDING });
        List<EventType> results    = new ArrayList<>();
        for(EventType eventType: eventTypes){
            if ( summitEventDataStore.countByEventType(eventType.getId()) > 0)
                results.add(eventType);
        }
        return createDTOList(results, NamedDTO.class);
    }

    @Override
    public List<String> getLevels() {
        return summitEventDataStore.getPresentationLevels();
    }

    @Override
    public List<NamedDTO> getVenues() {
        List<Venue> venues = venueDataStore.getInternalsBySummit(summitSelector.getCurrentSummitId());
        return createDTOList(venues, NamedDTO.class);
    }

    @Override
    public List<TrackGroupDTO> getTrackGroups() {
        List<TrackGroup> trackGroups = trackGroupDataStore.getAllBySummit(summitSelector.getCurrentSummitId());
        List<TrackGroup> results = new ArrayList<>();
        for(TrackGroup trackGroup: trackGroups){
            if ( summitEventDataStore.countByTrackGroup(trackGroup.getId()) > 0)
                results.add(trackGroup);
        }
        return createDTOList(results, TrackGroupDTO.class);
    }

    @Override
    public List<String> getTags() {
        List<Tag> tags = tagDataStore.getAll();
        List<String> dtos = new ArrayList<>();
        for (Tag tag: tags) {
            dtos.add(tag.getTag());
        }
        return dtos;
    }

}