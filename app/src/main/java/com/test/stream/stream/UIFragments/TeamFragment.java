package com.test.stream.stream.UIFragments;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.TeamManager;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;
import com.test.stream.stream.UI.Adapters.TeamAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamFragment extends Fragment {
    private TeamManager mTeamManager = new TeamManager();

    ArrayList<User> users = new ArrayList();
    private ListView mPinListView;
    private TeamAdapter teamAdapter;
    private TextView titleText;
    private Button addUserButton;
    private EditText addUserField;

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

        addUserField = (EditText) getView().findViewById(R.id.newTeamUserField);
        addUserButton = (Button) getView().findViewById(R.id.newProjectUserButton);

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String name = addUserField.getText().toString();
                handleEnteredUser(name);
                addUserField.getText().clear();
                hideKeyboard();
            }
        });

        assert ProjectManager.sharedInstance().getCurrentProject() != null; //If we are in the project, the project should not be null.
        mTeamManager.Initialize(dataListener);

    }

    /**
     * Call to hide the android soft keyboard
     */
    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    /**
     * Checks if the string passed represents a valid user in the database.
     * Updates UI accordingly.
     * Updates users list accordingly.
     * @param uDescription
     * The description entered by the user
     */
    private void handleEnteredUser(final String uDescription){
        ReadDataCallback userResult = new ReadDataCallback() {
            @Override
            public void onDataRetrieved(DataSnapshot result) {

                if (result.exists()){

                    GenericTypeIndicator<Map<String, User>> genericTypeIndicator = new GenericTypeIndicator<Map<String, User>>() {};
                    Map <String,User> resultMap = result.getValue(genericTypeIndicator);

                    String id = (String) resultMap.keySet().toArray()[0];
                    User user = resultMap.get(id);

                    mTeamManager.AddMemberToCurrentProject(user,false);
                }else{
                    // user is invalid and cannot be added to the project as a collaborator
                    String userInvalidHelp = uDescription + " - User not found";
                    Toast.makeText(getActivity(), userInvalidHelp, Toast.LENGTH_LONG).show();
                }
            }
        };
        UserManager.sharedInstance().fetchUserByUserName(uDescription,userResult);
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
