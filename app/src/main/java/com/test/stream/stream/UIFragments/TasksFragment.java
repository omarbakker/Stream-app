package com.test.stream.stream.UIFragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TasksFragment extends Fragment {

    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;
    EditText taskDateField;
    private int current_task;
    int[] DueDate = {0,0,0};
    ArrayList<Task> tasks = new ArrayList<>();
    private static final String TAG = TaskMain.class.getSimpleName();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mTaskListView = (ListView) getView().findViewById(R.id.list_task);
        Typeface Syncopate = Typeface.createFromAsset(getActivity().getAssets(), "Syncopate-Regular.ttf");
        final FloatingActionButton addTaskButton = (FloatingActionButton) getView().findViewById(R.id.create_new_task);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTask();
            }
        });

        TaskManager.getInstance().Initialize(this);
    }

    public void createTask() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_newtask, null);

        AlertDialog hi = new AlertDialog.Builder(getActivity())

                .setView(v)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Project currentProject = ProjectManager.sharedInstance().getCurrentProject();
                        taskDateField = (EditText) v.findViewById(R.id.newTaskDueDateField);
                        EditText input_name = (EditText) v.findViewById(R.id.task_name);
                        EditText description = (EditText) v.findViewById(R.id.description);

                        EditText user = (EditText) v.findViewById(R.id.user);
                        if(!getValidDate(taskDateField.getText().toString()))
                            handleInvalidDate();

                        User currentUser = UserManager.getInstance().getCurrentUser();

                        if(user != null)
                        {
                            TaskManager.getInstance().CreateTask(input_name.getText().toString(), description.getText().toString(), currentUser, DueDate, false);
                        }
                       // TaskManager.getInstance().CreateTask(input_name.getText().toString(), description.getText().toString(), user.getText().toString(), DueDate, false);
                        currentProject.setNumberOfActiveTasks(currentProject.getNumberOfActiveTasks()+1);
                        DatabaseManager.getInstance().updateObject(DatabaseFolders.Projects,currentProject.getId(),currentProject);
                    }
                }).setNegativeButton("Cancel", null)
                .create();
        hi.show();
    }


    public void updateUI() {
        List<Task> tasks = TaskManager.getInstance().GetTasksInProject();
        ArrayList<String> taskList = new ArrayList<>();
        int i = tasks.size() - 1;
        Log.d(TAG, String.valueOf(i));
        while (i >= 0) {
            Task task = tasks.get(i);
            taskList.add(task.getName());
            i--;
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.task_small,
                    R.id.task_name,
                    taskList);

            mTaskListView.setAdapter(mAdapter);

        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

    }

    public void expandTaskView(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_name);
        String taskName = String.valueOf(taskTextView.getText());
        Intent intent = new Intent(getActivity(), expand_task.class);
        intent.putExtra("taskName", taskName);
        startActivity(intent);
    }

    public void sortArraybyComplete(ArrayList<Task> tasks){
        for(int i = 0; i < tasks.size()-1; i++){
            if(tasks.get(i).getComplete()==true){
                Task task = tasks.get(i);
                tasks.set(i, tasks.get(i+1));
                tasks.set(tasks.size()-1, task);
            }
        }
    }

    private void handleInvalidDate(){
        taskDateField.setText(R.string.new_project_prompt_date);
        taskDateField.selectAll();
    }

    private boolean getValidDate(String date){
        String[] vals = date.split("/");
        if (vals.length != 3)
            return false;
        for (int i = 0; i < vals.length; i++){
            try{
                DueDate[i] = Integer.parseInt(vals[i]);
            }catch (NumberFormatException e){
                return false;
            }
        }
        return true;
    }

}
