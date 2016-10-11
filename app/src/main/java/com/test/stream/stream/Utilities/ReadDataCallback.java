package com.test.stream.stream.Utilities;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by cathe on 2016-10-10.
 */

public interface ReadDataCallback {
    void onDataRetrieved(DataSnapshot result);
}