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
import com.test.stream.stream.Services.NotificationService;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.Adapters.ProjectsAdapter;
import com.test.stream.stream.Utilities.Callbacks.FetchUserProjectsCallback;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;

import java.util.List;


public class ProjectsActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        ListView.OnItemClickListener{
    private static final String TAG = "ProjectsActivity";

    private TextView titleText;
    private ListView mProjectsListView;
    private TextView mProjectsTextView;
    private ProjectsAdapter mAdapter;

    boolean thread_running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        // pass 'this' to the projectsManager so it can call updateUI when a project is added
        ProjectManager.sharedInstance().setProjectsActivity(this);

        // Initialize view elements
        mProjectsListView = (ListView) findViewById(R.id.ProjectsList);
        mProjectsTextView = (TextView) findViewById(R.id.text_projects);
        mAdapter = new ProjectsAdapter(this);
        mProjectsListView.setAdapter(mAdapter);
        mProjectsListView.setOnItemClickListener(this);
        titleText = (TextView) findViewById(R.id.yourProjectsTitle);
        final FloatingActionButton addProjectButton = (FloatingActionButton) findViewById(R.id.addProjectButton);
        addProjectButton.setOnClickListener(this);

        // Set font
        setFont();

        //Populate with user data
        if(UserManager.sharedInstance().getCurrentUser() != null) {
            updateUI();
            NotificationService.sharedInstance().registerUserDevice();

        } else {
            UserManager.sharedInstance().InitializeUser(new ReadDataCallback() {
                @Override
                public void onDataRetrieved(DataSnapshot result) {
                    updateUI();
                    NotificationService.sharedInstance().registerUserDevice();
                }
            });
        }

        // Register user - device pair to heliohost server
//        try {
//            NotificationService.sharedInstance().registerUserDevice();
//        }catch(NullPointerException e){
//            e.printStackTrace();
//        }
    }

    /**
     * Used to open the new projects activity after the user taps the '+' button
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.addProjectButton:
                startActivity(new Intent(ProjectsActivity.this, newProjectActivity.class));
                this.finish();
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
    public void updateUI() {
        if (!ProjectManager.sharedInstance().hasProjects()) {
            mProjectsTextView.setText(R.string.no_projects);
            mProjectsTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            mProjectsTextView.setText(R.string.empty);
            mProjectsTextView.setVisibility(View.GONE);
        }

        ProjectManager.sharedInstance().fetchCurrentUserProjects(new FetchUserProjectsCallback() {
            @Override
            public void onUserProjectsListRetrieved(List<Project> projects) {
                mAdapter.updateData(projects);
            }
        });

    }

    /**
     * sets title font to syncopate
     */
    private void setFont()
    {
        //Changing font to Syncopate
        //Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Regular.ttf");
        Typeface ralewayBold = Typeface.createFromAsset(this.getAssets(), "Raleway-ExtraBold.ttf");
        Typeface raleway = Typeface.createFromAsset(this.getAssets(), "Raleway-Regular.ttf");
        titleText.setTypeface(ralewayBold);
        mProjectsTextView.setTypeface(raleway);
    }

    /**
     * Used to enter the project after the user taps on that project from the list.
     * Opens the projects page.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (parent == mProjectsListView){
            Project selectedProject = (Project) mAdapter.getItem(position);
            ProjectManager.sharedInstance().setCurrentProject(selectedProject);
            Intent intent = new Intent(this,ToolbarActivity.class);
            intent.putExtra("frgToLoad", "HOME");
            startActivity(intent);
            this.finish();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
