package com.test.stream.stream.UIFragments;


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.test.stream.stream.Controllers.HomeManager;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;
import com.test.stream.stream.UI.Adapters.HomeTaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProjectHomeFragment extends Fragment {

    private DonutProgress teamProgress;
    private DonutProgress userProgress;

    private static final String TAG = "Home Fragment";

    ArrayList<Task> taskMessages = new ArrayList<>();
    private HomeTaskAdapter homeTaskAdapter;

    private HomeManager homeManager;

    private DataEventListener dataListener = new DataEventListener() {
        @Override
        public void onDataChanged() {
            updateUI();
        }
    };
    private ListView listView;

    public ProjectHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        teamProgress = (DonutProgress) getView().findViewById(R.id.team_progress);
        userProgress = (DonutProgress) getView().findViewById(R.id.user_progress);

        homeManager = HomeManager.sharedInstance();
        homeManager.setUIListener(dataListener);

        // initialize both progresses to 0
        userProgress.setProgress(0);
        teamProgress.setProgress(0);
    }

    private void updateProgress(DonutProgress donutProgress, int newProgress){
        if(android.os.Build.VERSION.SDK_INT >= 11){
            // will update the "progress" propriety of seekbar until it reaches progress
            ObjectAnimator animation = ObjectAnimator.ofInt(donutProgress, "progress", newProgress);
            animation.setDuration(1500); // 1.5 seconds
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        }
        else
            donutProgress.setProgress(newProgress);
    }
/*
    private void updateTeamProgress(int newProgress){
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.progress_anim);
        set.setTarget();
        ObjectAnimator anim = ObjectAnimator.ofInt(R.id.team_progress, "circle_progress", 0, 100);
    }
*/
    /**
     * Function that updates the Adapter of the ListFragment
     */
    public void updateUI() {

        // Get all user tasks from the database
        List<Task> allTasks = homeManager.getUserTasks();

        ArrayList<Task> tasks = new ArrayList();
        listView = (ListView) getView().findViewById(R.id.task_list);

        // Go through each task in database
        for (Task currentTask : allTasks) {
            if (currentTask.getClass() == Task.class) {
                tasks.add(currentTask);
            }
        }
        if (homeTaskAdapter == null) {
            homeTaskAdapter = new HomeTaskAdapter(getActivity(), taskMessages);
            listView.setAdapter(homeTaskAdapter);
        }
        homeTaskAdapter.clear();
        homeTaskAdapter.addAll(tasks);
        homeTaskAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(listView);

        updateProgress(teamProgress, homeManager.getTeamProgress());
        updateProgress(userProgress, homeManager.getUserProgress());
    }

    /****
     * Method for Setting the Height of the ListView dynamically.
     * *** Hack to fix the issue of not showing all the items of the ListView
     * *** when placed inside a ScrollView
     ****/
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        System.out.println("AdapterCount: " + listAdapter.getCount());
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
