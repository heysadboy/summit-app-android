package org.openstack.android.summit.common.data_access.repositories.impl;

import org.openstack.android.summit.common.data_access.repositories.IPushNotificationDataStore;
import org.openstack.android.summit.common.data_access.repositories.strategies.IDeleteStrategy;
import org.openstack.android.summit.common.data_access.repositories.strategies.ISaveOrUpdateStrategy;
import org.openstack.android.summit.common.entities.Member;
import org.openstack.android.summit.common.entities.notifications.IPushNotification;
import org.openstack.android.summit.common.entities.notifications.PushNotification;
import org.openstack.android.summit.common.utils.RealmFactory;
import java.util.ArrayList;
import java.util.List;
import io.realm.Case;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sebastian on 8/20/2016.
 */
public class PushNotificationDataStore extends GenericDataStore<PushNotification> implements IPushNotificationDataStore {

    public PushNotificationDataStore(ISaveOrUpdateStrategy saveOrUpdateStrategy, IDeleteStrategy deleteStrategy) {
        super(PushNotification.class, saveOrUpdateStrategy, deleteStrategy);
    }

    @Override
    public long getNotOpenedCountBy(Member member) {
        RealmQuery<PushNotification> query = RealmFactory.getSession().where(PushNotification.class).equalTo("opened", false);
        if(member == null){
            query = query.isNull("owner");
        }
        else{
            query
                    .beginGroup()
                    .equalTo("owner.id", member.getId())
                    .or()
                    .isNull("owner")
                    .endGroup();
        }
        return query.count();
    }

    @Override
    public List<IPushNotification> getByFilter(String searchTerm, Member member, int page, int objectsPerPage) {
        RealmQuery<PushNotification> query = RealmFactory.getSession().where(PushNotification.class);

        if(member == null){
            query = query.isNull("owner");
        }
        else{
            query
                    .beginGroup()
                        .equalTo("owner.id", member.getId())
                        .or()
                        .isNull("owner")
                    .endGroup();
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            query
                    .beginGroup()
                        .contains("title", searchTerm, Case.INSENSITIVE)
                        .or()
                        .contains("body", searchTerm, Case.INSENSITIVE)
                    .endGroup();
        }

        RealmResults<PushNotification> results = query.findAll().sort("created_at", Sort.DESCENDING);

        ArrayList<IPushNotification> notifications = new ArrayList<>();
        int startRecord                            = (page-1) * objectsPerPage;
        int endRecord                              = (startRecord + (objectsPerPage - 1)) <= results.size()
                ? startRecord + (objectsPerPage - 1)
                : results.size() - 1;

        int size = results.size();
        if (startRecord <= endRecord) {
            int index = startRecord;
            while (index  <= endRecord && index < size) {
                notifications.add(results.get(index));
                index++;
            }
        }

        return notifications;
    }
}
