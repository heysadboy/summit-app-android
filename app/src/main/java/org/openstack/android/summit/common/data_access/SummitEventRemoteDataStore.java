package org.openstack.android.summit.common.data_access;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.data_access.deserialization.IDeserializer;
import org.openstack.android.summit.common.entities.Feedback;
import org.openstack.android.summit.common.entities.Summit;
import org.openstack.android.summit.common.network.HttpTask;
import org.openstack.android.summit.common.network.HttpTaskListener;
import org.openstack.android.summit.common.network.IHttpTaskFactory;
import org.openstack.android.summit.common.security.AccountType;

import java.util.List;

/**
 * Created by Claudio Redi on 2/19/2016.
 */
public class SummitEventRemoteDataStore implements ISummitEventRemoteDataStore {
    private IDeserializer deserializer;
    private IHttpTaskFactory httpTaskFactory;

    public SummitEventRemoteDataStore(IHttpTaskFactory httpTaskFactory, IDeserializer deserializer) {
        this.httpTaskFactory = httpTaskFactory;
        this.deserializer = deserializer;
    }


    @Override
    public void getFeedback(int eventId, int page, int objectsPerPage, final IDataStoreOperationListener<Feedback> dataStoreOperationListener) {
        try {
            HttpTaskListener httpTaskListener = new HttpTaskListener() {
                @Override
                public void onSucceed(String data) {
                    try {
                        List<Feedback> feedbackList = deserializer.deserializePage(data, Feedback.class);
                        dataStoreOperationListener.onSucceedWithDataCollection(feedbackList);
                    } catch (JSONException e) {
                        Crashlytics.logException(e);
                        Log.e(Constants.LOG_TAG, "Error deserializing feedback", e);
                        dataStoreOperationListener.onError(e.getMessage());
                    }
                }

                @Override
                public void onError(String error) {
                    dataStoreOperationListener.onError(error);
                }
            };
            String url = Constants.RESOURCE_SERVER_BASE_URL +
                    String.format("/api/v1/summits/current/events/%d/feedback?expand=owner&page=%d&per_page=%d", eventId, page, objectsPerPage);
            HttpTask httpTask = httpTaskFactory.create(AccountType.ServiceAccount, url, "GET", null, null, httpTaskListener);
            httpTask.execute();
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.e(Constants.LOG_TAG, e.getMessage(), e);
            dataStoreOperationListener.onError(e.getMessage());
        }
    }
}