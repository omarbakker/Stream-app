package com.test.stream.stream.UI.Adapters;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.DatabaseManager;

import java.util.ArrayList;
import java.util.concurrent.CompletionService;

import static android.R.attr.name;

/**
 * Created by OmarEyad on 2016-10-23.
 */

public class newProjectUsersAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> mDataSource;
    private User newUser;

    public newProjectUsersAdapter(Context context) {
        mContext = context;
        mDataSource = new ArrayList<String>();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void addItem(String item) {
        mDataSource.add(item);
        this.notifyDataSetChanged();
    }



    @Override
    public int getCount() { return mDataSource.size(); }



    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        newProjectUsersAdapter.ViewHolder holder;

        // check if the view already exists if so, no need to inflate and findViewById again!
        if (convertView == null) {

            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.new_project_user_list_item, parent, false);

            // create a new "Holder" with subviews
            holder = new newProjectUsersAdapter.ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.userListItemTextView);
            holder.validUserIndicator = (ImageView) convertView.findViewById(R.id.validUserIndicator);
            // hide the user valid image until the user is found and verified
            holder.indicateUserValid(true);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        }else{

            // skip all the expensive inflation/findViewById and just get the holder you already made
            holder = (newProjectUsersAdapter.ViewHolder) convertView.getTag();
        }

        // Get relevant subviews of row view
        TextView titleTextView = holder.titleTextView;

        // Display the description of the new  user
        titleTextView.setText((String) getItem(position));

        return convertView;
    }


    private static class ViewHolder {
        public TextView titleTextView;
        public ImageView validUserIndicator;

        public void indicateUserValid(boolean valid){
            if (valid){
                validUserIndicator.setVisibility(View.VISIBLE);
            }else{
                validUserIndicator.setVisibility(View.INVISIBLE);
            }
        }
    }
}