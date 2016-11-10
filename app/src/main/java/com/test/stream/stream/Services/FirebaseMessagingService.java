package com.test.stream.stream.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.MainLoginScreen;

/**
 * Created by Rohini Goyal on 10/23/2016.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Intent intent = new Intent(this, MainLoginScreen.class);    //needs to be changed
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//        notificationBuilder.setContentTitle("FCM Notification");
//        notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
//        notificationBuilder.setAutoCancel(true);
//        notificationBuilder.setSmallIcon(R.drawable.com_facebook_button_icon);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notificationBuilder.build());
        //check if the message contains data
        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, "Message data: " + remoteMessage.getData());
            displayNotification(remoteMessage.getData().toString());
        }

        //display notification if message contains notification
        if(remoteMessage.getNotification() != null){
            Log.d(TAG, "Message body: " + remoteMessage.getNotification().getBody());
            displayNotification(remoteMessage.getNotification().getBody());
        }
    }
    /**
     * Displays the notification sent from another user
     * @param body
     **/
    private void displayNotification(String body) {
        Intent intent = new Intent(this, MainLoginScreen.class);    //needs to be changed
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("FCM Notification")
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.com_facebook_button_icon);

        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= 21) notificationBuilder.setVibrate(new long[0]);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }
}
