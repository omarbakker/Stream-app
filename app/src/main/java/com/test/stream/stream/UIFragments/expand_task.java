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

import static com.test.stream.stream.R.string.reminder_notification_dialog_title;

public class expand_task extends AppCompatActivity implements View.OnClickListener {
    List<Task> tasks = new ArrayList<>();
    int current_task;
    private static final String TAG = TaskMain.class.getSimpleName();
    final Context context = this;

    //Reminder notification
    static AlertDialog dialog;
    EditText messageToSend;
    static TextView sendReminderAlertTitle;
    CheckBox sendAnonymously;
    View reminderDialogView;

    //Review notification
    static AlertDialog Reviewdialog;
    EditText reviewMessageToSend;
    TextView reviewTitle;
    static View reviewDialogView;


    protected void onCreate(Bundle savedInstanceState) {
        //View parent = (View) view.getParent();
        Log.d(TAG, "Enter expand_task");
        super.onCreate(savedInstanceState);

        Intent a = getIntent();
        tasks = TaskManager.getInstance().GetTasksInProject();
        String taskName = (String) a.getSerializableExtra("taskName");
        View view = (View) a.getSerializableExtra("view");
        setContentView(R.layout.item_details);

        Log.d(TAG, "print statements for the win");
        Task expandTask = new Task();
        Log.d(TAG, "fuck everything");
        int size = tasks.size();
        Log.d(TAG, String.valueOf(size));
        for (int i = 0; i < size; i++) {
            //Log.d(TAG, "about to get it");
            Task task = tasks.get(i);
            //Log.d(TAG, "got it");
            if (taskName.equals(task.getName())) {
                Log.d(TAG, "task name comparisons");
                Log.d(TAG, taskName);
                Log.d(TAG, task.getName());
                expandTask = task;
                current_task = i;
                break;
            }
        }
        //Log.d(TAG, "pre-content view set");
        setContentView(R.layout.item_details);

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

        Log.d(TAG, "finished");

        //----------------------------------------------------------------------------------------

        //initializes views for reminder alert dialog
        LayoutInflater ReminderInflater = LayoutInflater.from(context);
        reminderDialogView = ReminderInflater.inflate(R.layout.send_reminder_notification, null);
        messageToSend = (EditText) reminderDialogView.findViewById(R.id.reminderMessageToSend);
        sendReminderAlertTitle = (TextView) findViewById(R.id.reminderTitle);
        Task task = tasks.get(current_task);
        String title = getString(R.string.reminder_notification_dialog_title);
        //sendReminderAlertTitle.setText(title);
        //sendReminderAlertTitle.setText(R.string.reminder_notification_dialog_title);
        sendAnonymously = (CheckBox) findViewById(R.id.sendAnonymously);


        //Initializes views for review alert dialog
        LayoutInflater ReviewInflater = LayoutInflater.from(context);
        reviewDialogView = ReviewInflater.inflate(R.layout.send_review_notification, null);
        reviewMessageToSend = (EditText) reviewDialogView.findViewById(R.id.reminderMessageToSend);
        reviewTitle = (TextView) findViewById(R.id.reviewTitle);
        CharSequence title2 = getString(R.string.review_notification_dialog_title1);
        //reviewTitle.setText(title2);
        //reviewTitle.setText(R.string.review_notification_dialog_title1 + R.string.review_notification_dialog_title2);
        //reviewTitle.setText("Hello");


    }

    public void backToHome(){
        Intent intent = new Intent(getBaseContext(), TaskMain.class);
        startActivity(intent);
    }

    public void markAsComplete(View view){
        Task task = tasks.get(current_task);
        task.setComplete(true);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(reminderDialogView);
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String message = reviewMessageToSend.getText().toString();
                getReminderNotification(message);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public void appearReviewDialog() {
        //Initialize AlertDialog for Review
        AlertDialog.Builder Reviewbuilder = new AlertDialog.Builder(this);
        Reviewbuilder.setView(reviewDialogView);
        Reviewbuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String message = sendReminderAlertTitle.getText().toString();
                getReviewNotification(message);
            }
        });
        Reviewbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        Reviewdialog = Reviewbuilder.create();
        Reviewdialog.show();
    }

    public void getReminderNotification(String message) {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle("Here's a friendly reminder for you to complete your task!");
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
        nBuilder.setContentTitle("Here's a suggestion!");
        nBuilder.setContentText(message);
        nBuilder.setSmallIcon(R.drawable.com_facebook_button_icon);
        Notification notification = nBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        //Clear contents of the EditText
        reviewMessageToSend.setText("");
    }

}
