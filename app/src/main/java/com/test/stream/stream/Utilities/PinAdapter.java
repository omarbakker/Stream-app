package com.test.stream.stream.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.stream.stream.Objects.Board.PinMessage;
import com.test.stream.stream.R;

import java.util.ArrayList;

/**
 * Created by kevinwong on 2016-10-30.
 */

public class PinAdapter extends ArrayAdapter<PinMessage> {

    public static class ViewHolder{
        TextView title;
        TextView subTitle;
        TextView description;
    }
    public PinAdapter(Context context, ArrayList<PinMessage> pins){
        super(context, 0, pins);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        PinMessage pin = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_pin,parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_pin_title);
            viewHolder.subTitle = (TextView) convertView.findViewById(R.id.item_pin_subtitle);
            viewHolder.description = (TextView) convertView.findViewById(R.id.item_pin_description);
            //Use set tag to remember viewHolder which is holding reference to widgets
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate data into template view using data object
        viewHolder.title.setText(pin.getTitle());
        viewHolder.subTitle.setText(pin.getSubtitle());
        viewHolder.description.setText(pin.getDescription());

        return convertView;
    }
}
