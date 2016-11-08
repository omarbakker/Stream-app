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
    private int day;
    private int year;

    private int hour;
    private int minute;

    private String dayOfWeek;

    //Getters

    public String getCalendarId()
    {
        return calendarId;
    }
    public String getId()
    {
        return id;
    }
    public String getName() { return name;}
    public String getLocation() { return location; }
    public String getDescription() {return description; }


//    public String getMonth() { return month; }
//
//    public int getDay() { return day; }
//
//    public int getYear() { return year; }
//
//    public String getDayOfWeek() { return dayOfWeek; }
//
//    public Month getMonthAsEnum() {
//        return Month.valueOf(month);
//    }
//
//    public DayOfWeek getDayOfWeekAsEnum()
//    {
//        return DayOfWeek.valueOf(dayOfWeek);
//    }
//
//    public int getHour()
//    {
//        return hour;
//    }
//
//    public int getMinute()
//    {
//        return minute;
//    }

    //Setters

    public void setId(String id)
    {
        this.id = id;
    }

    public void setCalendarId(String calendarId)
    {
        this.calendarId = calendarId;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

//    public void setDate(Month month, int day, int year, DayOfWeek dayOfWeek)
//    {
//        this.month = month.toString();
//        this.day = day;
//        this.year = year;
//        this.dayOfWeek = dayOfWeek.toString();
//
//        if(this.day < 1)
//        {
//            this.day = 1;
//        }
//        else if(day > maxDaysInMonth(month, year))
//        {
//            this.day = maxDaysInMonth(month, year);
//        }
//    }

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
//    public void setTime(int hour, int minute)
//    {
//        int numAdditionalHours = 0;
//
//        if(minute >= 60)
//        {
//            numAdditionalHours = minute/60;
//            minute = minute%60;
//        }
//
//        hour = (hour + numAdditionalHours)%24; //maximum 24 hours
//
//        this.hour = hour;
//        this.minute = minute;
//    }


}

