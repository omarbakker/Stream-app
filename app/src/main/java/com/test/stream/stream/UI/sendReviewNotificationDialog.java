package com.test.stream.stream.UI;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.test.stream.stream.R;

/**
 * Created by Rohini Goyal on 10/21/2016.
 */

public class sendReviewNotificationDialog extends AppCompatActivity {

    final Context context = this;
    AlertDialog Reviewdialog;
    EditText reviewMessageToSend;
    TextView reviewTitle;
    View reviewDialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializes views for review alert dialog
        LayoutInflater ReviewInflater = LayoutInflater.from(context);
        reviewDialogView = ReviewInflater.inflate(R.layout.send_review_notification, null);
        reviewMessageToSend = (EditText) reviewDialogView.findViewById(R.id.reminderMessageToSend);
        reviewTitle = (TextView) findViewById(R.id.reminderTitle);

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


}
