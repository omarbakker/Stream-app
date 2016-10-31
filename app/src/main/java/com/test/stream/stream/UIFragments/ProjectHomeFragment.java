package com.test.stream.stream.UIFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.test.stream.stream.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectHomeFragment extends Fragment {

    private CircleProgress teamProgress;
    private CircleProgress userProgress;

    private static final String TAG = "Home Fragment";

    public ProjectHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        View view = getView();

        teamProgress = (CircleProgress) view.findViewById(R.id.team_progress);
        userProgress = (CircleProgress) view.findViewById(R.id.user_progress);

        updateProgressBar(teamProgress, 100);
        updateProgressBar(userProgress, 90);
    }

    private void updateProgressBar(CircleProgress progress, int newProgress){
        progress.setProgress(0);           // initialize to 0
        progress.setMax(100);              // set max to 100
        progress.setProgress(newProgress); // update progress
    }

}
