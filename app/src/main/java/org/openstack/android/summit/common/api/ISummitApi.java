package org.openstack.android.summit.common.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by smarcet on 11/9/16.
 */

public interface ISummitApi {

    @GET("v1/summits/{summit_id}")
    public Call<ResponseBody> getSummit(@Path("summit_id") String summitId, @Query("expand") String expand);
}