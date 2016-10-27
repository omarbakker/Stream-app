package com.test.stream.stream.UI;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.test.stream.stream.R;

public class sendReminderNotificationDialog extends AppCompatActivity  implements View.OnClickListener{

    Button getNotification, sendReview;
    final Context context = this;

    //Reminder notification
    AlertDialog dialog;
    EditText messageToSend;
    TextView sendReminderAlertTitle;
    CheckBox sendAnonymously;
    View reminderDialogView;

    //Review notification
    AlertDialog Reviewdialog;
    EditText reviewMessageToSend;
    TextView reviewTitle;
    View reviewDialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_notification);
        Intent intent = new Intent(this, MyFirebaseInstanceIDService.class);
        startService(intent);

        sendReview = (Button) findViewById(R.id.button2);
        // Initializes views for reminder alert dialog
        LayoutInflater ReminderInflater = LayoutInflater.from(context);
        reminderDialogView = ReminderInflater.inflate(R.layout.send_reminder_notification, null);
        messageToSend = (EditText) reminderDialogView.findViewById(R.id.reviewMessageToSend);
        getNotification = (Button) findViewById(R.id.getNotification);
        getNotification.setOnClickListener(this);
        sendReview.setOnClickListener(this);
        sendReminderAlertTitle = (TextView) findViewById(R.id.reviewTitle);
        sendAnonymously = (CheckBox) findViewById(R.id.sendAnonymously);

        //Initialize AlertDialog for Reminder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(reminderDialogView);
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String message = reviewMessageToSend.getText().toString();
                getReminderNotification(message);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();

        //
        //------------------------------------------------------------------------------------
        //

        // Initializes views for review alert dialog
        LayoutInflater ReviewInflater = LayoutInflater.from(context);
        reviewDialogView = ReviewInflater.inflate(R.layout.send_review_notification, null);
        reviewMessageToSend = (EditText) reviewDialogView.findViewById(R.id.reviewMessageToSend);
        reviewTitle = (TextView) findViewById(R.id.reviewTitle);

        //Initialize AlertDialog for Review
        AlertDialog.Builder Reviewbuilder = new AlertDialog.Builder(this);
        Reviewbuilder.setView(reviewDialogView);
        Reviewbuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String message = reviewMessageToSend.getText().toString();
                getReviewNotification(message);
            }
        });
        Reviewbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        Reviewdialog = Reviewbuilder.create();

    }

    public void getReminderNotification(String message) {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle("Here's a friendly reminder for you to complete your task!");
        nBuilder.setContentText(message);
        nBuilder.setSmallIcon(R.drawable.com_facebook_button_icon);
        Notification notification = nBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        //Clear contents of the EditText
        messageToSend.setText("");
    }

    public void getReviewNotification(String message) {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle("Here's a suggestion!");
        nBuilder.setContentText(message);
        nBuilder.setSmallIcon(R.drawable.com_facebook_button_icon);
        Notification notification = nBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        //Clear contents of the EditText
        reviewMessageToSend.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getNotification:
                dialog.show();
                break;
            case R.id.button2:
                Reviewdialog.show();
                break;
        }
    }
}
