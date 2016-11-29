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
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.R;
import com.test.stream.stream.UIFragments.TasksFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by OmarEyad on 2016-11-27.
 */

public class TaskAdapter extends BaseAdapter{

    public static final int DIVIDER_TYPE = 1;
    public static final int TASK_TYPE = 2;

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Object> mDataSource;

    public TaskAdapter(Context context, List<Object> list){
        mContext = context;
        mDataSource = list;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public TaskAdapter(Context context){
        mContext = context;
        mDataSource = new ArrayList<Object>();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Update the data source with a new list updatedList
     * @param updatedList
     * the updated list
     */
    public void updateData(List<Object> updatedList){
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


    @Override
    public int getViewTypeCount(){
        return 2;
    }

    public int getItemType(int position){
        // Your if else code and return type ( TYPE_1 to TYPE_5 )

        if (mDataSource.get(position).getClass() == Task.class){
            return TASK_TYPE;
        }else {
            return DIVIDER_TYPE;
        }
    }

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

        int itemType = getItemType(position);
        Typeface ralewayBold = Typeface.createFromAsset(mContext.getAssets(), "Raleway-SemiBold.ttf");
        Typeface raleway = Typeface.createFromAsset(mContext.getAssets(), "Raleway-Regular.ttf");

        switch (itemType){
            case DIVIDER_TYPE:

                TaskAdapter.Divider divider;

                // check if the view already exists if so, no need to inflate and findViewById again!
                if (convertView == null || convertView.getTag() instanceof TaskAdapter.ViewHolder) {

                    // Inflate the custom row layout from your XML.
                    convertView = mInflater.inflate(R.layout.task_list_divider, parent, false);

                    // create a new "Holder" with subviews
                    divider = new TaskAdapter.Divider();
                    divider.title = (TextView) convertView.findViewById(R.id.task_divider_title) ;

                    // hang onto this holder for future recyclage
                    convertView.setTag(divider);
                }else{
                    // skip all the expensive inflation/findViewById and just get the holder you already made
                    divider = (TaskAdapter.Divider) convertView.getTag();
                }

                // Get relevant subviews of row view
                TextView dividerTitleTextView = divider.title;

                //Get corresponding project for row
                String dividerTitle = (String) getItem(position);

                // Update row view's textviews to display projext information
                dividerTitleTextView.setText(dividerTitle);

                dividerTitleTextView.setTypeface(ralewayBold);

                return convertView;
            case TASK_TYPE:
            default:

                TaskAdapter.ViewHolder holder;

                // check if the view already exists if so, no need to inflate and findViewById again!
                if (convertView == null || convertView.getTag() instanceof TaskAdapter.Divider) {

                    // Inflate the custom row layout from your XML.
                    convertView = mInflater.inflate(R.layout.task_list_item, parent, false);

                    // create a new "Holder" with subviews
                    holder = new TaskAdapter.ViewHolder();
                    holder.titleTextView = (TextView) convertView.findViewById(R.id.TaskList_Title);
                    holder.infoTextView = (TextView) convertView.findViewById(R.id.Task_Info_Text);
                    holder.dueDateTextView = (TextView) convertView.findViewById(R.id.TaskDuedate_Text);

                    // hang onto this holder for future recyclage
                    convertView.setTag(holder);
                }else{
                    // skip all the expensive inflation/findViewById and just get the holder you already made
                    holder = (TaskAdapter.ViewHolder) convertView.getTag();
                }

                // Get relevant subviews of row view
                TextView titleTextView = holder.titleTextView;
                TextView infoTextView = holder.infoTextView;
                TextView dueDateTextView = holder.dueDateTextView;

                //Get corresponding project for row
                Task task = (Task) getItem(position);

                // Update row view's textviews to display projext information
                titleTextView.setText(task.getName());
                String assignee = task.getAssignee();
                String info = "Assigned to " + assignee;
                infoTextView.setText(info);
                dueDateTextView.setText(task.dueDateRepresentation());


                titleTextView.setTypeface(ralewayBold);
                infoTextView.setTypeface(raleway);
                dueDateTextView.setTypeface(raleway);

                convertView.setBackgroundColor(getColorForDueTask(task));


                return convertView;
        }

    }

    public static int getColorForDueTask(Task task){

        int color;
        if (TasksFragment.isAssignedToCurrentUser(task)){
            color = Color.argb(235,232,255,245);
        }else{
            color = Color.argb(255,225,237,255);
        }

        // red for past due and not complete
        int[] dueDate = {task.getDueYear(),task.getDueMonth(),task.getDueDay()};
        if (TasksFragment.isPastDue(dueDate) && !task.getComplete()){
            color = Color.argb(235,255,220,220);
        }else if (task.getComplete()){
            color = Color.argb(235,220,220,220);
        }

        return color;
    }

    private static class ViewHolder {
        public TextView titleTextView;
        public TextView infoTextView;
        public TextView dueDateTextView;
    }

    private static class Divider {
        public TextView title;
    }
}
