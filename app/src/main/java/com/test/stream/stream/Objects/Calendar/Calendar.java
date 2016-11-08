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
    public void setParentProjectId(String parentID) { this.parentProjectId = parentID; }

    public Map<String, Boolean> getMeetings() {
        return meetings;
    }

    //endregion

    /**
     * Constructor for Calendar object
     */
    public Calendar() {
        parentProjectId = "";
    }
    //endregion

    /**
     * Adds a meeting to a Calendar object
     * @param meetingId ID of the meeting that needs to be added
     * @param isActive flag to see that the meeting is still valid and hasn't yet occurred
     * @return boolean that the meeting was added in correctly
     */
    public boolean addMeeting(String meetingId, Boolean isActive) {
        if (hasMeeting(meetingId)) {
            return false;
        }

        meetings.put(meetingId, isActive);
        return true;
    }

    /**
     * Removes the meeting
     * @param meetingId ID of the meeting that needs to be deleted
     * @return flag indicating that the meeting was deleted
     */
    public boolean removeMeeting(String meetingId) {
        if (!hasMeeting(meetingId)) {
            return false;
        }

        meetings.remove(meetingId);
        return true;
    }

    /**
     * Checks to see if a meeting already exists
     * @param meetingId ID of the meeting that is being searched for
     * @return flag that indicates whether or not that meeting was found
     */
    public boolean hasMeeting(String meetingId) {
        return meetings.containsKey(meetingId);
    }
}

//endregion

