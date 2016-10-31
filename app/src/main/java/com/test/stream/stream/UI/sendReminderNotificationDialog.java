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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.test.stream.stream.R;
import com.test.stream.stream.Services.MyFirebaseInstanceIDService;

public class sendReminderNotificationDialog extends AppCompatActivity  {

    final Context context = this;

    //Reminder notification
    static AlertDialog dialog;
    EditText messageToSend;
    TextView sendReminderAlertTitle;
    CheckBox sendAnonymously;
    View reminderDialogView;

    //Review notification
    static AlertDialog Reviewdialog;
    EditText reviewMessageToSend;
    TextView reviewTitle;
    View reviewDialogView;

    //Task is complete
    boolean taskComplete = false;
    String Assignee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MyFirebaseInstanceIDService.class);
        startService(intent);

        //Obtain "complete" boolean from the task
        Bundle b = getIntent().getExtras();
        if(b != null) {
            taskComplete = b.getBoolean("ifTaskIsComplete");
            Assignee = b.getString("taskAssignedTo");
        }


        // Initializes views for reminder alert dialog
        LayoutInflater ReminderInflater = LayoutInflater.from(context);
        reminderDialogView = ReminderInflater.inflate(R.layout.send_reminder_notification, null);
        messageToSend = (EditText) reminderDialogView.findViewById(R.id.reminderMessageToSend);
        sendReminderAlertTitle = (TextView) findViewById(R.id.reminderTitle);
        sendReminderAlertTitle.setText(R.string.reminder_notification_dialog_title + Assignee);
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
        reviewMessageToSend = (EditText) reviewDialogView.findViewById(R.id.reminderMessageToSend);
        reviewTitle = (TextView) findViewById(R.id.reminderTitle);
        reviewTitle.setText(R.string.review_notification_dialog_title1 + Assignee + R.string.review_notification_dialog_title2);

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


        //Display a dialog depending on whether the activity is complete or not
        if(taskComplete)
            appearReviewDialog();
        else
            appearReminderDialog();


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


    public static void appearReminderDialog() {
        dialog.show();
    }

    public static void appearReviewDialog() {
        Reviewdialog.show();
    }
}
