package com.test.stream.stream.Objects.Calendar;

import com.test.stream.stream.Utilities.DayOfWeek;
import com.test.stream.stream.Utilities.Month;
import com.test.stream.stream.Utilities.PinType;

/**
 * Created by cathe on 2016-10-29.
 */

public class Meeting {
    private String id;
    private String calendarId;

    private String name;
    private String location;
    private String description;

    //Date and time
    private String month;
    private int numberMonth;
    private int day;
    private int year;
    private String dayOfWeek;

    private int hour;
    private int minute;
    private String am_pm;



    //Getters

    /**
     * gets the Calendar ID
     * @return Calendar ID
     */
    public String getCalendarId()
    {
        return calendarId;
    }

    /**
     * gets the project ID
     * @return project ID
     */
    public String getId()
    {
        return id;
    }

    /**
     * gets the Meeting name
     * @return meeting name
     */
    public String getName() { return name;}

    /**
     * Gets the location of the meeting
     * @return meeting location
     */
    public String getLocation() { return location; }

    /**
     * Gets the description of the meeting
     * @return meeting description
     */
    public String getDescription() {return description; }


    public String getMonth() { return month; }

    public int getNumberMonth() { return numberMonth; }

    public int getDay() { return day; }

    public int getYear() { return year; }

    public String getDayOfWeek() { return dayOfWeek; }

//    public Month getMonthAsEnum() {
//        return Month.valueOf(month);
//    }
//
//    public DayOfWeek getDayOfWeekAsEnum()
//    {
//        return DayOfWeek.valueOf(dayOfWeek);
//    }
//
    public int getHour()
    {
        return hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public String getAmPm()
    {
        return am_pm;
    }

    //Setters

    /**
     * Sets the ID
     * @param id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Sets the calendarID
     * @param calendarId
     */
    public void setCalendarId(String calendarId)
    {
        this.calendarId = calendarId;
    }

    /**
     * Sets the name of the meeting
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the location of the meeting
     * @param location
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * Sets the description of the meeting
     * @param description
     */
    public void setDescription (String description)
    {
        this.description = description;
    }

    public void setDate(String month, int day, int year, int numberMonth, String dayOfWeek)
    {
        this.month = month;
        this.day = day;
        this.year = year;
        this.numberMonth = numberMonth;
        this.dayOfWeek = dayOfWeek;

//        if(this.day < 1)
//        {
//            this.day = 1;
//        }
//        else if(day > maxDaysInMonth(month, year))
//        {
//            this.day = maxDaysInMonth(month, year);
//        }
    }

//    private int maxDaysInMonth(Month month, int year)
//    {
//        if(month == Month.April ||
//                month == Month.June ||
//                month == Month.September ||
//                month == Month.November)
//        {
//            return 30;
//        }
//
//        if(month == Month.February)
//        {
//            return isLeapYear(year)? 29 : 28;
//        }
//
//        return 31;
//    }
//
//    private boolean isLeapYear(int year)
//    {
//        if(year%400 == 0)
//        {
//            return true;
//        }
//        else if(year%4 == 0 && year%100 != 0)
//        {
//            return true;
//        }
//
//        return false;
//    }
//
    public void setTime(int hour, int minute)
    {
        int numAdditionalHours = 0;

        if(minute >= 60)
        {
            numAdditionalHours = minute/60;
            minute = minute%60;
        }

        hour = (hour + numAdditionalHours)%24; //maximum 24 hours

        this.hour = hour;
        this.minute = minute;
    }

    public void setAmPm(String ampm)
    {
        this.am_pm = ampm;
    }


}

