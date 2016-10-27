package com.test.stream.stream.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import com.test.stream.stream.R;

import java.util.ArrayList;
import java.util.List;


public class ProjectsActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView titleText;
    private ListView mProjectsList;
    private FloatingActionButton addProjectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        // Initialize view elements
        mProjectsList = (ListView) findViewById(R.id.ProjectsList);
        titleText = (TextView) findViewById(R.id.yourProjectsTitle);
        addProjectButton = (FloatingActionButton) findViewById(R.id.addProjectButton);
        addProjectButton.setOnClickListener(this);

        // Set font
        setFont();

        // User manager contains userID and all the projects. Should also contain
        // the username, but haven't included it yet.
        // UserManager userManager = UserManager.getInstance();
        List<String> projectList = new ArrayList<>();
/*
        // Adds all the projects into projectList
        Iterator projectIterator = userManager.getProjects().entrySet().iterator();
            while(projectIterator.hasNext()) {
                Map.Entry projectPair = (Map.Entry) projectIterator.next();
                projectList.add((String)projectPair.getValue());
            }
*/


    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.addProjectButton:
                System.out.println("Hello!");
                startActivity(new Intent(ProjectsActivity.this,newProjectActivity.class));
                break;
            default:
                break;
        }
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setFont(){
        //Changing font to Syncopate
        //Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Regular.ttf");
        Typeface SyncopateBold = Typeface.createFromAsset(this.getAssets(), "Syncopate-Bold.ttf");
        titleText.setTypeface(SyncopateBold);
    }
}
