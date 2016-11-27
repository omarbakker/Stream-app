package com.test.stream.stream.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.R;

import java.util.ArrayList;

/**
 * Created by robyn on 2016-10-30.
 */
public class HomeTaskAdapter extends ArrayAdapter<Task> {

    public HomeTaskAdapter(Context context, ArrayList<Task> tasks){
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Task task = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_task,parent, false);
        }

        TextView textTitle = (TextView) convertView.findViewById(R.id.item_task_title);
        TextView textDescription = (TextView) convertView.findViewById(R.id.item_task_description);
        TextView textDueDate = (TextView) convertView.findViewById(R.id.item_task_duedate);

        textTitle.setText(task.getName());
        textDescription.setText(task.getDescription());
        String dueDate = new String(task.getDueMonth() + "/" + task.getDueDay() + "/" + task.getDueYear());
        textDueDate.setText(dueDate);

        return convertView;
    }
}
