package com.test.stream.stream.UIFragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.AdapterView;
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
import com.test.stream.stream.Objects.Calendar.Calendar;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;

import com.test.stream.stream.UI.Adapters.TaskAdapter;

import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.R.attr.name;
import static android.R.id.input;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.test.stream.stream.R.id.view;

/**
 * A simple {@link Fragment} subclass.
 */
public class TasksFragment extends Fragment
        implements
        View.OnClickListener,
        EditText.OnEditorActionListener,
        TextWatcher,
        ListView.OnItemClickListener
{

    private AlertDialog newTaskDialog;
    private ListView myTaskListView;
    private TaskAdapter mTaskAdapter;

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

        myTaskListView = (ListView) getView().findViewById(R.id.list_task);
        myTaskListView.setOnItemClickListener(this);

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
    public void updateUI(){

        getCurrentDate();
        List<Object> tasks = new ArrayList<Object>(TaskManager.sharedInstance().GetTasksInProject());
        Project currentProject = ProjectManager.sharedInstance().getCurrentProject();
        List<Object> sortedTaskLists = sortTasks(tasks);

        if (mTaskAdapter == null) {

            mTaskAdapter = new TaskAdapter(this.getContext(),sortedTaskLists);
            myTaskListView.setAdapter(mTaskAdapter);

        } else {

            mTaskAdapter.updateData(sortedTaskLists);
            mTaskAdapter.notifyDataSetChanged();
        }

    }

    List<Object> sortTasks(List <Object> tasks){

        List<Object> sortedTasks = new ArrayList<Object>();
        List<Object> myTasks = new ArrayList<Object>();
        List<Object> othersTasks = new ArrayList<Object>();
        List<Object> dueTasks = new ArrayList<Object>();

        // add tasks to the appropriate list
        for (Object object:tasks){

            if (object instanceof Task){
                Task task = (Task)object;

                int[] dueDate = {task.getDueYear(),task.getDueMonth(),task.getDueDay()};
                boolean isPastDue = isPastDue(dueDate);
                boolean isComplete = task.getComplete();
                boolean isAssignedToMe = isAssignedToCurrentUser(task);

                if (isComplete) {
                    dueTasks.add(task);
                }else {

                    if (isAssignedToMe) {
                        myTasks.add(task);
                    } else {
                        othersTasks.add(task);
                    }
                }
            }

        }

        if (myTasks.size() != 0) {
            sortedTasks.add("Your Tasks");
            sortedTasks.addAll(myTasks);
        }
        if (othersTasks.size() != 0) {
            sortedTasks.add("Team-mate's tasks");
            sortedTasks.addAll(othersTasks);
        }
        if (dueTasks.size() != 0) {
            sortedTasks.add("Completed Tasks");
            sortedTasks.addAll(dueTasks);
        }

        return sortedTasks;
    }

    /**
     *
     * @param task
     * @return true if the task is assigned to the current user. relies on UserManager.
     */
    public static boolean isAssignedToCurrentUser(Task task){
        return task.getAssignee().equals(UserManager.sharedInstance().getCurrentUser().getUsername());
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


    /**
     * When the task is pressed switch to expanded Task view
     * @param object
     * The object representing a Task, retrieved from the mTaskAdapters list view
     */
    public void expandTaskView(Object object) {

        if (object instanceof Task) {
            Task task = (Task) object;
            Intent intent = new Intent(getActivity(), expand_task.class);
            intent.putExtra("taskName", task.getName());
            startActivity(intent);
        }
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
            if ((tasks.get(i).getComplete()) && !(tasks.get(i+1).getComplete())) {
                Task task = tasks.get(i);
                tasks.set(i, tasks.get(i+1));
                tasks.set(i+1, task);
            }
        }
        return tasks;
    }

    public void handleInvalidDate(){
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


    /**
     * Reads data inputs in the create task dialog and writes them to the database
     */
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

    /**
     * Used to enter the project after the user taps on that project from the list.
     * Opens the projects page.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (mTaskAdapter.getItemType(position) == TaskAdapter.TASK_TYPE)
            expandTaskView(mTaskAdapter.getItem(position));
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


    public void getCurrentDate(){
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    }

}