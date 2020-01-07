package com.baldino.myapp003.singletons;

import android.content.Context;

import com.baldino.myapp003.data_classes.DateRange;
import com.baldino.myapp003.data_classes.Day;
import com.baldino.myapp003.data_classes.MealFormat;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.data_classes.WeekData;

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

public class WeekManager
{
    public int year = 1970, month = 1, day_of_month = 1;

    public List<String> saved_weeks;
    public List<MealFormat> daily_meals;

    //---  Variables relative to the currently loaded week  ---//
    public boolean has_same_format, is_new_week;
    public List<Integer> courses_per_meal;
    public List<String> meal_names;

    public Day days[] = new Day[7];
    //---   ---//

    public WeekManager()
    {
        saved_weeks = new ArrayList<>();
        daily_meals = new ArrayList<>();
        courses_per_meal = new ArrayList<>();
        meal_names = new ArrayList<>();

        LocalDate curr_date = LocalDate.now();
        setCalendar(curr_date.getYear(), curr_date.getMonthValue(), curr_date.getDayOfMonth());
    }

    public void setCalendar(int year, int month, int day_of_month)
    {
        this.year = year;
        this.month = month;
        this.day_of_month = day_of_month;
    }

    public void saveDailyMeals(Context context)
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

        Util.saveFile(output_string, new File(context.getFilesDir(), Util.DAILY_MEALS_PATH));
    }
    public void loadDailyMeals(Context context)
    {
        daily_meals = new ArrayList<>();
        List<String> lines = Util.loadFile(new File(context.getFilesDir(), Util.DAILY_MEALS_PATH));

        if(!"ERR".equals(lines.get(0)))
        {
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
    }

    //---   UPDATING METHODS FOR WHEN THE RECIPES CHANGE    ---//
    public void addedRecipe(int collection_index, int pos)
    {
        for(int i = 0; i < daily_meals.size(); i++)
        {
            for(int j = 0; j < daily_meals.get(i).getDim(); j++)
            {
                if(daily_meals.get(i).getType(j) == collection_index)
                {
                    int std = daily_meals.get(i).getStd(j);
                    if(std-1 >= pos)
                    {
                        daily_meals.get(i).setStd(j, std+1);
                    }
                }
            }
        }
    }
    public void addedCollection(int pos)
    {
        for(int i = 0; i < daily_meals.size(); i++)
        {
            for(int j = 0; j < daily_meals.get(i).getDim(); j++)
            {
                int type = daily_meals.get(i).getType(j);
                if(type >= pos)
                {
                    daily_meals.get(i).setType(j, type+1);
                }
            }
        }
    }
    public void removedRecipe(int collection_index, int pos)
    {
        for(int i = 0; i < daily_meals.size(); i++)
        {
            for(int j = 0; j < daily_meals.get(i).getDim(); j++)
            {
                if(daily_meals.get(i).getType(j) == collection_index)
                {
                    int std = daily_meals.get(i).getStd(j);
                    if(std-1 == pos)
                    {
                        daily_meals.get(i).setStd(j, 0);
                    }
                    else if(std-1 > pos)
                    {
                        daily_meals.get(i).setStd(j, std-1);
                    }
                }
            }
        }
    }
    public void removedCollection(int pos)
    {
        for(int i = 0; i < daily_meals.size(); i++)
        {
            for(int j = 0; j < daily_meals.get(i).getDim(); j++)
            {
                int type = daily_meals.get(i).getType(j);
                if(type == pos)
                {
                    daily_meals.get(i).setStd(j, 0);
                    daily_meals.get(i).setType(j, 0);
                }
                else if(type > pos)
                {
                    daily_meals.get(i).setType(j, type-1);
                }
            }
        }
    }
    //---   ---//

    public void saveData(Context context)
    {
        LocalDate date = LocalDate.of(year, month, day_of_month);

        int d_offset = date.getDayOfWeek().getValue() - Util.FIRST_DAY_OF_WEEK;
        if(d_offset < 0) d_offset += 7;
        date = date.minusDays(d_offset);

        String week_file_path = Util.dateToFileName(date);

        addWeek(week_file_path);
        saveWeeks(context);

        WeekData data = new WeekData();

        for(int i = 0; i < daily_meals.size(); i++)
        {
            data.meal_names.add(daily_meals.get(i).getName());
            data.courses_per_meal.add(daily_meals.get(i).getDim());
        }

        data.is_new_week = true;
        data.meals_per_day = daily_meals.size();
        data.days = days;

        saveData(data, week_file_path, context);
    }
    public void loadData(Context context)
    {
        courses_per_meal = new ArrayList<>();
        meal_names = new ArrayList<>();

        LocalDate date = LocalDate.of(year, month, day_of_month);

        int d_offset = date.getDayOfWeek().getValue() - Util.FIRST_DAY_OF_WEEK;
        if(d_offset < 0) d_offset += 7;
        date = date.minusDays(d_offset);

        String week_file_path = Util.dateToFileName(date);

        WeekData week_data;
        if(!binaryExists(week_file_path)) week_data = initWeekAsNull();
        else week_data = loadData(week_file_path, context);

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

        this.has_same_format = check;
        this.is_new_week = week_data.is_new_week;
        this.days = week_data.days;
        this.courses_per_meal = week_data.courses_per_meal;
        this.meal_names = week_data.meal_names;
    }

    public void saveData(WeekData data, String path, Context context)
    {
        StringBuilder output_string = new StringBuilder("");
        output_string.append("[").append(data.meals_per_day).append("]\n");
        for(int i = 0; i < data.meals_per_day; i++)
        {
            output_string.append("[");
            output_string.append(data.meal_names.get(i));
            output_string.append("]");
        }
        output_string.append("\n");
        for(int i = 0; i < data.meals_per_day; i++)
        {
            output_string.append("[");
            output_string.append(data.courses_per_meal.get(i));
            output_string.append("]");
        }
        output_string.append("\n");

        for(int i = 0; i < 7; i++)
        {
            for(int j = 0; j < data.meals_per_day; j++)
            {
                for(int k = 0; k < data.courses_per_meal.get(j); k++)
                {
                    output_string.append("[");
                    output_string.append(data.days[i].getCourseOfmeal(k, j));
                    output_string.append("]");
                }
                output_string.append("\n");
            }
        }

        File folder = new File(context.getFilesDir(), Util.WEEKS_DATA_FOLDER);
        folder.mkdirs();

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(folder, path));
            fos.write(output_string.toString().getBytes(Util.STD_CHARSET));
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public WeekData loadData(String path, Context context)
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

        to_return.is_new_week = false;
        to_return.meals_per_day = curr_meals_per_day;
        to_return.courses_per_meal = curr_courses_per_meal;
        to_return.meal_names = curr_meal_names;

        return to_return;
    }

    private WeekData initWeekAsNull()
    {
        WeekData to_return = new WeekData();

        to_return.is_new_week = true;
        to_return.meals_per_day = daily_meals.size();

        for(int i = 0; i < daily_meals.size(); i++)
        {
            to_return.meal_names.add(daily_meals.get(i).getName());
            to_return.courses_per_meal.add(daily_meals.get(i).getDim());
        }

        for(int i = 0; i < 7; i++)
        {
            to_return.days[i] = new Day();
            for(int j = 0; j < daily_meals.size(); j++)
            {
                List<String> meal = new ArrayList<>();
                for(int k = 0; k < daily_meals.get(j).getDim(); k++)
                {
                    meal.add(Util.NULL_RECIPE);
                }
                to_return.days[i].addMeal(meal);
            }
        }

        return to_return;
    }

    public void saveWeeks(Context context)
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
    public void loadWeeks(Context context)
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
            if(name.equals(saved_weeks.get(i)))
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

    public void refactor(int old_first_day_of_week, int new_first_day_of_week, Context context)
    {
        if(saved_weeks.size() == 0) return;

        //---   Divide the saved weeks in ranges of consecutive weeks   ---//
        List<DateRange> ranges = new ArrayList<>();

        LocalDate oldDate, newDate;

        int begin_index = 0;

        oldDate = Util.fileNameToDate(saved_weeks.get(0));

        for(int i = 1; i < saved_weeks.size(); i++)
        {
            newDate = Util.fileNameToDate(saved_weeks.get(i));

            if(DAYS.between(oldDate, newDate) > 7)
            {
                DateRange new_rage = new DateRange();

                for(int j = begin_index; j < i; j++)
                {
                    new_rage.files.add(saved_weeks.get(j));
                }

                ranges.add(new_rage);

                begin_index = i;
            }

            oldDate = LocalDate.of(newDate.getYear(), newDate.getMonthValue(), newDate.getDayOfMonth());
        }

        DateRange last_rage = new DateRange();

        for(int j = begin_index; j < saved_weeks.size(); j++)
        {
            last_rage.files.add(saved_weeks.get(j));
        }

        ranges.add(last_rage);
        //---   ---//


        int offset = new_first_day_of_week - old_first_day_of_week;
        if(offset < 0) offset += 7;

        List<String> new_saved_weeks = new ArrayList<>();

        for(DateRange range : ranges)
        {
            WeekData first_week_data, second_week_data, shifted_week_data;

            //---   Create a fake empty first week  ---//
            first_week_data = loadData(range.files.get(0), context);
            for(int i = 0; i < 7; i++)
            {
                first_week_data.days[i] = new Day();
                for(int j = 0; j < first_week_data.meals_per_day; j++)
                {
                    List<String> meal = new ArrayList<>();
                    for(int k = 0; k < first_week_data.courses_per_meal.get(j); k++)
                    {
                        meal.add(Util.NULL_RECIPE);
                    }
                    first_week_data.days[i].addMeal(meal);
                }
            }
            //---   ---//

            for(int i = 0; i < range.files.size() + 1; i++)
            {
                if(i < range.files.size()) second_week_data = loadData(range.files.get(i), context);
                else
                {
                    //---   Create a fake empty last week   ---//
                    second_week_data = loadData(range.files.get(i-1), context);
                    for(int j = 0; j < offset; j++)
                    {
                        second_week_data.days[j] = new Day();
                        for(int k = 0; k < second_week_data.meals_per_day; k++)
                        {
                            List<String> meal = new ArrayList<>();
                            for(int h = 0; h < second_week_data.courses_per_meal.get(k); h++)
                            {
                                meal.add(Util.NULL_RECIPE);
                            }
                            second_week_data.days[j].addMeal(meal);
                        }
                    }
                    //---   ---//
                }
                LocalDate first_date, shifted_date;
                if(i > 0) first_date = Util.fileNameToDate(range.files.get(i-1));
                else first_date = Util.fileNameToDate(range.files.get(0)).minusDays(7);
                shifted_date = first_date.plusDays(offset);
                shifted_week_data = new WeekData();

                if(second_week_data.sameFormatAs(first_week_data))
                {
                    shifted_week_data.courses_per_meal = second_week_data.courses_per_meal;
                    shifted_week_data.meal_names = second_week_data.meal_names;
                    shifted_week_data.meals_per_day = second_week_data.meals_per_day;

                    for(int j = 0; j < 7 - offset; j++)
                    {
                        shifted_week_data.days[j] = first_week_data.days[j+offset];
                    }
                    for(int j = 7 - offset; j < 7; j++)
                    {
                        shifted_week_data.days[j] = second_week_data.days[j - (7 - offset)];
                    }
                }
                else
                {
                    //---   Check how big is the offset in order to lose as little data as possible
                    //      when cutting off data from either the first or the second week  ---//
                    if(offset < 4)
                    {
                        shifted_week_data.courses_per_meal = first_week_data.courses_per_meal;
                        shifted_week_data.meal_names = first_week_data.meal_names;
                        shifted_week_data.meals_per_day = first_week_data.meals_per_day;

                        //---   Save data from first week and ignore the data from second week  ---//
                        for(int j = 0; j < 7 - offset; j++)
                        {
                            shifted_week_data.days[j] = first_week_data.days[j+offset];
                        }
                        for(int j = 7 - offset; j < 7; j++)
                        {
                            shifted_week_data.days[j] = new Day();
                            for(int k = 0; k < shifted_week_data.meals_per_day; k++)
                            {
                                List<String> meal = new ArrayList<>();
                                for(int h = 0; h < shifted_week_data.courses_per_meal.get(k); h++)
                                {
                                    meal.add(Util.NULL_RECIPE);
                                }
                                shifted_week_data.days[j].addMeal(meal);
                            }
                        }
                        //---   ---//
                    }
                    else
                    {
                        shifted_week_data.courses_per_meal = second_week_data.courses_per_meal;
                        shifted_week_data.meal_names = second_week_data.meal_names;
                        shifted_week_data.meals_per_day = second_week_data.meals_per_day;

                        //---   Save data from second week and ignore the data from first week  ---//
                        for(int j = 0; j < 7 - offset; j++)
                        {
                            shifted_week_data.days[j] = new Day();
                            for(int k = 0; k < shifted_week_data.meals_per_day; k++)
                            {
                                List<String> meal = new ArrayList<>();
                                for(int h = 0; h < shifted_week_data.courses_per_meal.get(k); h++)
                                {
                                    meal.add(Util.NULL_RECIPE);
                                }
                                shifted_week_data.days[j].addMeal(meal);
                            }
                        }
                        for(int j = 7 - offset; j < 7; j++)
                        {
                            shifted_week_data.days[j] = second_week_data.days[j - (7 - offset)];
                        }
                        //---   ---//
                    }
                }

                //---   Save the new shifted data, delete the first (now useless) week and make
                //      "current second week" the first week for the next loop  ---//
                String path = Util.dateToFileName(shifted_date);
                new_saved_weeks.add(path);
                saveData(shifted_week_data, path, context);

                String file_to_delete = Util.dateToFileName(first_date);
                File folder = new File(context.getFilesDir(), Util.WEEKS_DATA_FOLDER);
                folder.mkdirs();
                File old_file = new File(folder, file_to_delete);
                if(old_file.exists()) old_file.delete();

                first_week_data = second_week_data;
                //---   ---//
            }
        }

        saved_weeks = new_saved_weeks;
        saveWeeks(context);
    }
}
