package com.test.stream.stream.Objects.Tasks;

/**
 * Created by janemacgillivray on 2016-11-16.
 */

public class miniTask {
    private String Task_name;

    public miniTask(String task_name, int due_till_due) {
        Task_name = task_name;
        this.due_till_due = due_till_due;
    }

    private int due_till_due;

    public String getTask_name() {
        return Task_name;
    }

    public void setTask_name(String task_name) {
        Task_name = task_name;
    }

    public int getDue_till_due() {
        return due_till_due;
    }

    public void setDue_till_due(int due_till_due) {
        this.due_till_due = due_till_due;
    }
}
