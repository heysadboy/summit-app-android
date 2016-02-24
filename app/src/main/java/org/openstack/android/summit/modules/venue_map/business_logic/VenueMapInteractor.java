package org.openstack.android.summit.modules.venue_map.business_logic;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.DTOs.VenueDTO;
import org.openstack.android.summit.common.DTOs.VenueListItemDTO;
import org.openstack.android.summit.common.business_logic.BaseInteractor;
import org.openstack.android.summit.common.data_access.IGenericDataStore;
import org.openstack.android.summit.common.data_access.data_polling.IDataUpdatePoller;
import org.openstack.android.summit.common.entities.Venue;

/**
 * Created by Claudio Redi on 2/11/2016.
 */
public class VenueMapInteractor extends BaseInteractor implements IVenueMapInteractor {
    private IGenericDataStore genericDataStore;

    public VenueMapInteractor(IGenericDataStore genericDataStore, IDTOAssembler dtoAssembler, IDataUpdatePoller dataUpdatePoller) {
        super(dtoAssembler, dataUpdatePoller);
        this.genericDataStore = genericDataStore;
    }

    @Override
    public VenueListItemDTO getVenue(int venueId) {
        Venue venue = genericDataStore.getByIdLocal(venueId, Venue.class);
        VenueDTO dto = dtoAssembler.createDTO(venue, VenueDTO.class);
        return dto;
    }
}
