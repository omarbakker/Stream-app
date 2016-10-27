package com.test.stream.stream.UI;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.Adapters.newProjectUsersAdapter;


public class newProjectActivity extends AppCompatActivity implements View.OnClickListener,EditText.OnEditorActionListener{

    // UI Elements
    private TextView titleText;
    private EditText projectNameField;
    private FloatingActionButton doneButton;
    private ListView newUsersList;
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
        addUserField = (EditText) findViewById(R.id.newUserField);
        addUserField.setOnEditorActionListener(this);
        addUserField.setImeActionLabel("next",EditorInfo.IME_ACTION_DONE);
        doneButton = (FloatingActionButton) findViewById(R.id.doneAddingProject);
        doneButton.setOnClickListener(this);

        // Initialize list for new users, set its adapter to an instance of newProjectUsersAdapter
        newUsersAdapter = new newProjectUsersAdapter(this);
        newUsersList = (ListView) findViewById(R.id.newProjectUsersList);
        newUsersList.setAdapter(newUsersAdapter);

        // Initialize a new Project object, this projects fields will be populated from the info the user enters
        newProject = new Project();

        // set font
        setSyncopateFont();

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        switch (v.getId()){
            case R.id.Project_name_Field:

                if (actionId == EditorInfo.IME_ACTION_DONE){
                    hideKeyboard();
                    String name = v.getText().toString();
                    newProject.setName(name);
                    handled = true;
                }
                break;

            case R.id.newUserField:

                if (actionId == EditorInfo.IME_ACTION_DONE){
                    hideKeyboard();
                    newUsersAdapter.addItem(v.getText().toString());
                    v.setText("");
                    v.clearFocus();
                    handled = true;
                }
                break;
            default:
                break;
        }

        return handled;
    }



    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.doneAddingProject:
                break;
            default:
                break;
        }
    }


    /**
     *  Add a new user to the project being created
     * @param userDescription
     * Any method of identifying the user (Email or user name).
     * If userDescription can be resolved to a valid user then
     * @return
     * True if the user was found and successfully added. false otherwise
     */
    public boolean addUserToProject(String userDescription){
        return true;
    }

    private void setSyncopateFont(){
        //Changing font to Syncopate
        //Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Regular.ttf");
        Typeface SyncopateBold = Typeface.createFromAsset(this.getAssets(), "Syncopate-Bold.ttf");
        titleText.setTypeface(SyncopateBold);
    }

}
