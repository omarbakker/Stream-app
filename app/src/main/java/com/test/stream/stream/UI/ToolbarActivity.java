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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.test.stream.stream.R;
import com.test.stream.stream.UIFragments.CalendarFragment;
import com.test.stream.stream.UIFragments.ChatFragment;
import com.test.stream.stream.UIFragments.ProjectHomeFragment;
import com.test.stream.stream.UIFragments.SettingsFragment;
import com.test.stream.stream.UIFragments.BoardFragment;
import com.test.stream.stream.UIFragments.TaskMain;
import com.test.stream.stream.UIFragments.TasksFragment;
import com.test.stream.stream.UIFragments.expand_task;

public class ToolbarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Toolbar Activity";
    public static final String PIN_ID_EXTRA = "com.test.stream.stream identifier";
    public static final String PIN_TITLE_EXTRA = "com.test.stream.stream Title";
    public static final String PIN_SUBTITLE_EXTRA = "com.test.stream.stream Subtitle";
    public static final String PIN_DESCRIPTION_EXTRA = "com.test.stream.stream Description";
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbaractivity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.manager = getSupportFragmentManager();
        ProjectHomeFragment projectHomeFragment = new ProjectHomeFragment();
        manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                projectHomeFragment,
                projectHomeFragment.getTag()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent taskIntent = new Intent(ToolbarActivity.this, TaskMain.class);
        switch(id){
            case R.id.nav_chat:
                ChatFragment chatFragment = new ChatFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        chatFragment,
                        chatFragment.getTag()).commit();
                break;
            case R.id.nav_calendar:
                CalendarFragment calendarFragment = new CalendarFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        calendarFragment,
                        calendarFragment.getTag()).commit();
                break;
            case R.id.nav_pinboard:
                BoardFragment boardFragment = new BoardFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        boardFragment,
                        boardFragment.getTag()).commit();

                break;
            case R.id.nav_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        settingsFragment,
                        settingsFragment.getTag()).commit();
                break;
            case R.id.nav_tasks:
                TasksFragment taskFragment = new TasksFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        taskFragment,
                        taskFragment.getTag()).commit();
               // ToolbarActivity.this.startActivity(taskIntent);
                break;
            case R.id.nav_home:
                ProjectHomeFragment projectHomeFragment = new ProjectHomeFragment();
                manager.beginTransaction().replace(R.id.relative_layout_for_fragment,
                        projectHomeFragment,
                        projectHomeFragment.getTag()).commit();
                break;
            case R.id.nav_projects:

                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setTitle(CharSequence title) {

    }
    public void expandTaskView(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_name);
        String taskName = String.valueOf(taskTextView.getText());
        Intent intent = new Intent(this, expand_task.class);
        Log.d(TAG, "so fucked up oh my god");
        //intent.putExtra("tasks",tasks);
        intent.putExtra("taskName", taskName);
        Log.d(TAG, "everything is awful");
        startActivity(intent);
    }
}
