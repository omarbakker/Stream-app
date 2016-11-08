package com.test.stream.stream.Objects.Calendar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cathe on 2016-10-29.
 */

public class Calendar {
    //region Variables
    private String parentProjectId;
    private Map<String, Boolean> meetings = new HashMap<String, Boolean>();  //meeting Id - meeting active
    //endregion

    //region Setters and Getters
    public String getParentProjectId() {
        return parentProjectId;
    }
    public Map<String, Boolean> getMeetings() {
        return meetings;
    }

    public void setParentProjectId(String parentID) { this.parentProjectId = parentID; }
    //endregion


    /**
     * Create a new Calendar object
     */
    public Calendar() {

        parentProjectId = "";
    }

    /**
     * Add a new meeting to the group's calendar
     * @param meetingId The id of the meeting (firebase key)
     * @param isActive Whether or not the meeting is to be displayed
     * @return true if the meeting has been added to the calendar
     */
    public boolean addMeeting(String meetingId, Boolean isActive) {
        if (hasMeeting(meetingId)) {
            return false;
        }

        meetings.put(meetingId, isActive);
        return true;
    }

    /**
     * Remove a meeting from the calendar
     * @param meetingId The id of the meeting to remove (firebase key)
     * @return true if the meeting was successfully removed. False otherwise
     */
    public boolean removeMeeting(String meetingId) {
        if (!hasMeeting(meetingId)) {
            return false;
        }

        meetings.remove(meetingId);
        return true;
    }

    /**
     * Check if the meeting is in the calendar
     * @param meetingId the id of the meeting to check
     * @return true if the meeting is in the calendar. False otherwise.
     */
    public boolean hasMeeting(String meetingId) {

        return meetings.containsKey(meetingId);
    }
}



