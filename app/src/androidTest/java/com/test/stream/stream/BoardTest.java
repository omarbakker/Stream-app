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

@RunWith(AndroidJUnit4.class)
public class BoardTest {
    //Information for one pin
    final String title = "title";
    final String color = "color";
    final String description = "description";

    //Information for another pin, or for editing.
    final String title2 = "title2";
    final String color2 = "color2";
    final String description2 = "description2";

    //Global variables for tests
    private List<Pin> pins = null;
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
        // login to test user
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
        project.setBoardId("-KWajQa6MtgOdRK2GLdK");
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

    /**
     * Confirm that pins can be added to the board.
     */
    @Test
    public void addPin() {
        AtomicInteger pinCount = new AtomicInteger(0);
        initializeBoardManager(pinCount, new AtomicInteger(0), new AtomicBoolean(false));

        //Check if creating a pin is successful
        int initialPinCount = pins.size();
        boolean created = BoardManager.sharedInstance()
                .CreateMessagePin(title, color, description);
        assert(created);


        // assert the pin was created
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(pinCount,equalTo(initialPinCount + 1));

        // deregister all listeners
        BoardManager.sharedInstance().Destroy();
    }

    /**
     * Confirm that pins can be deleted.
     */
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

    /**
     * Confirm that the contents of each pin are the values we expect.
     */
    @Test
    public void pinDetails() {
        // set up the change listener
        AtomicInteger pinCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeBoardManager(pinCount, new AtomicInteger(0), dataChanged);
        int initialPinCount = pins.size();

        boolean created = BoardManager.sharedInstance()
                .CreateMessagePin(title, color, description);
        assert(created);

        // assert the pin was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(pinCount,equalTo(initialPinCount + 1));
        Pin fetchedPin = pins.get(pins.size() - 1);

        // we have now guaranteed that a new pin exists.
        // Test the correctness of the details of the pin.
        assertEquals(fetchedPin.getBoardId(), ProjectManager.sharedInstance().getCurrentProject().getBoardId());
        assertEquals(fetchedPin.getDescription(), description);
        assertEquals(fetchedPin.getColor(), color);
        assertEquals(fetchedPin.getTitle(), title);

        //Delete the pin
        boolean deleted = BoardManager.sharedInstance().RemovePin(fetchedPin);
        assert(deleted);

        // deregister all listeners
        BoardManager.sharedInstance().Destroy();
    }

    /**
     * Confirm that pins can be edited and the database is updated to reflect this.
     */
    @Test
    public void editPin()
    {
        // set up the change listener
        AtomicInteger pinCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeBoardManager(pinCount, new AtomicInteger(0), dataChanged);
        int initialPinCount = pins.size();

        boolean created = BoardManager.sharedInstance()
                .CreateMessagePin(title, color, description);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(pinCount,equalTo(initialPinCount + 1));
        Pin fetchedPin = pins.get(pins.size() - 1);

        // Modify the task details and update
        fetchedPin.setTitle(title2);
        fetchedPin.setDescription(description2);
        fetchedPin.setColor(color2);

        //Wait for data to change
        dataChanged.set(false);

        // update the database
        BoardManager.sharedInstance().UpdatePin(fetchedPin);

        // wait for the listener to receive an update
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        Pin updatedPin = pins.get(pins.size() - 1);

        // Test the correctness of the details of the updated task.
        assertEquals(updatedPin.getDescription(), description2);
        assertEquals(updatedPin.getColor(), color2);
        assertEquals(updatedPin.getTitle(), title2);

        //Delete the pin
        boolean deleted = BoardManager.sharedInstance().RemovePin(updatedPin);
        assert(deleted);

        // deregister all listeners
        BoardManager.sharedInstance().Destroy();
    }

    /**
     * Log the current user out after tests.
     */
    @AfterClass
    public static void clean()
    {
        mAuth.signOut();
    }


}
