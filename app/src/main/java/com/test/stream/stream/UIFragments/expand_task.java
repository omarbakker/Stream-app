package com.test.stream.stream.UIFragments;

/**
 * Created by janemacgillivray on 2016-10-23.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.api.model.StringList;
import com.test.stream.stream.Controllers.ProjectManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Objects.Notifications.NotificationObject;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.*;
import com.test.stream.stream.R;
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
        FloatingActionButton sendNotification = (FloatingActionButton) findViewById(R.id.sendTaskNotification);
        sendNotification.setOnClickListener(this);  //button listener

        // ----- Set the text fields from information of the current task ----- //

        //task name
        TextView task_name = (TextView) findViewById(R.id.task_name_expanded);
        task_name.setText(expandTask.getName());
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
        String due = String.valueOf(expandTask.getDueDay())+"/"+String.valueOf(expandTask.getDueMonth())+"/"+String.valueOf(expandTask.getDueYear());
        dueDate.setText(due);

        //determines if the task has be set as complete
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        if(expandTask.getComplete() == true)
            checkBox.setChecked(true);

        Log.d(TAG, "finished");

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

        TaskManager.sharedInstance().UpdateTask(task);

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
        reminder.setTitle("Here's a friendly reminder for you to complete your task!");
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

}
