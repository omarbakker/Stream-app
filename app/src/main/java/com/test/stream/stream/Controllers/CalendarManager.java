package com.test.stream.stream.Controllers;

import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.test.stream.stream.Objects.Calendar.Calendar;
import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.UIFragments.CalendarFragment;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cathe on 2016-10-29.
 */

public class CalendarManager  extends DataManager{
    private DataEventListener listener;
    private static CalendarManager instance = new CalendarManager();
    public static CalendarManager sharedInstance() { return instance; }

    private Calendar currentCalendar;
    private ConcurrentHashMap<String, Meeting> meetingsInCalendar = new ConcurrentHashMap<String, Meeting>(); //Task Id - task

    private CalendarManager(){};

    /**
     * Gets a list of all of the current meetings in this project
     * @return List of Meetings
     */
    public List<Meeting> GetMeetingsInProject()
    {
        List<Meeting> meetings = new ArrayList();
        meetings.addAll(meetingsInCalendar.values());

        return meetings;
    }

    /**
     * Requires: Calendar has loaded.
     * @return true is this project has meetings. False otherwise.
     */
    public Boolean hasMeetings()
    {
        return (currentCalendar.getMeetings().size() > 0);
    }

    @Override
    public void parentUpdated(DataSnapshot dataSnapshot) {
        currentCalendar = dataSnapshot.getValue(Calendar.class);
        registerMeetings();

        if(!hasMeetings())
        {
            listener.onDataChanged();
        }
    }

    @Override
    public void parentDeleted() {
        currentCalendar = null;
        listener.onDataChanged();
    }

    @Override
    public void childUpdated(DataSnapshot dataSnapshot) {
        Meeting meeting = dataSnapshot.getValue(Meeting.class);
        meetingsInCalendar.put(meeting.getId(), meeting);

        if(meetingsInCalendar.size() == currentCalendar.getMeetings().size())
            listener.onDataChanged();
    }

    @Override
    public void childDeleted(String id) {
        meetingsInCalendar.remove(id);
        listener.onDataChanged();
    }

    /**
     * Listener to see if the meeting has been updated by a user and that the UI needs to be updated
     */
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

    /**
     * Updates the information in a meeting
     * @param meeting meeting object that needs to be updated
     * @return flag indicating if the meeting was successfully updated
     */
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

    /**
     * Creates a new meeting object and writes it to the database
     * @param name name of the new meeting
     * @param description description of the new meeting
     * @param location location of the new meeting
     * @return flag indicating that the meeting was created
     */
    public boolean CreateMeeting(String name, String description, String location,
                                 int hour, int minute, int day, int month, int year,
                                 String monthName, String dayOfWeek, String AmPm)
    {
        if(currentCalendar == null)
        {
            return false; //Cannot create a task without the project selected.
        }

        Meeting meeting = new Meeting();
        meeting.setCalendarId(ProjectManager.sharedInstance().getCurrentProject().getCalendarId());
        meeting.setName(name);
        meeting.setDescription(description);
        meeting.setLocation(location);
        meeting.setTime(hour, minute);
        meeting.setAmPm(AmPm);
        meeting.setDate(monthName, day, year, month, dayOfWeek);
        String objectKey = DatabaseManager.getInstance().writeObject(DatabaseFolders.Meetings, meeting);

        //Store the firebase object key as the object id.
        meeting.setId(objectKey);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Meetings, objectKey, meeting);

        //Store the task in the Calendar
        currentCalendar.addMeeting(objectKey, true);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Calendars, ProjectManager.sharedInstance().getCurrentProject().getCalendarId(), currentCalendar);


        return true;
    }

    /**
     * Deletes a meeting
     * @param meeting meeting to be deleted
     * @return flag indicating if the meeting was deleted
     */
    public boolean DeleteMeeting(Meeting meeting)
    {
        String meetingId = meeting.getId();

        if(!meetingsInCalendar.containsKey(meetingId))
        {
            return false;
        }

        DatabaseReference refToDelete = DatabaseManager.getInstance()
                .getReference(DatabaseFolders.Meetings.toString())
                .child(meetingId);

        refToDelete.removeValue();

        currentCalendar.removeMeeting(meetingId);
        meetingsInCalendar.remove(meeting);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Calendars,
                ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId(),
                currentCalendar);

        return true;

    }

    /**
     * Initializes an instance of CalendarManager and the database
     * @param listener the lisener to call back to once data has been updated.
     */
    public void Initialize(DataEventListener listener) {
        this.listener = listener;
        super.registerParent(DatabaseFolders.Calendars, ProjectManager.sharedInstance().getCurrentProject().getCalendarId());;
    }

    @Override
    public void Destroy()
    {
        instance = new CalendarManager();
        super.Destroy();
    }

}
