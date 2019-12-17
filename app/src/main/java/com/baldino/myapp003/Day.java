package com.baldino.myapp003;

import static com.baldino.myapp003.Util.normalizeString;

public class Day
{
    private String lunch, dinner, side_dinner;

    public Day(boolean error)
    {
        if(error)
        {
            lunch = "NULL_LUNCH";
            dinner = "NULL_DINNER";
            side_dinner = "NULL_SIDE";
        }
        else
        {
            lunch = "-";
            dinner = "-";
            side_dinner = "-";
        }
    }

    public Day(String lunch, String dinner, String side_dinner)
    {
        setLunch(lunch);
        setDinner(dinner);
        setSideDinner(side_dinner);
    }

    public Day(Recipe lunch, Recipe dinner, Recipe side_dinner)
    {
        setLunch(lunch.getName());
        setDinner(dinner.getName());
        setSideDinner(side_dinner.getName());
    }

    public String getLunch() { return lunch; }
    public String getDinner() { return dinner; }
    public String getSideDinner() { return side_dinner; }

    public void setLunch(String lunch) { this.lunch = normalizeString(lunch); }
    public void setDinner(String dinner) { this.dinner = normalizeString(dinner); }
    public void setSideDinner(String side_dinner) { this.side_dinner = normalizeString(side_dinner); }
}
