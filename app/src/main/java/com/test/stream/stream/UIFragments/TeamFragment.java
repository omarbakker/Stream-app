package com.test.stream.stream.UIFragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Controllers.TeamManager;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamFragment extends Fragment {
    private TeamManager mTeamManager = new TeamManager();

    private DataEventListener dataListener = new DataEventListener() {
        @Override
        public void onDataChanged() {
            System.out.println("Num members " + mTeamManager.GetUsers().size());
            //TODO: update the UI since the member list has changed.
        }
    };

    public TeamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        assert ProjectManager.sharedInstance().getCurrentProject() != null; //If we are in the project, the project should not be null.
        mTeamManager.Initialize(dataListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onDestroyView() {
        mTeamManager.Destroy();
        super.onDestroyView();
    }
}
