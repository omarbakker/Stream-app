package com.test.stream.stream;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Controllers.BoardManager;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Board.Pin;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import org.junit.Before;
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

@RunWith(AndroidJUnit4.class)
public class BoardTest {
    final String title = "title";
    final String subtitle = "subtitle";
    final String description = "description";

    final String title2 = "title2";
    final String subtitle2 = "subtitle2";
    final String description2 = "description2";

    private List<Pin> pins = null;
    private static User user = null;

    //User must be signed in to write to the database
    @Before
    public void userSignInSetup() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
        // login to test user
        mAuth.signInWithEmailAndPassword("unit@test.com", "123456");
    }

    /**
     * Manually set the project board ID
     */
    @Before
    public void setProject()
    {
        Project project = new Project();
        project.setId("-KW_lArl8Gz3u6mqljKx");
    //    project.setCalendarId("-KWa8RaBR39DrKuWrqJA");
     //   project.setCalendarId("-KWa8Ra8vZ-lNnmSPlpq");
        project.setBoardId("-KWa8Ra6X66HalKOEL97");
        ProjectManager.sharedInstance().setCurrentProject(project);
    }

    private Callable<Boolean> newUserIsAdded() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return user != null; // The condition that must be fulfilled
            }
        };
    }

    /**
     * Confirms that BoardManager has been successfully initialized.
     */
    private void initializeBoardManager(final AtomicInteger pinCount,
                                       final AtomicInteger dataChangeCount, final AtomicBoolean dataChanged)
    {
        //Confirm initialization
        BoardManager.sharedInstance().InitializePins(new DataEventListener() {
            @Override
            public void onDataChanged() {
                pins = BoardManager.sharedInstance().GetPinsInProject();
                dataChangeCount.incrementAndGet();
                dataChanged.set(true);
                pinCount.set(pins.size());
            }
        });

        await().atMost(10,TimeUnit.SECONDS).untilAtomic(dataChangeCount, equalTo(1));
    }

    @Test
    public void verifySignedIn() {
        await().atMost(10, TimeUnit.SECONDS).until(newUserIsAdded());
        assertEquals("unit@test.com", user.getEmail());
        assertEquals(user.getEmail(), UserManager.sharedInstance().getCurrentUser().getEmail());
    }


    @Test
    public void addPin() {
        AtomicInteger pinCount = new AtomicInteger(0);
        initializeBoardManager(pinCount, new AtomicInteger(0), new AtomicBoolean(false));

        //Check if creating a pin is successful
        int initialPinCount = pins.size();
        boolean created = BoardManager.sharedInstance()
                .CreateMessagePin(title, subtitle, description);
        assert(created);


        // assert the pin was created
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(pinCount,equalTo(initialPinCount + 1));

        // deregister all listeners
        BoardManager.sharedInstance().Destroy();
    }

    @Test
    public void deletePin() {
        // set up the change listener
        AtomicInteger pinCount = new AtomicInteger(0);
        initializeBoardManager(pinCount, new AtomicInteger(0), new AtomicBoolean(false));

        //Get a pin to delete
        Pin pinToDelete = pins.get(0);
        int initialPinCount = pins.size();

        //Delete the pin
        boolean deleted = BoardManager.sharedInstance().RemovePin(pinToDelete);
        assert(deleted);

        // assert the pin was deleted;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(pinCount,equalTo(initialPinCount - 1));

        // deregister all listeners
        BoardManager.sharedInstance().Destroy();
    }

    @Test
    public void pinDetails() {
        // set up the change listener
        AtomicInteger pinCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeBoardManager(pinCount, new AtomicInteger(0), dataChanged);
        int initialPinCount = pins.size();

        boolean created = BoardManager.sharedInstance()
                .CreateMessagePin(title, subtitle, description);
        assert(created);

        // assert the pin was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(pinCount,equalTo(initialPinCount + 1));
        Pin fetchedPin = pins.get(pins.size() - 1);

        // we have now guaranteed that a new pin exists.
        // Test the correctness of the details of the pin.
        assertEquals(fetchedPin.getBoardId(), ProjectManager.sharedInstance().getCurrentProject().getBoardId());
        assertEquals(fetchedPin.getDescription(), description);
        assertEquals(fetchedPin.getSubtitle(), subtitle);
        assertEquals(fetchedPin.getTitle(), title);

        //Delete the pin
        boolean deleted = BoardManager.sharedInstance().RemovePin(fetchedPin);
        assert(deleted);

        // deregister all listeners
        BoardManager.sharedInstance().Destroy();
    }

    @Test
    public void editPin()
    {
        // set up the change listener
        AtomicInteger pinCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeBoardManager(pinCount, new AtomicInteger(0), dataChanged);
        int initialPinCount = pins.size();

        boolean created = BoardManager.sharedInstance()
                .CreateMessagePin(title, subtitle, description);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(pinCount,equalTo(initialPinCount + 1));
        Pin fetchedPin = pins.get(pins.size() - 1);

        // Modify the task details and update
        fetchedPin.setTitle(title2);
        fetchedPin.setDescription(description2);
        fetchedPin.setSubtitle(subtitle2);

        //Wait for data to change
        dataChanged.set(false);

        // update the database
        BoardManager.sharedInstance().UpdatePin(fetchedPin);

        // wait for the listener to receive an update
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        Pin updatedPin = pins.get(pins.size() - 1);

        // Test the correctness of the details of the updated task.
        assertEquals(updatedPin.getDescription(), description2);
        assertEquals(updatedPin.getSubtitle(), subtitle2);
        assertEquals(updatedPin.getTitle(), title2);

        //Delete the task
        boolean deleted = BoardManager.sharedInstance().RemovePin(updatedPin);
        assert(deleted);

        // deregister all listeners
        BoardManager.sharedInstance().Destroy();
    }

}
