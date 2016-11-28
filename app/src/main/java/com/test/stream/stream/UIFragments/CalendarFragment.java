package com.test.stream.stream.UIFragments;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.test.stream.stream.Controllers.CalendarManager;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.Adapters.CalendarAdapter;
import com.test.stream.stream.UI.CreateNewMeeting;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements ListView.OnItemClickListener{

    private ListView mCalendarListView;
    private TextView mCalendarTextView;
    private CalendarAdapter mAdapter;
    private int current_meeting;
    AlertDialog meetingReminderDialog;
    private Meeting expandMeeting;
    List<Meeting> meetings = new ArrayList<>();
    AlertDialog.Builder meetingReminderBuilder;
    final Context context = this.getActivity();

    private AlertDialog popupDialog;

    private DataEventListener dataListener = new DataEventListener() {
        @Override
        public void onDataChanged() {
            updateUI();
        }
    };

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.calendar_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCalendarTextView = (TextView) getView().findViewById(R.id.text_calendar);
        mCalendarListView = (ListView) getView().findViewById(R.id.list_calendar);
        mCalendarListView.setOnItemClickListener(this);

        Typeface Syncopate = Typeface.createFromAsset(getActivity().getAssets(), "Raleway-Regular.ttf");
        mCalendarTextView.setTypeface(Syncopate);

        final FloatingActionButton addCalendarButton = (FloatingActionButton) getView().findViewById(R.id.create_new_meeting);
        addCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMeeting();
            }
        });

        CalendarManager.sharedInstance().Initialize(dataListener);

    }

    /**
     * Displays the AlertDialog for creating a new meeting
     */
    public void createMeeting() {
        Intent intent = new Intent(getActivity(), CreateNewMeeting.class);
        startActivity(intent);
    }

    /**
     * Gets all of the meeting objects from the database and then displays them to the UI
     */
    private void updateUI() {

        if(!CalendarManager.sharedInstance().hasMeetings())
        {
            mCalendarTextView.setText(R.string.no_meetings);
            mCalendarTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            mCalendarTextView.setText(R.string.empty);
            mCalendarTextView.setVisibility(View.GONE);
        }

        List<Meeting> meetings = CalendarManager.sharedInstance().GetMeetingsInProject();

        ArrayList<String> meetingList = new ArrayList<>();
        int i = meetings.size() - 1;
        while(i >= 0) {
            Meeting meeting = meetings.get(i);
            if(meeting != null) {
                meetingList.add(meeting.getName());
            }

            i--;
        }

        if (mAdapter == null) {
            mAdapter = new CalendarAdapter(getActivity(), R.layout.calendar_listview, meetings);
            mCalendarListView.setAdapter(mAdapter);
        }
        else {
            mAdapter.clear();
            mAdapter.addAll(meetings);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestroyView()
    {
        CalendarManager.sharedInstance().Destroy();
        super.onDestroyView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mCalendarListView) {
            meetings = CalendarManager.sharedInstance().GetMeetingsInProject();
            Meeting selectedMeeting = (Meeting) mAdapter.getItem(position);
            int size = meetings.size();
            for(int i = 0; i < size; i++) {
                Meeting meeting = meetings.get(i);
                if(selectedMeeting.getName().equals(meeting.getName())) {
                    current_meeting = i;
                    break;
                }
            }
            expandMeetingDetailsPopup(view, selectedMeeting);
        }

    }

    public void expandMeetingDetailsPopup(View v, Meeting selectedMeeting) {
        Typeface Raleway = Typeface.createFromAsset(getActivity().getAssets(), "Raleway-Regular.ttf");
        Typeface RalewayBold = Typeface.createFromAsset(getActivity().getAssets(), "Raleway-Bold.ttf");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View popupView = inflater.inflate(R.layout.calendar_details, null);
        expandMeeting = selectedMeeting;

        popupDialog = new AlertDialog.Builder(getActivity()).setView(popupView).create();

        //set buttons
        FloatingActionButton sendMeetingReminderNotification = (FloatingActionButton) popupView.findViewById(R.id.sendMeetingReminderNotification);
        sendMeetingReminderNotification.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
                //reminderMeetingNotification();
                LayoutInflater ReminderMeetingInflater = getActivity().getLayoutInflater();
                View reminderMeetingDialogView = ReminderMeetingInflater.inflate(R.layout.send_meeting_reminder_notification, null);

                final EditText meetingReminderMessage = (EditText) reminderMeetingDialogView.findViewById(R.id.meetingMessageToSend);
                meetingReminderBuilder = new AlertDialog.Builder(getActivity());
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
                meetingReminderDialog.show();
            }
        }));
        FloatingActionButton deleteMeting = (FloatingActionButton) popupView.findViewById(R.id.deleteMeeting);
        deleteMeting.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
                deleteMeeting(v);
            }
        }));

        //get each field
        TextView expandedMeetingName = (TextView) popupView.findViewById(R.id.meeting_name_expanded);
        TextView expandedMeetingDescription = (TextView) popupView.findViewById(R.id.meeting_description_expanded);
        TextView expandedMeetingLocation = (TextView) popupView.findViewById(R.id.meetingLocation_expanded);
        TextView expandedMeetingTime = (TextView) popupView.findViewById(R.id.meetingTime_expanded);
        TextView expandedMeetingTime2 = (TextView) popupView.findViewById(R.id.expanded_meeting_time);
        expandedMeetingName.setTypeface(RalewayBold);
        expandedMeetingLocation.setTypeface(Raleway);
        expandedMeetingDescription.setTypeface(Raleway);
        expandedMeetingTime.setTypeface(Raleway);
        expandedMeetingTime2.setTypeface(Raleway);

        //assign values
        expandedMeetingName.setText(expandMeeting.getName());
        expandedMeetingDescription.setText(expandMeeting.getDescription());
        expandedMeetingLocation.setText(expandMeeting.getLocation());
        expandedMeetingTime.setText(expandMeeting.getDayOfWeek() + ", " + expandMeeting.getDay() + " " + expandMeeting.getMonth() + " " + expandMeeting.getYear());
        if(expandMeeting.getMinute() < 10) {
            expandedMeetingTime2.setText(expandMeeting.getHour() + ":" + "0" + expandMeeting.getMinute() + " " + expandMeeting.getAmPm());
        }
        else {
            expandedMeetingTime2.setText(expandMeeting.getHour() + ":" + expandMeeting.getMinute() + " " + expandMeeting.getAmPm());
            System.out.println("AMPM EXPANDED VERSION   " + expandMeeting.getAmPm());
        }

        if (current_meeting%2 == 0){
            expandedMeetingName.setBackgroundColor(Color.argb(255,225,237,255));
        }else{
            expandedMeetingName.setBackgroundColor(Color.argb(235,232,255,245));
        }

        popupDialog.show();
    }

    /**
     * Displays the dialog for sending a meeting reminder notification
     */
    public void reminderMeetingNotification() {
        //initialize views for reminder meeting dialog
        LayoutInflater ReminderMeetingInflater = LayoutInflater.from(this.getActivity());
        View reminderMeetingDialogView = ReminderMeetingInflater.inflate(R.layout.send_meeting_reminder_notification, null);

        final EditText meetingReminderMessage = (EditText) reminderMeetingDialogView.findViewById(R.id.meetingMessageToSend);
        meetingReminderBuilder = new AlertDialog.Builder(context);
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
        meetingReminderDialog.show();
    }

    /**
     * Upon the press of the delete button, the current task is deleted from the database and the user is returned
     * to the main task screen
     * @param view
     */
    public void deleteMeeting(View view){
        CalendarManager.sharedInstance().DeleteMeeting(expandMeeting);
        Project currentProject = ProjectManager.sharedInstance().getCurrentProject();
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Projects,currentProject.getId(),currentProject);
    }

    public void getMeetingReminderNotification(String message) {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
        nBuilder.setContentTitle(getString(R.string.meeting_reminder_notification_title_send));
        nBuilder.setContentText(message);
        nBuilder.setSmallIcon(R.drawable.com_facebook_button_icon);
        Notification notification = nBuilder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

}
