package com.baldino.myapp003;

import com.baldino.myapp003.singletons.WeekManagerSingleton;

import java.util.ArrayList;
import java.util.List;

public class Day
{
    private List<List<String>> meals;

    public Day()
    {
        meals = new ArrayList<>();
    }

    public String getCourseOfmeal(int course, int meal)
    {
        return meals.get(meal).get(course);
    }
    public void addMeal(List<String> courses)
    {
        meals.add(courses);
    }
    public void setMeal(int meal, List<String> courses)
    {
        meals.set(meal, courses);
    }
}
