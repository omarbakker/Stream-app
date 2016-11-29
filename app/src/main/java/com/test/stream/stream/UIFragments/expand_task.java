package com.test.stream.stream.UIFragments;

/**
 * Created by janemacgillivray on 2016-10-23.
 */
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.test.stream.stream.Controllers.ProjectManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Notifications.NotificationObject;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.*;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Services.NotificationService;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;

public class expand_task extends AppCompatActivity implements View.OnClickListener {

    List<Task> tasks = new ArrayList<>();
    Task expandTask = new Task();
    int current_task;
    private static final String TAG = TasksFragment.class.getSimpleName();
    final Context context = this;

    //Reminder notification
    static AlertDialog dialog;
    EditText messageToSend;
    static TextView sendReminderAlertTitle;
    CheckBox sendAnonymously;
    View reminderDialogView;
    TextView reminderInfo;
    AlertDialog.Builder builder;

    //Review notification
    static AlertDialog Reviewdialog;
    EditText reviewMessageToSend;
    TextView reviewTitle;
    static View reviewDialogView;
    TextView reviewInfo;
    AlertDialog.Builder Reviewbuilder;
    private AlertDialog changedTaskDialog;


    //fields for new task input
    ImageView changedTaskValidAssigneeIndicator;
    TextInputEditText changedtaskDateField;
    TextInputEditText changedTaskNameField;
    TextInputEditText changedTaskDescriptionField;
    TextInputEditText changedTaskAssigneeField;
    User changedTaskAssignee;
    int[] DueDate = {0,0,0};


    /**
     * Set up the new view of the expanded task
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent a = getIntent();
        tasks = TaskManager.sharedInstance().GetTasksInProject();
        String taskName = (String) a.getSerializableExtra("taskName");
        View view = (View) a.getSerializableExtra("view");
        setContentView(R.layout.task_details);
        //Get the task that has been clicked

        //register device token
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();

        //register device token
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();

        int size = tasks.size();
        Log.d(TAG, String.valueOf(size));
        for (int i = 0; i < size; i++) {
            Task task = tasks.get(i);
            if (taskName.equals(task.getName())) {
                current_task = i;
                break;
            }
        }
        expandTask = tasks.get(current_task);
        //Set new content view
        setContentView(R.layout.task_details);
        //initialize the sendNotificiation button
        final FloatingActionButton changeTaskButton = (FloatingActionButton) findViewById(R.id.editTask);
        changeTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeTaskDialog();
            }
        });
        FloatingActionButton sendNotification = (FloatingActionButton) findViewById(R.id.sendTaskNotification);
        sendNotification.setOnClickListener(this);  //button listener


        updateExpandedUI();

        //------------------------------------REMINDERS--------------------------------------------------------------------------//
        //-----------------------------------------------------------------------------------------------------------------------//

        Task task = tasks.get(current_task);

        //initializes views for reminder alert dialog
        LayoutInflater ReminderInflater = LayoutInflater.from(context);
        reminderDialogView = ReminderInflater.inflate(R.layout.send_reminder_notification, null);
        messageToSend = (EditText) reminderDialogView.findViewById(R.id.reminderMessageToSend);
        sendReminderAlertTitle = (TextView) reminderDialogView.findViewById(R.id.reminderTitle);
        sendReminderAlertTitle.setText(getString(R.string.reminder_notification_dialog_title) + task.getAssignee() + "?");
        reminderInfo = (TextView) reminderDialogView.findViewById(R.id.reminderNotificationInfo);
        reminderInfo.setText(getString(R.string.reminder_notification_info) + task.getAssignee() + getString(R.string.reminder_notification_info2));
        builder = new AlertDialog.Builder(this);
        builder.setView(reminderDialogView);
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String message = messageToSend.getText().toString();
                getReminderNotification(message);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();


        //Initializes views for review alert dialog
        LayoutInflater ReviewInflater = LayoutInflater.from(context);
        reviewDialogView = ReviewInflater.inflate(R.layout.send_review_notification, null);
        reviewMessageToSend = (EditText) reviewDialogView.findViewById(R.id.reviewMessageToSend);
        reviewTitle = (TextView) reviewDialogView.findViewById(R.id.reviewTitle);
        reviewTitle.setText(getString(R.string.review_notification_dialog_title1) + task.getAssignee() + getString(R.string.review_notification_dialog_title2));
        reviewInfo = (TextView) reviewDialogView.findViewById(R.id.ReviewNotificationInfo);
        reviewInfo.setText(getString(R.string.review_notification_info) + task.getAssignee() + getString(R.string.review_notification_info2));
        Reviewbuilder = new AlertDialog.Builder(this);
        Reviewbuilder.setView(reviewDialogView);
        //Initialize AlertDialog for Review
        Reviewbuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String message = reviewMessageToSend.getText().toString();
                getReviewNotification(message);
            }
        });
        Reviewbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Reviewdialog.dismiss();
            }
        });
        Reviewdialog = Reviewbuilder.create();



    }


    /**

     * shows a reminder dialog
     */
    public void appearReminderDialog() {

        dialog.show();
    }

