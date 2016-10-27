package com.test.stream.stream.Objects.Board;

import com.test.stream.stream.Utilities.PinType;

/**
 * Created by cathe on 2016-10-26.
 */

public abstract class Pin {
    private String id;
    private String boardId;
    private String pinType;

    public String getBoardId()
    {
        return boardId;
    }

    public String getId()
    {
        return id;
    }

    public String getPinType()
    {
        return pinType;
    }

    public void setId(String id)
    {
        this.id = id;
    }
    public void setBoardId(String boardId)
    {
        this.boardId = boardId;
    }

    public void setPinType(PinType pinType)
    {
        this.pinType = pinType.toString();
    }
}
