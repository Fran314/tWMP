package com.baldino.myapp003;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.baldino.myapp003.data_classes.Ingredient;
import com.baldino.myapp003.data_classes.RecIngredient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Util
{
    public static final String STD_CHARSET = "UTF-16";
    public static final String NULL_RECIPE = "-";

    public static final String SL_VALUES_PATH = "shopping_list_values.txt";

    public static final String STANDARD_INGR_PATH = "standard_ingredients.txt";
    public static final String MINOR_INGR_PATH = "minor_ingredients.txt";

    public static final String REC_COLLECTIONS_PATH = "recipe_types.txt";
    public static final String COLLECTIONS_FOLDER = "recipes_data";

    public static final String LAST_DATE_PATH = "last_date.txt";
    public static final String WEEKS_LIST_PATH = "weeks_list.txt";
    public static final String WEEKS_DATA_FOLDER = "weeks_data";
    public static final String DAILY_MEALS_PATH = "daily_meals.txt";

    public static final String SETTINGS_PATH = "settings.txt";

    public static final String APP_VERSION = "1.0.0";

    public static String getStringFromLine(String line)
    {
        int pos[] = isValidAndGetPos(line, 1);
        if(pos[0] == -1) return "ERROR_NAME";
        else return line.substring(pos[0]+1, pos[1]);
    }
    public static int getIntFromLine(String line)
    {
        int pos[] = isValidAndGetPos(line, 1);
        if(pos[0] == -1) return -1;
        else return stringToInt(line.substring(pos[0]+1, pos[1]));
    }
    public static boolean getBooleanFromLine(String line)
    {
        int pos[] = isValidAndGetPos(line, 1);
        if(pos[0] == -1) return false;
        else return ("true".equals(line.substring(pos[0]+1, pos[1])));
    }
    public static List<String> getStringsFromLine(String line, int amount)
    {
        int pos[] = isValidAndGetPos(line, amount);
        List<String> to_return = new ArrayList<>();
        if(pos[0] != -1)
        {

            for(int i = 0; i < amount; i++)
            {
                to_return.add(line.substring(pos[i]+1, pos[amount+i]));
            }
        }

        return to_return;
    }
    public static List<Integer> getIntsFromLine(String line, int amount)
    {
        int pos[] = isValidAndGetPos(line, amount);
        List<Integer> to_return = new ArrayList<>();
        if(pos[0] != -1)
        {

            for(int i = 0; i < amount; i++)
            {
                to_return.add(stringToInt(line.substring(pos[i]+1, pos[amount+i])));
            }
        }

        return to_return;
    }
    public static int[] getTypeAndStd(String line)
    {
        int pos[] = isValidAndGetPos(line, 2);
        if(pos[0] == -1) return new int[]{0, 0};
        else return new int[]{stringToInt(line.substring(pos[0]+1, pos[2])), stringToInt(line.substring(pos[1]+1, pos[2+1]))};
    }
    public static RecIngredient getRecipeIngredient(String line)
    {
        String name = "ERROR_NAME";
        float amount = 0f;
        int pos[] = isValidAndGetPos(line, 2);
        if(pos[0] != -1)
        {
            name = line.substring(pos[0]+1, pos[2+0]);
            try
            {
                amount = stringToFloat(line.substring(pos[1]+1, pos[2+1]));
            }
            catch(NumberFormatException nfe)
            {

            }
        }

        return new RecIngredient(name, amount);
    }
    public static Ingredient getIngredient(String line)
    {
        String name = "ERROR_NAME";
        float amount = 0f;
        float price = 0f;

        int pos[] = isValidAndGetPos(line, 3);
        if(pos[0] != -1)
        {
            name = line.substring(pos[0]+1, pos[3+0]);
            try
            {
                amount = stringToFloat(line.substring(pos[1]+1, pos[3+1]));
                price = stringToFloat(line.substring(pos[2]+1, pos[3+2]));
            }
            catch(NumberFormatException nfe)
            {
            }
        }

        return new Ingredient(name, amount, price);
    }

    public static int[] isValidAndGetPos(String s, int n_par)
    {
        if(n_par < 1) return new int[] {-1};

        int to_return[] = new int[2*n_par];
        int counter_a = 0;
        int counter_b = 0;
        int counter_bal = 0;
        boolean error = false;

        for(int i = 0; i < s.length() && !error; i++)
        {
            if(s.charAt(i) == '[')
            {
                counter_a++;
                counter_bal++;
                if(counter_a <= n_par && counter_bal <= 1) to_return[counter_a-1] = i;
                else error = true;
            }
            else if(s.charAt(i) == ']')
            {
                counter_b++;
                counter_bal--;
                if(counter_b <= n_par && counter_bal >= 0) to_return[n_par + counter_b-1] = i;
                else error = true;
            }
        }

        if(counter_a != n_par || counter_b != n_par || counter_bal != 0) error = true;
        if(error) to_return[0] = -1;

        return to_return;
    }

    public static String normalizeString(String arg)
    {
        String lines[] = arg.split(System.getProperty("line.separator"));

        String to_return = lines[0];
        to_return.replace("[", "").replace("]", "");
        return to_return;
    }
    public static String nameToFileName(String name)
    {
        return name.toLowerCase().replaceAll("[^A-Za-z0-9 ]", "").replace(" ", "_");
    }

    public static int stringToInt(String arg)
    {
        int to_return = -1;
        try
        {
            to_return = Integer.parseInt(arg);
        }
        catch(NumberFormatException nfe)
        {
        }

        return to_return;
    }
    public static Float stringToFloat(String arg)
    {
        Float to_return = 0f;
        try
        {
            to_return = Float.parseFloat(arg);
        }
        catch(NumberFormatException nfe)
        {
        }

        return to_return;
    }

    public static int compareStrings(String arg0, String arg1)
    {
        arg0 = arg0.toUpperCase();
        arg1 = arg1.toUpperCase();

        if(arg0.equals(arg1)) return 0;
        else if(arg0.compareTo(arg1) < 0 ) return -1;
        else return 1;
    }

    public static String dateToString(LocalDate date, boolean with_day_of_week, Context context)
    {
        String to_return = "";

        String[] days = context.getResources().getStringArray(R.array.days_of_week);
        String[] months = context.getResources().getStringArray(R.array.months);

        if(with_day_of_week) to_return += days[date.getDayOfWeek().getValue() - 1] + ", ";
        to_return += date.getDayOfMonth() + " ";
        to_return += months[date.getMonthValue()-1] + " ";
        to_return += date.getYear();

        return to_return;
    }

    public static LocalDate fileNameToDate(String file_name)
    {
        return LocalDate.of(Util.stringToInt(file_name.substring(0, 4)),
                Util.stringToInt(file_name.substring(5, 7)),
                Util.stringToInt(file_name.substring(8, 10)));
    }
    public static String dateToFileName(LocalDate date)
    {
        String to_return = String.format("%04d", date.getYear()) + "-";
        to_return += String.format("%02d", date.getMonthValue()) + "-";
        to_return += String.format("%02d", date.getDayOfMonth());
        to_return += ".txt";

        return to_return;
    }

    public static List<String> readFile(File file)
    {
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(file);
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

            fis.close();
        } catch (IOException e)
        {
            e.printStackTrace();

            Log.e("ERROR", "Had some error while reading the file " + file.getPath() + "/" + file.getName());
        }

        return lines;
    }

    //  Function copied from StackOverflow, can't remember the specific question though //
    public static int intToDp(int arg, Context context)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, arg, context.getResources().getDisplayMetrics());
    }
    public static int intToSp(int arg, Context context)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, arg, context.getResources().getDisplayMetrics());
    }

    /*
        Thanks to Tom Esterez & Kathir from StackOverflow for the next two functions
        [https://stackoverflow.com/questions/4946295/android-expand-collapse-animation]
     */
    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int)(targetHeight * 1 / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int)(initialHeight * 1 / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    /*
        Thanks to Macarse & cricket_007 from StackOverflow for the next function
        [https://stackoverflow.com/questions/4427608/android-getting-resource-id-from-string]
     */
    public static int getResId(String resName, Class<?> c)
    {
        try
        {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public static int saveFile(StringBuilder output_string, File file)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
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
    public static List<String> loadFile(File file)
    {
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(file);
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
            if(lines.size() > 0) lines.set(0, "ERR");
            else lines.add("ERR");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            if(lines.size() > 0) lines.set(0, "ERR");
            else lines.add("ERR");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            if(lines.size() > 0) lines.set(0, "ERR");
            else lines.add("ERR");
        }

        return lines;
    }
}
