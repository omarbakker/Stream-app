package com.test.stream.stream.Controllers;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Tasks.TaskGroup;
import com.test.stream.stream.UIFragments.TasksFragment;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The abstract controller class for Stream
 *
 * Created by Catherine on 2016-10-29.
 */

public abstract class DataManager {
    private ConcurrentHashMap<Query, ChildEventListener> listenerCollection = new ConcurrentHashMap<Query, ChildEventListener>();

    /**
     * Register the a parent object for a data type to listen for
     * changes to the object.
     *
     * @param parentType the Firebase ID of the parent object
     * @param parentId the enum representing the object's data type
     */
    protected void registerParent(DatabaseFolders parentType, String parentId)
    {
        DatabaseReference myRef = DatabaseManager.getInstance().getReference(parentType.toString());
        Query query = myRef.orderByKey().equalTo(parentId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //If a new parent object has been added to the database, perform the update specified in the
                //implementation class
                if(dataSnapshot.exists())
                {
                    parentUpdated(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //If the parent object has been modified, perform the update specified in
                //the implementation class
                if(dataSnapshot.exists())
                {
                    parentUpdated(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //If the parent object has been removed, perform the update
                //specified in the implementation class
                if(dataSnapshot.exists())
                {
                    parentDeleted();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //Do nothing for the time being if the paret has been moved in the database
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do not update the UI if a read request has been cancel
            }
        };

        //Save the listener for eventual deletion.
        query.addChildEventListener(listener);
        listenerCollection.put(query, listener);
    }

    /**
     * Register the a child object for a data type to listen for
     * changes to the object.
     *
     * @param childId the Firebase ID of the child object
     * @param childType the enum representing the object's data type
     */
    protected void registerChild(final String childId, DatabaseFolders childType)
    {
        DatabaseReference myRef = DatabaseManager.getInstance().getReference(childType.toString());
        Query query = myRef.orderByKey().equalTo(childId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            //If a new child object has been added to the database, perform the update specified in the
            //implementation class
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    childUpdated(dataSnapshot);
                }
            }

            //If the child object has been modified, perform the update specified in
            //the implementation class
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    childUpdated(dataSnapshot);
                }
            }

            //If the child object has been removed, perform the update
            //specified in the implementation class
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    childDeleted(childId);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //Do nothing for the time being if the child has been moved in the database
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do not update the UI if a read request has been cancel
            }
        };

        //Store the listeners to deregister at deletion
        query.addChildEventListener(listener);
        listenerCollection.put(query, listener);
    }

    /**
     * Triggered by an update of the parent object, this updates the
     * UI accordingly
     *
     * @param dataSnapshot The object returned by Firebase containing the read object and its key.
     */
    public abstract void parentUpdated(DataSnapshot dataSnapshot);

    /**
     * Triggered by the deletion of the parent object, this updates the UI
     * accordingly
     */
    public abstract void parentDeleted();

    /**
     * Triggered by an update of a child object, this updates the
     * UI accordingly
     *
     * @param dataSnapshot The object returned by Firebase containing the read object and its key.
     */
    public abstract void childUpdated(DataSnapshot dataSnapshot);

    /**
     * Triggered by the deletion of a child object, this updates the
     * UI accordingly
     */
    public abstract void childDeleted(String id);

    /**
     * Destroy any listeners still registered on objects when we don't need
     * this instance of the DataManager
     */
    public void Destroy() //Call only when you don't need the tasks anymore.
    {
        //De-register all listeners
        for(Query query: listenerCollection.keySet())
        {
            query.removeEventListener(listenerCollection.get(query));
        }

    }


}
