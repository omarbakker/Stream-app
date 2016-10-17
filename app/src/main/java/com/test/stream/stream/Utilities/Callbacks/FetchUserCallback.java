package com.test.stream.stream.Utilities.Callbacks;

import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Objects.Users.User;

/**
 * Created by cathe on 2016-10-16.
 */

public interface FetchUserCallback {
    void onDataRetrieved(User result);
}
