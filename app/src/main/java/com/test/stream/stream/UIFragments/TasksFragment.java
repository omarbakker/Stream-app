package com.test.stream.stream.UIFragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

<<<<<<< 890176be134d59f58cb124fae152003e69cfe394
=======
import static android.R.attr.name;
import static android.R.id.input;
import static com.test.stream.stream.R.id.view;

>>>>>>> colour of expanded task based on user
/**
 * A simple {@link Fragment} subclass.
 */
public class TasksFragment extends Fragment
        implements
        View.OnClickListener,
        EditText.OnEditorActionListener,
        TextWatcher
{

    private AlertDialog newTaskDialog;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    //fields for new task input
    TextInputEditText newtaskDateField;
    TextInputEditText newTaskNameField;
    TextInputEditText newTaskDescriptionField;
    TextInputEditText newTaskAssigneeField;
    User newTaskAssignee;
    //TextView welcome = (TextView) getView().findViewById(R.id.welcome_message);

    ImageView newTaskValidAssigneeIndicator;

    private int current_task;
    int[] DueDate = {0,0,0};
    ArrayList<Task> tasks = new ArrayList<>();
    private static final String TAG = TasksFragment.class.getSimpleName();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private DataEventListener dataListener = new DataEventListener() {
        @Override
        public void onDataChanged() {
            updateUI();
        }
    };



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
        //intialize
        TaskManager.sharedInstance().Initialize(dataListener);
    }

    /**
     * Inflates an Alert Dialog that prompts the user to input information about the task and writes the new task to the database
     */

    public void showNewTaskDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_newtask, null);

        newTaskDialog = new AlertDialog.Builder(getActivity()).setView(v).create();
        //set view and text type
        TextView title = (TextView) v.findViewById(R.id.newTaskPageTitle);
        Typeface Syncopate = Typeface.createFromAsset(getActivity().getAssets(), "Syncopate-Bold.ttf");
        title.setTypeface(Syncopate);

        //sets buttons
        Button done = (Button) v.findViewById(R.id.doneAddingTask);
        Button cancel = (Button) v.findViewById(R.id.CancelAddingTask);
        Button addUser = (Button) v.findViewById(R.id.newTaskAddUserButton);

        //get each field
        newTaskValidAssigneeIndicator = (ImageView) v.findViewById(R.id.newTaskValidAssigneeIndicator);
        newTaskAssigneeField = (TextInputEditText) v.findViewById(R.id.newTaskNewUserField);
        newtaskDateField = (TextInputEditText) v.findViewById(R.id.newTaskDueDateField);
        newTaskNameField = (TextInputEditText) v.findViewById(R.id.newTaskNameField);
        newTaskDescriptionField = (TextInputEditText) v.findViewById(R.id.newTaskDescriptionField);

        //create listeners
        newTaskAssigneeField.setOnEditorActionListener(this);
        newtaskDateField.setOnEditorActionListener(this);
        newTaskNameField.setOnEditorActionListener(this);
        newTaskDescriptionField.setOnEditorActionListener(this);

        //set button functions
        addUser.setOnClickListener(this);
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        newtaskDateField.addTextChangedListener(this);
        newTaskDialog.show();
    }


    /**x
     * updates the user interface to display all tasks
     */
