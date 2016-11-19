package com.test.stream.stream.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.Typeface;
import com.test.stream.stream.Objects.Board.Pin;
import com.test.stream.stream.R;

import java.util.ArrayList;

/**
 * Created by kevinwong on 2016-10-30.
 */

public class PinAdapter extends ArrayAdapter<Pin> {

    private Context mContext;
    /**
     * Declare the elements of how the Pin should look
     */
    public static class ViewHolder{
        TextView title;
        TextView subTitle;
        TextView description;
    }

    /**
     * Constructor for making PinAdapter
     * @param context
     * @param pins
     */
    public PinAdapter(Context context, ArrayList<Pin> pins){
        super(context, 0, pins);
        mContext = context;
    }

    /**
     * Logic for adding text to the title, subTitle and description fields of the PinMessage
     * @param position
     * @param convertView
     * @param parent
     * @return the View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        // Get the PinMessage that is being created
        Pin pin = getItem(position);
        // Create the viewHolder that stores the Pins
        ViewHolder viewHolder;
        // if there is nothing on PinBoard, add a new viewHolder
        if(convertView == null){
            viewHolder = new ViewHolder();
            Typeface ralewayBold = Typeface.createFromAsset(mContext.getAssets(), "Raleway-SemiBold.ttf");
            Typeface raleway = Typeface.createFromAsset(mContext.getAssets(), "Raleway-Regular.ttf");
            // Get the view for the pinboard
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_pin,parent, false);
            // Get the TextView elements on the PinBoard
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_pin_title);
            viewHolder.subTitle = (TextView) convertView.findViewById(R.id.item_pin_subtitle);
            viewHolder.description = (TextView) convertView.findViewById(R.id.item_pin_description);

            viewHolder.title.setTypeface(ralewayBold);
            viewHolder.subTitle.setTypeface(raleway);
            viewHolder.description.setTypeface(raleway);
            //Use set tag to remember viewHolder which is holding reference to widgets
            // Use the viewHolder
            convertView.setTag(viewHolder);
        } else {
            // If convertView is not null, simply update
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate data into template view using data object
        viewHolder.title.setText(pin.getTitle());
        viewHolder.subTitle.setText(pin.getSubtitle());
        viewHolder.description.setText(pin.getDescription());

        /*if(pin.getTitle().equals("kevstest")){
            convertView.setBackgroundColor(Color.RED);
        }*/
        return convertView;
    }
}
