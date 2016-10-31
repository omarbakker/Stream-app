package com.test.stream.stream.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.stream.stream.Objects.Tasks.TaskMessage;
import com.test.stream.stream.R;

import java.util.ArrayList;

/**
 * Created by robyn on 2016-10-30.
 */

public class TaskAdapter extends ArrayAdapter<TaskMessage> {

    public TaskAdapter(Context context, ArrayList<TaskMessage> tasks){
        super(context, 0, tasks);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        TaskMessage task = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_pin,parent, false);
        }

        TextView textTitle = (TextView) convertView.findViewById(R.id.item_pin_title);
        TextView textDescription = (TextView) convertView.findViewById(R.id.item_pin_subtitle);
        TextView textDueDate = (TextView) convertView.findViewById(R.id.item_pin_description);

        textTitle.setText(task.getName());
        textDescription.setText(task.getDescription());
        String dueDate = new String(task.getDueMonth() + "/" + task.getDueDay() + "/" + task.getDueYear());
        textDueDate.setText(dueDate);

        return convertView;
    }
}
