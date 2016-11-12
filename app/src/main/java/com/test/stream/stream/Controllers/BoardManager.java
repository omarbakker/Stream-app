package com.test.stream.stream.Controllers;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.test.stream.stream.Objects.Board.Board;
import com.test.stream.stream.Objects.Board.Pin;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A controller class for the Board functionality
 *
 * Created by Catherine Lee on 2016-10-26.
 */

public class BoardManager extends DataManager{
    private static BoardManager instance = new BoardManager();

    private Board currentBoard;
    private ConcurrentHashMap<String, Pin> pins = new ConcurrentHashMap<String, Pin>();
    private DataEventListener listener;

    /**
     * Ensure that BoardManager can only be instantiated within the class.
     */
    private BoardManager(){};

    /**
     *
     * @return the only instance of this class (singleton)
     */
    public static BoardManager getInstance() { return instance; }


    /**
     * Fetches a list of pinned items of a project sorted by creation time.
     *
     * @return a list of pin objects, sorted by time order
     */
    public List<Pin> GetPinsInProject()
    {
        List<Pin> pins = new ArrayList();
        List<String> keys = new ArrayList();
        keys.addAll(this.pins.keySet());
        Collections.sort(keys); //Keys in Firebase are stored automatically in date time order, so sorting keys will suffice

        for(String key: keys)
        {
            pins.add(this.pins.get(key));
        }

        return pins;
    }

    /**
     * Initializes the BoardManager so that it can maintain updated information of pins in
     * the current project.
     *
     * @param listener The listener to trigger when the data has been updated
     */
    public void InitializePins(DataEventListener listener)
    {
        this.listener = listener;
        super.registerParent(DatabaseFolders.Boards, ProjectManager.sharedInstance().getCurrentProject().getBoardId());
    }

    /**
     * Triggered by an update of the parent board object, this updates the
     * UI accordingly
     *
     * @param dataSnapshot The object returned by Firebase containing the read object and its key.
     */
    @Override
    public void parentUpdated(DataSnapshot dataSnapshot) {
        currentBoard = dataSnapshot.getValue(Board.class);
        registerPins();
        listener.onDataChanged();
    }

    /**
     * Triggered by the deletion of the parent board object, this updates the UI
     * accordingly
     */
    @Override
    public void parentDeleted() {
        currentBoard = null;
        listener.onDataChanged();
    }

    /**
     * Triggered by an update of a pin object, this updates the
     * UI accordingly
     *
     * @param dataSnapshot The object returned by Firebase containing the read object and its key.
     */
    @Override
    public void childUpdated(DataSnapshot dataSnapshot) {

        Pin pin = dataSnapshot.getValue(Pin.class);
        pins.put(pin.getId(), pin);

        if(currentBoard.getPins().size() == pins.size())
        {
            listener.onDataChanged();
        }
    }


    /**
     * Triggered by the deletion of a pin object, this updates the
     * UI accordingly
     */
    @Override
    public void childDeleted(String id) {
        pins.remove(id);
    }

    /**
     * Registers a listener to each pin not already stored in the BoardManager
     */
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

    /**
     * Update a pin.
     *
     * @param pin the updated pin object
     * @return true if the update request was made to Firebase. False otherwise.
     */
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

    /**
     * Creates a message-type pin and writes it to the database from
     * the provided parameters
     *
     * @param title the title or name of the pin
     * @param subtitle a secondary title for the pin
     * @param description the contents of the message pin
     * @return true if the pin was created and a the object is written to Firebase. False otherwise.
     */
    public boolean CreateMessagePin(String title, String subtitle, String description)
    {
        if(currentBoard == null)
        {
            return false; //Cannot create a pin without the project selected.
        }

        Pin message = new Pin(title, subtitle, description);
        message.setBoardId(ProjectManager.sharedInstance().getCurrentProject().getBoardId());

        String objectKey = DatabaseManager.getInstance().writeObject(DatabaseFolders.Pins, message);

        //Store the firebase object key as the object id.
        message.setId(objectKey);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Pins, objectKey, message);

        //Store the pins in the board.
        currentBoard.addPin(message.getId());
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Boards, ProjectManager.sharedInstance().getCurrentProject().getBoardId(), currentBoard);

        return true;
    }

    /**
     * Removes the provided pin from the database
     *
     * @param pin the pin object to remove from Firebase
     * @return true if the pin exists and a removal request was sent to Firebase. False otherwise.
     */
    public boolean RemovePin(Pin pin)
    {
        String pinId = pin.getId();

        //Check that we only remove pins that are on the database.
        if(!pins.containsKey(pinId)) {
            return false;
        }

        //Get the reference for the pin on Firebase.
        DatabaseReference refToDelete = DatabaseManager.getInstance()
                .getReference(DatabaseFolders.Pins.toString())
                .child(pinId);

        //Delete the pin
        refToDelete.removeValue();
        currentBoard.removePin(pinId);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Boards,
                ProjectManager.sharedInstance().getCurrentProject().getBoardId(),
                currentBoard);

        return true;

    }

    @Override
    public void Destroy()
    {
        instance = new BoardManager();
        super.Destroy();
    }

}
