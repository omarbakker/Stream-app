package com.test.stream.stream.Utilities;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;

/**
 * A wrapper that handles most database functionality
 *
 * Created by Catherine Lee on 2016-10-01.
 */


public class DatabaseManager {
    private static DatabaseManager instance = new DatabaseManager();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Ensure that the DatabaseManager can only be created within this class.
     */
    private DatabaseManager(){}

    /**
     *
     * @return the singular instance of the DatabaseManager (singleton)
     */
    public static DatabaseManager getInstance()
    {
        return instance;
    }

    /**
     * Write an object to the database
     *
     * @param objectType the enum representation of the object type.
     * @param itemToWrite the object to write to the database.
     * @return the key of the object written to the database.
     */
    public String writeObject(DatabaseFolders objectType, Object itemToWrite)
    {
        DatabaseReference myRef = database.getReference(objectType.toString());
        DatabaseReference newRef = myRef.push();
        newRef.setValue(itemToWrite);

        return newRef.getKey();
    }

    /**
     * Write a new object to database with the provided information. Note: if the
     * key is the key of an existing object, the object will be overwritten in the
     * database.
     *
     * @param objectType the folder the object should be stored in
     * @param key the key the object should be stored under
     * @param itemToWrite the object to store
     * @return the key of the object
     */
    public String writeObjectWithKey(DatabaseFolders objectType, String key, Object itemToWrite)
    {
        return updateObject(objectType, key, itemToWrite); //Updating the object undergoes the same procedure as writing with a key.
    }

    /**
     * Update an object in the database or write an object to the database with a specified key
     *
     * @param objectType the enum representation of the object type.
     * @param objectID the object's key in the database
     * @param itemToWrite the object to write to the database.

     * @return the key of the object written to the database
     */
    public String updateObject(DatabaseFolders objectType, String objectID, Object itemToWrite)
    {
        DatabaseReference myRef = database.getReference(objectType.toString());
        myRef.child(objectID).setValue(itemToWrite);

        return myRef.getKey();
    }

    /**
     * Get a database reference (path) for a specific type of object or string
     *
     * @param path Either the string representation of a folder in the database or a string representing
     *             the path of an object in the form of "dataType/objectKey"
     * @return
     */
    public DatabaseReference getReference(String path)
    {
        return database.getReference(path);
    }

    /**
     * Fetch an object from the database using the object's key.
     *
     * @param objectType the folder this object belongs to
     * @param key the key of the object in the database
     * @param callback is triggered when the read has completed
     */
    public void fetchObjectByKey(DatabaseFolders objectType, String key, ReadDataCallback callback) {

        DatabaseReference myRef = database.getReference(objectType.toString());
        Query query = myRef.orderByKey().equalTo(key);

        new ReadDataRequest(query, callback);
    }

    /**
     * Fetch an object by one of the object's parameters
     *
     * @param objectType the folder this object belongs to
     * @param key the key of the value we wish to query by
     * @param value the value we wish to query for
     * @param callback is triggered when the read has completed
     */
    public void fetchObjectByChild(DatabaseFolders objectType, String key, String value, ReadDataCallback callback) {

        DatabaseReference myRef = database.getReference(objectType.toString());
        Query query = myRef.orderByChild(key).equalTo(value);

        new ReadDataRequest(query, callback);
    }

    /**
     * Fetch an object in its current state from the database.
     */
    private class ReadDataRequest {

        final ReadDataCallback callback;
        Query searchQuery;

        /**
         * Conduct a read request
         *
         * @param query the query to search the database with
         * @param callback  an event triggered when the read has been completed
         */
        ReadDataRequest(Query query, ReadDataCallback callback) {
            this.callback = callback;
            searchQuery = query;

            fetchQuery();
        }


        /**
         * Fetch an object for the read request's query.
         */
      private void fetchQuery()
      {
          searchQuery.addListenerForSingleValueEvent(
                  new ValueEventListener() {

                      /**
                       * Triggered when data requested has been retrieved.
                       *
                       * @param dataSnapshot a snapshot of the database containing
                       *                     the key of and value of the requested object
                       */
                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {
                          callback.onDataRetrieved(dataSnapshot);
                      }


                      /**
                       * Triggered when the data request has failed.
                       *
                       * @param databaseError an error for when the database request is cancelled
                       */
                      @Override
                      public void onCancelled(DatabaseError databaseError) {
                          callback.onDataRetrieved(null);
                      }
                  });
      }
    }

}
