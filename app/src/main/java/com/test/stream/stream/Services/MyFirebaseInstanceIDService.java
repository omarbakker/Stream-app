package com.test.stream.stream.Services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.DatabaseManager;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
//import static com.test.stream.stream.R.id.user;

/**
 * Created by Rohini Goyal on 10/23/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String REG_TOKEN = "REG_TOKEN";
    //private User currentUser = DatabaseManager.getInstance().

    @Override
    public void onTokenRefresh() {
//        Log.d("PLEASE WORK", "PLEASE WORK");
//        String recent_token = FirebaseInstanceId.getInstance().getToken();
//        Log.d(REG_TOKEN, recent_token);
        //Get updated token
        //Log.d("PLEASE WORK", "PLEASE WORK");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        registerToken(refreshedToken);
        Log.d(TAG, "New Token: " + refreshedToken);

    }

    /**
     * Registers unique token to third party app server
     * @author Xingyu (Nov 7 2016)
     * @param token
     */
    private void registerToken(String token){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();
        Request request = new Request.Builder()
                .url("http://128.189.195.254/fcm/register.php")
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            //client.newCall(request).execute();
            System.out.println(response.body().string());
            Log.d(TAG, response.body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
