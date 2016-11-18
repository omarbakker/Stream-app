package com.test.stream.stream.UIFragments;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.test.stream.stream.Controllers.HomeManager;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;
import com.test.stream.stream.Utilities.TaskAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProjectHomeFragment extends Fragment {

    private DonutProgress teamProgress;
    private DonutProgress userProgress;

    private static final String TAG = "Home Fragment";

    ArrayList<Task> taskMessages = new ArrayList<>();
    private TaskAdapter taskAdapter;

    private HomeManager homeManager;

    private DataEventListener dataListener = new DataEventListener() {
        @Override
        public void onDataChanged() {
            updateUI();
        }
    };

    public ProjectHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        teamProgress = (DonutProgress) getView().findViewById(R.id.team_progress);
        userProgress = (DonutProgress) getView().findViewById(R.id.user_progress);

        updateProgressBar(teamProgress, 90);
        updateProgressBar(userProgress, 60);
        homeManager = new HomeManager(dataListener);
    }

    /**
     * Takes in a certain circle progress, then sets progress to new progress.
     *
     * @param progress    CircleProgress to be modified.
     * @param newProgress what progress will be updated to.
     */
    private void updateProgressBar(DonutProgress progress, int newProgress) {
        progress.setProgress(0);           // initialize to 0
        progress.setMax(100);              // set max to 100
        progress.setProgress(newProgress); // update progress
    }

    private void updateTeamProgress(int newProgress){
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.progress_anim);
        set.setTarget();
        ObjectAnimator anim = ObjectAnimator.ofInt(R.id.team_progress, "circle_progress", 0, 100);
    }

    /**
     * Function that updates the Adapter of the ListFragment
     */
    public void updateUI() {

        // Get all user tasks from the database
        List<Task> allTasks = homeManager.getUserTasks();

        ArrayList<Task> tasks = new ArrayList();
        ListView listView = (ListView) getView().findViewById(R.id.task_list);

        // Go through each task in database
        for (Task currentTask : allTasks) {
            if (currentTask.getClass() == Task.class) {
                tasks.add(currentTask);
            }
        }
        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(getActivity(), taskMessages);
            listView.setAdapter(taskAdapter);
        }
        taskAdapter.clear();
        taskAdapter.addAll(tasks);
        taskAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(listView);
    }

    /**
     * Creates a task with pre-set values
     *
     * @return Task
     */
    private Task createTask() {
        Task newTask = new Task();

        newTask.setDueDay(10);
        newTask.setDueMonth(10);
        newTask.setDueYear(1990);

        newTask.setDescription("Task Description");
        newTask.setName("Task name");
        return newTask;
    }

    /****
     * Method for Setting the Height of the ListView dynamically.
     * *** Hack to fix the issue of not showing all the items of the ListView
     * *** when placed inside a ScrollView
     ****/
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        System.out.println("AdapterCount: " + listAdapter.getCount());
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
