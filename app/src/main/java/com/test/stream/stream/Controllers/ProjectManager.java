package com.test.stream.stream.Controllers;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Objects.Board.Board;
import com.test.stream.stream.Objects.Calendar.Calendar;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.TaskGroup;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.UI.ProjectsActivity;
import com.test.stream.stream.Utilities.Callbacks.FetchUserCallback;
import com.test.stream.stream.Utilities.Callbacks.FetchUserProjectsCallback;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.ReadDataCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by OmarBakker on 2016-10-26.
 * This class uses the singleton method of instantiation.
 * Note: if the user is signed in, use the getCurrentproject method to retrieve a Project object for the current project
 * Note: if the user is signed in, use the getUserProject method to retrieve a list of Project objects for the current user
 */

public class ProjectManager {

    private static ProjectManager instance = null;
    private static Project currentProject;

    private ProjectsActivity projectsActivity;
    public void setProjectsActivity(ProjectsActivity projectsActivity) { this.projectsActivity = projectsActivity; }

    /**
     * Exists only to prevent external initialization.
     */
    private ProjectManager(){ }

    /**
     * @return
     *  The one and only ProjectManager instance (singleton)
     */
    public static ProjectManager sharedInstance(){

        if (instance == null)
            instance = new ProjectManager();
        return instance;
    }

    /**
     * Requires: User is signed in.
     * @return
     * Return a Project object for the current project, or the first project from the users list if no project is currently 'open'.
     */
    public Project getCurrentProject(){

        if (currentProject == null)
            currentProject = new Project();
        return currentProject;
    }

    /**
     * Set the current project in the app
     * @param project
     * The project object to set the current project to be
     */
    public void setCurrentProject(Project project){
        // TODO: maybe make sure the project is a valid project here before setting it to be the current project ?
        currentProject = project;
    }

    /**
     * Creates a project representing the Project object in the parameter.
     * The object is written to the database
     * @param project
     * Writes the object to the database
     */
    public void CreateProject(Project project) {

        //Set inputted information
        String projectID = DatabaseManager.getInstance().writeObject(DatabaseFolders.Projects, project);

        // Create a new pin board id for the project
        Board newBoard = new Board();
        newBoard.setParentProjectId(projectID);
        String pinBoardKey = DatabaseManager.getInstance().writeObject(DatabaseFolders.Boards,newBoard);

        // Create a new Task group id for the project
        TaskGroup newTaskGroup = new TaskGroup(projectID);
        String taskGroupID = DatabaseManager.getInstance().writeObject(DatabaseFolders.TaskGroups,newTaskGroup);

        // Create a new Calender object for the project
        Calendar newCalender = new Calendar();
        newCalender.setParentProjectId(projectID);
        String calenderID = DatabaseManager.getInstance().writeObject(DatabaseFolders.Calendars,newCalender);

        // update the project with the new IDs
        project.setId(projectID);
        project.setBoardId(pinBoardKey);
        project.setTaskGroupId(taskGroupID);
        project.setCalendarId(calenderID);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Projects,projectID,project);

        // update the users list of projects, and the projects list view
        addToCurrentUserProjects(project.getId());
    }

    /**
     * Requires: User is signed in.
     * Fetches the project objects from firebase using the project ids the current users getProjects()
     * Fetching is asynchronous and the result will be given in a FetchUserProjectsCallback.
     * Only id's which correspond to actual Projects in the database will have a Project object in the retrieved list
     * @param callback
     * Result will be delivered in this callback.
     */
    public void fetchCurrentUserProjects(final FetchUserProjectsCallback callback) {
        // TODO: get actual user object after login/UserManager issues are resolved
        User currentUser = UserManager.getInstance().getCurrentUser();

        final List<Project> projects = new ArrayList<Project>();
        final AtomicInteger numOfProjectsFetched = new AtomicInteger(0);
        final int numOfProjectsToFetch = currentUser.getProjects().size();
        for (String id : currentUser.getProjects().keySet()) {

            DatabaseManager.getInstance().fetchObjectByKey(DatabaseFolders.Projects, id, new ReadDataCallback() {
                @Override
                public void onDataRetrieved(DataSnapshot result) {

                    if (result.exists()) {
                        Project project = null;
                        for (DataSnapshot snapshot : result.getChildren())
                            project = snapshot.getValue(Project.class);
                        if (project != null)
                            projects.add(project);
                    }
                    // if done fetching all projects, callback
                    if (numOfProjectsToFetch == numOfProjectsFetched.incrementAndGet())
                        callback.onUserProjectsListRetrieved(projects);

                }
            });
        }
    }

    public void addToCurrentUserProjects(String projectId){
        // TODO: get actual user object after login/UserManager issues are resolved
        User user = UserManager.getInstance().getCurrentUser();
        user.addProject(projectId);
        UserManager.getInstance().updateUser(user);

        if (projectsActivity != null)
        {
            projectsActivity.updateUI();
        }
    }


}
