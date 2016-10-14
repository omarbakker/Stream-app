package com.test.stream.stream.UI;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.test.stream.stream.R;

import org.w3c.dom.Text;

public class CreateNewMeeting extends AppCompatActivity {

    TextView newMeetingTitle, saveButton, newMeetingDirections;
    EditText meetingName;
    Button sendMeetingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_meeting);

        //Initialization
        newMeetingTitle = (TextView) findViewById(R.id.newMeetingTitle);
        saveButton = (TextView) findViewById(R.id.saveButton);
        sendMeetingRequest = (Button) findViewById(R.id.sendMeetingRequest);
        newMeetingDirections = (TextView) findViewById(R.id.newMeetingDirections);
        meetingName = (EditText) findViewById(R.id.meetingName);

        //Change font to Syncopate
        //Changing font to Syncopate
        Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Regular.ttf");
        Typeface SyncopateBold = Typeface.createFromAsset(this.getAssets(), "Syncopate-Bold.ttf");
        newMeetingTitle.setTypeface(Syncopate);
        saveButton.setTypeface(SyncopateBold);
        newMeetingDirections.setTypeface(Syncopate);
        sendMeetingRequest.setTypeface(Syncopate);
        meetingName.setTypeface(Syncopate);


    }
}
