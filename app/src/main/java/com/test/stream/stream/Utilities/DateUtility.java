package com.test.stream.stream.Utilities;

import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.Task;

import org.joda.time.DateTime;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by omarBakker on 2016-11-29.
 */

public class DateUtility {

    public static void sortTaskObjectsByDueDate(List<Object> array){
        Collections.sort(array, new taskObjectDateSort());
    }

    public static void sortTasksByDueDate(List<Task> array){
        Collections.sort(array, new taskDateSort());
    }

    public static void sortMeetingsByDueDate(List<Meeting> array){
        Collections.sort(array, new meetingDateSort());
    }

    public static void sortProjectsByDueDate(List<Project> array){
        Collections.sort(array, new projectDateSort());
    }


    /**
     * Returns an int array of format  {year,month,day}
     * @param year
     * @param month
     * @param day
     * @return
     */
    private static int[] getDateArray(int year,int month, int day){
        int[] date = {year,month,day};
        return date;
    }

    /**
     * @param date
     * @return
     * True if date is later than now, depends on dateIsLater(...) and uses the same date format
     */
    public static boolean isPastDue(int[] date){

        int[] nowDateArray = {0,0,0};
        DateTime now = DateTime.now();
        nowDateArray[0] = now.getYear();
        nowDateArray[1] = now.getMonthOfYear();
        nowDateArray[2] = now.getDayOfMonth();
        return dateIsLater(nowDateArray,date);
    }

    /**
     * Returns true if thisDate is later or equal to thatDate
     * @param thisDate
     * size must equal 3, index 0 is year, 1 is month, 2 is day.
     * @param thatDate
     * size must equal 3, index 0 is year, 1 is month, 2 is day.
     * @return
     * True if thisDate is later or equal to thatDate
     */
    public static boolean dateIsLater(int[] thisDate, int[] thatDate){

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

        return !(thisDate[2] < thatDate[2]);
    }

    private static class projectDateSort implements Comparator<Project>{
        public int compare(Project s1, Project s2){
            StreamDate d1 = new StreamDate();
            d1.date = getDateArray(s1.getDueYear(),s1.getDueMonth(), s1.getDueDay());
            StreamDate d2 = new StreamDate();
            d2.date = getDateArray(s2.getDueYear(),s2.getDueMonth(),s2.getDueDay());

            if (dateIsLater(d1.date,d2.date)){
                return 1;
            }else{
                return -1;
            }
        }
    }

    private static class meetingDateSort implements Comparator<Meeting>{
        public int compare(Meeting s1, Meeting s2){
            StreamDate d1 = new StreamDate();
            d1.date = getDateArray(s1.getYear(),s1.getNumberMonth(), s1.getDay());
            StreamDate d2 = new StreamDate();
            d2.date = getDateArray(s2.getYear(),s2.getNumberMonth(),s2.getDay());

            if (dateIsLater(d1.date,d2.date)){
                return 1;
            }else{
                return -1;
            }
        }
    }

    private static class taskDateSort implements Comparator<Task>{
        public int compare(Task s1, Task s2){
            StreamDate d1 = new StreamDate();
            d1.date = getDateArray(s1.getDueYear(),s1.getDueMonth(), s1.getDueDay());
            StreamDate d2 = new StreamDate();
            d2.date = getDateArray(s2.getDueYear(),s2.getDueMonth(),s2.getDueDay());

            if (dateIsLater(d1.date,d2.date)){
                return 1;
            }else{
                return -1;
            }
        }
    }

    private static class taskObjectDateSort implements Comparator<Object>{
        public int compare(Object s1, Object s2){
            Task task1 = (Task)s1;
            Task task2 = (Task)s2;
            StreamDate d1 = new StreamDate();
            d1.date = getDateArray(task1.getDueYear(),task1.getDueMonth(), task1.getDueDay());
            StreamDate d2 = new StreamDate();
            d2.date = getDateArray(task2.getDueYear(),task2.getDueMonth(),task2.getDueDay());

            if (dateIsLater(d1.date,d2.date)){
                return 1;
            }else{
                return -1;
            }
        }
    }

    private static class StreamDate{
        int[] date;
    }
}
