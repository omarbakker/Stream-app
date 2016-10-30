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
 * Created by cathe on 2016-10-29.
 */

public abstract class DataManager {
    private ConcurrentHashMap<Query, ChildEventListener> listenerCollection = new ConcurrentHashMap<Query, ChildEventListener>();


    //Assumes a project exists.
    protected void registerParent(DatabaseFolders parentType, String parentId)
    {
        DatabaseReference myRef = DatabaseManager.getInstance().getReference(parentType.toString());
        Query query = myRef.orderByKey().equalTo(parentId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    parentUpdated(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    parentUpdated(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    parentDeleted();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addChildEventListener(listener);
        listenerCollection.put(query, listener);
    }

    protected void registerChild(final String childId, DatabaseFolders childType)
    {
        DatabaseReference myRef = DatabaseManager.getInstance().getReference(childType.toString());
        Query query = myRef.orderByKey().equalTo(childId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    childUpdated(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    childUpdated(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    childDeleted(childId);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addChildEventListener(listener);
        listenerCollection.put(query, listener);
    }

    public abstract void parentUpdated(DataSnapshot dataSnapshot);

    public abstract void parentDeleted();

    public abstract void childUpdated(DataSnapshot dataSnapshot);

    public abstract void childDeleted(String id);


    public void Destroy() //Call only when you don't need the tasks anymore.
    {
        //De-register all listeners
        for(Query query: listenerCollection.keySet())
        {
            query.removeEventListener(listenerCollection.get(query));
        }

    }


}
