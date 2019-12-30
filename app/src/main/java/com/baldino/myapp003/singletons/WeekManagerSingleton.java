package com.baldino.myapp003.singletons;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.baldino.myapp003.Day;
import com.baldino.myapp003.MealFormat;
import com.baldino.myapp003.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class WeekManagerSingleton
{
    private static WeekManagerSingleton singleton_instance = null;

    private Context context;

    public int year = 1970, month = 1, day_of_month = 1;

    public List<String> saved_weeks;
    public List<MealFormat> daily_meals;

    //---- Variables relative to the currently loaded week ----//
    public boolean has_same_format, is_new_week;
    public List<Integer> courses_per_meal;
    public List<String> meal_names;
    //---- ----//

    public Day days[] = new Day[7];

    private WeekManagerSingleton()
    {
        saved_weeks = new ArrayList<>();
        daily_meals = new ArrayList<>();
        courses_per_meal = new ArrayList<>();
        meal_names = new ArrayList<>();

        LocalDate curr_date = LocalDate.now();
        setCalendar(curr_date.getYear(), curr_date.getMonthValue(), curr_date.getDayOfMonth());
    }

    public synchronized static WeekManagerSingleton getInstance()
    {
        if (singleton_instance == null)
            singleton_instance = new WeekManagerSingleton();

        return singleton_instance;
    }

    public void setContext(Context context) { this.context = context; }

    public void setCalendar(int year, int month, int day_of_month)
    {
        this.year = year;
        this.month = month;
        this.day_of_month = day_of_month;
    }

    public void saveDailyMeals()
    {
        StringBuilder output_string = new StringBuilder("");
        for(int i = 0; i < daily_meals.size(); i++)
        {
            output_string.append("[");
            output_string.append(daily_meals.get(i).getName());
            output_string.append("]\n[");
            output_string.append(daily_meals.get(i).getDim());
            output_string.append("]\n");

            for(int j = 0; j < daily_meals.get(i).getDim(); j++)
            {
                output_string.append("[");
                output_string.append(daily_meals.get(i).getType(j));
                output_string.append("][");
                output_string.append(daily_meals.get(i).getStd(j));
                output_string.append("]\n");
            }
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), Util.DAILY_MEALS_PATH));
            fos.write(output_string.toString().getBytes(Util.STD_CHARSET));
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void loadDailyMeals()
    {
        daily_meals = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        try
        {
            FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), Util.DAILY_MEALS_PATH));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, Util.STD_CHARSET));
            String line = null;

            while((line = reader.readLine()) != null)
            {
                if(line.length() > 0 && line.charAt(0) != '%')
                {
                    if(line.lastIndexOf(']') != -1) lines.add(line.substring(0, line.lastIndexOf(']') + 1));
                    else lines.add(line);
                }
            }
        }
        catch (FileNotFoundException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for(int i = 0; i < lines.size(); i++)
        {
            String name = Util.getStringFromLine(lines.get(i));
            MealFormat new_meal_format = new MealFormat(name);

            i++;
            int dim = 0;
            if(i < lines.size()) dim = Util.getIntFromLine(lines.get(i));

            for(int iter = 0; iter < dim; iter++)
            {
                i++;
                if(i < lines.size())
                {
                    int vals[] = Util.getTypeAndStd(lines.get(i));
                    new_meal_format.addMeal(vals[0], vals[1]);
                }
            }

            daily_meals.add(new_meal_format);
        }
    }

    public void saveData()
    {
        File folder = new File(context.getFilesDir(), Util.WEEKS_DATA_FOLDER);
        folder.mkdirs();

        LocalDate date = LocalDate.of(year, month, day_of_month);

        int d_offset = date.getDayOfWeek().getValue() - Util.FIRST_DAY_OF_WEEK;
        if(d_offset < 0) d_offset += 7;
        date = date.minusDays(d_offset);

        String week_file_path = String.format("%04d", date.getYear()) + "-";
        week_file_path += String.format("%02d", date.getMonthValue()) + "-";
        week_file_path += String.format("%02d", date.getDayOfMonth());
        week_file_path += ".txt";

        addWeek(week_file_path);
        saveWeeks();

        StringBuilder output_string = new StringBuilder("");
        output_string.append("[").append(daily_meals.size()).append("]\n");
        for(int i = 0; i < daily_meals.size(); i++)
        {
            output_string.append("[");
            output_string.append(daily_meals.get(i).getName());
            output_string.append("]");
        }
        output_string.append("\n");
        for(int i = 0; i < daily_meals.size(); i++)
        {
            output_string.append("[");
            output_string.append(daily_meals.get(i).getDim());
            output_string.append("]");
        }
        output_string.append("\n");

        for(int i = 0; i < 7; i++)
        {
            for(int j = 0; j < daily_meals.size(); j++)
            {
                for(int k = 0; k < daily_meals.get(j).getDim(); k++)
                {
                    output_string.append("[");
                    output_string.append(days[i].getCourseOfmeal(k, j));
                    output_string.append("]");
                }
                output_string.append("\n");
            }
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(folder, week_file_path));
            fos.write(output_string.toString().getBytes(Util.STD_CHARSET));
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void loadData()
    {
        courses_per_meal = new ArrayList<>();
        meal_names = new ArrayList<>();

        LocalDate date = LocalDate.of(year, month, day_of_month);

        int d_offset = date.getDayOfWeek().getValue() - Util.FIRST_DAY_OF_WEEK;
        if(d_offset < 0) d_offset += 7;
        date = date.minusDays(d_offset);

        String week_file_path = String.format("%04d", date.getYear()) + "-";
        week_file_path += String.format("%02d", date.getMonthValue()) + "-";
        week_file_path += String.format("%02d", date.getDayOfMonth());
        week_file_path += ".txt";

        if(!binaryExists(week_file_path))
        {
            initWeekAsNull();
            return;
        }

        WeekData week_data = loadData(week_file_path);

        boolean check = true;
        if(daily_meals.size() != week_data.meal_names.size()) check = false;
        for(int i = 0; i < daily_meals.size() && check; i++)
        {
            if(daily_meals.get(i).getDim() != week_data.courses_per_meal.get(i)) check = false;
        }
        for(int i = 0; i < daily_meals.size() && check; i++)
        {
            if(Util.compareStrings(daily_meals.get(i).getName(), week_data.meal_names.get(i)) != 0) check = false;
        }

        has_same_format = check;
        is_new_week = false;
        days = week_data.days;
        this.courses_per_meal = week_data.courses_per_meal;
        this.meal_names = week_data.meal_names;
    }

    public WeekData loadData(String path)
    {
        WeekData to_return = new WeekData();

        File folder = new File(context.getFilesDir(), Util.WEEKS_DATA_FOLDER);
        folder.mkdirs();
        List<String> lines = Util.readFile(new File(folder, path));

        List<String> curr_meal_names = new ArrayList<>();
        List<Integer> curr_courses_per_meal = new ArrayList<>();
        int curr_meals_per_day = 0;

        //  Here I HEAVILY assume that all the data read from now on has the exact format it
        //  should have to be read. Why? First, because realistically the only way some data might
        //  be there is if I wrote it there before (and hence will have the exact format).
        //  Secondly, I tried to write the error handling part and fuck that

        curr_meals_per_day = Util.getIntFromLine(lines.get(0));
        curr_meal_names = Util.getStringsFromLine(lines.get(1), curr_meals_per_day);
        curr_courses_per_meal = Util.getIntsFromLine(lines.get(2), curr_meals_per_day);
        int counter = 3;

        for(int i = 0; i < 7; i++)
        {
            to_return.days[i] = new Day();
            for(int j = 0; j < curr_meals_per_day; j++)
            {
                to_return.days[i].addMeal(Util.getStringsFromLine(lines.get(counter), curr_courses_per_meal.get(j)));
                counter++;
            }
        }

        to_return.meals_per_day = curr_meals_per_day;
        to_return.courses_per_meal = curr_courses_per_meal;
        to_return.meal_names = curr_meal_names;

        return to_return;
    }

    private void initWeekAsNull()
    {
        for(int i = 0; i < 7; i++)
        {
            days[i] = new Day();
            for(int j = 0; j < daily_meals.size(); j++)
            {
                List<String> meal = new ArrayList<>();
                for(int k = 0; k < daily_meals.get(j).getDim(); k++)
                {
                    meal.add("-");
                }
                days[i].addMeal(meal);
            }
        }
        is_new_week = true;
        has_same_format = true;
        for(int i = 0; i < daily_meals.size(); i++)
        {
            this.meal_names.add(daily_meals.get(i).getName());
            this.courses_per_meal.add(daily_meals.get(i).getDim());
        }
    }

    public void saveWeeks()
    {
        StringBuilder output_string = new StringBuilder();
        for(int i = 0; i < saved_weeks.size(); i++)
        {
            output_string.append("[");
            output_string.append(saved_weeks.get(i));
            output_string.append("]\n");
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), Util.WEEKS_LIST_PATH));
            fos.write(output_string.toString().getBytes(Util.STD_CHARSET));
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void loadWeeks()
    {
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), Util.WEEKS_LIST_PATH));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, Util.STD_CHARSET));
            String line;

            while((line = reader.readLine()) != null)
            {
                if(line.length() > 0 && line.charAt(0) != '%')
                {
                    if(line.lastIndexOf(']') != -1) lines.add(line.substring(0, line.lastIndexOf(']') + 1));
                    else lines.add(line);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for(int i = 0; i < lines.size(); i++)
        {
            addWeek(Util.getStringFromLine(lines.get(i)));
        }
    }

    private void addWeek(String name)
    {
        boolean exists = false, found_place = false;
        int pos = saved_weeks.size();
        for(int i = 0; i < saved_weeks.size() && !exists && !found_place; i++)
        {
            if(name.equals(saved_weeks.get(i)))     //Only checks if fc_names are the same
                exists = true;
            else if(name.compareTo(saved_weeks.get(i)) < 0)
            {
                found_place = true;
                pos = i;
            }
        }
        if(!exists)
        {
            saved_weeks.add(pos, name);
        }
    }

    private boolean binaryExists(String name) { return binaryExists(name, 0, saved_weeks.size()-1); }
    private boolean binaryExists(String name, int left, int right)
    {
        if(left  > right) return false;

        int mid = left + ((right - left)/2);
        if(name.equals(saved_weeks.get(mid))) return true;
        else if(name.compareTo(saved_weeks.get(mid)) < 0) return binaryExists(name, left, mid-1);
        else return binaryExists(name, mid+1, right);
    }

    public void refactor(int old_first_day_of_week, int new_first_day_of_week)
    {
        //TODO actually implement this refactoring thing
        List<DateRange> ranges = new ArrayList<>();

        LocalDate oldDate;
        LocalDate newDate;

        int b_year = 0, b_month = 0, b_day_of_month = 0;
        int n_year = 0, n_month = 0, n_day_of_month = 0;

        if(saved_weeks.size() > 0)
        {
            b_year = Util.stringToInt(saved_weeks.get(0).substring(0, 4));
            b_month = Util.stringToInt(saved_weeks.get(0).substring(5, 7));
            b_day_of_month = Util.stringToInt(saved_weeks.get(0).substring(8, 10));

            oldDate = LocalDate.of(b_year, b_month, b_day_of_month);

            for(int i = 1; i < saved_weeks.size(); i++)
            {
                n_year = Util.stringToInt(saved_weeks.get(i).substring(0, 4));
                n_month = Util.stringToInt(saved_weeks.get(i).substring(5, 7));
                n_day_of_month = Util.stringToInt(saved_weeks.get(i).substring(8, 10));

                newDate = LocalDate.of(n_year, n_month, n_day_of_month);

                if(DAYS.between(oldDate, newDate) > 7)
                {
                    DateRange new_rage = new DateRange();
                    new_rage.setBeginning(b_year, b_month, b_day_of_month);
                    new_rage.setEnding(oldDate.getYear(), oldDate.getMonthValue(), oldDate.getDayOfMonth());

                    ranges.add(new_rage);

                    b_year = n_year;
                    b_month = n_month;
                    b_day_of_month = n_day_of_month;
                }

                oldDate = LocalDate.of(n_year, n_month, n_day_of_month);
            }

            DateRange last_rage = new DateRange();
            last_rage.setBeginning(b_year, b_month, b_day_of_month);
            last_rage.setEnding(oldDate.getYear(), oldDate.getMonthValue(), oldDate.getDayOfMonth());

            ranges.add(last_rage);

            for(DateRange range : ranges)
            {
                Log.w("AAA", range.b_day_of_month + "/" + range.b_month + "/" + range.b_year + " - " +
                                        range.e_day_of_month + "/" + range.e_month + "/"+ range.e_year);
            }

            //  Get all ranges
            //  For each range
            //      create 14 days
            //      initiate the first 7 days as
            for(DateRange range : ranges)
            {
                List<Integer> first_courses_per_meal, second_courses_per_meal;
                List<String> first_meal_names, second_meal_namesdesxza;
                Day[] refactor_days = new Day[14];
            }
        }
    }

    private class WeekData
    {
        public List<Integer> courses_per_meal;
        public List<String> meal_names;
        public int meals_per_day;

        public Day[] days = new Day[7];

        public WeekData()
        {

        }
    }

    private class DateRange
    {
        public int b_year, b_month, b_day_of_month;
        public int e_year, e_month, e_day_of_month;

        public void DateRange()
        {
            b_year = 0;
            b_month = 0;
            b_day_of_month = 0;
            e_year = 0;
            e_month = 0;
            e_day_of_month = 0;
        }

        public void setBeginning(int b_year, int b_month, int b_day_of_month)
        {
            this.b_year = b_year;
            this.b_month = b_month;
            this.b_day_of_month = b_day_of_month;
        }

        public void setEnding(int e_year, int e_month, int e_day_of_month)
        {
            this.e_year = e_year;
            this.e_month = e_month;
            this.e_day_of_month = e_day_of_month;
        }
    }
}
