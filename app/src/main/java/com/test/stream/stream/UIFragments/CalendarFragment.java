package com.test.stream.stream.UIFragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.google.firebase.auth.api.model.GetAccountInfoUser;
import com.test.stream.stream.Controllers.CalendarManager;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    private ListView mCalendarListView;
    private ArrayAdapter<String> mAdapter;
    private int current_meeting;
    ArrayList<Meeting> meetings = new ArrayList<>();



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

        mCalendarListView = (ListView) getView().findViewById(R.id.list_calendar);
        final FloatingActionButton addCalendarButton = (FloatingActionButton) getView().findViewById(R.id.create_new_meeting);
        addCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMeeting();
            }
        });

        CalendarManager.getInstance().Initialize(this);

    }

    /**
     * Displays the AlertDialog for creating a new meeting
     */
    public void createMeeting() {
        Log.d("PLEASE WORK", "PLEASE WORK");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_new_meeting, null);

        AlertDialog newMeeting = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText meeting_name = (EditText) v.findViewById(R.id.new_meeting_name);
                        EditText meeting_description = (EditText) v.findViewById(R.id.new_meeting_description);
                        EditText meeting_location = (EditText) v.findViewById(R.id.new_meeting_location);

                        Log.d("PLEASE WORK", "EVERYTHING IS INITIALIZED. TIME TO CREATE");
                        CalendarManager.getInstance().CreateMeeting(meeting_name.getText().toString(), meeting_description.getText().toString(), meeting_location.getText().toString());
                        Log.d("PLEASE WORK", "CALENDAR MANAGER WAS CALLED");
                    }
                }).setNegativeButton("Cancel", null)
                .create();
        Log.d("PLEASE WORK", "SOMETHING WAS CREATED");
        newMeeting.show();
        Log.d("PLEASE WORK", "OKAY DIALOG SHOWS");
    }

    /**
     * Gets all of the meeting objects from the database and then displays them to the UI
     */
    public void updateUI() {
        List<Meeting> meetings = CalendarManager.getInstance().GetMeetingsInProject();
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
            mAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.calendar_small,
                    R.id.meeting_name,
                    meetingList);
            mCalendarListView.setAdapter(mAdapter);
        }
        else {
            mAdapter.clear();
            mAdapter.addAll(meetingList);
            mAdapter.notifyDataSetChanged();
        }

    }


    /**
     * Function that is called to display the expanded view of a meeting activity
     * @param v current view
     */
    public void expandMeetingView(View v) {
        View parent = (View) v.getParent();
        TextView meetingTextview = (TextView) parent.findViewById(R.id.meeting_name);
        String meetingName = String.valueOf(meetingTextview.getText());
        Intent intent = new Intent(getActivity(), ExpandMeeting.class);
        intent.putExtra("meetingName", meetingName);
        startActivity(intent);
    }
}
