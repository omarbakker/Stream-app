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
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.*;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.ToolbarActivity;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.UIFragments.TasksFragment;

import static com.test.stream.stream.R.id.newTaskDescriptionField;
import static com.test.stream.stream.R.id.newTaskDialog;
import static com.test.stream.stream.R.id.newTaskNameField;
import static com.test.stream.stream.R.id.newTaskValidAssigneeIndicator;

public class expand_task extends AppCompatActivity implements View.OnClickListener {

    List<Task> tasks = new ArrayList<>();
    Task expandTask = new Task();
    int current_task;
    private static final String TAG = TaskMain.class.getSimpleName();
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
        tasks = TaskManager.getInstance().GetTasksInProject();
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

        Log.d(TAG, "print statements for the win");
        Task expandTask = new Task();
        Log.d(TAG, "fuck everything");
        int size = tasks.size();
        Log.d(TAG, String.valueOf(size));
        for (int i = 0; i < size; i++) {
            Task task = tasks.get(i);
            if (taskName.equals(task.getName())) {
                expandTask = task;
                current_task = i;
                break;
            }
        }

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

        // ----- Set the text fields from information of the current task ----- //


        updateExpandedUI();

        //----------------------------------------------------------------------------------------

        Task task = tasks.get(current_task);

        //initializes views for reminder alert dialog
        LayoutInflater ReminderInflater = LayoutInflater.from(context);
        reminderDialogView = ReminderInflater.inflate(R.layout.send_reminder_notification, null);
        messageToSend = (EditText) reminderDialogView.findViewById(R.id.reminderMessageToSend);
        sendReminderAlertTitle = (TextView) reminderDialogView.findViewById(R.id.reminderTitle);
        sendReminderAlertTitle.setText(getString(R.string.reminder_notification_dialog_title) + task.getAssignee() + "?");
        reminderInfo = (TextView) reminderDialogView.findViewById(R.id.reminderNotificationInfo);
        reminderInfo.setText(getString(R.string.reminder_notification_info) + task.getAssignee() + getString(R.string.reminder_notification_info2));
        sendAnonymously = (CheckBox) findViewById(R.id.sendAnonymously);
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
     * Marks a task as complete when the completion check box has been pressed
     * @param view
     */
    public void markAsComplete(View view) {
        Task task = tasks.get(current_task);
        if(task.getComplete() == false)
            task.setComplete(true);
        else
            task.setComplete(false);

        TaskManager.getInstance().UpdateTask(task);
    }


    /**
     * Intialize the alertDialog to send a task notification
     * @param v
     */
    public void onClick(View v){
        switch (v.getId()){
            case R.id.sendTaskNotification:
                Task task = tasks.get(current_task);
                if(task.getComplete())
                    appearReviewDialog();
                else
                    appearReminderDialog();

            case R.id.editTask:
                showChangeTaskDialog();
                editTask(tasks.get(current_task));

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
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle(getString(R.string.reminder_notification_title));
        nBuilder.setContentText(message);
        nBuilder.setSmallIcon(R.drawable.com_facebook_button_icon);
        Notification notification = nBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        //Clear contents of the EditText
        messageToSend.setText("");
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


    /**
     * Upon the press of the delete button, the current task is deleted from the database and the user is returned
     * to the main task screen
     * @param view
     */
    public void deleteTask(View view){
        TaskManager.getInstance().DeleteTask(expandTask);
        Project currentProject = ProjectManager.sharedInstance().getCurrentProject();
        currentProject.setNumberOfActiveTasks(currentProject.getNumberOfActiveTasks()-1);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Projects,currentProject.getId(),currentProject);
        super.onBackPressed();
    }


    public void editTask(Task task){
        final String name = changedTaskNameField.getText().toString();
        final String description = changedTaskDescriptionField.getText().toString();

        if (name.isEmpty()){
            changedTaskNameField.setText("Change your task Name");
            changedTaskNameField.requestFocus();
            changedTaskNameField.selectAll();
            return;
        }

        if (description.isEmpty()){
            changedTaskDescriptionField.setText("Change your task Description");
            changedTaskDescriptionField.requestFocus();
            changedTaskDescriptionField.selectAll();
            return;
        }

        if(!getValidDate(changedtaskDateField.getText().toString())) {
            handleInvalidDate();
            return;
        }

        if(changedTaskAssignee == null) {
            changedTaskAssigneeField.setText("change your user");
            changedTaskAssigneeField.selectAll();
            return;
        }

        task.setName(name);
        task.setDueDay(DueDate[0]);
        task.setDueMonth(DueDate[1]);
        task.setDueYear(DueDate[2]);
        task.setUser(changedTaskAssignee);

        TaskManager.getInstance().UpdateTask(task);
        changedTaskDialog.dismiss();



    }

    public void handleInvalidDate(){
        changedtaskDateField.setText(R.string.new_project_prompt_date);
        changedtaskDateField.requestFocus();
        changedtaskDateField.selectAll();
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
                    changedTaskAssignee = resultMap.get(id);
                    changedTaskValidAssigneeIndicator.setVisibility(View.VISIBLE);
                }else{
                    // user is invalid and cannot be added to the project as a collaborator
                    String userInvalidHelp = uDescription + " - Invalid user";
                    changedTaskAssigneeField.setText(userInvalidHelp);
                    changedTaskAssigneeField.selectAll();
                    changedTaskValidAssigneeIndicator.setVisibility(View.INVISIBLE);
                }
            }
        };
        UserManager.getInstance().checkUserExists(uDescription,userResult);
    }


    public void showChangeTaskDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_newtask, null);

        changedTaskDialog = new AlertDialog.Builder(this).setView(v).create();
        //set view and text type
        TextView title = (TextView) v.findViewById(R.id.newTaskPageTitle);
        Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Bold.ttf");
        title.setTypeface(Syncopate);

        //sets buttons
        Button done = (Button) v.findViewById(R.id.doneAddingTask);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                changedtaskDateField.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changedtaskDateField.setError(null);
            }
        });
        changedTaskDialog.show();
    }


    public void updateExpandedUI() {

        TextView task_name = (TextView) findViewById(R.id.task_name_expanded);
        task_name.setText(expandTask.getName());
        //int colour = expandTask.getAssignee().length() * -1500;
        //Log.d(TAG, String.valueOf(colour));
        //task_name.setBackgroundColor(colour);
        //description
        TextView task_description = (TextView) findViewById(R.id.description_expanded);
        task_description.setText(expandTask.getDescription());
        Log.d(TAG, expandTask.getDescription());
        task_description.setVisibility(View.VISIBLE);
        //user
        TextView user = (TextView) findViewById(R.id.user_expanded);
        Log.d(TAG, expandTask.getAssignee());
        user.setText(expandTask.getAssignee());
        //due_date
        TextView dueDate = (TextView) findViewById(R.id.due_date_expanded);
        String due = String.valueOf(expandTask.getDueDay()) + "/" + String.valueOf(expandTask.getDueMonth()) + "/" + String.valueOf(expandTask.getDueYear());
        dueDate.setText(due);

        //determines if the task has be set as complete
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        if (expandTask.getComplete() == true)
            checkBox.setChecked(true);

        Log.d(TAG, "finished");
    }
}