<<<<<<< 890176be134d59f58cb124fae152003e69cfe394
    private void updateUI(){
        List<Task> tasks = TaskManager.sharedInstance().GetTasksInProject();
=======
    public void updateUI(){
        //welcome.setVisibility(getView().INVISIBLE);
        List<Task> tasks = TaskManager.getInstance().GetTasksInProject();
        tasks = sortArraybyComplete(tasks);
>>>>>>> colour of expanded task based on user
        ArrayList<String> taskList = new ArrayList<>();
        Project currentProject = ProjectManager.sharedInstance().getCurrentProject();
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

//        int size = taskList.size();
//        for( int a = 0; a < size; a++) {
//            View listViewChildAt = mTaskListView.getChildAt(a);
//            Task task = tasks.get(a);
//            int colour = task.getAssignee().length()*-1500;
//            listViewChildAt.setBackgroundColor(colour);
//        }
    }


    /**
     * When the task is pressed switch to expanded Task view
     * @param view
     */
    public void expandTaskView(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_name);
        String taskName = String.valueOf(taskTextView.getText());
        Intent intent = new Intent(getActivity(), expand_task.class);
        intent.putExtra("taskName", taskName);
        startActivity(intent);
    }

    /**
     * Sort the array of tasks
     * @param tasks
     */
    public List<Task> sortArraybyComplete(List<Task> tasks){
        if(tasks.size() == 1){
            return tasks;
        }
        for(int i = 0; i < tasks.size()-1; i++){
            if ((tasks.get(i).getComplete()) == true && (tasks.get(i+1).getComplete()) != true) {
                Task task = tasks.get(i);
                tasks.set(i, tasks.get(i+1));
                tasks.set(i+1, task);

            }
        }
        return tasks;
    }

    private void handleInvalidDate(){
        newtaskDateField.setText(R.string.new_project_prompt_date);
        newtaskDateField.requestFocus();
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



    public void createTask(){
        final String name = newTaskNameField.getText().toString();
        final String description = newTaskDescriptionField.getText().toString();

        if (name.isEmpty()){
            newTaskNameField.setText("Please Enter a name");
            newTaskNameField.requestFocus();
            newTaskNameField.selectAll();
            return;
        }

        if (description.isEmpty()){
            newTaskDescriptionField.setText("Please Enter a Description");
            newTaskDescriptionField.requestFocus();
            newTaskDescriptionField.selectAll();
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

        TaskManager.sharedInstance().CreateTask(name, description, newTaskAssignee, DueDate, false);
        newTaskDialog.dismiss();
    }

    /**
     * Checks if the string passed represents a valid user in the database.
     * Updates newTaskAssignee accordingly.
     * @param uDescription
     * The description entered by the user
     */
    private void handleEnteredUser(final String uDescription){
        if (uDescription.isEmpty()) return;
        ReadDataCallback userResult = new ReadDataCallback() {
            @Override
            public void onDataRetrieved(DataSnapshot result) {

                if (result.exists()){
                    // user is valid and can be added to the project as a collaborator
                    newTaskAssigneeField.clearFocus();
                    GenericTypeIndicator<Map<String, User>> genericTypeIndicator = new GenericTypeIndicator<Map<String, User>>() {};
                    Map <String,User> resultMap = result.getValue(genericTypeIndicator);
                    String id = (String) resultMap.keySet().toArray()[0];

                    if(ProjectManager.sharedInstance().getCurrentProject().isMember(resultMap.get(id))) //Confirm that the assignee is a member of the project
                    {
                        newTaskAssignee = resultMap.get(id);
                        newTaskValidAssigneeIndicator.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        newTaskAssignee = null;
                    }
                }

                if(newTaskAssignee == null){
                    // user is invalid or is not a member of the project
                    String userInvalidHelp = uDescription + " - Invalid Member";
                    newTaskAssigneeField.setText(userInvalidHelp);
                    newTaskAssigneeField.selectAll();
                    newTaskValidAssigneeIndicator.setVisibility(View.INVISIBLE);
                }
            }
        };
        UserManager.sharedInstance().fetchUserByUserName(uDescription,userResult);
    }


    /*
     Omar was here.
     Monitor text changes, use these to help the user with his input.
     I will use them to fill in the date '/' for the user.
     Don't forget to do this for the editText you want to watch:
        newtaskDateField.addTextChangedListener(this);
     It may be smart to put this into its own class when refactoring
    */
    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if ((s.length() == 2 || s.length() == 5) && count > 0) {
            newtaskDateField.append("/");
        }
    }

    @Override
    public void onDestroyView()
    {
        TaskManager.sharedInstance().Destroy();
        super.onDestroyView();
    }

}