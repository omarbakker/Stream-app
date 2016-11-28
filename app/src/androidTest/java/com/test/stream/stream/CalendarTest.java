package com.test.stream.stream;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Controllers.BoardManager;
import com.test.stream.stream.Controllers.CalendarManager;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Board.Pin;
import com.test.stream.stream.Objects.Calendar.Meeting;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
/**
 * Created by robyn on 2016-11-07.
 */
@RunWith(AndroidJUnit4.class)
public class CalendarTest {
    //Information for one meeting object
    final String name = "meetingName";
    final String description = "meetingDescrp";
    final String location = "venue";
    final int minute = 50;
    final int day = 20;
    final int hour = 4;
    final int month = 2;
    final int year = 1999;
    final String monthName = "February";
    final String dayOfTheWeek = "Monday";
    final String amPm = "AM";

    //Information for a second meeting object (new values to modify the original to)
    final String name2 = "meetingName2";
    final String description2 = "meetingDescrp2";
    final String location2 = "venue2";
    final int minute2 = 40;
    final int day2 = 10;
    final int hour2 = 2;
    final int month2 = 3;
    final int year2 = 2000;
    final String monthName2 = "March";
    final String dayOfTheWeek2 = "Tuesday";
    final String amPm2 = "PM";

    //Global variables for tests
    private List<Meeting> meetings = null;
    private static User user = null;
    private static FirebaseAuth mAuth;

