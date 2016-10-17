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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Profile;
import com.test.stream.stream.R;

import org.w3c.dom.Text;

public class getNotification extends AppCompatActivity  implements View.OnClickListener{

    Button getNotification;
    final Context context = this;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_notification);

        getNotification = (Button) findViewById(R.id.getNotification);
        getNotification.setOnClickListener(this);

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
//        builder.setMessage(R.string.reminder_notification_dialog_title)
//                .setTitle("hello");
        builder.setView(R.layout.send_reminder_notification);

        // Add the buttons
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                getnotification();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        // Create the AlertDialog

       dialog = builder.create();

    }

  //  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void getnotification() {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle("Test Title that is very long on purpose");
        nBuilder.setContentText("text that is also long on purpose");
        nBuilder.setSmallIcon(R.drawable.com_facebook_button_icon);


        Notification notification = nBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    public void startDialog(){
        final Dialog dialog  = new Dialog(context);
        dialog.setContentView(R.layout.send_reminder_notification);

        TextView dialogTitle = (TextView) findViewById(R.id.reminderTitle);
        //TextView personBeingSentto = (TextView) findViewById(R.id.SendTo);
        EditText message = (EditText) findViewById(R.id.editText2);
        //Button send = (Button) findViewById(R.id.button2);
        startDialog();

        //send.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getNotification:
                //getnotification();
                dialog.show();
                break;
        }
    }
}
