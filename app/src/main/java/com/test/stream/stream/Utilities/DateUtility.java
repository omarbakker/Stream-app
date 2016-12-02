package com.test.stream.stream.Utilities;

import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.Task;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by omarBakker on 2016-11-29.
 * A utility class containing helper functions for handling dates
 */

public class DateUtility {

    private static final int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /**
     * Given a list of type Object, where each object is an instance od type Task.
     * Sorts the list in ascending order by duedate
     *
     * @param array
     */
    public static void sortTaskObjectsByDueDate(List<Object> array) {
        Collections.sort(array, new taskObjectDateSort());
    }

    /**
     * Given a list of type Task
     * Sorts the list in ascending order by duedate
     *
     * @param array
     */
    public static void sortTasksByDueDate(List<Task> array) {
        Collections.sort(array, new taskDateSort());
    }

    /**
     * Given a list of type Meeting
     * Sorts the list in ascending order by duedate
     *
     * @param array
     */
    public static void sortMeetingsByDueDate(List<Meeting> array) {
        Collections.sort(array, new meetingDateSort());
    }

    /**
     * Given a list of type Meeting
     * Sorts the list in descending order by duedate
     *
     * @param array
     */
    public static void sortMeetingsByDueDateReverse(List<Meeting> array) {
        Collections.sort(array, new meetingDateSortReverse());
    }

    /**
     * Given a list of type Object, where each object is an instance od type Task.
     * Sorts the list in ascending order by duedate
     *
     * @param array
     */
    public static void sortProjectsByDueDate(List<Project> array) {
        Collections.sort(array, new projectDateSort());
    }


    /**
     * Returns an int array of format  {year,month,day}
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    private static int[] getDateArray(int year, int month, int day) {
        int[] date = {year, month, day};
        return date;
    }

    /**
     * Returns an int array of format  {year,month,day}
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    private static int[] getMeetingDateArray(int year, int month, int day, int am_pm, int hour, int minutes) {
        int[] date = {year, month, day, am_pm, hour, minutes};
        return date;
    }

    /**
     * @param date
     * @return True if date is later than now, depends on dateIsLater(...) and uses the same date format
     */
    public static boolean isPastDue(int[] date) {

        int[] nowDateArray = {0, 0, 0};
        DateTime now = DateTime.now();
        nowDateArray[0] = now.getYear();
        nowDateArray[1] = now.getMonthOfYear();
        nowDateArray[2] = now.getDayOfMonth();

        return dateIsLater(nowDateArray, date);
    }

