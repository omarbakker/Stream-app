package com.test.stream.stream.UI;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.Profile;
import com.test.stream.stream.R;

import org.w3c.dom.Text;

public class sendReminderNotificationDialog extends AppCompatActivity  implements View.OnClickListener{

    Button getNotification;
    final Context context = this;
    AlertDialog dialog;
    EditText messageToSend;
    TextView sendReminderAlertTitle;
    CheckBox sendAnonymously;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_notification);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.send_reminder_notification, null);
        messageToSend = (EditText) v.findViewById(R.id.reminderMessageToSend);

        getNotification = (Button) findViewById(R.id.getNotification);
        getNotification.setOnClickListener(this);

        sendReminderAlertTitle = (TextView) findViewById(R.id.reminderTitle);
        sendAnonymously = (CheckBox) findViewById(R.id.sendAnonymously);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.send_reminder_notification);
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getnotification();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
       dialog = builder.create();

    }

    public void getnotification() {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle("Here's a friendly reminder for you to complete your task!");
        nBuilder.setContentText(messageToSend.getText().toString());
        nBuilder.setSmallIcon(R.drawable.com_facebook_button_icon);
        Notification notification = nBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getNotification:
                dialog.show();
                break;
        }
    }
}
