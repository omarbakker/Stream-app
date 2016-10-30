package com.test.stream.stream.Utilities.Callbacks;
import com.test.stream.stream.Objects.Users.User;

/**
 * Created by OmarEyad on 2016-10-26.
 */

public interface FetchUserCallback {
    void onDataRetrieved(User result);
}
