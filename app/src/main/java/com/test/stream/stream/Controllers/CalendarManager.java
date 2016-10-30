package com.test.stream.stream.Controllers;

import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.test.stream.stream.Objects.Calendar.Calendar;
import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Tasks.TaskGroup;
import com.test.stream.stream.UIFragments.TasksFragment;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.Month;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cathe on 2016-10-29.
 */

public class CalendarManager  extends DataManager{
    private static CalendarManager instance = new CalendarManager();
    public static CalendarManager getInstance() { return instance; }

    public Context context; //Note: replace the Context object with your UI class
    private Calendar currentCalendar;
    private ConcurrentHashMap<String, Meeting> meetingsInCalendar = new ConcurrentHashMap<String, Meeting>(); //Task Id - task

    private ConcurrentHashMap<Query, ChildEventListener> listenerCollection = new ConcurrentHashMap<Query, ChildEventListener>();;

    private CalendarManager(){};

    public List<Meeting> GetMeetingsInProject()
    {
        List<Meeting> meetings = new ArrayList();
        meetings.addAll(meetingsInCalendar.values());

        return meetings;
    }

    @Override
    public void parentUpdated(DataSnapshot dataSnapshot) {
        currentCalendar = dataSnapshot.getValue(Calendar.class);
        registerMeetings();
    }

    @Override
    public void parentDeleted() {
        currentCalendar = null;
    }

    @Override
    public void childUpdated(DataSnapshot dataSnapshot) {
        Meeting meeting = dataSnapshot.getValue(Meeting.class);
        meetingsInCalendar.put(meeting.getId(), meeting);
    }

    @Override
    public void childDeleted(String id) {
        meetingsInCalendar.remove(id);
    }

    private void registerMeetings()
    {
        for(String id : currentCalendar.getMeetings().keySet()) //Ensure that each task only is register once.
        {
            if(!meetingsInCalendar.containsValue(id))
            {
                super.registerChild(id, DatabaseFolders.Meetings);
            }
        }
    }

    public boolean UpdateMeeting(Meeting meeting)
    {
        if(currentCalendar == null || !meetingsInCalendar.containsKey(meeting))
        {
            return false;
        }

        DatabaseManager.getInstance().updateObject(
                DatabaseFolders.Meetings,
                meeting.getId(),
                meeting);

        return true;
    }

    public boolean CreateMeeting(String name, String description, String location)
    {
        if(currentCalendar == null)
        {
            return false; //Cannot create a task without the project selected.
        }

        Meeting meeting = new Meeting();
        meeting.setCalendarId(ProjectManager.currentProject.getCalendarId());
        String objectKey = DatabaseManager.getInstance().writeObject(DatabaseFolders.Meetings, meeting);

        //Store the firebase object key as the object id.
        meeting.setId(objectKey);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Meetings, objectKey, meeting);

        //Store the task in the Calendar
        currentCalendar.addMeeting(objectKey, true);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Calendars, ProjectManager.currentProject.getTaskGroupId(), currentCalendar);

        return true;
    }

    public boolean DeleteMeeting(Meeting meeting)
    {
        String meetingId = meeting.getId();

        if(!meetingsInCalendar.containsKey(meetingId))
        {
            return false;
        }

        DatabaseReference refToDelete = DatabaseManager.getInstance()
                .getReference(DatabaseFolders.Tasks.toString())
                .child(meetingId);

        refToDelete.removeValue();

        currentCalendar.removeMeeting(meetingId);
        meetingsInCalendar.remove(meeting);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.TaskGroups, ProjectManager.currentProject.getTaskGroupId(), currentCalendar);

        return true;

    }

}
