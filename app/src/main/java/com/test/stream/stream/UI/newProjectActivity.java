package com.test.stream.stream.UI;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.Adapters.newProjectUsersAdapter;
import com.test.stream.stream.Utilities.Callbacks.FetchUserCallback;
import com.test.stream.stream.Utilities.ReadDataCallback;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.R.attr.key;
import static android.R.attr.name;
import static android.os.Build.VERSION_CODES.M;


public class newProjectActivity extends AppCompatActivity implements View.OnClickListener,EditText.OnEditorActionListener{

    // UI Elements
    private TextView titleText;
    private EditText projectNameField;
    private EditText projectDateField;
    private Button doneButton;
    private Button addUserButton;
    private EditText addUserField;
    private newProjectUsersAdapter newUsersAdapter;
    private Project newProject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        // Initializations
        titleText = (TextView) findViewById(R.id.newProjectPageTitle);
        projectNameField = (EditText) findViewById(R.id.Project_name_Field);
        projectNameField.setOnEditorActionListener(this);
        projectNameField.setImeActionLabel("next",EditorInfo.IME_ACTION_DONE);
        projectDateField = (EditText) findViewById(R.id.newProjectDueDateField);
        projectDateField.setOnEditorActionListener(this);
        projectDateField.setImeActionLabel("set",EditorInfo.IME_ACTION_DONE);
        addUserField = (EditText) findViewById(R.id.newUserField);
        addUserField.setOnEditorActionListener(this);
        addUserField.setImeActionLabel("Add",EditorInfo.IME_ACTION_DONE);
        doneButton = (Button) findViewById(R.id.doneAddingProject);
        doneButton.setOnClickListener(this);
        addUserButton = (Button) findViewById(R.id.newProjectAddUserButton);
        addUserButton.setOnClickListener(this);

        // Initialize list for new users, set its adapter to an instance of newProjectUsersAdapter
        newUsersAdapter = new newProjectUsersAdapter(this);
        ListView newUsersListView = (ListView) findViewById(R.id.newProjectUsersList);
        newUsersListView.setAdapter(newUsersAdapter);

        // Initialize a new Project object, this projects fields will be populated from the info the user enters
        newProject = new Project();

        // TODO: Replace with actual logged in user
        UserManager.getInstance().tempFetchHardCodedUser(new ReadDataCallback() {
            @Override
            public void onDataRetrieved(DataSnapshot result) {
                newProject.addMember(result.getValue(User.class),true);
            }
        });

        // set font
        setSyncopateFont();
    }



    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.doneAddingProject:

                // Check for valid inputs again
                if (projectNameField.getText().toString().isEmpty()){
                    handleInvalidName();
                    projectNameField.requestFocus();
                }else if (newProject.getDueYear() == 0 && !getValidDate(projectDateField.getText().toString())){
                    handleInvalidDate();
                    projectDateField.requestFocus();
                }else{

                    // set name again in case user did not click on 'done'
                    newProject.setName(projectNameField.getText().toString());

                    // Have ProjectManager handle creating the project
                    ProjectManager.sharedInstance().CreateProject(newProject);

                    // dismiss the new project wizard
                    this.finish();
                }
                break;
            case R.id.newProjectAddUserButton:
                handleEnteredUser(addUserField.getText().toString());
                hideKeyboard();
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        switch (v.getId()){

            case R.id.Project_name_Field:

                if (!isValidProjectName(projectNameField.getText().toString())){
                    handleInvalidName();
                    break;
                }
                projectDateField.requestFocus();
                newProject.setName(projectNameField.getText().toString());
                break;

            case R.id.newProjectDueDateField:
                if (!getValidDate(projectDateField.getText().toString())){
                    handleInvalidDate();
                    break;
                }
                addUserField.requestFocus();
                hideKeyboard();
                break;

            case R.id.newUserField:

                handleEnteredUser(v.getText().toString());
                hideKeyboard();
                break;

            default:
                break;
        }

        return true;
    }

    private boolean isValidProjectName(String name){
        return !name.isEmpty();
    }

    private boolean getValidDate(String date){
        String[] vals = date.split("/");
        int[] dateVals = {0,0,0};
        if (vals.length != 3)
            return false;
        for (int i = 0; i < vals.length; i++){
            try{
                dateVals[i] = Integer.parseInt(vals[i]);
            }catch (NumberFormatException e){
                return false;
            }
        }
        newProject.setDueDate(dateVals[0],dateVals[1],dateVals[2]);
        return true;
    }

    private void handleInvalidName(){
        projectNameField.setText(R.string.new_project_prompt_name);
        projectNameField.selectAll();
    }

    private void handleInvalidDate(){
        projectDateField.setText(R.string.new_project_prompt_date);
        projectDateField.selectAll();
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
                    // user is valid and can be added to the project as a collaborator
                    addUserField.setText("");
                    addUserField.clearFocus();
                    newUsersAdapter.addItem(uDescription);

                    GenericTypeIndicator<Map<String, User>> genericTypeIndicator = new GenericTypeIndicator<Map<String, User>>() {};
                    Map <String,User> resultMap = result.getValue(genericTypeIndicator);

                    String id = (String) resultMap.keySet().toArray()[0];
                    User user = resultMap.get(id);

                    newProject.addMember(user,false);
                    hideKeyboard();
                }else{
                    // user is invalid and cannot be added to the project as a collaborator
                    String userInvalidHelp = uDescription + " - User not found";
                    addUserField.setText(userInvalidHelp);
                    addUserField.selectAll();
                }
            }
        };
        UserManager.getInstance().checkUserExists(uDescription,userResult);
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setSyncopateFont(){
        //Changing font to Syncopate
        Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Regular.ttf");
        Typeface SyncopateBold = Typeface.createFromAsset(this.getAssets(), "Syncopate-Bold.ttf");
        titleText.setTypeface(SyncopateBold);
        doneButton.setTypeface(Syncopate);
        addUserButton.setTypeface(Syncopate);
    }


}