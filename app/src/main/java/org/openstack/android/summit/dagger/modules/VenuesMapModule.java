package org.openstack.android.summit.dagger.modules;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.INavigationParametersStore;
import org.openstack.android.summit.common.data_access.IGenericDataStore;
import org.openstack.android.summit.common.data_access.data_polling.IDataUpdatePoller;
import org.openstack.android.summit.modules.venue_detail.IVenueDetailWireframe;
import org.openstack.android.summit.modules.venues_map.IVenuesMapWireframe;
import org.openstack.android.summit.modules.venues_map.VenuesMapWireframe;
import org.openstack.android.summit.modules.venues_map.business_logic.IVenuesMapInteractor;
import org.openstack.android.summit.modules.venues_map.business_logic.VenuesMapInteractor;
import org.openstack.android.summit.modules.venues_map.user_interface.IVenuesMapPresenter;
import org.openstack.android.summit.modules.venues_map.user_interface.VenuesMapFragment;
import org.openstack.android.summit.modules.venues_map.user_interface.VenuesMapPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Claudio Redi on 2/11/2016.
 */
@Module
public class VenuesMapModule {
    @Provides
    VenuesMapFragment providesVenuesMapFragment() {
        return new VenuesMapFragment();
    }

    @Provides
    IVenuesMapWireframe providesVenuesMapWireframe(IVenueDetailWireframe venueDetailWireframe, INavigationParametersStore navigationParametersStore) {
        return new VenuesMapWireframe(venueDetailWireframe, navigationParametersStore);
    }

    @Provides
    IVenuesMapInteractor providesVenuesMapInteractor(IGenericDataStore genericDataStore, IDTOAssembler dtoAssembler, IDataUpdatePoller dataUpdatePoller) {
        return new VenuesMapInteractor(genericDataStore, dtoAssembler, dataUpdatePoller);
    }

    @Provides
    IVenuesMapPresenter providesVenuesMapPresenter(IVenuesMapInteractor interactor, IVenuesMapWireframe wireframe) {
        return new VenuesMapPresenter(interactor, wireframe);
    }
} 