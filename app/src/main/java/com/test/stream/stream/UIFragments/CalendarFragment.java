package com.test.stream.stream.UIFragments;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.test.stream.stream.Controllers.CalendarManager;
import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.CreateNewMeeting;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    private ListView mCalendarListView;
    private TextView mCalendarTextView;
    private ArrayAdapter<String> mAdapter;
    private int current_meeting;
    ArrayList<Meeting> meetings = new ArrayList<>();

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
//        Log.d("PLEASE WORK", "PLEASE WORK");
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        final View v = inflater.inflate(R.layout.dialog_new_meeting, null);
//
//        AlertDialog newMeeting = new AlertDialog.Builder(getActivity())
//                .setView(v)
//                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        EditText meeting_name = (EditText) v.findViewById(R.id.new_meeting_name);
//                        EditText meeting_description = (EditText) v.findViewById(R.id.new_meeting_description);
//                        EditText meeting_location = (EditText) v.findViewById(R.id.new_meeting_location);
//
//                        Log.d("PLEASE WORK", "EVERYTHING IS INITIALIZED. TIME TO CREATE");
//                        CalendarManager.sharedInstance().CreateMeeting(meeting_name.getText().toString(), meeting_description.getText().toString(), meeting_location.getText().toString());
//                        Log.d("PLEASE WORK", "CALENDAR MANAGER WAS CALLED");
//                    }
//                }).setNegativeButton("Cancel", null)
//                .create();
//        Log.d("PLEASE WORK", "SOMETHING WAS CREATED");
//        newMeeting.show();
//        Log.d("PLEASE WORK", "OKAY DIALOG SHOWS");
    }

    /**
     * Gets all of the meeting objects from the database and then displays them to the UI
     */
    private void updateUI() {
        if(!CalendarManager.sharedInstance().hasMeetings())
        {
            mCalendarTextView.setText(R.string.no_meetings);
            return;
        }


        List<Meeting> meetings = CalendarManager.sharedInstance().GetMeetingsInProject();

        //Hide text otherwise.
        mCalendarTextView.setText(R.string.empty);
        mCalendarTextView.setVisibility(View.GONE);

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

    @Override
    public void onDestroyView()
    {
        CalendarManager.sharedInstance().Destroy();
        super.onDestroyView();
    }
}
