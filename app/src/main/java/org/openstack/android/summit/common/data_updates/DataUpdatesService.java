package org.openstack.android.summit.common.data_updates;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import org.openstack.android.summit.OpenStackSummitApplication;
import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.data_access.data_polling.IDataUpdatePoller;
import org.openstack.android.summit.common.network.IReachability;
import javax.inject.Inject;

public class DataUpdatesService extends IntentService {

    private static final int POLL_INTERVAL = 1000 * 60; // 60 seconds

    @Inject
    IDataUpdatePoller dataUpdatePoller;

    @Inject
    IReachability reachability;

    public static Intent newIntent(Context context) {
        return new Intent(context, DataUpdatesService.class);
    }

    public DataUpdatesService() {
        super("DataUpdatesService");
        this.setIntentRedelivery(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        ((OpenStackSummitApplication)getApplication()).getApplicationComponent().inject(this);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!reachability.isNetworkingAvailable(this)) {
            return;
        }
        // normal flow ...
        dataUpdatePoller.pollServer();
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = DataUpdatesService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
            Log.i(Constants.LOG_TAG, "Initialized Service DataUpdatesService");
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
            Log.i(Constants.LOG_TAG, "Stopping Service DataUpdatesService");
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = DataUpdatesService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}