package com.test.stream.stream.Controllers;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Xingyu on 11/9/2016.
 */

public class NotificationManager {
    public static final String TAG = "NotificationManager";
    public static NotificationManager instance = null;


    /**
     * prevents outside initiation
     */
    private NotificationManager(){ }

    /**
     * @return
     *  The one and only NotificationManager instance (singleton)
     */
    public static NotificationManager sharedInstance(){

        if (instance == null)
            instance = new NotificationManager();
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
                    String username = UserManager.getInstance().getCurrentUser().getUid();
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


}
