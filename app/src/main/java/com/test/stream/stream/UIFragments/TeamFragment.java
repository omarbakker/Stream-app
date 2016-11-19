package com.test.stream.stream.UIFragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Controllers.BoardManager;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.TeamManager;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;
import com.test.stream.stream.Utilities.TeamAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamFragment extends Fragment {
    private TeamManager mTeamManager = new TeamManager();

    ArrayList<User> users = new ArrayList();
    private ListView mPinListView;
    private TeamAdapter teamAdapter;
    private TextView titleText;

    /*
     * Listen to data change and update UI
     */
    private DataEventListener dataListener = new DataEventListener() {
        @Override
        public void onDataChanged() {
            //System.out.println("Num members " + mTeamManager.GetUsers().size());
            updateUI();
        }
    };

    public TeamFragment() {
        // Required empty public constructor
    }

    /**
     * Function that updates the Adapter of the Fragment
     */
    private void updateUI() {

        List<User> allUsers = mTeamManager.GetUsers();
        ArrayList<User> users = new ArrayList();
        // Go through each user in database
        for (User currentUser : allUsers) {
            users.add(currentUser);
        }
        // Reverse User order to show newly created on top
        Collections.reverse(users);
        if (teamAdapter == null) {
            // If nothing in adapter then create a new one and set the adapter to show users
            teamAdapter = new TeamAdapter(getActivity(), this.users);
            mPinListView.setAdapter(teamAdapter);

            // Otherwise add all the users in the current adapter and notify that adapter changed
        } else {
            teamAdapter.clear();
            teamAdapter.addAll(users);
            teamAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPinListView = (ListView) getView().findViewById(R.id.list_team);
        teamAdapter = new TeamAdapter(getActivity(), users);
        mPinListView.setAdapter(teamAdapter);
        // Set font of text tile
        titleText = (TextView) getView().findViewById(R.id.team_text);
        Typeface Syncopate = Typeface.createFromAsset(getActivity().getAssets(), "Raleway-Regular.ttf");
        titleText.setTypeface(Syncopate);
        assert ProjectManager.sharedInstance().getCurrentProject() != null; //If we are in the project, the project should not be null.
        mTeamManager.Initialize(dataListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_team, container, false);
    }

    @Override
    public void onDestroyView() {
        mTeamManager.Destroy();
        super.onDestroyView();
    }
}
