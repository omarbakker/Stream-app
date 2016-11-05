package com.test.stream.stream.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.Adapters.ProjectsAdapter;
import com.test.stream.stream.Utilities.Callbacks.FetchUserProjectsCallback;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;

import java.util.List;


public class ProjectsActivity extends AppCompatActivity implements View.OnClickListener,ListView.OnItemClickListener{

    private TextView titleText;
    private ListView mProjectsListView;
    private ProjectsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        // pass 'this' to the projectsManager so it can call updateUI when a project is added
        ProjectManager.sharedInstance().setProjectsActivity(this);

        // Initialize view elements
        mProjectsListView = (ListView) findViewById(R.id.ProjectsList);
        mAdapter = new ProjectsAdapter(this);
        mProjectsListView.setAdapter(mAdapter);
        mProjectsListView.setOnItemClickListener(this);
        titleText = (TextView) findViewById(R.id.yourProjectsTitle);
        final FloatingActionButton addProjectButton = (FloatingActionButton) findViewById(R.id.addProjectButton);
        addProjectButton.setOnClickListener(this);

        // Set font
        setFont();

        //Populate with user data
        if(UserManager.getInstance().getCurrentUser() != null) {
            updateUI();
        } else {
            UserManager.getInstance().InitializeUser(new ReadDataCallback() {
                @Override
                public void onDataRetrieved(DataSnapshot result) {
                    updateUI();
                }
            });
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.addProjectButton:

                startActivity(new Intent(ProjectsActivity.this,newProjectActivity.class));
                break;
            default:
                break;
        }
    }

    @Override public void onStart() {
        super.onStart();
    }
    @Override public void onStop() {
        super.onStop();
    }

    /**
     * updates the list view to reflect changes in the project list fetched from ProjectManager.
     */
    public void updateUI()
    {
        ProjectManager.sharedInstance().fetchCurrentUserProjects(new FetchUserProjectsCallback() {
            @Override
            public void onUserProjectsListRetrieved(List<Project> projects) {
                mAdapter.updateData(projects);
            }
        });
    }

    private void setFont()
    {
        //Changing font to Syncopate
        //Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Regular.ttf");
        Typeface SyncopateBold = Typeface.createFromAsset(this.getAssets(), "Syncopate-Bold.ttf");
        titleText.setTypeface(SyncopateBold);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        System.out.println("AYYYYYYY LMAO!");
        if (parent == mProjectsListView){
            Project selectedProject = (Project) mAdapter.getItem(position);
            ProjectManager.sharedInstance().setCurrentProject(selectedProject);
            Intent intent = new Intent(this,ToolbarActivity.class);
            startActivity(intent);
        }

    }

}
