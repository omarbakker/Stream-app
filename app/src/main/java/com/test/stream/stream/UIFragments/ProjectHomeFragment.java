package com.test.stream.stream.UIFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.TaskAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectHomeFragment extends Fragment {

    private CircleProgress teamProgress;
    private CircleProgress userProgress;

    private static final String TAG = "Home Fragment";

    ArrayList<Task> taskMessages = new ArrayList<>();
    private TaskAdapter taskAdapter;

    public ProjectHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        View view = getView();
        System.out.println("On Activity Created");

        teamProgress = (CircleProgress) view.findViewById(R.id.team_progress);
        userProgress = (CircleProgress) view.findViewById(R.id.user_progress);

        System.out.println("TeamProgress:" + teamProgress.getProgress());

        updateProgressBar(teamProgress, 100);

        System.out.println("TeamProgress:" + teamProgress.getProgress());

        updateProgressBar(userProgress, 90);
        updateUI();
    }

    private void updateProgressBar(CircleProgress progress, int newProgress) {
        progress.setProgress(0);           // initialize to 0
        progress.setMax(100);              // set max to 100
        progress.setProgress(newProgress); // update progress
    }

    // Function that updates the Adapter of the ListFragment
    public void updateUI() {
        System.out.println("UPDATING TASKS UI");

        // Get all user tasks from the database
        List<Task> allTasks = new LinkedList<>();

        allTasks.add(createTask());
        allTasks.add(createTask());
        allTasks.add(createTask());

        ArrayList<Task> tasks = new ArrayList();
        ListView listView = (ListView) getView().findViewById(R.id.task_list);

        // Go through each task in database
        for (Task currentTask : allTasks) {
            if (currentTask.getClass() == Task.class) {
                System.out.println("Retrieving tasks");
                Task currentMessage = (Task) currentTask;
                tasks.add(currentMessage);
            }
        }
        if (taskAdapter == null) {
            System.out.println("taskAdapter == null");
            taskAdapter = new TaskAdapter(getActivity(), taskMessages);
            listView.setAdapter(taskAdapter);
        }
        taskAdapter.clear();
        taskAdapter.addAll(tasks);
        taskAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(listView);
    }

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
            System.out.println("LAYOUTADAPTER NULL");
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
            System.out.println("Total Height:" + totalHeight);
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
