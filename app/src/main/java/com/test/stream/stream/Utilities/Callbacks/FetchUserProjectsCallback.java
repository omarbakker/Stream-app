package com.test.stream.stream.Utilities.Callbacks;

import com.test.stream.stream.Objects.Projects.Project;

import java.util.List;

/**
 * Created by OmarEyad on 2016-10-29.
 */

public interface FetchUserProjectsCallback {
    void onUserProjectsListRetrieved(List<Project> projects);
}
