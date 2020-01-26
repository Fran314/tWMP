package com.baldino.myapp003.singletons;

import android.content.Context;

import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

public class InitiatorClass
{
    /*
        The real and unique purpose of this class is to create the initial files if the user
        chooses the Basic Template when opening the app for the first time.

        There's no real need for this to be its own class, but I didn't really want to leave it
        in the Util class where it was before, and I hope it will be much more clean and organised
        this way.
     */

    public static void initBasicTemplate(Context context, int first_day_of_week)
    {
        createIngredients(context);
        createDailyMeals(context);
        createRecipeCategories(context);
        createWeek(context, first_day_of_week);
    }

    public static void createIngredients(Context context)
    {
        String initial_standard_ingredients = context.getResources().getString(R.string.initial_standard_ingredients);
        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), Util.STANDARD_INGR_PATH));
            fos.write(initial_standard_ingredients.getBytes(Util.STD_CHARSET));
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        String initial_minor_ingredients = context.getResources().getString(R.string.initial_minor_ingredients);
        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), Util.MINOR_INGR_PATH));
            fos.write(initial_minor_ingredients.getBytes(Util.STD_CHARSET));
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static void createDailyMeals(Context context)
    {
        String initial_standard_ingredients = context.getResources().getString(R.string.initial_daily_meals);
        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), Util.DAILY_MEALS_PATH));
            fos.write(initial_standard_ingredients.getBytes(Util.STD_CHARSET));
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void createRecipeCategories(Context context)
    {
        String[] initial_recipe_collections = context.getResources().getStringArray(R.array.initial_recipe_collections);
        String initial_lunch_courses = context.getResources().getString(R.string.initial_lunch_courses);
        String initial_dinner_courses = context.getResources().getString(R.string.initial_dinner_courses);
        String initial_side_dishes = context.getResources().getString(R.string.initial_side_dishes);
        String initial_extras = context.getResources().getString(R.string.initial_extras);
        String initial_recipe_collections_file = context.getResources().getString(R.string.initial_recipe_collections_file);

        File recipe_types_folder = new File(context.getFilesDir(), Util.COLLECTIONS_FOLDER);
        recipe_types_folder.mkdirs();

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(recipe_types_folder, Util.nameToFileName(initial_recipe_collections[0]) + ".txt"));
            fos.write(initial_lunch_courses.getBytes(Util.STD_CHARSET));
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(recipe_types_folder, Util.nameToFileName(initial_recipe_collections[1]) + ".txt"));
            fos.write(initial_dinner_courses.getBytes(Util.STD_CHARSET));
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(recipe_types_folder, Util.nameToFileName(initial_recipe_collections[2]) + ".txt"));
            fos.write(initial_side_dishes.getBytes(Util.STD_CHARSET));
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(recipe_types_folder, Util.nameToFileName(initial_recipe_collections[3]) + ".txt"));
            fos.write(initial_extras.getBytes(Util.STD_CHARSET));
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), Util.REC_COLLECTIONS_PATH));
            fos.write(initial_recipe_collections_file.getBytes(Util.STD_CHARSET));
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void createWeek(Context context, int first_day_of_week)
    {
        LocalDate curr_date = LocalDate.now();

        int d_offset = curr_date.getDayOfWeek().getValue() - first_day_of_week;
        if(d_offset < 0) d_offset += 7;
        curr_date = curr_date.minusDays(d_offset);

        File folder = new File(context.getFilesDir(), Util.WEEKS_DATA_FOLDER);
        folder.mkdirs();

        String week_file_path = Util.dateToFileName(curr_date);
        String week_file = context.getResources().getString(R.string.initial_week);
        try
        {
            FileOutputStream fos = new FileOutputStream(new File(folder, week_file_path));
            fos.write(week_file.getBytes(Util.STD_CHARSET));
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        StringBuilder weeks_list = new StringBuilder();
        weeks_list.append("[").append(week_file_path).append("]");
        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), Util.WEEKS_LIST_PATH));
            fos.write(weeks_list.toString().getBytes(Util.STD_CHARSET));
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
