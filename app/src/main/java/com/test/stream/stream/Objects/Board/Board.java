package com.test.stream.stream.Objects.Board;

import com.test.stream.stream.Utilities.PinType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cathe on 2016-10-01.
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
    public Board()
    {
        parentProjectId = "";
    }
    //endregion

    //region Core Functions
    public boolean addPin(String pinId, PinType type)
    {
        if(hasPin(pinId)) {
            return false;
        }

        pins.put(pinId, type.toString());
        return true;
    }

    public boolean removePin(String pinId)
    {
        if(!hasPin(pinId))
        {
            return false;
        }

        pins.remove(pinId);
        return true;
    }

    public boolean hasPin(String pinId)
    {
        return pins.containsKey(pinId);
    }

    //endregion

}