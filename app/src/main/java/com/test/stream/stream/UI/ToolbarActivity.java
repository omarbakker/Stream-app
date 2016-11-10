package com.test.stream.stream.UI;

import android.content.Intent;
import android.os.Bundle;
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

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.test.stream.stream.R;
import com.test.stream.stream.UIFragments.CalendarFragment;
import com.test.stream.stream.UIFragments.ExpandMeeting;
import com.test.stream.stream.UIFragments.ProjectHomeFragment;
import com.test.stream.stream.UIFragments.SettingsFragment;
import com.test.stream.stream.UIFragments.BoardFragment;
import com.test.stream.stream.UIFragments.TasksFragment;
import com.test.stream.stream.UIFragments.expand_task;

public class ToolbarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Toolbar Activity";
    // variables for the passing extras from Activity to Fragment
    public static final String PIN_ID_EXTRA = "com.test.stream.stream identifier";
    public static final String PIN_TITLE_EXTRA = "com.test.stream.stream Title";
    public static final String PIN_SUBTITLE_EXTRA = "com.test.stream.stream Subtitle";
    public static final String PIN_DESCRIPTION_EXTRA = "com.test.stream.stream Description";

    FragmentManager manager;

    /**
     * On create the ToolbarActivity, all initializations
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set how the view will look like
        setContentView(R.layout.toolbaractivity_main);
        // Set how the toolbar will look like
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Show the details of the navigation
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.manager = getSupportFragmentManager();

        String intentFragment = getIntent().getExtras().getString("frgToLoad");
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
                setTitle("Calendar");
                CalendarFragment calendarFragment = new CalendarFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        calendarFragment,
                        calendarFragment.getTag()).commit();
                break;
            // If PinBoard is clicked, launch PinBoard Fragment
            case R.id.nav_pinboard:
                setTitle("Pin Board");
                BoardFragment boardFragment = new BoardFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        boardFragment,
                        boardFragment.getTag()).commit();

                break;
            // If Settings is clicked, launch Settings Fragment
            case R.id.nav_settings:
                setTitle("Settings");
                SettingsFragment settingsFragment = new SettingsFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        settingsFragment,
                        settingsFragment.getTag()).commit();
                break;
            // if Tasks is clicked, launch Tasks Fragment
            case R.id.nav_tasks:
                setTitle("Tasks");
                TasksFragment taskFragment = new TasksFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        taskFragment,
                        taskFragment.getTag()).commit();
                break;
            // If Home button is clicked launch Home Fragment
            case R.id.nav_home:
                setTitle("My Project");
                ProjectHomeFragment projectHomeFragment = new ProjectHomeFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        projectHomeFragment,
                        projectHomeFragment.getTag()).commit();
                break;
            // If Projects clicked, Launch Projects page
            case R.id.nav_projects:
                finish();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(ToolbarActivity.this, MainLoginScreen.class);
                startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                setTitle("Calendar");
                CalendarFragment calendarFragment = new CalendarFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        calendarFragment,
                        calendarFragment.getTag()).commit();
                break;
            // If PinBoard is clicked, launch PinBoard Fragment
            case "PINBOARD":
                setTitle("Pin Board");
                BoardFragment boardFragment = new BoardFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        boardFragment,
                        boardFragment.getTag()).commit();

                break;
            // If Settings is clicked, launch Settings Fragment
            case "SETTINGS":
                setTitle("Settings");
                SettingsFragment settingsFragment = new SettingsFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        settingsFragment,
                        settingsFragment.getTag()).commit();
                break;
            // if Tasks is clicked, launch Tasks Fragment
            case "TASKS":
                setTitle("Tasks");
                TasksFragment taskFragment = new TasksFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        taskFragment,
                        taskFragment.getTag()).commit();
                break;
            // If Home button is clicked launch Home Fragment
            case "HOME":
                setTitle("My Project");
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

    public void expandMeetingView(View v) {
        View parent = (View) v.getParent();
        TextView meetingTextview = (TextView) parent.findViewById(R.id.meeting_name);
        String meetingName = String.valueOf(meetingTextview.getText());
        Intent intent = new Intent(this, ExpandMeeting.class);
        intent.putExtra("meetingName", meetingName);
        startActivity(intent);
    }
}
