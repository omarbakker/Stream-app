package com.test.stream.stream.UI.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.stream.stream.Objects.Users.User;

import java.util.ArrayList;
import com.test.stream.stream.R;

/**
 * Created by kevinwong on 2016-11-18.
 */

public class TeamAdapter extends ArrayAdapter<User> {

    private Context mContext;

    public static class ViewHolder{
        ImageView image;
        TextView userName;
        TextView userEmail;
    }

    public TeamAdapter(Context context, ArrayList<User> users){
        super(context, 0, users);
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
        User user = getItem(position);
        // Create the viewHolder that stores the Pins
        ViewHolder viewHolder;
        // if there is nothing on PinBoard, add a new viewHolder
        if(convertView == null){
            viewHolder = new ViewHolder();
            Typeface ralewayBold = Typeface.createFromAsset(mContext.getAssets(), "Raleway-SemiBold.ttf");
            Typeface raleway = Typeface.createFromAsset(mContext.getAssets(), "Raleway-Regular.ttf");
            // Get the view for the pinboard
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_team,parent, false);
            // Get the TextView elements on the PinBoard
            viewHolder.image = (ImageView) convertView.findViewById(R.id.item_member_image);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.item_username);
            viewHolder.userEmail = (TextView) convertView.findViewById(R.id.item_user_email);

            viewHolder.userName.setTypeface(ralewayBold);
            viewHolder.userEmail.setTypeface(raleway);
            //Use set tag to remember viewHolder which is holding reference to widgets
            // Use the viewHolder
            convertView.setTag(viewHolder);
        } else {
            // If convertView is not null, simply update
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate data into template view using data object
        viewHolder.userName.setText(user.getName());
        viewHolder.userEmail.setText(user.getEmail());

        return convertView;
    }
}


