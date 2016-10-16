package com.test.stream.stream.Utilities;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by cathe on 2016-10-01.
 * Note: This uses the Singleton Pattern of instantiation
 *       Reads use callbacks to retrieve items.
 */


public class DatabaseManager {
    private static DatabaseManager instance = new DatabaseManager();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseManager(){}

    public static DatabaseManager getInstance()
    {
        return instance;
    }

    public String writeObject(DatabaseFolders objectType, Object itemToWrite)
    {
        DatabaseReference myRef = database.getReference(objectType.toString());
        DatabaseReference newRef = myRef.push();
        newRef.setValue(itemToWrite);

        return newRef.getKey();
    }


    public String writeObjectByReference(DatabaseReference myRef, Object itemToWrite)
    {
        DatabaseReference newRef = myRef.push();
        newRef.setValue(itemToWrite);

        return newRef.getKey();
    }

    public String updateObject(DatabaseFolders objectType, String objectID, Object itemToWrite)
    {
        DatabaseReference myRef = database.getReference(objectType.toString());
        myRef.child(objectID).setValue(itemToWrite);

        return myRef.getKey();
    }

    public DatabaseReference getReference(String objectType)
    {
        return database.getReference(objectType);
    }

    public void fetchObjectByKey(DatabaseFolders objectType, String key, ReadDataCallback callback) {

        DatabaseReference myRef = database.getReference(objectType.toString());
        Query query = myRef.orderByKey().equalTo(key);

        new ReadDataRequest(query, callback);
    }

    public void fetchObjectByChild(DatabaseFolders objectType, String key, String value, ReadDataCallback callback) {

        DatabaseReference myRef = database.getReference(objectType.toString());
        Query query = myRef.orderByChild(key).equalTo(value);

        new ReadDataRequest(query, callback);
    }


    public class ReadDataRequest {

        final ReadDataCallback callback;
        Query searchQuery;

        ReadDataRequest(Query query, ReadDataCallback callback) {
            this.callback = callback;
            searchQuery = query;

            fetchQuery();
        }


      private void fetchQuery()
      {
          searchQuery.addListenerForSingleValueEvent(
                  new ValueEventListener() {
                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {
                          callback.onDataRetrieved(dataSnapshot);
                      }

                      @Override
                      public void onCancelled(DatabaseError databaseError) {
                          callback.onDataRetrieved(null);
                      }
                  });
      }
    }

}
