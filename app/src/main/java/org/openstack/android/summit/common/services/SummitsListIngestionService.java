package org.openstack.android.summit.common.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openstack.android.summit.OpenStackSummitApplication;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.api.ISummitApi;
import org.openstack.android.summit.common.api.ISummitSelector;
import org.openstack.android.summit.common.data_access.ISummitDataStore;
import org.openstack.android.summit.common.data_access.deserialization.ISummitDeserializer;
import org.openstack.android.summit.common.entities.Summit;
import org.openstack.android.summit.common.network.IReachability;
import org.openstack.android.summit.common.utils.RealmFactory;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by smarcet on 12/7/16.
 */

public class SummitsListIngestionService extends IntentService {

    public static final String PENDING_RESULT = "pending_result";
    public static final int RESULT_CODE_OK                              = 0xFF03;
    public static final int RESULT_CODE_OK_INITIAL_LOADING              = 0xFF04;
    public static final int RESULT_CODE_OK_NEW_SUMMIT_AVAILABLE_LOADING = 0xFF05;
    public static final int RESULT_CODE_ERROR                           = 0xFF06;
    public static boolean isRunning                                     = false;

    @Inject
    IReachability reachability;

    @Inject
    ISummitDeserializer deserializer;

    @Inject
    ISummitSelector summitSelector;

    @Inject
    ISummitDataStore summitDataStore;

    @Inject
    @Named("ServiceProfile")
    Retrofit restClient;

    public static Intent newIntent(Context context) {
        return new Intent(context, SummitsListIngestionService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ((OpenStackSummitApplication) getApplication()).getApplicationComponent().inject(this);
        return super.onStartCommand(intent, flags, startId);
    }

    public SummitsListIngestionService() {
        super("SummitsListIngestionService");
        this.setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent result       = new Intent();
        PendingIntent reply = intent.getParcelableExtra(PENDING_RESULT);

        if (reply == null) return;

        try {

            isRunning = true;

            if (!reachability.isNetworkingAvailable(this)) {
                isRunning = false;
                reply.send(this, RESULT_CODE_ERROR, result);
                return;
            }

            Log.d(Constants.LOG_TAG, "SummitsListIngestionService.onHandleIntent: getting list of summits ...");

            Call<ResponseBody> call = restClient.create(ISummitApi.class).getSummits();

            final retrofit2.Response<ResponseBody> response = call.execute();

            if (!response.isSuccessful())
                throw new Exception(String.format("SummitsListIngestionService: invalid http code %d", response.code()));

            final String body = response.body().string();
            final JSONObject jsonObject = new JSONObject(body);

            int res = RealmFactory.transaction(new RealmFactory.IRealmCallback<Integer>() {
                @Override
                public Integer callback(Realm session) throws Exception {
                    Boolean mustReadSummitData = false;
                    Log.d(Constants.LOG_TAG, "SummitsListIngestionService.onHandleIntent: deserializing summit list data ...");
                    JSONArray summitsList = jsonObject.getJSONArray("data");

                    for (int i = 0; i < summitsList.length(); i++) {
                        JSONObject summitJson = summitsList.getJSONObject(i);
                        Summit summit = deserializer.deserialize(summitJson.toString());
                        session.copyToRealmOrUpdate(summit);
                    }

                    // get latest summit
                    Summit latestSummit = summitDataStore.getLatest();
                    int currentSummitId = summitSelector.getCurrentSummitId();
                    int res             = RESULT_CODE_OK;

                    if(latestSummit.getId() != currentSummitId || !latestSummit.isScheduleLoaded()) {
                        res = RESULT_CODE_OK_INITIAL_LOADING;
                        if(currentSummitId > 0 && latestSummit.getId() > currentSummitId){
                            // we have a new summit available
                            return RESULT_CODE_OK_NEW_SUMMIT_AVAILABLE_LOADING;
                        }
                        summitSelector.setCurrentSummitId(latestSummit.getId());
                    }

                    return res;
                }
            });
            Log.d(Constants.LOG_TAG, "SummitsListIngestionService.onHandleIntent: summit data loaded !!!");
            reply.send(this, res, result);

        } catch (Exception ex) {
            try {
                isRunning = false;
                reply.send(this, RESULT_CODE_ERROR, result);
            } catch (PendingIntent.CanceledException ex2) {
                Crashlytics.logException(ex2);
            }
        } finally {
            isRunning = false;
            RealmFactory.closeSession();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

}