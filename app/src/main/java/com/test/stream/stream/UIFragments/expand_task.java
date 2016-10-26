package com.test.stream.stream.UIFragments;

/**
 * Created by janemacgillivray on 2016-10-23.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import com.test.stream.stream.Objects.Tasks.*;
import com.test.stream.stream.R;
import com.test.stream.stream.UIFragments.TaskMain;

public class expand_task extends Activity {
    ArrayList<Task> tasks = new ArrayList<>();
    int current_task;
    private static final String TAG = TaskMain.class.getSimpleName();


    protected void onCreate(Bundle savedInstanceState) {
        //View parent = (View) view.getParent();
        Log.d(TAG, "Enter expand_task");
        super.onCreate(savedInstanceState);

        Intent a = getIntent();
        tasks = (ArrayList<Task>) a.getSerializableExtra("tasks");
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
            if (taskName.equals(task.getTASK_NAME())) {
                Log.d(TAG, "task name comparisons");
                Log.d(TAG, taskName);
                Log.d(TAG, task.getTASK_NAME());
                expandTask = task;
                current_task = i;
                break;
            }
        }
        //Log.d(TAG, "pre-content view set");
        setContentView(R.layout.item_details);


        TextView task_name = (TextView) findViewById(R.id.task_name_expanded);
        task_name.setText(expandTask.getTASK_NAME());

        TextView task_description = (TextView) findViewById(R.id.description_expanded);
        task_description.setText(expandTask.getTASK_DESCRIPTION());

        TextView user = (TextView) findViewById(R.id.user_expanded);
        user.setText(expandTask.getTASK_USER());

        Log.d(TAG, "finished");

    }

    public void backToHome(){
        Intent intent = new Intent(getBaseContext(), TaskMain.class);
        startActivity(intent);

    }

    public void markAsComplete(View view){
        Task task = tasks.get(current_task);
        task.setCOMPLETE(1);
    }

}
