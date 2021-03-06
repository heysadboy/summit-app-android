package org.openstack.android.summit.common.data_access.repositories;

import org.joda.time.DateTime;
import org.openstack.android.summit.common.entities.Feedback;
import org.openstack.android.summit.common.entities.SummitEvent;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Claudio Redi on 12/21/2015.
 */
public interface ISummitEventDataStore extends IGenericDataStore<SummitEvent> {

    List<SummitEvent> getByFilter(DateTime startDate, DateTime endDate, List<Integer> eventTypes, List<Integer> summitTypes, List<Integer> trackGroups, List<Integer> tracks, List<String> tags, List<String> levels, List<Integer> venues);

    List<String> getPresentationLevels();

    List<SummitEvent> getBySearchTerm(int summitId, String searchTerm);

    List<SummitEvent> getSpeakerEvents(int speakerId, DateTime startDate, DateTime endDate);

    Observable<List<Feedback>> getFeedback(int eventId, int page, int objectsPerPage);

    Observable<Double> getAverageFeedback(int eventId);

    long countByTrackGroup(int trackGroupId);

    long countByTrack(int trackId);

    long countByEventType(int eventTypeId);

    long countBySummitType(int sumitTypeId);

    long countByLevel(String level);
}