    /**
     * shows a review dialog
     */
    public void appearReviewDialog() {

        Reviewdialog.show();
    }

    /**
     * Creates a notification for the user of the task telling them to complete their task
     * @param message
     */
    public void getReminderNotification(String message) {
//        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
//        nBuilder.setContentTitle(getString(R.string.reminder_notification_title));
//        nBuilder.setContentText(message);
//        nBuilder.setSmallIcon(R.drawable.com_facebook_button_icon);
//        Notification notification = nBuilder.build();
//        NotificationService notificationManager = (NotificationService) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(1, notification);
//
//        //Clear contents of the EditText
//        messageToSend.setText("");

        Task task = tasks.get(current_task);
        //Currenlty only one person per task, so array always just one name
//        ArrayList<String> usernames =new ArrayList<>();
//        usernames.add(task.getAssignee());
        String[] usernames = {task.getAssignee()};
        Log.d(TAG,usernames[0]);
        //NotificationObject reminder = new NotificationObject("Here's a friendly reminder for you to complete your task!",message,usernames);
        NotificationObject reminder = new NotificationObject();
        reminder.setTitle("Friendly Reminder");
        reminder.setMessage(message);
        reminder.setUsers(usernames);
        Log.d(TAG,reminder.getUsers().toString());
        NotificationService.sharedInstance().sendNotificationTo(reminder);

    }

    /**
     * Creates a notification for the user assigned to the task with suggestion inputed by current user
     * @param message
     */
    public void getReviewNotification(String message) {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle(getString(R.string.review_notification_title));
        nBuilder.setContentText(message);
        nBuilder.setSmallIcon(R.drawable.com_facebook_button_icon);
        Notification notification = nBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        //Clear contents of the EditText
        reviewMessageToSend.setText("");
    }


    //---------------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------//




    /**
     * Marks a task as complete when the completion check box has been pressed
     * @param view
     */
    public void markAsComplete(View view) {
        Task task = tasks.get(current_task);
        if (task.getComplete() == false)
            task.setComplete(true);
        else
            task.setComplete(false);
        TaskManager.sharedInstance().UpdateTask(task);
    }

