package com.test.stream.stream.UIFragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.test.stream.stream.Controllers.CalendarManager;
import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohini Goyal on 10/30/2016.
 */

public class ExpandMeeting extends AppCompatActivity implements View.OnClickListener{

    List<Meeting> meetings = new ArrayList<>();
    Meeting expandMeeting = new Meeting();
    int current_meeting;
    final Context context = this;

    //Initializing views for the reminder alert dialog
    static AlertDialog meetingReminderDialog;
    EditText meetingReminderMessage;
    View reminderMeetingDialogView;
    TextView meetingReminderInfo;
    AlertDialog.Builder meetingReminderBuilder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent a = getIntent();
        meetings = CalendarManager.sharedInstance().GetMeetingsInProject();
        String meetingName = (String) a.getSerializableExtra("meetingName");
        View view = (View) a.getSerializableExtra("v");
        setContentView(R.layout.calendar_details);

        int size = meetings.size();
        for(int i = 0; i < size; i++) {
            Meeting meeting = meetings.get(i);
            if(meetingName.equals(meeting.getName())) {
                expandMeeting = meeting;
                current_meeting = i;
                break;
            }
        }
        setContentView(R.layout.calendar_details);
        FloatingActionButton sendMeetingReminderNotification = (FloatingActionButton) findViewById(R.id.sendMeetingReminderNotification);
        sendMeetingReminderNotification.setOnClickListener(this);

        TextView expandedMeetingName = (TextView) findViewById(R.id.meeting_name_expanded);
        expandedMeetingName.setText(expandMeeting.getName());

        TextView expandedMeetingDescription = (TextView) findViewById(R.id.meeting_description_expanded);
        expandedMeetingDescription.setText(expandMeeting.getDescription());
        expandedMeetingDescription.setVisibility(View.VISIBLE);

        TextView expandedMeetingLocation = (TextView) findViewById(R.id.meetingLocation_expanded);
        expandedMeetingLocation.setText(expandMeeting.getLocation());

        TextView expandedMeetingTime = (TextView) findViewById(R.id.meetingTime_expanded);
        if(expandMeeting.getMinute() < 10) {
            expandedMeetingTime.setText(expandMeeting.getDayOfWeek() + ", " + expandMeeting.getDay() + " " + expandMeeting.getMonth() + " " + expandMeeting.getYear() + "       " + expandMeeting.getHour() + ":" + "0" + expandMeeting.getMinute() + " " + expandMeeting.getAmPm());
        }
        else {
            expandedMeetingTime.setText(expandMeeting.getDayOfWeek() + ", " + expandMeeting.getDay() + " " + expandMeeting.getMonth() + " " + expandMeeting.getYear() + "       " + expandMeeting.getHour() + ":" + expandMeeting.getMinute() + " " + expandMeeting.getAmPm());
            System.out.println("AMPM EXPANDED VERSION   " + expandMeeting.getAmPm());
        }


        //initialize views for reminder meeting dialog
        LayoutInflater ReminderMeetingInflater = LayoutInflater.from(context);
        reminderMeetingDialogView = ReminderMeetingInflater.inflate(R.layout.send_meeting_reminder_notification, null);
        meetingReminderMessage = (EditText) reminderMeetingDialogView.findViewById(R.id.meetingMessageToSend);
        meetingReminderBuilder = new AlertDialog.Builder(this);
        meetingReminderBuilder.setView(reminderMeetingDialogView);
        meetingReminderBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String message = meetingReminderMessage.getText().toString();
                getMeetingReminderNotification(message);
            }
        });
        meetingReminderBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                meetingReminderDialog.dismiss();
            }
        });
        meetingReminderDialog = meetingReminderBuilder.create();
    }

    /**
     * Determines what should happen next when you click the "send notification" button
     * @param v current view
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMeetingReminderNotification:
                reminderMeetingNotification();
        }
    }

    /**
     * Displays the dialog for sending a meeting reminder notification
     */
    public void reminderMeetingNotification() {
        meetingReminderDialog.show();
    }

    public void getMeetingReminderNotification(String message) {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle(getString(R.string.meeting_reminder_notification_title_send));
        nBuilder.setContentText(message);
        nBuilder.setSmallIcon(R.drawable.com_facebook_button_icon);
        Notification notification = nBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

}
