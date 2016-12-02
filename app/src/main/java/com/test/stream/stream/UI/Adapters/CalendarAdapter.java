package com.test.stream.stream.UI.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.DateUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohini Goyal on 11/22/2016.
 */

public class CalendarAdapter extends BaseAdapter {

    private  Context context;
    private  List<Object> data;
    private  int layoutResourceId;
    public static final int DIVIDER_TYPE = 1;
    public static final int MEETING_TYPE = 2;
    private LayoutInflater mInflater;

    public CalendarAdapter(Context context, int layoutResourceId, List<Object> data) {
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }


    public int getItemType(int position) {
        if (data.get(position).getClass() == Meeting.class)
            return MEETING_TYPE;
        else
            return DIVIDER_TYPE;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position){
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemType = getItemType(position);
        Typeface ralewayBold = Typeface.createFromAsset(context.getAssets(), "Raleway-SemiBold.ttf");
        Typeface raleway = Typeface.createFromAsset(context.getAssets(), "Raleway-Regular.ttf");

        switch (itemType) {
            case DIVIDER_TYPE:
                CalendarAdapter.Divider divider;

                if (convertView == null || convertView.getTag() instanceof CalendarAdapter.ViewHolder) {

                    // Inflate the custom row layout from your XML.
                    convertView = mInflater.inflate(R.layout.task_list_divider, parent, false);

                    // create a new "Holder" with subviews
                    divider = new CalendarAdapter.Divider();
                    divider.title = (TextView) convertView.findViewById(R.id.task_divider_title);

                    // hang onto this holder for future recyclage
                    convertView.setTag(divider);
                }
                else {
                    // skip all the expensive inflation/findViewById and just get the holder you already made
                    divider = (CalendarAdapter.Divider) convertView.getTag();
                }

                // Get relevant subviews of row view
                TextView dividerTitleTextView = divider.title;

                //Get corresponding project for row
                String dividerTitle = (String) getItem(position);

                // Update row view's textviews to display projext information
                dividerTitleTextView.setText(dividerTitle);

                dividerTitleTextView.setTypeface(ralewayBold);

                return convertView;

            case MEETING_TYPE:
            default:

                View row = convertView;
                ViewHolder holder = null;

                if (row == null || row.getTag() instanceof  CalendarAdapter.Divider) {
                    LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                    row = inflater.inflate(layoutResourceId, parent, false);

                    holder = new ViewHolder();
                    holder.meetingName = (TextView)row.findViewById(R.id.meeting_name);
                    holder.meetingLocation = (TextView)row.findViewById(R.id.meeting_location);
                    holder.meetingTime = (TextView)row.findViewById(R.id.meeting_time);
                    holder.meetingDate = (TextView)row.findViewById(R.id.meeting_date);

                    row.setTag(holder);
                }
                else {
                    holder = (CalendarAdapter.ViewHolder)row.getTag();
                }

                Meeting meeting = (Meeting) data.get(position);
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
                int[] meetingDate = {meeting.getYear(), meeting.getNumberMonth(), meeting.getDay()};
                boolean isPast = DateUtility.isPastDue(meetingDate);
                if (isPast)
                    row.setBackgroundColor(Color.argb(235,220,220,220));
                else {
                    if (position%2 == 0){
                        row.setBackgroundColor(Color.argb(255,225,237,255));
                    }else{
                        row.setBackgroundColor(Color.argb(235,232,255,245));
                    }
                }
                return row;
        }

    }

    static class ViewHolder
    {
        TextView meetingName;
        TextView meetingLocation;
        TextView meetingTime;
        TextView meetingDate;
        //TextView meetingDescription;
    }

    public void addAll(ArrayList<Object> datas) {
        for(int i = 0; i < datas.size(); i ++) {
            data.add(datas.get(i));
        }

    }
    public void updateData(List<Object> updatedList){
        data = updatedList;
        this.notifyDataSetChanged();
    }

    private static class Divider {
        public TextView title;
    }

}
