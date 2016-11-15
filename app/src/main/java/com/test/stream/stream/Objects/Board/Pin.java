package com.test.stream.stream.Objects.Board;

/**
 *  This is a representation of the abstract pin datatype which stores a pin, its type,
 *  and the board it belongs to
 *
 *  Created by Catherine Lee on 2016-10-26.
 */

public class Pin {
    private String id;
    private String boardId;
    private String title;
    private String subtitle;
    private String description;


    public String getBoardId()
    {
        return boardId;
    }
    public String getId()
    {
        return id;
    }
    public String getSubtitle(){
        return subtitle;
    }
    public String getTitle()
    {
        return title;
    }
    public String getDescription()
    {
        return description;
    }

    public void setId(String id)
    {
        this.id = id;
    }
    public void setBoardId(String boardId)
    {
        this.boardId = boardId;
    }
    public void setTitle(String title){ this.title = title;}
    public void setDescription(String description) {this.description = description; }
    public void setSubtitle(String subtitle) {this.subtitle = subtitle; }

    /**
     * Create a new message type pin
     *
     * @param title the title or name of the pin
     * @param subtitle the secondary title of the pin
     * @param description the contents of the pin
     */
    public Pin(String title, String subtitle, String description){
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
    }

    public Pin()
    {

    }


}