package com.test.stream.stream.Controllers;

import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.test.stream.stream.Objects.Board.Board;
import com.test.stream.stream.Objects.Board.Pin;
import com.test.stream.stream.Objects.Board.PinMessage;
import com.test.stream.stream.UIFragments.BoardFragment;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.PinType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cathe on 2016-10-26.
 */

public class BoardManager {
    private static BoardManager instance = new BoardManager();
    public static BoardManager getInstance() { return instance; }

    private Board currentBoard;
    private ConcurrentHashMap<String, Pin> pins = new ConcurrentHashMap<String, Pin>();

    private ConcurrentHashMap<Query, ChildEventListener> listenerCollection = new ConcurrentHashMap<Query, ChildEventListener>();;
    private BoardManager(){};

    public List<Pin> GetPinsInProject()
    {
        List<Pin> pins = new ArrayList();
        pins.addAll(this.pins.values());

        return pins;
    }

    public void InitializePins(BoardFragment context) //Note: Change context to your activity class & do it for the private functions
    {
        registerBoard(context);
    }

    //Assumes a project exists.
    private void registerBoard(final BoardFragment context)
    {
        DatabaseReference myRef = DatabaseManager.getInstance().getReference(DatabaseFolders.Boards.toString());
        Query query = myRef.orderByKey().equalTo(ProjectManager.sharedInstance().getCurrentProject().getBoardId());

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    currentBoard = dataSnapshot.getValue(Board.class);
                    registerPins(context);
                    context.updateUI();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    currentBoard = dataSnapshot.getValue(Board.class);
                    registerPins(context);
                    context.updateUI();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                currentBoard = null;
                context.updateUI();
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

    private void registerPins(BoardFragment context)
    {
        for(String id :  currentBoard.getPins().keySet()) //Ensure that each pin only is register once.
        {
            if(!pins.containsKey(id))
            {
                registerPins(id, context);
            }
        }
    }

    private void registerPins(final String pinId, final BoardFragment context)
    {
        DatabaseReference myRef = DatabaseManager.getInstance().getReference(DatabaseFolders.Pins.toString());
        Query query = myRef.orderByKey().equalTo(pinId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    String pinType = dataSnapshot.child("pinType").getValue().toString();

                    if(pinType.equals(PinType.Message.toString()))
                    {
                        PinMessage pin = dataSnapshot.getValue(PinMessage.class);
                        pins.put(pinId, pin);
                        context.updateUI();
                    }
                    else
                    {
                        System.out.println("Error, did not get pin");
                    }

                    //Do whatever you need with the "context" ie. call the updateUI function
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    Pin parentPin = dataSnapshot.getValue(Pin.class);

                    if(parentPin.getPinType().equals(PinType.Message))
                    {
                        PinMessage pin = dataSnapshot.getValue(PinMessage.class);
                        pins.put(pinId, pin);
                    }
                    //Do whatever you need with the "context" ie. call the updateUI function
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    pins.remove(pinId);
                    //Do whatever you need with the "context" ie. call the updateUI function
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

    public boolean UpdatePin(Pin pin)
    {
        if(currentBoard == null || !pins.containsKey(pin.getId()))
        {
            return false;
        }

        DatabaseManager.getInstance().updateObject(
                DatabaseFolders.Pins,
                pin.getId(),
                pin);

        return true;
    }

    public boolean CreateMessagePin(String title, String subtitle, String description)
    {
        if(currentBoard == null)
        {
            return false; //Cannot create a pin without the project selected.
        }

        PinMessage message = new PinMessage(title, subtitle, description);

        //Set inputted information
        //message.setDescription(description);
        //message.setSubtitle(subtitle);
        //message.setTitle(title);
        message.setPinType(PinType.Message);

        String objectKey = DatabaseManager.getInstance().writeObject(DatabaseFolders.Pins, message);

        //Store the firebase object key as the object id.
        message.setId(objectKey);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Pins, objectKey, message);

        //Store the pins in the board.
        currentBoard.addPin(message.getId(), PinType.Message);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Boards, ProjectManager.sharedInstance().getCurrentProject().getBoardId(), currentBoard);

        return true;
    }

    public boolean RemovePin(Pin pin)
    {
        String pinId = pin.getId();

        if(!pins.containsKey(pinId))
        {
            return false;
        }

        DatabaseReference refToDelete = DatabaseManager.getInstance()
                .getReference(DatabaseFolders.Pins.toString())
                .child(pinId);

        refToDelete.removeValue();
        currentBoard.removePin(pinId);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Boards, ProjectManager.sharedInstance().getCurrentProject().getBoardId(), currentBoard);

        return true;

    }

    public void Destroy() //Call only when you don't need the pins anymore.
    {
        //De-register all listeners
        for(Query query: listenerCollection.keySet())
        {
            query.removeEventListener(listenerCollection.get(query));
        }

        instance = new BoardManager(); //Refresh the instance

    }
}
