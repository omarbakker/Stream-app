package com.test.stream.stream.Objects.Board;

/**
 * This is an object representation of a message-type pin.
 *
 * Created by Catherine Lee on 2016-10-26.
 */

public class PinMessage extends Pin{
    private String title;
    private String subtitle;
    private String description;


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

    public void setTitle(String title)
    {
        this.title = title;
    }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setDescription(String description)
    {
        this.description = description;
    }


    /**
     * Create a new message type pin
     *
     * @param title the title or name of the pin
     * @param subtitle the secondary title of the pin
     * @param description the contents of the pin
     */
    public PinMessage(String title, String subtitle, String description){
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
    }

    public PinMessage(){

    }
}