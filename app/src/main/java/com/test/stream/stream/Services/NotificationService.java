package com.test.stream.stream.Services;

import android.content.pm.LauncherApps;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Notifications.NotificationObject;

import org.json.JSONArray;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Xingyu on 11/9/2016.
 */

public class NotificationService {
    public static final String TAG = "NotificationService";
    public static NotificationService instance = null;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * prevents outside initiation
     */
    private NotificationService(){ }

    /**
     * @return
     *  The one and only NotificationService instance (singleton)
     */
    public static NotificationService sharedInstance(){

        if (instance == null)
            instance = new NotificationService();
        return instance;
    }

    /**
     * Registers current User and Device to third party Server
     * Pre: user is logged in and DeviceToken exists
     * Post: new table entry with username and device token created in server
     *
     * Created by Xingyu Tao 2016-11-09
     */
    public void registerUserDevice(){
        Log.d(TAG,"registering new device");

        Thread t = new Thread(new Runnable(){
            boolean thread_running = true;
            @Override
            public void run(){
                while(thread_running){
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    String username = UserManager.sharedInstance().getCurrentUser().getUsername();
                    Log.d(TAG, "device: " + deviceToken + " " + "username: " + username + "...");
                    if(deviceToken != null){
                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = new FormBody.Builder()
                                .add("Token",  deviceToken)
                                .add("Username", username)
                                .build();
                        Request request = new Request.Builder()
                                //.url("http://128.189.196.101/fcm/register.php")
                                .url("http://stream.heliohost.org/fcm/register.php")
                                .post(body)
                                .build();
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();
                            Log.d(TAG, response.body().string());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        thread_running = false;
                    }
                    try{
                        Thread.sleep(1000);
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });t.start();

    }

    /**
     * builds http request for notification sending, queries heliohost database
     * @param notification
     * @throws IOException
     */
    public void sendNotificationTo(final NotificationObject notification) throws IOException{
        Log.d(TAG, "sending notification: " + notification.getTitle());

        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "device: " + deviceToken);
        if (deviceToken != null) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("Usernames",getToString(notification.getUsers()))
                    .add("Title", notification.getTitle())
                    .add("Message", notification.getMessage())
                    .build();
            Request request = new Request.Builder()
                    .url("http://stream.heliohost.org/fcm/send_notification.php")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, IOException e) {
                    e.printStackTrace();

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //do some toast shit
//                        }
//                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException{
                    String res = response.body().string();
                    Log.d(TAG,res);
                }
            });
        }
    }

    /**
     * drops row with device token
     */
    public void deleteDeviceTokenFromDatabse(){
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Deleting Token: " + deviceToken);
        if (deviceToken != null) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("Token", deviceToken)
                    .build();
            Request request = new Request.Builder()
                    .url("http://stream.heliohost.org/fcm/delete_token.php")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, IOException e) {
                    e.printStackTrace();

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //do some toast shit
//                        }
//                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException{
                    String res = response.body().string();
                    Log.d(TAG,res);
                }
            });
        }
    }

    public String getToString(String[] arrayData) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0 ; i < arrayData.length; i++) {
            stringBuilder.append(arrayData[i]);
            if (i < arrayData.length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

}
