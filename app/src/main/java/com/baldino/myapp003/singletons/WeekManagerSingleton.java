package com.baldino.myapp003.singletons;

import android.content.Context;
import android.util.Log;

import com.baldino.myapp003.Day;
import com.baldino.myapp003.MealFormat;
import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekManagerSingleton
{
    private static WeekManagerSingleton singleton_instance = null;

    private Context context;

    public int year = 1970, month = 0, day_of_month = 1;

    private List<String> existing_files;
    public List<MealFormat> daily_meals;

    //---- Variables relative to the currently loaded week ----//
    public boolean has_same_format;
    public List<Integer> courses_per_meal;
    public List<String> meal_names;
    //---- ----//

    public Day days[] = new Day[7];

    private WeekManagerSingleton()
    {
        existing_files = new ArrayList<>();
        daily_meals = new ArrayList<>();
        courses_per_meal = new ArrayList<>();
        meal_names = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int curr_year = c.get(Calendar.YEAR);
        int curr_month = c.get(Calendar.MONTH);
        int curr_day_of_month = c.get(Calendar.DAY_OF_MONTH);
        setCalendar(curr_year, curr_month, curr_day_of_month);
    }

    public static WeekManagerSingleton getInstance()
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
            String name = Util.getMealFormatName(lines.get(i));
            MealFormat new_meal_format = new MealFormat(name);

            i++;
            int dim = 0;
            if(i < lines.size()) dim = Util.getInt(lines.get(i));

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

        File folder = new File(context.getFilesDir(), Util.SUBFOLDER_PATH);
        folder.mkdirs();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day_of_month);

        String week_file_path = "";
        c.set(Calendar.WEEK_OF_MONTH, c.get(Calendar.WEEK_OF_MONTH));
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        week_file_path += String.format("%04d", c.get(Calendar.YEAR));
        week_file_path += String.format("%02d", c.get(Calendar.MONTH));
        week_file_path += String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
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

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day_of_month);

        String week_file_path = "";
        c.set(Calendar.WEEK_OF_MONTH, c.get(Calendar.WEEK_OF_MONTH));
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        week_file_path += String.format("%04d", c.get(Calendar.YEAR));
        week_file_path += String.format("%02d", c.get(Calendar.MONTH));
        week_file_path += String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
        week_file_path += ".txt";

        if(!binaryExists(week_file_path))
        {
            for(int i = 0; i < 7; i++)
            {
                //TODO: sistema qua con il nuovo tipo di Day
                days[i] = new Day(true);
            }
            has_same_format = true;
            for(int i = 0; i < daily_meals.size(); i++)
            {
                this.meal_names.add(daily_meals.get(i).getName());
                this.courses_per_meal.add(daily_meals.get(i).getDim());
            }
            return;
        }

        List<String> lines = new ArrayList<>();

        File folder = new File(context.getFilesDir(), Util.SUBFOLDER_PATH);
        folder.mkdirs();

        try
        {
            FileInputStream fis = new FileInputStream(new File(folder, week_file_path));
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
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            for(int i = 0; i < 7; i++)
            {
                //TODO: sistema qua con il nuovo tipo di Day
                days[i] = new Day(true);
            }
            has_same_format = true;
            for(int i = 0; i < daily_meals.size(); i++)
            {
                this.meal_names.add(daily_meals.get(i).getName());
                this.courses_per_meal.add(daily_meals.get(i).getDim());
            }
            return;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        List<String> curr_meal_names = new ArrayList<>();
        List<Integer> curr_courser_per_meal = new ArrayList<>();
        int meals_per_day = 0;

        int counter = 0;
        if(counter < lines.size())
        {
            meals_per_day = Util.getInt(lines.get(counter));
            counter++;
        }
        if(counter < lines.size())
        {
            curr_meal_names = Util.getStrings(lines.get(counter), meals_per_day);
            counter++;
        }
        if(counter < lines.size())
        {
            curr_courser_per_meal = Util.getInts(lines.get(counter), meals_per_day);
            counter++;
        }

        if(lines.size() - counter != 7*meals_per_day)
        {
            //TODO: do some error handling stuff
            for(int i = 0; i < 7; i++)
            {
                days[i] = new Day(true);
            }
            has_same_format = true;
            for(int i = 0; i < daily_meals.size(); i++)
            {
                this.meal_names.add(daily_meals.get(i).getName());
                this.courses_per_meal.add(daily_meals.get(i).getDim());
            }
            return;
        }
        else
        {
            boolean check = true;
            if(daily_meals.size() != curr_meal_names.size()) check = false;
            for(int i = 0; i < daily_meals.size() && check; i++)
            {
                if(daily_meals.get(i).getDim() != curr_courser_per_meal.get(i)) check = false;
            }
            for(int i = 0; i < daily_meals.size() && check; i++)
            {
                if(Util.compareStrings(daily_meals.get(i).getName(), curr_meal_names.get(i)) != 0) check = false;
            }

            has_same_format = check;
            for(int i = 0; i < 7; i++)
            {
                days[i] = new Day(false);
                for(int j = 0; j < meals_per_day; j++)
                {
                    days[i].addMeal(Util.getStrings(lines.get(counter), curr_courser_per_meal.get(j)));
                    counter++;
                }
            }

            this.courses_per_meal = curr_courser_per_meal;
            this.meal_names = curr_meal_names;
        }
    }

    public int saveWooks()
    {
        StringBuilder output_string = new StringBuilder("");
        //output_string.append(context.getResources().getString(context.getResources().getStringArray(R.array.id_array)[0]));

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(),"test.txt"));
            fos.write(output_string.toString().getBytes(Util.STD_CHARSET));
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return -1;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return -2;
        }

        return 0;
    }

    public int saveWeeks()
    {
        StringBuilder output_string = new StringBuilder("");
        for(int i = 0; i < existing_files.size(); i++)
        {
            output_string.append("[");
            output_string.append(existing_files.get(i));
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
            return -1;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return -2;
        }

        return 0;
    }
    public int loadWeeks()
    {
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), Util.WEEKS_LIST_PATH));
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
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return -1;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return -2;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return -3;
        }

        for(int i = 0; i < lines.size(); i++)
        {
            addWeek(Util.getFileName(lines.get(i)));
        }

        return 0;
    }

    public int addWeek(String name)
    {
        boolean exists = false, found_place = false;
        int pos = existing_files.size();
        for(int i = 0; i < existing_files.size() && !exists && !found_place; i++)
        {
            if(name.equals(existing_files.get(i)))     //Only checks if fc_names are the same
                exists = true;
            else if(name.compareTo(existing_files.get(i)) < 0)
            {
                found_place = true;
                pos = i;
            }
        }
        if(exists) return -1;
        else
        {
            existing_files.add(pos, name);

            return 1;
        }
    }

    public boolean binaryExists(String name) { return binaryExists(name, 0, existing_files.size()-1); }
    public boolean binaryExists(String name, int left, int right)
    {
        if(left  > right) return false;

        int mid = left + ((right - left)/2);
        if(name.equals(existing_files.get(mid))) return true;
        else if(name.compareTo(existing_files.get(mid)) < 0) return binaryExists(name, left, mid-1);
        else return binaryExists(name, mid+1, right);
    }

}
