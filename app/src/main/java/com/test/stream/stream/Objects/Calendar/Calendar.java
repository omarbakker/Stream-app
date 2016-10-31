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

    //region Constructors
    public Calendar() {
        parentProjectId = "";
    }
    //endregion

    //region Core Functions
    public boolean addMeeting(String meetingId, Boolean isActive) {
        if (hasMeeting(meetingId)) {
            return false;
        }

        meetings.put(meetingId, isActive);
        return true;
    }

    public boolean removeMeeting(String meetingId) {
        if (!hasMeeting(meetingId)) {
            return false;
        }

        meetings.remove(meetingId);
        return true;
    }

    public boolean hasMeeting(String meetingId) {
        return meetings.containsKey(meetingId);
    }
}

//endregion

