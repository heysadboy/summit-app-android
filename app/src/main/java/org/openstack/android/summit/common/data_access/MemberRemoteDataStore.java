package org.openstack.android.summit.common.data_access;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONException;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.data_access.deserialization.IDeserializer;
import org.openstack.android.summit.common.entities.Member;
import org.openstack.android.summit.common.entities.Summit;
import org.openstack.android.summit.common.network.HttpTask;
import org.openstack.android.summit.common.network.HttpTaskFactory;
import org.openstack.android.summit.common.network.HttpTaskListener;
import org.openstack.android.summit.common.network.IHttpTaskFactory;
import org.openstack.android.summit.common.security.AccountType;

import java.security.spec.InvalidParameterSpecException;

import javax.inject.Inject;

/**
 * Created by Claudio Redi on 12/16/2015.
 */
public class MemberRemoteDataStore implements IMemberRemoteDataStore {
    private IDeserializer deserializer;
    private IHttpTaskFactory httpTaskFactory;

    @Inject
    public MemberRemoteDataStore(IHttpTaskFactory httpTaskFactory, IDeserializer deserializer) {
        this.httpTaskFactory = httpTaskFactory;
        this.deserializer = deserializer;
    }

    @Override
    public void getLoggedInMember(IDataStoreOperationListener<Member> dataStoreOperationListener) {
        final IDataStoreOperationListener<Member> finalDataStoreOperationListener = dataStoreOperationListener;

        HttpTaskListener httpTaskListener = new HttpTaskListener() {
            @Override
            public void onSucceed(String data) {
                try {
                    Member member = deserializer.deserialize(data, Member.class);
                    finalDataStoreOperationListener.onSuceedWithData(member);
                } catch (JSONException e) {
                    Log.e(Constants.LOG_TAG, "Error deserializing member", e);
                    finalDataStoreOperationListener.onError(e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                finalDataStoreOperationListener.onError(error);
            }
        };

        String url = Constants.RESOURCE_SERVER_BASE_URL + "/api/v1/summits/current/attendees/me?expand=speaker,feedback";
        HttpTask httpTask = null;
        try {
            httpTask = httpTaskFactory.Create(AccountType.OIDC, url, HttpRequest.METHOD_GET, httpTaskListener);
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }
        httpTask.execute();
    }
}
