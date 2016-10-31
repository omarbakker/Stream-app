package com.test.stream.stream.UIFragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TasksFragment extends Fragment implements View.OnClickListener, EditText.OnEditorActionListener{

    private AlertDialog newTaskDialog;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    TextInputEditText newtaskDateField;
    TextInputEditText newTaskNameField;
    TextInputEditText newTaskDescriptionField;
    TextInputEditText newTaskAssigneeField;
    User newTaskAssignee;

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
        final FloatingActionButton addTaskButton = (FloatingActionButton) getView().findViewById(R.id.create_new_task);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewTaskDialog();
            }
        });

        TaskManager.getInstance().Initialize(this);
    }

    public void showNewTaskDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_newtask, null);

        newTaskDialog = new AlertDialog.Builder(getActivity()).setView(v).create();
        TextView title = (TextView) v.findViewById(R.id.newTaskPageTitle);
        Typeface Syncopate = Typeface.createFromAsset(getActivity().getAssets(), "Syncopate-Bold.ttf");
        title.setTypeface(Syncopate);
        Button done = (Button) v.findViewById(R.id.doneAddingTask);
        Button cancel = (Button) v.findViewById(R.id.CancelAddingTask);
        Button addUser = (Button) v.findViewById(R.id.newTaskAddUserButton);
        newTaskAssigneeField = (TextInputEditText)v.findViewById(R.id.newTaskNewUserField);
        newtaskDateField = (TextInputEditText)v.findViewById(R.id.newTaskDueDateField);
        newTaskNameField = (TextInputEditText)v.findViewById(R.id.newTaskNameField);
        newTaskDescriptionField = (TextInputEditText)v.findViewById(R.id.newTaskDescriptionField);
        newTaskAssigneeField.setOnEditorActionListener(this);
        newtaskDateField.setOnEditorActionListener(this);
        newTaskNameField.setOnEditorActionListener(this);
        newTaskDescriptionField.setOnEditorActionListener(this);
        addUser.setOnClickListener(this);
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        newTaskDialog.show();
    }


    public void updateUI() {
        List<Task> tasks = TaskManager.getInstance().GetTasksInProject();
        ArrayList<String> taskList = new ArrayList<>();
        Project currentProject = ProjectManager.sharedInstance().getCurrentProject();
        int i = tasks.size() - 1;
        Log.d(TAG, String.valueOf(i));
        while (i >= 0) {
            Task task = tasks.get(i);
            taskList.add(task.getName());
            Log.d(TAG, currentProject.getName());
            Log.d("Just added new task", "Just added new task");
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
        newtaskDateField.setText(R.string.new_project_prompt_date);
        newtaskDateField.selectAll();
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

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.doneAddingTask:
                createTask();
                newTaskDialog.dismiss();
                break;
            case R.id.CancelAddingTask:
                newTaskDialog.dismiss();
                break;
            case R.id.newTaskAddUserButton:
                handleEnteredUser(newTaskAssigneeField.getText().toString());
                break;
            default:
                break;
        }

    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        switch (v.getId()){

            case R.id.newTaskNameField:
                break;

            case R.id.newTaskDescriptionField:
                break;

            case R.id.newTaskDueDateField:
                break;

            case R.id.newTaskNewUserField:
                handleEnteredUser(newTaskAssigneeField.getText().toString());
                hideKeyboard();
                break;

            default:
                break;
        }
        return true;
    }



    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)newTaskAssigneeField.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(newTaskAssigneeField.getWindowToken(),0);
    }



    private void createTask(){
        final String name = newTaskNameField.getText().toString();
        final String description = newTaskDescriptionField.getText().toString();

        if (name.isEmpty()){
            newTaskNameField.setText("Please Enter a name");
            return;
        }

        if (description.isEmpty()){
            newTaskDescriptionField.setText("Please Enter a Description");
            return;
        }

        if(!getValidDate(newtaskDateField.getText().toString())) {
            handleInvalidDate();
            return;
        }

        if(newTaskAssignee == null) {
            newTaskAssigneeField.setText("Please assign a user");
            newTaskAssigneeField.selectAll();
            return;
        }

        TaskManager.getInstance().CreateTask(name, description, newTaskAssignee, DueDate, false);
    }



    /**
     * Checks if the string passed represents a valid user in the database.
     * Updates UI accordingly.
     * Updates users list accordingly.
     * @param uDescription
     * The description entered by the user
     */
    private void handleEnteredUser(final String uDescription){
        ReadDataCallback userResult = new ReadDataCallback() {
            @Override
            public void onDataRetrieved(DataSnapshot result) {

                if (result.exists()){
                    // user is valid and can be added to the project as a collaborator
                    newTaskAssigneeField.clearFocus();
                    GenericTypeIndicator<Map<String, User>> genericTypeIndicator = new GenericTypeIndicator<Map<String, User>>() {};
                    Map <String,User> resultMap = result.getValue(genericTypeIndicator);
                    String id = (String) resultMap.keySet().toArray()[0];
                    newTaskAssignee = resultMap.get(id);
                }else{
                    // user is invalid and cannot be added to the project as a collaborator
                    String userInvalidHelp = uDescription + " - Invalid user";
                    newTaskAssigneeField.setText(userInvalidHelp);
                    newTaskAssigneeField.selectAll();
                }
            }
        };
        UserManager.getInstance().checkUserExists(uDescription,userResult);
    }


}