    /**
     * Upon the press of the delete button, the current task is deleted from the database and the user is returned
     * to the main task screen
     * @param view
     */
    public void deleteTask(View view){
        TaskManager.sharedInstance().DeleteTask(expandTask);
        Project currentProject = ProjectManager.sharedInstance().getCurrentProject();
        currentProject.setNumberOfActiveTasks(currentProject.getNumberOfActiveTasks()-1);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Projects,currentProject.getId(),currentProject);
        super.onBackPressed();
    }


    /**
     * Intialize the alertDialog to send a task notification
     * @param v
     */
    public void onClick(View v){
        switch (v.getId()){
            case R.id.sendTaskNotification :
                Task task = tasks.get(current_task);
                if (task.getComplete())
                    appearReviewDialog();
                else
                    appearReminderDialog();
                break;
            case R.id.editTask:
                showChangeTaskDialog();
                break;
            case R.id.doneAddingTask:
                editTask(tasks.get(current_task));
                break;
            case R.id.CancelAddingTask:
                changedTaskDialog.dismiss();
                break;
            case R.id.newTaskAddUserButton:
                handleEnteredUser(changedTaskAssigneeField.getText().toString());
                break;
            default:
                break;

        }
    }


    /**
     * Reads Newly inputed information and updates the task in the database
     * @param task
     */
    public void editTask(Task task){
        final String name = changedTaskNameField.getText().toString();
        final String description = changedTaskDescriptionField.getText().toString();
        final String due_date = changedtaskDateField.getText().toString();

        //fill the name field with the original names
        if (name.isEmpty()){
            changedTaskNameField.setText(expandTask.getName());
            changedTaskNameField.requestFocus();
            changedTaskNameField.selectAll();
            return;
        }

        //fill the description field with the original description
        if (description.isEmpty()){
            changedTaskDescriptionField.setText(expandTask.getDescription());
            changedTaskDescriptionField.requestFocus();
            changedTaskDescriptionField.selectAll();
            return;
        }

        if(!getValidDate(changedtaskDateField.getText().toString())) {
            handleInvalidDate();
            return;
        }

        //fill in orginal assignee
        if(changedTaskAssignee == null) {
            changedTaskAssigneeField.setText(expandTask.getAssignee());
            changedTaskAssigneeField.selectAll();
            return;
        }

        task.setName(name);
        task.setDescription(description);
        task.setDueDay(DueDate[0]);
        task.setDueMonth(DueDate[1]);
        task.setDueYear(DueDate[2]);
        task.setUser(changedTaskAssignee);


        TaskManager.sharedInstance().UpdateTask(task);
        changedTaskDialog.dismiss();
        updateExpandedUI();
    }


    /**
     * tests for invaild date being entered
     */
    public void handleInvalidDate(){
        changedtaskDateField.setText(R.string.new_project_prompt_date);
        changedtaskDateField.requestFocus();
        changedtaskDateField.selectAll();
    }

    /**
     * Determines if data is valid
     * @param date
     * @return
     */
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


    /**
     * Dialog for edit task
     */
    public void showChangeTaskDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_newtask, null);
        changedTaskDialog = new AlertDialog.Builder(this).setView(v).create();
        //set view and text type
        TextView title = (TextView) v.findViewById(R.id.newTaskPageTitle);
        title.setText("Edit Task");

        EditText task_name = (EditText) v.findViewById(R.id.newTaskNameField);
        task_name.setText(expandTask.getName());
        EditText task_description = (EditText) v.findViewById(R.id.newTaskDescriptionField);
        task_description.setText(expandTask.getDescription());
        EditText task_user = (EditText) v.findViewById(R.id.newTaskNewUserField);
        task_user.setText(String.valueOf(expandTask.getAssignee()));
        TextView task_date = (TextView) v.findViewById(R.id.newTaskDueDateField);
        task_date.setText(String.valueOf(expandTask.getDueDay()) + "/" + String.valueOf(expandTask.getDueMonth()) + "/" + String.valueOf(expandTask.getDueYear()));;
        Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Bold.ttf");
        title.setTypeface(Syncopate);

        //sets buttons
        Button done = (Button) v.findViewById(R.id.doneAddingTask);
        done.setText("Done");
        Button cancel = (Button) v.findViewById(R.id.CancelAddingTask);
        Button addUser = (Button) v.findViewById(R.id.newTaskAddUserButton);

        //get each field
        changedTaskValidAssigneeIndicator = (ImageView) v.findViewById(R.id.newTaskValidAssigneeIndicator);
        changedTaskAssigneeField = (TextInputEditText) v.findViewById(R.id.newTaskNewUserField);
        changedtaskDateField = (TextInputEditText) v.findViewById(R.id.newTaskDueDateField);
        changedTaskNameField = (TextInputEditText) v.findViewById(R.id.newTaskNameField);
        changedTaskDescriptionField = (TextInputEditText) v.findViewById(R.id.newTaskDescriptionField);

        //create listeners
        changedTaskAssigneeField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
        changedtaskDateField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                return false;
            }
        });
        changedTaskNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                return false;
            }
        });
        changedTaskDescriptionField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                return false;
            }
        });

        //set button functions
        addUser.setOnClickListener(this);
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        changedtaskDateField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((s.length() == 2 || s.length() == 5) && count > 0) {
                    changedtaskDateField.append("/");
                }
            }
        });
        changedTaskDialog.show();
    }

    /**
     * Fill in all of the data task details with their given information
     */
    public void updateExpandedUI() {
        Log.d(TAG, "BEGINNING EXPANDING TASK");
        TextView task_name = (TextView) findViewById(R.id.task_name_expanded);
        Log.d(TAG, String.valueOf(task_name));
        task_name.setText(expandTask.getName());
        TextView task_description = (TextView) findViewById(R.id.description_expanded);
        task_description.setText(expandTask.getDescription());
        Log.d(TAG, expandTask.getDescription());
        task_description.setVisibility(View.VISIBLE);
        //user
        TextView user = (TextView) findViewById(R.id.user_expanded);
        user.setText(String.valueOf(expandTask.getAssignee()));

        //due_date
        TextView dueDate = (TextView) findViewById(R.id.due_date_expanded);
        String due = String.valueOf(expandTask.getDueDay()) + "/" + String.valueOf(expandTask.getDueMonth()) + "/" + String.valueOf(expandTask.getDueYear());
        dueDate.setText(due);

        //determines if the task has be set as complete
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        if (expandTask.getComplete() == true) {
            checkBox.setChecked(true);
            checkBox.setText("Mark as incomplete");
        }
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
                    changedTaskAssigneeField.clearFocus();
                    GenericTypeIndicator<Map<String, User>> genericTypeIndicator = new GenericTypeIndicator<Map<String, User>>() {};
                    Map <String,User> resultMap = result.getValue(genericTypeIndicator);
                    String id = (String) resultMap.keySet().toArray()[0];

                    if(ProjectManager.sharedInstance().getCurrentProject().isMember(resultMap.get(id))) //Confirm that the assignee is a member of the project
                    {
                        changedTaskAssignee = resultMap.get(id);
                        changedTaskValidAssigneeIndicator.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        changedTaskAssignee = null;
                    }
                }

                if(changedTaskAssignee == null){
                    // user is invalid or is not a member of the project
                    String userInvalidHelp = uDescription + " - Invalid Member";
                    changedTaskAssigneeField.setText(userInvalidHelp);
                    changedTaskAssigneeField.selectAll();
                    changedTaskValidAssigneeIndicator.setVisibility(View.INVISIBLE);
                }
            }
        };
        UserManager.sharedInstance().fetchUserByUserName(uDescription,userResult);
    }

}
