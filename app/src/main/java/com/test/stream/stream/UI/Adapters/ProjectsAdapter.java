package com.test.stream.stream.UI.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.ProjectsActivity;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by OmarEyad on 2016-10-13.
 * The adapter design style used here is an interface between the data source and the list view -
 * that displays this data source (Projects and projectsActivity in this case).
 * This adapter does the following:
 * 1- fetches the data from the data source (A dummy list at the moment, firebase later on).
 * 2- decides how each item is going to be displayed.
 *
 * Credit goes to raywenderlich.com for the android list view tutorial.
 */

public class ProjectsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Project> mDataSource;


    public ProjectsAdapter(Context context, ArrayList<Project> list){
        mContext = context;
        mDataSource = list;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ProjectsAdapter(Context context){
        mContext = context;
        mDataSource = new ArrayList<Project>();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Update the data source with a new list updatedList
     * @param updatedList
     * the updated list
     */
    public void updateData(List<Project> updatedList){
        mDataSource = updatedList;
        this.notifyDataSetChanged();
    }

    /**
     * How many items (projects) are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() { return mDataSource.size(); }


    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // check if the view already exists if so, no need to inflate and findViewById again!
        if (convertView == null) {

            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.projects_list_item, parent, false);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.ProjectList_Title);
            holder.infoTextView = (TextView) convertView.findViewById(R.id.Project_Info_Text);
            holder.dueDateTextView = (TextView) convertView.findViewById(R.id.Duedate_Text);
            holder.teamMatesTextView = (TextView) convertView.findViewById(R.id.TeamMates_Text);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        }else{
            // skip all the expensive inflation/findViewById and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        // Get relevant subviews of row view
        TextView titleTextView = holder.titleTextView;
        TextView infoTextView = holder.infoTextView;
        TextView dueDateTextView = holder.dueDateTextView;
        TextView teamMatesTextView = holder.teamMatesTextView;

        //Get corresponding project for row
        Project project = (Project) getItem(position);

        // Update row view's textviews to display projext information
        titleTextView.setText(project.getName());
        String members = project.getMembers().size() + " members";
        teamMatesTextView.setText(members);
        int activeTasks = project.getNumberOfActiveTasks();
        String info = activeTasks == 0 ? "No Active Tasks": activeTasks == 1 ? "1 Active Task": activeTasks + " Active Tasks";
        infoTextView.setText(info);
        dueDateTextView.setText(project.dueDateRepresentation());

        Typeface ralewayBold = Typeface.createFromAsset(mContext.getAssets(), "Raleway-SemiBold.ttf");
        Typeface raleway = Typeface.createFromAsset(mContext.getAssets(), "Raleway-Regular.ttf");
        titleTextView.setTypeface(ralewayBold);
        teamMatesTextView.setTypeface(raleway);
        infoTextView.setTypeface(raleway);
        dueDateTextView.setTypeface(raleway);

        if (position%2 == 0){
            convertView.setBackgroundColor(Color.argb(255,225,237,255));
        }else{
            convertView.setBackgroundColor(Color.argb(235,232,255,245));
        }

        return convertView;
    }

    private static class ViewHolder {
        public TextView titleTextView;
        public TextView infoTextView;
        public TextView dueDateTextView;
        public TextView teamMatesTextView;
    }
}