    /**
     * Returns the amount of days from today to the dueDate
     *
     * @param dueDate size must equal 3, index 0 is year, 1 is month, 2 is day.
     * @return long
     */
    public static long getDaysToDueDate(int[] dueDate) {
        long diffDays = 0;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        // Get the current time
        int[] nowDateArray = {0, 0, 0};
        DateTime now = DateTime.now();
        nowDateArray[0] = now.getYear();
        nowDateArray[1] = now.getMonthOfYear();
        nowDateArray[2] = now.getDayOfMonth();

        // Get the time difference of the dueDate to the current time in days
        try {
            Date d1 = format.parse(convertToSimpleFormat(nowDateArray));
            Date d2 = format.parse(convertToSimpleFormat(dueDate));

            long diff = d2.getTime() - d1.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return diffDays;
    }

    /**
     * Converts dateToConvert into a string which is in simple format.
     *
     * @param dateToConvert size must equal 3, index 0 is year, 1 is month, 2 is day.
     * @return dateToConvert in simpleFormat
     */
    private static String convertToSimpleFormat(int[] dateToConvert) {
        String simpleFormatDate = new String(dateToConvert[1] + "/" + dateToConvert[2] + "/" + dateToConvert[0]
                + " 00:00:00");
        System.out.println(simpleFormatDate);
        return simpleFormatDate;
    }

    /**
     * Returns true if thisDate is later or equal to thatDate
     *
     * @param thisDate size must equal 3, index 0 is year, 1 is month, 2 is day.
     * @param thatDate size must equal 3, index 0 is year, 1 is month, 2 is day.
     * @return True if thisDate is later or equal to thatDate
     */
    public static boolean dateIsLater(int[] thisDate, int[] thatDate) {

        if (thisDate[0] > thatDate[0])
            return true;

        if (thisDate[0] < thatDate[0])
            return false;

        // years are equal
        // compare months
        if (thisDate[1] > thatDate[1])
            return true;

        if (thisDate[1] < thatDate[1])
            return false;

        // months are equal
        // compare days
        if (thisDate[2] > thatDate[2])
            return true;

        return !(thisDate[2] <= thatDate[2]);
    }

    /**
     * Returns true if thisDate is later or equal to thatDate
     *
     * @param thisDate size must equal 6, index 0 is year, 1 is month, 2 is day, 3 is (am = 0) or (pm = 1),
     *                 4 is hours, 5 is minutes
     * @param thatDate same format as thisDate
     * @return True if thisDate is later or equal to thatDate
     */
    public static boolean meetingDateIsLater(int[] thisDate, int[] thatDate) {
        if (dateIsLater(thisDate, thatDate)) {
            return true;
        } else if (dateIsLater(thatDate, thisDate))
                return false;
        else {
            // compare am/pm
            if (thisDate[3] > thatDate[3]) {
                return true;
            }

            if (thisDate[3] < thatDate[3]) {
                return false;
            }


            if (thisDate[4] > thatDate[4]) {

                return true;
            }

            if (thisDate[4] < thatDate[4]) {
                return false;
            }

            if (thisDate[5] > thatDate[5]) {
                return true;
            }

            return !(thisDate[5] < thatDate[5]);

        }
    }

    /**
     * Pass a new projectDateSort() as a comparator to the collections.sort to sort by duedate for projects
     */
    private static class projectDateSort implements Comparator<Project> {
        public int compare(Project s1, Project s2) {
            StreamDate d1 = new StreamDate();
            d1.date = getDateArray(s1.getDueYear(), s1.getDueMonth(), s1.getDueDay());
            StreamDate d2 = new StreamDate();
            d2.date = getDateArray(s2.getDueYear(), s2.getDueMonth(), s2.getDueDay());

            if (dateIsLater(d1.date, d2.date)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * Pass a new meetingDateSort() as a comparator to the collections.sort to sort by duedate for meetings
     */
    private static class meetingDateSort implements Comparator<Meeting> {
        public int compare(Meeting s1, Meeting s2) {
            StreamDate d1 = new StreamDate();
            int am_pm1 = s1.getAmPm().equalsIgnoreCase("AM") ? 0 : 1;
            d1.date = getMeetingDateArray(s1.getYear(), s1.getNumberMonth(), s1.getDay(), am_pm1, s1.getHour(), s1.getMinute());
            int am_pm2 = s2.getAmPm().equalsIgnoreCase("AM") ? 0 : 1;
            StreamDate d2 = new StreamDate();
            d2.date = getMeetingDateArray(s2.getYear(), s2.getNumberMonth(), s2.getDay(), am_pm2, s2.getHour(), s2.getMinute());

            if (meetingDateIsLater(d1.date, d2.date)) {
                return 1;
            } else {
                return -1;
            }
        }
    }


    /**
     * Pass a new meetingDateSort() as a comparator to the collections.sort to sort in reverse chronological order by duedate for meetings
     */
    private static class meetingDateSortReverse implements Comparator<Meeting> {
        public int compare(Meeting s1, Meeting s2) {
            StreamDate d1 = new StreamDate();
            int am_pm1 = s1.getAmPm().equalsIgnoreCase("AM") ? 0 : 1;
            d1.date = getMeetingDateArray(s1.getYear(), s1.getNumberMonth(), s1.getDay(), am_pm1, s1.getHour(), s1.getMinute());
            int am_pm2 = s2.getAmPm().equalsIgnoreCase("AM") ? 0 : 1;
            StreamDate d2 = new StreamDate();
            d2.date = getMeetingDateArray(s2.getYear(), s2.getNumberMonth(), s2.getDay(), am_pm2, s2.getHour(), s2.getMinute());

            if (meetingDateIsLater(d1.date, d2.date)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    /**
     * Pass a new taskDateSort() as a comparator to the collections.sort to sort by duedate for tasks
     */
    private static class taskDateSort implements Comparator<Task> {
        public int compare(Task s1, Task s2) {
            StreamDate d1 = new StreamDate();
            d1.date = getDateArray(s1.getDueYear(), s1.getDueMonth(), s1.getDueDay());
            StreamDate d2 = new StreamDate();
            d2.date = getDateArray(s2.getDueYear(), s2.getDueMonth(), s2.getDueDay());

            if (dateIsLater(d1.date, d2.date)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * Pass a new taskObjectDateSort() as a comparator to the collections.sort to sort by duedate
     * for Objects that are all instances of task
     */
    private static class taskObjectDateSort implements Comparator<Object> {
        public int compare(Object s1, Object s2) {
            Task task1 = (Task) s1;
            Task task2 = (Task) s2;
            StreamDate d1 = new StreamDate();
            d1.date = getDateArray(task1.getDueYear(), task1.getDueMonth(), task1.getDueDay());
            StreamDate d2 = new StreamDate();
            d2.date = getDateArray(task2.getDueYear(), task2.getDueMonth(), task2.getDueDay());

            if (dateIsLater(d1.date, d2.date)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * An enclosing class for the date array.
     */
    private static class StreamDate {
        int[] date;
    }
}
