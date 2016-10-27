package com.test.stream.stream.UIFragments;


import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.content.Intent;

import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.Task;
//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.test.stream.stream.R;

public class TaskMain extends AppCompatActivity {
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;
    private int current_task;
    ArrayList<Task> tasks = new ArrayList<>();
    private static final String TAG = TaskMain.class.getSimpleName();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_main);
        mTaskListView = (ListView) findViewById(R.id.list_todo);
        Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Regular.ttf");
        final FloatingActionButton addTaskButton = (FloatingActionButton) findViewById(R.id.create_new_task);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTask();
            }
        });
        Project projectTest = new Project();
        projectTest.setTaskGroupId("janeId");
        ProjectManager.currentProject = projectTest;
        TaskManager.getInstance().InitializeTasks(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void createTask() {
        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_newtask, null);

        AlertDialog hi = new AlertDialog.Builder(this)

                .setView(v)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //Task task = new Task();
                        EditText input_name = (EditText) v.findViewById(R.id.task_name);
                        //task.setName(input_name.getText().toString());
                        Log.d(TAG, input_name.getText().toString());
                        EditText description = (EditText) v.findViewById(R.id.description);
                        //task.setDescription(description.getText().toString());

                        EditText user = (EditText) v.findViewById(R.id.user);
                        //task.setTASK_USER(user.getText().toString());
                        Log.d(TAG, "pre-datePicker \n");
                        //int[] date = datePicker();
                        //Log.d("DATE", String.valueOf(date[0]));
                        //.setTASK_DUE_DATE(date);
                        //task.setCOMPLETE(0);
                        int[] date = new int[]{0, 0, 0};
                        Log.d(TAG, "hi\n");
                        //tasks.add(task);
                        TaskManager.getInstance().CreateTask(input_name.getText().toString(), description.getText().toString(), user.getText().toString(), date, false);
                    }
                }).setNegativeButton("Cancel", null)
                .create();
        hi.show();
    }

//    public int[] datePicker(){
//        LayoutInflater inflater = this.getLayoutInflater();
//        final View v = inflater.inflate(R.layout.newtask_datepicker, null);
//        final int[] due_date = new int[3];
//        AlertDialog hi = new AlertDialog.Builder(this)
//
//                .setView(v)
//                .setPositiveButton("Next", new DialogInterface.OnClickListener(){
//                    public void onClick(DialogInterface dialog, int id){
//                        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
//                        int day = datePicker.getDayOfMonth();
//                        Log.d(TAG, String.valueOf(day));
//                        int month = datePicker.getMonth() + 1;
//                        Log.d(TAG, String.valueOf(month));
//                        int year = datePicker.getYear();
//                        Log.d(TAG, String.valueOf(year));
//                        due_date[0] = day;
//                        due_date[1] = month;
//                        due_date[2] = year;
//                    }
//               }).setNegativeButton("Cancel", null)
//                .create();
//                hi.show();
//                return due_date;
//
//    }


    public void updateUI() {
        List<Task> tasks = TaskManager.getInstance().GetTasksInProject();
        ArrayList<String> taskList = new ArrayList<>();
        int i = tasks.size() - 1;
        Log.d(TAG, String.valueOf(i));
        while (i >= 0) {
            Task task = tasks.get(i);
            Log.d(TAG, "writing to the tasks list");
            taskList.add(task.getName());
            i--;
        }

        if (mAdapter == null) {
            Log.d(TAG, "mAdapter is apparently null?");
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_small,
                    R.id.task_name,
                    taskList);

            mTaskListView.setAdapter(mAdapter);

        } else {
            mAdapter.clear();
            Log.d(TAG, "why are you like this");
            mAdapter.addAll(taskList);
            Log.d(TAG, "adding all the things");
            mAdapter.notifyDataSetChanged();
        }

    }

    public void expandTaskView(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_name);
        String taskName = String.valueOf(taskTextView.getText());
        Intent intent = new Intent(this, expand_task.class);
        Log.d(TAG, "so fucked up oh my god");
        //intent.putExtra("tasks",tasks);
        intent.putExtra("taskName", taskName);
        Log.d(TAG, "everything is awful");
        startActivity(intent);
    }
//        View parent = (View) view.getParent();
//        Log.d(TAG, "SO MANY PRINT STATEMENTS WHY");
//        TextView taskTextView = (TextView) parent.findViewById(R.id.task_name);
//        Log.d(TAG, "ass ass ass ass");
//        String taskName = String.valueOf(taskTextView.getText());
//        Log.d(TAG, "print statements for the win");
//        Task expandTask = new Task();
//        Log.d(TAG, "fuck everything");
//        int size = tasks.size();
//        Log.d(TAG, "pre-for loop");
//        for (int i = 0; i < size; i++) {
//            Log.d(TAG, "about to get it");
//            Task task = tasks.get(i);
//            Log.d(TAG, "got it");
//            if (taskName.equals(task.getTASK_NAME())) {
//                Log.d(TAG, "#struggles");
//                expandTask = task;
//                Log.d(TAG, "hi friends");
//                current_task = i;
//                break;
//            }
//        }
//        Log.d(TAG, "pre-content view set");
//        setContentView(R.layout.item_details);
//
//        Log.d(TAG, "post content view set");
//        TextView task_name = (TextView) findViewById(R.id.task_name_expanded);
//        Log.d(TAG, "got the text view we want");
//        //Log.d(TAG,String.valueOf(task_name));
//        task_name.setText(expandTask.getTASK_NAME());
//        Log.d(TAG, "assigned it");
//        TextView task_description = (TextView) findViewById(R.id.description_expanded);
//        task_description.setText(expandTask.getTASK_DESCRIPTION());
//        TextView user = (TextView) findViewById(R.id.user_expanded);
//        user.setText(expandTask.getTASK_USER());



//   public void backToHome(View view){
//       setContentView(R.layout.activity_main);
//       updateUI();
//   }

    public void sortArraybyComplete(ArrayList<Task> tasks){
        for(int i = 0; i < tasks.size()-1; i++){
            if(tasks.get(i).getComplete()==true){
                Task task = tasks.get(i);
                tasks.set(i, tasks.get(i+1));
                tasks.set(tasks.size()-1, task);
            }
        }

    }

}