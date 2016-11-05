package com.test.stream.stream.Objects.Board;

import com.test.stream.stream.Utilities.PinType;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a representation of the Board datatype, which is the encapsulation of
 * all pins within a project
 *
 * Created by Catherine Lee on 2016-10-01.
 */

public class Board {

    //region Variables
    private String parentProjectId;
    private Map<String, String> pins = new HashMap<String, String>();  //pin Id - pin type (message or file)
    //endregion

    //region Setters and Getters
    public String getParentProjectId() {
        return parentProjectId;
    }
    public Map<String, String> getPins() {
        return pins;
    }

    public void setParentProjectId(String parentProjectId) { this.parentProjectId = parentProjectId; }


    //endregion

    //region Constructors

    /**
     * Create a new board object
     */
    public Board()
    {
        parentProjectId = "";
    }
    //endregion

    //region Core Functions

    /**
     * Add a new pin to the board
     *
     * @param pinId the firebase id of the pin
     * @param type the enum representing the pin's class
     * @return true if the pin has been added. False otherwise.
     */
    public boolean addPin(String pinId, PinType type)
    {
        if(hasPin(pinId)) {
            return false;
        }

        pins.put(pinId, type.toString());
        return true;
    }

    /**
     * Remove the specified pin from the board
     *
     * @param pinId The id of the pin to remove
     * @return true if the pin has been removed. False otherwise
     */
    public boolean removePin(String pinId)
    {
        if(!hasPin(pinId))
        {
            return false;
        }

        pins.remove(pinId);
        return true;
    }

    /**
     * Check if the specified pin exists in the board
     *
     * @param pinId The id of the pin to check
     * @return true if the pin is in the board. False otherwise
     */
    public boolean hasPin(String pinId)
    {
        return pins.containsKey(pinId);
    }

    //endregion

}