    /**
     * Sign in the user before writing anything to the database
     * so that the user will have write permission.
     */
    @BeforeClass
    public static void userSignInSetup() {

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                UserManager.sharedInstance().InitializeUser(new ReadDataCallback() {

                    @Override
                    public void onDataRetrieved(DataSnapshot result) {
                        AtomicBoolean once = new AtomicBoolean(false);
                        for (DataSnapshot child : result.getChildren()) {
                            if (!once.getAndSet(true)) {
                                user = child.getValue(User.class);
                            }
                        }
                    }
                });
            }
        };
        mAuth.addAuthStateListener(listener);
        // login to the stub test user
        mAuth.signInWithEmailAndPassword("unit@test.com", "123456");

        await().atMost(10, TimeUnit.SECONDS).until(newUserIsAdded());
        assertEquals("unit@test.com", user.getEmail());
        assertEquals(user.getEmail(), UserManager.sharedInstance().getCurrentUser().getEmail());
    }

    /**
     * Manually set the project board ID to access a stub project.
     */
    @Before
    public void setProject()
    {
        Project project = new Project();
        project.setId("-KWajQa24etXw8G_OkpN");
        project.setCalendarId("-KWajQaANjqT30UO6-rc");
        ProjectManager.sharedInstance().setCurrentProject(project);
    }

    /**
     * Confirm that the user has been added.
     * @return a callable object that will trigger a callback when
     * the user successfully logs into Stream.
     */
    private static Callable<Boolean> newUserIsAdded() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return user != null; // The condition that must be fulfilled
            }
        };
    }


    /**
     * Confirm that CalendarManager has been successfully initialized from the current project.
     * @param meetingCount An integer that reflects the number of meetings in the project.
     * @param dataChangeCount An integer that reflects the number of times data has been updated,
     *                        according to the listener.
     * @param dataChanged A boolean that confirms that data has been changed.
     */
    private void initializeCalendarManager(final AtomicInteger meetingCount,
                                        final AtomicInteger dataChangeCount, final AtomicBoolean dataChanged)
    {
        //Confirm initialization
        CalendarManager.sharedInstance().Initialize(new DataEventListener() {
            @Override
            public void onDataChanged() {
                meetings = CalendarManager.sharedInstance().GetMeetingsInProject();
                dataChangeCount.incrementAndGet();
                dataChanged.set(true);
                meetingCount.set(meetings.size());
            }
        });

        await().atMost(10, TimeUnit.SECONDS).untilAtomic(dataChangeCount, equalTo(1));
    }

    /**
     * Test that a meeting is successfully added.
     */
    @Test
    public void addMeeting() {
        AtomicInteger meetingCount = new AtomicInteger(0);
        initializeCalendarManager(meetingCount, new AtomicInteger(0), new AtomicBoolean(false));

        //Check if creating a meeting is successful
        int initialMeetingCount = meetings.size();
        boolean created = CalendarManager.sharedInstance()
                .CreateMeeting(name, description, location, hour, minute, day,
                month, year, monthName, dayOfTheWeek, amPm);
        assert(created);

        // assert the meeting was created
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(meetingCount,equalTo(initialMeetingCount + 1));

        // deregister all listeners
        CalendarManager.sharedInstance().Destroy();
    }

    /**
     * Confirm a meeting can be successfully deleted.
     */
    @Test
    public void deleteMeeting() {
        // set up the change listener
        AtomicInteger meetingCount = new AtomicInteger(0);
        initializeCalendarManager(meetingCount, new AtomicInteger(0), new AtomicBoolean(false));

        //Get a meeting to delete
        Meeting meetingToDelete = meetings.get(0);
        int initialPinCount = meetings.size();

        //Delete the meeting
        boolean deleted = CalendarManager.sharedInstance().DeleteMeeting(meetingToDelete);
        assert(deleted);

        // assert the meeting was deleted;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(meetingCount,equalTo(initialPinCount - 1));

        // deregister all listeners
        CalendarManager.sharedInstance().Destroy();
    }

    /**
     * Confirm that the meeting's details are the correct values inputted.
     */
    @Test
    public void meetingDetails() {
        // set up the change listener
        AtomicInteger meetingCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeCalendarManager(meetingCount, new AtomicInteger(0), dataChanged);

        //Check if creating a meeting is successful
        int initialMeetingCount = meetings.size();
        boolean created = CalendarManager.sharedInstance()
                .CreateMeeting(name, description, location, hour, minute, day,
                        month, year, monthName, dayOfTheWeek, amPm);
        assert(created);

        // assert the meeting was created
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(meetingCount,equalTo(initialMeetingCount + 1));
        Meeting fetchedMeeting = meetings.get(meetings.size()-1);

        // we have now guaranteed that a new meeting exists.
        // Test the correctness of the details of the meeting.
        assertEquals(fetchedMeeting.getCalendarId(), ProjectManager.sharedInstance().getCurrentProject().getCalendarId());
        assertEquals(fetchedMeeting.getName(), name);
        assertEquals(fetchedMeeting.getDescription(), description);
        assertEquals(fetchedMeeting.getLocation(), location);
        assertEquals(fetchedMeeting.getHour(), hour);
        assertEquals(fetchedMeeting.getMinute(), minute);
        assertEquals(fetchedMeeting.getDay(), day);
        assertEquals(fetchedMeeting.getNumberMonth(), month);
        assertEquals(fetchedMeeting.getMonth(), monthName);
        assertEquals(fetchedMeeting.getYear(), year);
        assertEquals(fetchedMeeting.getDayOfWeek(), dayOfTheWeek);
        assertEquals(fetchedMeeting.getAmPm(), amPm);

        //Delete the meeting
        boolean deleted = CalendarManager.sharedInstance().DeleteMeeting(fetchedMeeting);
        assert(deleted);

        // deregister all listeners
        CalendarManager.sharedInstance().Destroy();
    }


    /**
     * Confirm that the meeting can be edited correctly. Check that the new values are
     * reflected in the database.
     */
    @Test
    public void editMeeting()
    {
        // set up the change listener
        AtomicInteger meetingCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

       initializeCalendarManager(meetingCount, new AtomicInteger(0), dataChanged);
        int initialMeetingCount = meetings.size();

        boolean created = CalendarManager.sharedInstance()
                .CreateMeeting(name, description, location, hour, minute, day,
                        month, year, monthName, dayOfTheWeek, amPm);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(meetingCount,equalTo(initialMeetingCount + 1));
        Meeting fetchedMeeting = meetings.get(meetings.size() - 1);

        // Modify the task details and update
        fetchedMeeting = modifyMeeting(fetchedMeeting);

        //Wait for data to change
        dataChanged.set(false);

        // update the database
        CalendarManager.sharedInstance().UpdateMeeting(fetchedMeeting);

        // wait for the listener to receive an update
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        Meeting updatedMeeting = meetings.get(meetings.size() - 1);

        // Test the correctness of the details of the updated task.
        assertEquals(updatedMeeting.getName(), name2);
        assertEquals(updatedMeeting.getDescription(), description2);
        assertEquals(updatedMeeting.getLocation(), location2);
        assertEquals(updatedMeeting.getHour(), hour2);
        assertEquals(updatedMeeting.getMinute(), minute2);
        assertEquals(updatedMeeting.getDay(), day2);
        assertEquals(updatedMeeting.getMonth(), monthName2);
        assertEquals(updatedMeeting.getNumberMonth(), month2);
        assertEquals(updatedMeeting.getYear(), year2);
        assertEquals(updatedMeeting.getDayOfWeek(), dayOfTheWeek2);
        assertEquals(updatedMeeting.getAmPm(), amPm2);

        //Delete the task
        boolean deleted = CalendarManager.sharedInstance().DeleteMeeting(updatedMeeting);
        assert(deleted);

        // deregister all listeners
        CalendarManager.sharedInstance().Destroy();
    }

    /**
     * Helper class to edit meeting.
     * @param meetingToEdit The meeting to obtain updated values.
     * @return The modified meeting object.
     */
    private Meeting modifyMeeting(Meeting meetingToEdit)
    {
        meetingToEdit.setName(name2);
        meetingToEdit.setDescription(description2);
        meetingToEdit.setLocation(location2);
        meetingToEdit.setTime(hour2, minute2);
        meetingToEdit.setDate(monthName2, day2, year2, month2, dayOfTheWeek2);
        meetingToEdit.setAmPm(amPm2);

        return meetingToEdit;
    }

    /**
     * After the tests, sign out.
     */
    @AfterClass
    public static void clean()
    {
        mAuth.signOut();
    }

}
