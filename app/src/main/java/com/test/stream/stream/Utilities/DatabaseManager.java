package com.test.stream.stream.Utilities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by cathe on 2016-10-01.
 * Note: This uses the Singleton Pattern of instantiation
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





}
