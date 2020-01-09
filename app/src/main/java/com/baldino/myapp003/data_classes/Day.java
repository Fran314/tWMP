package com.baldino.myapp003.data_classes;

import java.util.ArrayList;
import java.util.List;

public class Day
{
    private List<List<String>> meals;

    public Day()
    {
        meals = new ArrayList<>();
    }

    public int getMealsPerDay() { return meals == null ? 0 : meals.size(); }
    public int getCoursesDimOfMeal(int meal)
    {
        if(meal < 0 || meal >= getMealsPerDay()) return -1;
        else return meals.get(meal) == null ? 0 : meals.get(meal).size();
    }
    public String getCourseOfmeal(int course, int meal)
    {
        if(meal < 0 || meal >= getMealsPerDay()) return "ERR";
        if(course < 0 || course >= getCoursesDimOfMeal(meal)) return "ERR";
        return meals.get(meal).get(course);
    }
    public int addMeal(List<String> courses)
    {
        meals.add(courses);
        return 0;
    }
    public int setMeal(int meal, List<String> courses)
    {
        if(meal < 0 || meal >= getMealsPerDay()) return -1;

        meals.set(meal, courses);
        return 0;
    }
}
