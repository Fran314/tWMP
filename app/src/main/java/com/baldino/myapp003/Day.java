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
        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
        if(true)
        {
            for(int j = 0; j < sWeekManager.daily_meals.size(); j++)
            {
                List<String> meal = new ArrayList<>();
                for(int k = 0; k < sWeekManager.daily_meals.get(j).getDim(); k++)
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
    public void addMeal(List<String> courses)
    {
        meals.add(courses);
    }
    public void setMeal(int meal, List<String> courses)
    {
        meals.set(meal, courses);
    }
}
