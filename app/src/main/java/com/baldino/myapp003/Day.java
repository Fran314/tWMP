package com.baldino.myapp003;

import java.util.List;

import static com.baldino.myapp003.Util.normalizeString;

public class Day
{
    private List<List<String>> meals;

    public Day(boolean error)
    {
        if(error)
        {
            //  TODO
            //  do something in case of error
        }
        else
        {
            //  TODO
            //  do some "standard format" stuff
        }
    }

    public Day(String lunch, String dinner, String side_dinner)
    {
    }

    public String getCourseOfmeal(int course, int meal)
    {
        return meals.get(meal).get(course);
    }
    public void setMeal(int meal, List<String> courses)
    {
        meals.set(meal, courses);
    }
}
