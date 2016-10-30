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

public class BoardManager extends DataManager{
    private static BoardManager instance = new BoardManager();
    public static BoardManager getInstance() { return instance; }

    private BoardFragment context;
    private Board currentBoard;
    private ConcurrentHashMap<String, Pin> pins = new ConcurrentHashMap<String, Pin>();

    private BoardManager(){};

    public List<Pin> GetPinsInProject()
    {
        List<Pin> pins = new ArrayList();
        pins.addAll(this.pins.values());

        return pins;
    }

    public void InitializePins(BoardFragment context) //Note: Change context to your activity class & do it for the private functions
    {
        this.context = context;
        super.registerParent(DatabaseFolders.Boards, ProjectManager.currentProject.getBoardId());
    }

    @Override
    public void parentUpdated(DataSnapshot dataSnapshot) {
        currentBoard = dataSnapshot.getValue(Board.class);
        registerPins();
        context.updateUI();
    }

    @Override
    public void parentDeleted() {
        currentBoard = null;
        context.updateUI();
    }

    @Override
    public void childUpdated(DataSnapshot dataSnapshot) {
        String pinType = dataSnapshot.child("pinType").getValue().toString();

        if(pinType.equals(PinType.Message.toString()))
        {
            PinMessage pin = dataSnapshot.getValue(PinMessage.class);
            pins.put(pin.getId(), pin);
        }
        else
        {
            System.out.println("Error, did not get pin");
        }

        if(currentBoard.getPins().size() == pins.size())
        {
            context.updateUI();
        }

        //Do whatever you need with the "context" ie. call the updateUI function
    }

    @Override
    public void childDeleted(String id) {
        pins.remove(id);
    }

    private void registerPins()
    {
        for(String id :  currentBoard.getPins().keySet()) //Ensure that each pin only is register once.
        {
            if(!pins.containsKey(id))
            {
                super.registerChild(id, DatabaseFolders.Pins);
            }
        }
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

        PinMessage message = new PinMessage();

        //Set inputted information
        message.setDescription(description);
        message.setSubtitle(subtitle);
        message.setTitle(title);
        message.setPinType(PinType.Message);

        String objectKey = DatabaseManager.getInstance().writeObject(DatabaseFolders.Pins, message);

        //Store the firebase object key as the object id.
        message.setId(objectKey);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Pins, objectKey, message);

        //Store the pins in the board.
        currentBoard.addPin(message.getId(), PinType.Message);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Boards, ProjectManager.currentProject.getBoardId(), currentBoard);

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
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Boards, ProjectManager.currentProject.getBoardId(), currentBoard);

        return true;

    }
}
