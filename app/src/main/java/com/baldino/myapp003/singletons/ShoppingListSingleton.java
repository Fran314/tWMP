package com.baldino.myapp003.singletons;

import android.content.Context;

import com.baldino.myapp003.data_classes.Ingredient;
import com.baldino.myapp003.R;
import com.baldino.myapp003.data_classes.RecIngredient;
import com.baldino.myapp003.data_classes.Recipe;
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
import java.util.List;

public class ShoppingListSingleton
{
    private static ShoppingListSingleton singleton_instance = null;

    private Context context;

    private List<RecIngredient> shopping_list;
    public List<String> labels;
    public List<Boolean> values;
    public List<Integer> colors;

    public String additional_text;

    private ShoppingListSingleton()
    {

    }

    public synchronized static ShoppingListSingleton getInstance()
    {
        if(singleton_instance == null)
            singleton_instance = new ShoppingListSingleton();

        return singleton_instance;
    }

    public void setContext(Context context) { this.context = context; }

    public void updateShoppingList()
    {
        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
        RecipeManagerSingleton sRecipeManager = RecipeManagerSingleton.getInstance();
        Database D = Database.getInstance();

        shopping_list = new ArrayList<>();
        labels = new ArrayList<>();
        values = new ArrayList<>();
        colors = new ArrayList<>();

        additional_text = "";

        for(int i = 0; i < 7; i++)
        {
            if(sWeekManager.has_same_format)
            {
                for(int j = 0; j < sWeekManager.courses_per_meal.size(); j++)
                {
                    for(int k = 0; k < sWeekManager.courses_per_meal.get(j); k++)
                    {
                        Recipe rec = sRecipeManager.getType(sWeekManager.daily_meals.get(j).getType(k)).binaryFind(sWeekManager.days[i].getCourseOfmeal(k,j));
                        if(rec != null)
                        {
                            for(int h = 0; h < rec.ingredients.size(); h++)
                            {
                                addItem(rec.ingredients.get(h).getName(), rec.ingredients.get(h).getAmount());
                            }
                        }
                    }
                }
            }
        }

        for(RecIngredient rec_ingr : shopping_list)
        {
            boolean is_std = true;
            Ingredient ingr = D.binaryFindStdIngr(rec_ingr.getName());
            if(ingr == null)
            {
                ingr = D.binaryFindMnrIngr(rec_ingr.getName());
                is_std = false;
            }

            if(ingr != null)
            {
                if(is_std)
                {
                    int packages = (int) Math.ceil(rec_ingr.getAmount()/ingr.getAmount());
                    labels.add(rec_ingr.getName() + " x" + packages);
                    colors.add(context.getResources().getColor(R.color.colorBlack));
                    values.add(false);
                }
                else
                {
                    labels.add("" + rec_ingr.getName());
                    colors.add(context.getResources().getColor(R.color.colorBlack));
                    values.add(false);
                }
            }
            else
            {
                //TODO eventually change standard unit measure
                labels.add(rec_ingr.getName() + " x" + rec_ingr.getAmount() + "kg");
                colors.add(context.getResources().getColor(R.color.colorErrorRed));
                values.add(false);
            }
        }
    }

    public void saveValues()
    {
        StringBuilder output_string = new StringBuilder();
        output_string.append("[").append(values.size()).append("]\n");
        for(int i = 0; i < values.size(); i++)
        {
            output_string.append("[");
            output_string.append(values.get(i));
            output_string.append("]\n");
        }
        output_string.append(additional_text);

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), Util.SL_VALUES_PATH));
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
    public void loadValues()
    {
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), Util.SL_VALUES_PATH));
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

        if(lines.size() > 0)
        {
            int numb_values = Util.getIntFromLine(lines.get(0));

            for(int i = 0; i < numb_values && i < values.size() && i+1 < lines.size(); i++)
            {
                values.set(i, Util.getBooleanFromLine(lines.get(i+1)));
            }

            StringBuilder temp = new StringBuilder();
            for(int i = numb_values+1; i < lines.size(); i++)
            {
                temp.append(lines.get(i));
                if(i != lines.size() - 1) temp.append("\n");
            }

            additional_text = temp.toString();
        }
    }

    private void addItem(String name, float amount)
    {
        int index = binaryFindIndex(name);
        if(index == -1)
        {
            int pos = shopping_list.size();
            for(int i = 0; i < shopping_list.size(); i++)
            {
                if(Util.compareStrings(name, shopping_list.get(i).getName()) < 0)
                {
                    pos = i;
                    break;
                }
            }
            shopping_list.add(pos, new RecIngredient(name, amount));
        }
        else
        {
            shopping_list.get(index).setAmount(shopping_list.get(index).getAmount() + amount);
        }
    }

    private int binaryFindIndex(String name) { return binaryFindIndex(name, 0, shopping_list.size()-1); }
    private int binaryFindIndex(String name, int left, int right)
    {
        if(left  > right) return -1;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, shopping_list.get(mid).getName()) == 0) return mid;
        else if(Util.compareStrings(name, shopping_list.get(mid).getName()) < 0) return binaryFindIndex(name, left, mid-1);
        else return binaryFindIndex(name, mid+1, right);
    }
}
