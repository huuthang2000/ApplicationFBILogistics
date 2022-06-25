package com.example.demoapp.services.ApiCallVideo;

import com.example.demoapp.Utils.Cons;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({"Authorization: key=" + Cons.REMOTE_SERVER_KEY, "Content-Type:application/json"})
    @POST("send")
    Call<String> sendRemoteMessage(
            @Body String remoteBody
    );

    /**
     * Send the message FCM push notification through the remote server
     * @param notification Notification data string
     */
    @Headers({"Authorization: key=" + Cons.REMOTE_SERVER_KEY, "Content-Type:application/json"})
    @POST("send")
    Call<ResponseBody> sendNotification(
            @Body String notification
    );
}
