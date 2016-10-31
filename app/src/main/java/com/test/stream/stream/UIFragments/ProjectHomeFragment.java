package com.test.stream.stream.UIFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Tasks.TaskMessage;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectHomeFragment extends Fragment {

    private CircleProgress teamProgress;
    private CircleProgress userProgress;

    private static final String TAG = "Home Fragment";

    ArrayList<TaskMessage> taskMessages = new ArrayList<>();
    private TaskAdapter taskAdapter;

    public ProjectHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("OnCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_home, container, false);
        System.out.println("After view fetch");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        View view = getView();
        System.out.println("On Activity Created");

        teamProgress = (CircleProgress) view.findViewById(R.id.team_progress);
        userProgress = (CircleProgress) view.findViewById(R.id.user_progress);

        System.out.println("TeamProgress:" + teamProgress.getProgress());

        updateProgressBar(teamProgress, 100);

        System.out.println("TeamProgress:" + teamProgress.getProgress());

        updateProgressBar(userProgress, 90);
    }

    private void updateProgressBar(CircleProgress progress, int newProgress){
        progress.setProgress(0);           // initialize to 0
        progress.setMax(100);              // set max to 100
        progress.setProgress(newProgress); // update progress
    }

    // Function that updates the Adapter of the ListFragment
    public void updateUI() {
        // Get all user tasks from the database
        List<Task> allTasks = ProjectManager.sharedInstance().fetchCurrentUserProjects();
        ArrayList<TaskMessage> tasks = new ArrayList();
        ListView listView = (ListView) getView().findViewById(R.id.task_list);
        // Go through each task in database
        for (Task currentTask : allTasks) {
            if (currentTask.getClass() == TaskMessage.class) {
                TaskMessage currentMessage = (TaskMessage) currentTask;
                tasks.add(currentMessage);
            }
        }
        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(getActivity(), taskMessages);
            listView.setAdapter(taskAdapter);
        } else {
            taskAdapter.clear();
            taskAdapter.addAll(tasks);
            taskAdapter.notifyDataSetChanged();
        }
    }
}
