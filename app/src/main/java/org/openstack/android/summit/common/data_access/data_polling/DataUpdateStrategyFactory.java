package org.openstack.android.summit.common.data_access.data_polling;

/**
 * Created by Claudio Redi on 2/8/2016.
 */
public class DataUpdateStrategyFactory implements IDataUpdateStrategyFactory {

    IDataUpdateStrategy genericDataUpdateProcessStrategy;
    IDataUpdateStrategy myScheduleDataUpdateStrategy;
    IDataUpdateStrategy summitDataUpdateStrategy;
    IDataUpdateStrategy trackGroupDataUpdateStrategy;
    IDataUpdateStrategy venueImageDataUpdateStrategy;
    IDataUpdateStrategy presentationMaterialDataUpdateStrategy;
    IDataUpdateStrategy venueLocationsDataUpdateStrategy;
    IDataUpdateStrategy summitGroupEventDataUpdateStrategy;
    IDataUpdateStrategy myFavoriteDataUpdateStrategy;
    IDataUpdateStrategy wifiConnectionsDataUpdateStrategy;

    public DataUpdateStrategyFactory
    (
        IDataUpdateStrategy genericDataUpdateProcessStrategy,
        IDataUpdateStrategy myScheduleDataUpdateStrategy,
        IDataUpdateStrategy myFavoriteDataUpdateStrategy,
        IDataUpdateStrategy summitDataUpdateStrategy,
        IDataUpdateStrategy trackGroupDataUpdateStrategy,
        IDataUpdateStrategy venueImageDataUpdateStrategy,
        IDataUpdateStrategy presentationMaterialDataUpdateStrategy,
        IDataUpdateStrategy venueLocationsDataUpdateStrategy,
        IDataUpdateStrategy summitGroupEventDataUpdateStrategy,
        IDataUpdateStrategy wifiConnectionsDataUpdateStrategy
    )
    {
        this.genericDataUpdateProcessStrategy       = genericDataUpdateProcessStrategy;
        this.myScheduleDataUpdateStrategy           = myScheduleDataUpdateStrategy;
        this.summitDataUpdateStrategy               = summitDataUpdateStrategy;
        this.trackGroupDataUpdateStrategy           = trackGroupDataUpdateStrategy;
        this.venueImageDataUpdateStrategy           = venueImageDataUpdateStrategy;
        this.presentationMaterialDataUpdateStrategy = presentationMaterialDataUpdateStrategy;
        this.venueLocationsDataUpdateStrategy       = venueLocationsDataUpdateStrategy;
        this.summitGroupEventDataUpdateStrategy     = summitGroupEventDataUpdateStrategy;
        this.myFavoriteDataUpdateStrategy           = myFavoriteDataUpdateStrategy;
        this.wifiConnectionsDataUpdateStrategy      = wifiConnectionsDataUpdateStrategy;
    }

    @Override
    public IDataUpdateStrategy create(String className) {
        IDataUpdateStrategy dataUpdateProcessStrategy;

        switch (className) {
            case "MySchedule":
                dataUpdateProcessStrategy = myScheduleDataUpdateStrategy;
                break;
            case "MyFavorite":
                dataUpdateProcessStrategy = myFavoriteDataUpdateStrategy;
                break;
            case "Summit":
                dataUpdateProcessStrategy = summitDataUpdateStrategy;
                break;
            case "PresentationCategoryGroup":
                dataUpdateProcessStrategy = trackGroupDataUpdateStrategy;
                break;
            case "SummitLocationImage":
            case "SummitLocationMap":
                dataUpdateProcessStrategy = venueImageDataUpdateStrategy;
                break;
            case "PresentationVideo":
            case "PresentationLink":
            case "PresentationSlide":
                dataUpdateProcessStrategy = presentationMaterialDataUpdateStrategy;
                break;
            case "SummitGroupEvent":
                dataUpdateProcessStrategy = summitGroupEventDataUpdateStrategy;
                break;
            case "SummitVenueFloor":
            case "SummitVenueRoom":
                dataUpdateProcessStrategy = venueLocationsDataUpdateStrategy;
                break;
            case "SummitWIFIConnection":
                dataUpdateProcessStrategy = wifiConnectionsDataUpdateStrategy;
                break;
            default:
                dataUpdateProcessStrategy = genericDataUpdateProcessStrategy;
        }
        return dataUpdateProcessStrategy;
    }
}
