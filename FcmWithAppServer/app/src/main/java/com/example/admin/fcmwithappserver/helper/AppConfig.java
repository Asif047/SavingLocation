package com.example.admin.fcmwithappserver.helper;

import com.android.volley.Response;

import retrofit.http.POST;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

import static com.android.volley.Request.Method.POST;

/**
 * Created by admin on 9/9/2017.
 */

public class AppConfig {

    public interface update {
        @retrofit.http.FormUrlEncoded
        @retrofit.http.POST("/fcmtest2/updateData.php")
        void updateData(


                @retrofit.http.Field("latitude") String latitude,
                @retrofit.http.Field("longitude") String longitude,
                @retrofit.http.Field("email") String email,

                retrofit.Callback<retrofit.client.Response> callback);
    }

}
