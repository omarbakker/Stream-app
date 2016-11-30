package com.test.stream.stream.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.test.stream.stream.Controllers.BoardManager;
import com.test.stream.stream.Controllers.CalendarManager;
import com.test.stream.stream.Controllers.HomeManager;
import com.test.stream.stream.Listener.UndoTaskOnClickListener;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.R;
import com.test.stream.stream.Services.NotificationService;
import com.test.stream.stream.UIFragments.CalendarFragment;
import com.test.stream.stream.UIFragments.ProjectHomeFragment;
import com.test.stream.stream.UIFragments.TeamFragment;
import com.test.stream.stream.UIFragments.BoardFragment;
import com.test.stream.stream.UIFragments.TasksFragment;
import com.test.stream.stream.UIFragments.expand_task;

import java.util.List;

public class ToolbarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Toolbar Activity";
    // variables for the passing extras from Activity to Fragment

    private FirebaseAuth firebase = FirebaseAuth.getInstance();
    boolean thread_running = true;
    private Toolbar toolbar;
    DrawerLayout drawer;
    private FragmentManager manager;
    /**
     * On create the ToolbarActivity, all initializations
     * @param savedInstanceState
     */
//    String deviceToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set how the view will look like
        setContentView(R.layout.toolbaractivity_main);
        // Set how the toolbar will look like
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //register device token
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Show the details of the navigation
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        this.manager = getSupportFragmentManager();
        String intentFragment;

        try {
            intentFragment = getIntent().getExtras().getString("frgToLoad");
        }
        catch (NullPointerException e) {
            intentFragment = "HOME";
        }
        initializeFragment(intentFragment);

    }

    /**
     * When clicking the Back button
     */
    @Override
    public void onBackPressed() {
        // Find the specified back button
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Create the menu to show clickable toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Check which item on navigation menu is clicked
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check which button is clicked on Toolbar and go to that fragment
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            // If Calendar button is clicked, launch CalendarFragment
            case R.id.nav_calendar:
                getSupportActionBar().setTitle("Calendar");
                CalendarFragment calendarFragment = new CalendarFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        calendarFragment,
                        calendarFragment.getTag()).commit();
                break;
            // If PinBoard is clicked, launch PinBoard Fragment
            case R.id.nav_pinboard:
                getSupportActionBar().setTitle("Pin Board");
                BoardFragment boardFragment = new BoardFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        boardFragment,
                        boardFragment.getTag()).commit();

                break;
            // If Settings is clicked, launch Settings Fragment
            case R.id.nav_team:
                getSupportActionBar().setTitle("Team");
                TeamFragment teamFragment = new TeamFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        teamFragment,
                        teamFragment.getTag()).commit();
                break;
            // if Tasks is clicked, launch Tasks Fragment
            case R.id.nav_tasks:
                getSupportActionBar().setTitle("Tasks");
                TasksFragment taskFragment = new TasksFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        taskFragment,
                        taskFragment.getTag()).commit();
                break;
            // If Home button is clicked launch Home Fragment
            case R.id.nav_home:
                getSupportActionBar().setTitle("My Project");
                ProjectHomeFragment projectHomeFragment = new ProjectHomeFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        projectHomeFragment,
                        projectHomeFragment.getTag()).commit();
                break;
            // If Projects clicked, Launch Projects page
            case R.id.nav_projects:
                clearProjectInstance();
                finish();
                break;
            case R.id.nav_logout:
                clearProjectInstance();
                NotificationService.sharedInstance().deleteDeviceTokenFromDatabse();
                com.test.stream.stream.Controllers.UserManager
                        .sharedInstance().logout();

                Intent intent = new Intent(ToolbarActivity.this, MainLoginScreen.class);
                startActivity(intent);
                this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearProjectInstance()
    {
        HomeManager.sharedInstance().Destroy();
        CalendarManager.sharedInstance().Destroy();
        BoardManager.sharedInstance().Destroy();
    }


    /**
     * Launches a fragment based on the id given.
     *
     * @param id
     */
    public void initializeFragment(String id){
        switch(id){
            // If Calendar button is clicked, launch CalendarFragment
            case "CALENDAR":
                getSupportActionBar().setTitle("Calendar");
                CalendarFragment calendarFragment = new CalendarFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        calendarFragment,
                        calendarFragment.getTag()).commit();
                break;
            // If PinBoard is clicked, launch PinBoard Fragment
            case "PINBOARD":
                getSupportActionBar().setTitle("Pin Board");
                BoardFragment boardFragment = new BoardFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        boardFragment,
                        boardFragment.getTag()).commit();

                break;
            // If Settings is clicked, launch Settings Fragment
            case "TEAM":
                getSupportActionBar().setTitle("Team");
                TeamFragment teamFragment = new TeamFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        teamFragment,
                        teamFragment.getTag()).commit();
                break;
            // if Tasks is clicked, launch Tasks Fragment
            case "TASKS":
                getSupportActionBar().setTitle("Tasks");
                TasksFragment taskFragment = new TasksFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        taskFragment,
                        taskFragment.getTag()).commit();
                break;
            // If Home button is clicked launch Home Fragment
            case "HOME":
                getSupportActionBar().setTitle("My Project");
                ProjectHomeFragment projectHomeFragment = new ProjectHomeFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        projectHomeFragment,
                        projectHomeFragment.getTag()).commit();
                break;
        }
    }
    public void setTitle(CharSequence title) {

    }



    /**
     * Launch the Task view to see details
     * @param view
     */

    public void expandTaskView(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_name);
        String taskName = String.valueOf(taskTextView.getText());
        Intent intent = new Intent(this, expand_task.class);
        intent.putExtra("taskName", taskName);
        startActivity(intent);
    }

    /**
     * Marks a task as complete when the completion check box has been pressed
     * @param view
     */
    public void markAsComplete(View view) {
        View parent = (View) view.getParent().getParent();

        TextView taskTitleView = (TextView) parent.findViewById(R.id.task_header).findViewById(R.id.item_task_title);
        String taskName = String.valueOf(taskTitleView.getText());
        HomeManager homeManager = HomeManager.sharedInstance();

        List<Task> tasks = homeManager.getUserTasks();
        for(Task task:tasks){
            if(task.getName().equals(taskName)){
                task.setComplete(true);
                homeManager.UpdateTask(task);
                Snackbar snackbar = Snackbar
                        .make(drawer, "Task Completed", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new UndoTaskOnClickListener(task, homeManager) {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(drawer, "Task Restored", Snackbar.LENGTH_SHORT);
                                getTask().setComplete(false);
                                getHomeManager().UpdateTask(getTask());
                                snackbar1.show();
                            }
                        });
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.show();
            }
        }
    }


}
