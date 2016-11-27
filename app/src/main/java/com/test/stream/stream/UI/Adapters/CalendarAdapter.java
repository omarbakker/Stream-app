package com.test.stream.stream.UI.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.PinAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohini Goyal on 11/22/2016.
 */

public class CalendarAdapter extends ArrayAdapter<Meeting> {

    private final Context context;
    private final List<Meeting> data;
    private final int layoutResourceId;

    public CalendarAdapter(Context context, int layoutResourceId, List<Meeting> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.meetingName = (TextView)row.findViewById(R.id.meeting_name);
            holder.meetingLocation = (TextView)row.findViewById(R.id.meeting_location);
            holder.meetingTime = (TextView)row.findViewById(R.id.meeting_time);
            holder.meetingDate = (TextView)row.findViewById(R.id.meeting_date);
           // holder.meetingDescription = (TextView)row.findViewById(R.id.meeting_description);

            row.setTag(holder);
        }
        else {
            holder = (ViewHolder)row.getTag();
        }

        Meeting meeting = data.get(position);
        holder.meetingName.setText(meeting.getName());
        holder.meetingLocation.setText("Location: " + meeting.getLocation());
        holder.meetingDate.setText(meeting.getDayOfWeek() + ", " + meeting.getDay() + " " + meeting.getMonth() + " " + meeting.getYear());
        if(meeting.getMinute() < 10) {
            holder.meetingTime.setText(meeting.getHour() + ":" + "0" + meeting.getMinute() + " " + meeting.getAmPm());
        }
        else {
            holder.meetingTime.setText(meeting.getHour() + ":" + meeting.getMinute() + " " + meeting.getAmPm());
        }

        //holder.meetingDescription.setText(meeting.getDescription());

        if (position%2 == 0){
            row.setBackgroundColor(Color.argb(255,225,237,255));
        }else{
            row.setBackgroundColor(Color.argb(235,232,255,245));
        }

        return row;
    }

    static class ViewHolder
    {
        TextView meetingName;
        TextView meetingLocation;
        TextView meetingTime;
        TextView meetingDate;
        //TextView meetingDescription;
    }

    public void addAll(ArrayList<Meeting> datas) {
        for(int i = 0; i < datas.size(); i ++) {
            data.add(datas.get(i));
        }

    }
}
