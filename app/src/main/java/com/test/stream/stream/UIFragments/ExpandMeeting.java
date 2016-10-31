package com.test.stream.stream.UIFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.test.stream.stream.Controllers.CalendarManager;
import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohini Goyal on 10/30/2016.
 */

public class ExpandMeeting extends AppCompatActivity{

    List<Meeting> meetings = new ArrayList<>();
    Meeting expandMeeting = new Meeting();
    int current_meeting;
    final Context context = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent a = getIntent();
        meetings = CalendarManager.getInstance().GetMeetingsInProject();
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
        FloatingActionButton sendNotification = (FloatingActionButton) findViewById(R.id.sendMeetingReminderNotification);
    }

}
