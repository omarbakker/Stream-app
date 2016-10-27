package com.test.stream.stream.Objects.Board;

/**
 * Created by cathe on 2016-10-26.
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



    public void setSubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}