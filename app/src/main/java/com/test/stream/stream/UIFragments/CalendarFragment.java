package com.test.stream.stream.UIFragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.app.AlertDialog;

import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    private ListView mCalendarListView;
    private ArrayAdapter<String> mAdapter;
    private int current_meeting;


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
        //TODO: SOME STUFF

    }

    public void createMeeting() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_new_meeting, null);

        AlertDialog newMeeting = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText meeting_name = (EditText) v.findViewById(R.id.new_meeting_name);
                        EditText meeting_description = (EditText) v.findViewById(R.id.new_meeting_description);
                        EditText meeting_location = (EditText) v.findViewById(R.id.new_meeting_location);
                        int[] date = new int[]{0, 0, 0};
                        //call taskmanager
                    }
                }).setNegativeButton("Cancel", null)
                .create();
        newMeeting.show();
    }

    public void updateUI() {

    }

}
