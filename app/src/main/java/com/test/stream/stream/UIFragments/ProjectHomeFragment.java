package com.test.stream.stream.UIFragments;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.test.stream.stream.Controllers.HomeManager;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.DateUtility;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;
import com.test.stream.stream.UI.Adapters.HomeTaskAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ProjectHomeFragment extends Fragment
        implements View.OnClickListener,
        EditText.OnEditorActionListener,
        TextWatcher {

    private DonutProgress teamProgress;
    private DonutProgress userProgress;

    private static final String TAG = "Home Fragment";

    ArrayList<Task> taskMessages = new ArrayList<>();
    private HomeTaskAdapter homeTaskAdapter;

    private HomeManager homeManager;

    private DataEventListener dataListener = new DataEventListener() {
        @Override
        public void onDataChanged() {
            updateUI();
        }
    };
    private ListView listView;
    int[] DueDate = {0,0,0};
    private AlertDialog newTaskDialog;
    //fields for new task input
    TextInputEditText newtaskDateField;
    TextInputEditText newTaskNameField;
    TextInputEditText newTaskDescriptionField;
    TextInputEditText newTaskAssigneeField;
    User newTaskAssignee;
    //TextView welcome = (TextView) getView().findViewById(R.id.welcome_message);

    ImageView newTaskValidAssigneeIndicator;

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

        homeManager = HomeManager.sharedInstance();
        homeManager.setUIListener(dataListener);

        final FloatingActionButton addTaskButton = (FloatingActionButton) getView().findViewById(R.id.create_task);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewTaskDialog();
            }
        });

        // initialize both progresses to 0
        int blueColor = Color.argb(255, 225, 237, 255);
        userProgress.setFinishedStrokeColor(blueColor);
        teamProgress.setFinishedStrokeColor(blueColor);
        userProgress.setProgress(0);
        teamProgress.setProgress(0);
    }

    private void updateProgress(DonutProgress donutProgress, int newProgress) {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            // will update the "progress" propriety of seekbar until it reaches progress
            ObjectAnimator animation = ObjectAnimator.ofInt(donutProgress, "progress", newProgress);
            animation.setDuration(1500); // 1.5 seconds
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        } else
            donutProgress.setProgress(newProgress);
    }
/*
    private void updateTeamProgress(int newProgress){
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.progress_anim);
        set.setTarget();
        ObjectAnimator anim = ObjectAnimator.ofInt(R.id.team_progress, "circle_progress", 0, 100);
    }
*/

    /**
     * Function that updates the Adapter of the ListFragment
     */
    public void updateUI() {

        // Get all user tasks from the database
        List<Task> allTasks = homeManager.getUserTasks();

        ArrayList<Task> tasks = new ArrayList();
        listView = (ListView) getView().findViewById(R.id.task_list);

        // Go through each task in database
        for (Task currentTask : allTasks) {
            if (currentTask.getClass() == Task.class) {
                tasks.add(currentTask);
            }
        }
        if (homeTaskAdapter == null) {
            homeTaskAdapter = new HomeTaskAdapter(getActivity(), taskMessages);
            listView.setAdapter(homeTaskAdapter);
        }
        homeTaskAdapter.clear();
        DateUtility.sortTasksByDueDate(tasks);
        homeTaskAdapter.addAll(tasks);
        homeTaskAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(listView);

        updateProgress(teamProgress, homeManager.getTeamProgress());
        updateProgress(userProgress, homeManager.getUserProgress());
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

        newTaskAssigneeField.setText(UserManager.sharedInstance().getCurrentUser().getUsername());

        //set button functions
        addUser.setOnClickListener(this);
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        newtaskDateField.addTextChangedListener(this);
        newTaskDialog.show();
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

}
