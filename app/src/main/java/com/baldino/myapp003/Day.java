package com.baldino.myapp003;

import com.baldino.myapp003.singletons.WeekManagerSingleton;

import java.util.ArrayList;
import java.util.List;

public class Day
{
    private List<List<String>> meals;
    public boolean isNew, hasSameFormat;

    public Day(boolean isNew)
    {
        this.isNew = isNew;
        meals = new ArrayList<>();
        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
        if(isNew)
        {
            for(int i = 0; i < sWeekManager.daily_meals.size(); i++)
            {
                List<String> meal = new ArrayList<>();
                for(int j = 0; j < sWeekManager.daily_meals.get(i).getDim(); j++)
                {
                    meal.add("-");
                }
                meals.add(meal);
            }
        }
        else
        {
            meals = new ArrayList<>();
        }
    }

    public String getCourseOfmeal(int course, int meal)
    {
        return meals.get(meal).get(course);
    }
    public void addMeal(List<String> courser)
    {
        meals.add(courser);
    }
    public void setMeal(int meal, List<String> courses)
    {
        meals.set(meal, courses);
    }
}
