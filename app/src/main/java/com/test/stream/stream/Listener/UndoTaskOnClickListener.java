package com.test.stream.stream.Listener;

import android.view.View;

import com.test.stream.stream.Controllers.HomeManager;
import com.test.stream.stream.Objects.Tasks.Task;

/**
 * Created by robyn on 2016-11-27.
 */

public class UndoTaskOnClickListener implements View.OnClickListener {
    private final Task taskToRead;
    private final HomeManager homeManager;

    public UndoTaskOnClickListener(Task task, HomeManager homeManager){
        this.taskToRead = task;
        this.homeManager = homeManager;
    }

    public Task getTask(){
        return taskToRead;
    }

    public HomeManager getHomeManager(){
        return homeManager;
    }

    @Override
    public void onClick(View v) {

    }
}
