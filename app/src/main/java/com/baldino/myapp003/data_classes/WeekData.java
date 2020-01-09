package com.baldino.myapp003.data_classes;

import com.baldino.myapp003.Util;

import java.util.ArrayList;
import java.util.List;

public class WeekData
{
    public List<Integer> courses_per_meal;
    public List<String> meal_names;
    public int meals_per_day = 0;
    public boolean is_new_week = true;

    public Day[] days = new Day[7];

    public WeekData()
    {
        courses_per_meal = new ArrayList<>();
        meal_names = new ArrayList<>();

        for(int i = 0; i  < 7; i++)
        {
            days[i] = new Day();
        }
    }

    public boolean sameFormatAs(WeekData arg)
    {
        boolean to_return = true;
        if(meals_per_day != arg.meals_per_day) to_return = false;
        if(to_return && courses_per_meal.size() != arg.courses_per_meal.size()) to_return = false;
        if(to_return && meal_names.size() != arg.meal_names.size()) to_return = false;
        for(int i = 0; to_return && i < meals_per_day; i++)
        {
            if(courses_per_meal.get(i) != arg.courses_per_meal.get(i)) to_return = false;
            if(Util.compareStrings(meal_names.get(i), arg.meal_names.get(i)) != 0) to_return = false;
        }

        return to_return;
    }
}
