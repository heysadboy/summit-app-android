package org.openstack.android.summit.common.data_access.data_polling;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.api.ISummitSelector;
import org.openstack.android.summit.common.data_access.repositories.IPresentationDataStore;
import org.openstack.android.summit.common.data_access.repositories.IPresentationLinkDataStore;
import org.openstack.android.summit.common.data_access.repositories.IPresentationSlideDataStore;
import org.openstack.android.summit.common.data_access.repositories.IPresentationVideoDataStore;
import org.openstack.android.summit.common.entities.DataUpdate;
import org.openstack.android.summit.common.entities.Presentation;
import org.openstack.android.summit.common.entities.PresentationLink;
import org.openstack.android.summit.common.entities.PresentationSlide;
import org.openstack.android.summit.common.entities.PresentationVideo;
import org.openstack.android.summit.common.utils.RealmFactory;
import org.openstack.android.summit.common.utils.Void;

import io.realm.Realm;

/**
 * Created by sebastian on 8/10/2016.
 */
public class PresentationMaterialDataUpdateStrategy extends DataUpdateStrategy  {

    private IPresentationDataStore presentationDataStore;
    private IPresentationSlideDataStore presentationSlideDataStore;
    private IPresentationVideoDataStore presentationVideoDataStore;
    private IPresentationLinkDataStore presentationLinkDataStore;

    public PresentationMaterialDataUpdateStrategy
    (
        IPresentationDataStore presentationDataStore,
        IPresentationSlideDataStore presentationSlideDataStore,
        IPresentationVideoDataStore presentationVideoDataStore,
        IPresentationLinkDataStore presentationLinkDataStore,
        ISummitSelector summitSelector
    ) {
        super(summitSelector);

        this.presentationDataStore      = presentationDataStore;
        this.presentationSlideDataStore = presentationSlideDataStore;
        this.presentationVideoDataStore = presentationVideoDataStore;
        this.presentationLinkDataStore  = presentationLinkDataStore;
    }

    @Override
    public void process(final DataUpdate dataUpdate) throws DataUpdateException {
        final String className      = dataUpdate.getEntityClassName();
        switch (dataUpdate.getOperation()) {
            case DataOperation.Insert:
                final JSONObject entityJSON = dataUpdate.getOriginalJSON().optJSONObject("entity");
                if(entityJSON == null) return;
                final Integer presentation_id = entityJSON.optInt("presentation_id");

                if (presentation_id == null)
                    throw new DataUpdateException("It wasn't possible to find presentation_id on data update json");

                try {

                    RealmFactory.transaction(new RealmFactory.IRealmCallback<Void>() {
                        @Override
                        public Void callback(Realm session) throws Exception {
                            Presentation managedPresentation = presentationDataStore.getById(presentation_id);
                            if (managedPresentation == null)
                                throw new DataUpdateException(String.format("Presentation with id %d not found", presentation_id));

                            if (className.equals("PresentationSlide")) {
                                PresentationSlide slide = (PresentationSlide) dataUpdate.getEntity();
                                slide.setPresentation(managedPresentation);
                                managedPresentation.getSlides().add(slide);
                            }
                            if (className.equals("PresentationVideo")) {
                                PresentationVideo video = (PresentationVideo) dataUpdate.getEntity();
                                video.setYouTubeId(entityJSON.getString("youtube_id"));
                                video.setPresentation(managedPresentation);
                                managedPresentation.getVideos().add(video);
                            }
                            if (className.equals("PresentationLink")) {
                                PresentationLink link = (PresentationLink) dataUpdate.getEntity();
                                link.setPresentation(managedPresentation);
                                managedPresentation.getLinks().add(link);
                            }
                            return Void.getInstance();
                        }
                    });
                }
                catch (Exception ex){
                    Crashlytics.logException(ex);
                    Log.e(Constants.LOG_TAG, ex.getMessage(), ex);
                }
                break;
            case DataOperation.Update:
                try {
                    final JSONObject entityJSONUpdate = dataUpdate.getOriginalJSON().optJSONObject("entity");
                    if (entityJSONUpdate == null) return;

                    if (className.equals("PresentationSlide")) {
                        presentationSlideDataStore.saveOrUpdate((PresentationSlide) dataUpdate.getEntity());
                    }
                    if (className.equals("PresentationVideo")) {
                        PresentationVideo video = (PresentationVideo) dataUpdate.getEntity();
                        video.setYouTubeId(entityJSONUpdate.getString("youtube_id"));
                        presentationVideoDataStore.saveOrUpdate(video);
                    }
                    if (className.equals("PresentationLink")) {
                        presentationLinkDataStore.saveOrUpdate((PresentationLink) dataUpdate.getEntity());
                    }
                }
                catch (Exception ex){
                    Crashlytics.logException(ex);
                    Log.e(Constants.LOG_TAG, ex.getMessage(), ex);
                }
                break;
            case DataOperation.Delete:
                if(className.equals("PresentationSlide")){
                    presentationSlideDataStore.delete(((PresentationSlide)dataUpdate.getEntity()).getId());
                }
                if(className.equals("PresentationVideo")){
                    presentationVideoDataStore.delete(((PresentationVideo)dataUpdate.getEntity()).getId());
                }
                if(className.equals("PresentationLink")){
                    presentationLinkDataStore.delete(((PresentationLink)dataUpdate.getEntity()).getId());
                }
                break;
        }
    }
}

