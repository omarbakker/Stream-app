package com.test.stream.stream.Controllers;
import com.test.stream.stream.Objects.Projects.Project;
import java.util.List;

/**
 * Created by OmarEyad on 2016-10-26.
 * This class uses the singleton method of instantiation.
 * Note: if the user is signed in, use the getCurrentproject method to retrieve a Project object for the current project
 * Note: if the user is signed in, use the getUserProject method to retrieve a list of Project objects for the current project
 */

public class ProjectManager {
    private static ProjectManager instance = null;

    /**
     * Exists only to prevent external initialization.
     */
    private ProjectManager(){ }

    /**
     * @return
     *  The one and only ProjectManager instance (singleton)
     */
    public ProjectManager sharedInstance(){
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

        return null;
    }

    /**
     * Requires: User is signed in.
     * @return
     * a list of Project objects for the current project
     */
    public List<Project> getCurrentProjectsList(){
        return null;
    }
}
