package com.test.stream.stream;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Controllers.BoardManager;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.TeamManager;
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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by cathe on 2016-11-14.
 */

public class TeamTest {
    //Global variables for tests
    private List<User> members = null;
    private static User user = null;
    private TeamManager mTeamManager = null;
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
     * Manually set the project board ID to access a stub project.
     */
    @Before
    public void setProject()
    {
        Project project = new Project();
        project.setName("test");
        project.setId("-KYpp1pjb3fIHtp3MhdP");
        project.setTaskGroupId("-KYpp1q-omA_l9JPxYtU");
        project.setCalendarId("-KYpp1q47YMWqm5OAOkL");
        project.setBoardId("-KYpp1proK9j4ciRBQB_");
        ProjectManager.sharedInstance().setCurrentProject(project);
    }

    /**
     * Requires: The user is an existing user on the database.
     * Manually create the test user on the database.
     *
     * @return the test user.
     */
    private User getOtherUser()
    {
        User user = new User();
        user.setEmail("unit2@test.com");
        user.setUsername("unit2");
        user.setName("unit2");
        user.setUid("3mrZiPb4QCg361KVQy1fcMZxchX2");

        return user;
    }


    /**
     * Confirm that TeamManager has been successfully initialized from the current project.
     * @param memberCount An integer that reflects the number of members in the project.
     * @param dataChangeCount An integer that reflects the number of times data has been updated,
     *                        according to the listener.
     * @param dataChanged A boolean that confirms that data has been changed.
     */
    private void initializeTeamManager(final AtomicInteger memberCount,
                                        final AtomicInteger dataChangeCount, final AtomicBoolean dataChanged)
    {
        mTeamManager = new TeamManager();
        //Confirm initialization
        mTeamManager.Initialize(new DataEventListener() {
            @Override
            public void onDataChanged() {
                members = mTeamManager.GetUsers();
                dataChangeCount.incrementAndGet();
                dataChanged.set(true);
                memberCount.set(members.size());
            }
        });

        await().atMost(10, TimeUnit.SECONDS).untilAtomic(dataChangeCount, equalTo(1));
    }

    /**
     * Confirm meetings can be added and removed
     */
    @Test
    public void addRemoveMember() {
        assertNotNull(user);

        AtomicInteger memberCount = new AtomicInteger(0);
        initializeTeamManager(memberCount, new AtomicInteger(0), new AtomicBoolean(false));

        //Check if creating a pin is successful
        int initialMemberCount = members.size();

        //Assert that the user was added
        boolean added = mTeamManager.AddMemberToCurrentProject(getOtherUser(), false);
        assert(added);

        //Confirm that we have added the member.
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(memberCount,equalTo(initialMemberCount + 1));

        //Confirm that the member we have added is correct.
        User newMember = members.get(members.size()-1);
        assertEquals(newMember.getUid(), "3mrZiPb4QCg361KVQy1fcMZxchX2");

        boolean removed = mTeamManager.DeleteMemberFromCurrentProject(getOtherUser());
        assert(removed);
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(memberCount,equalTo(initialMemberCount));


        // deregister all listeners
        mTeamManager.Destroy();
    }

    /**
     * Sign the current user out after the tests.
     */
    @AfterClass
    public static void clean()
    {
        mAuth.signOut();
    }
}

