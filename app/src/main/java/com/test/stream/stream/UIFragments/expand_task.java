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

import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Objects.Tasks.*;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.ToolbarActivity;

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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent a = getIntent();
        tasks = TaskManager.getInstance().GetTasksInProject();
        String taskName = (String) a.getSerializableExtra("taskName");
        View view = (View) a.getSerializableExtra("view");
        setContentView(R.layout.task_details);


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
        setContentView(R.layout.task_details);
        FloatingActionButton sendNotification = (FloatingActionButton) findViewById(R.id.sendTaskNotification);
        sendNotification.setOnClickListener(this);

        TextView task_name = (TextView) findViewById(R.id.task_name_expanded);
        task_name.setText(expandTask.getName());

        TextView task_description = (TextView) findViewById(R.id.description_expanded);
        task_description.setText(expandTask.getDescription());
        Log.d(TAG, expandTask.getDescription());
        task_description.setVisibility(View.VISIBLE);

        TextView user = (TextView) findViewById(R.id.user_expanded);
        Log.d(TAG, expandTask.getAssignee());
        user.setText(expandTask.getAssignee());

        TextView dueDate = (TextView) findViewById(R.id.due_date_expanded);
        String due = String.valueOf(expandTask.getDueDay())+"/"+String.valueOf(expandTask.getDueMonth())+"/"+String.valueOf(expandTask.getDueYear());
        dueDate.setText(due);

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

    public void markAsComplete(View view) {
        Task task = tasks.get(current_task);
        if(task.getComplete() == false)
            task.setComplete(true);
        else
            task.setComplete(false);
    }

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

    public void appearReminderDialog() {

        dialog.show();
    }

    public void appearReviewDialog() {

        Reviewdialog.show();
    }

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

    public void deleteTask(View view){
        TaskManager.getInstance().DeleteTask(expandTask);
        Log.d(TAG, "successfully deleted");
        super.onBackPressed();
    }

}
