package com.test.stream.stream.Objects.Tasks;
import java.io.Serializable;
import java.util.List;

/**
 * Created by janemacgillivray on 2016-10-02.
 */

public class Task implements Serializable{
    String TASK_NAME = "task name";
    int TASK_DUE_DATE_DAY = 0;
    int TASK_DUE_DATE_MONTH = 0;
    int TASK_DUE_DATE_YEAR = 0;
    String TASK_DESCRIPTION = "description of task";
    String TASK_USER = "user assigned to task";
    int COMPLETE = 0;

    public Task( ) {
        this.TASK_NAME = TASK_NAME;
        this.TASK_DUE_DATE_MONTH = TASK_DUE_DATE_MONTH;
        this.TASK_DUE_DATE_DAY = TASK_DUE_DATE_DAY;
        this.TASK_DUE_DATE_YEAR = TASK_DUE_DATE_YEAR;
        this.TASK_DESCRIPTION = TASK_DESCRIPTION;
        this.TASK_USER = TASK_USER;
        this.COMPLETE = COMPLETE;
    }

    public String getTASK_NAME() {
        return TASK_NAME;
    }

    public int[] getTASK_DUE_DATE() {
        int[] due_date = new int[3];
        due_date[0] = TASK_DUE_DATE_DAY;
        due_date[1] = TASK_DUE_DATE_MONTH;
        due_date[2] = TASK_DUE_DATE_YEAR;
        return due_date;
    }

    public String getTASK_DESCRIPTION() {
        return TASK_DESCRIPTION;
    }

    public String getTASK_USER() {
        return TASK_USER;
    }

    public int getCOMPLETE(){
        return COMPLETE;
    }

    public void setTASK_NAME(String TASK_NAME) {
        this.TASK_NAME = TASK_NAME;
    }

    public void setTASK_DUE_DATE(int[] due_date){
        this.TASK_DUE_DATE_DAY = due_date[0];
        this.TASK_DUE_DATE_MONTH = due_date[1];
        this.TASK_DUE_DATE_YEAR = due_date[2];
    }


    public void setTASK_DESCRIPTION(String TASK_DESCRIPTION) {
        this.TASK_DESCRIPTION = TASK_DESCRIPTION;
    }

    public void setTASK_USER(String TASK_USER) {
        this.TASK_USER = TASK_USER;
    }

    public void setCOMPLETE(int COMPLETE){
        this.COMPLETE = COMPLETE;
    }
}

