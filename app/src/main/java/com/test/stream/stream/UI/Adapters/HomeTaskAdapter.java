package com.test.stream.stream.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.DateUtility;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by robyn on 2016-10-30.
 */
public class HomeTaskAdapter extends ArrayAdapter<Task> {

    public HomeTaskAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Task task = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_task, parent, false);
        }

        TextView textTitle = (TextView) convertView.findViewById(R.id.item_task_title);
        TextView textDescription = (TextView) convertView.findViewById(R.id.item_task_description);
        TextView textDueDate = (TextView) convertView.findViewById(R.id.item_task_duedate);
        TextView textDueIn = (TextView) convertView.findViewById(R.id.num_days);

        int[] dueDate = {task.getDueYear(), task.getDueMonth(), task.getDueDay()};

        long daysToDueDate = DateUtility.getDaysToDueDate(dueDate);
        if (daysToDueDate > 365)
            textDueIn.setText("> 1 YEAR");
        else if(daysToDueDate > 0)
            textDueIn.setText(daysToDueDate + " DAY(s)");
        else if (daysToDueDate < 0)
            textDueIn.setText("PAST DUE");
        else
            textDueIn.setText("< 1 DAY");

        textTitle.setText(task.getName());
        textDescription.setText(task.getDescription());
        String dueTime = new String(task.getDueMonth() + "/" + task.getDueDay() + "/" + task.getDueYear());
        textDueDate.setText(dueTime);

        return convertView;
    }
}
