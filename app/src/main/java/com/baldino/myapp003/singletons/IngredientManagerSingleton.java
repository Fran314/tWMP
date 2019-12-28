package com.baldino.myapp003.singletons;

import android.content.Context;

import com.baldino.myapp003.Ingredient;
import com.baldino.myapp003.StdIngrListAdapter;
import com.baldino.myapp003.Util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class IngredientManagerSingleton
{
    private static IngredientManagerSingleton singleton_instance = null;

    private Context context;

    public List<Ingredient> standard_ingredients;
    public StdIngrListAdapter standard_ingr_list_adapter = null;

    //TODO actually implement these
    public List<Ingredient> minor_ingredients;
    public StdIngrListAdapter minor_ingr_list_adapter = null;

    public int expandedVal = -1;

    private IngredientManagerSingleton()
    {
        standard_ingredients = new ArrayList<>();
        standard_ingr_list_adapter = new StdIngrListAdapter();

        minor_ingredients = new ArrayList<>();
        minor_ingr_list_adapter = new StdIngrListAdapter();
    }

    public static IngredientManagerSingleton getInstance()
    {
        if (singleton_instance == null)
            singleton_instance = new IngredientManagerSingleton();

        return singleton_instance;
    }

    public void setContext(Context context) { this.context = context; }

    public int addIngredient(Ingredient ingredient)
    {
        boolean exists = false, found_place = false;
        int pos = standard_ingredients.size();
        for(int i = 0; i < standard_ingredients.size() && !exists && !found_place; i++)
        {
            if(Ingredient.areEqual(ingredient, standard_ingredients.get(i)))
                exists = true;
            else if(Ingredient.alphabFirst(ingredient, standard_ingredients.get(i)))
            {
                found_place = true;
                pos = i;
            }
        }
        if(exists) return -1;
        else
        {
            standard_ingredients.add(pos, ingredient);
            standard_ingr_list_adapter.notifyItemInserted(pos);

            return 1;
        }
    }
    public int removeIngredient(int pos)
    {
        if(pos < 0 || pos >= standard_ingredients.size()) return -1;
        standard_ingredients.remove(pos);
        standard_ingr_list_adapter.notifyItemRemoved(pos);

        int last_expanded = expandedVal;
        expandedVal = -1;
        if(last_expanded != -1) standard_ingr_list_adapter.notifyItemChanged(last_expanded);

        return 0;
    }

    public Ingredient binaryFindIngredient(String name) { return binaryFindIngredient(name, 0, standard_ingredients.size()-1); }
    public Ingredient binaryFindIngredient(String name, int left, int right)
    {
        if(left  > right) return null;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, standard_ingredients.get(mid).getName()) == 0) return standard_ingredients.get(mid);
        else if(Util.compareStrings(name, standard_ingredients.get(mid).getName()) < 0) return binaryFindIngredient(name, left, mid-1);
        else return binaryFindIngredient(name, mid+1, right);
    }

    public void saveIngredients()
    {
        StringBuilder output_string = new StringBuilder("");
        for(int i = 0; i < standard_ingredients.size(); i++)
        {
            output_string.append('[');
            output_string.append(standard_ingredients.get(i).getName());
            output_string.append("][");
            output_string.append(standard_ingredients.get(i).getAmount());
            output_string.append("][");
            output_string.append(standard_ingredients.get(i).getUnit());
            output_string.append("][");
            output_string.append(standard_ingredients.get(i).getPrice());
            output_string.append("]\n");
        }

        try
        {
            FileOutputStream fos = context.openFileOutput(Util.INGREDIENTS_PATH, Context.MODE_PRIVATE);
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
    public void loadIngredients()
    {
        standard_ingredients = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = context.openFileInput(Util.INGREDIENTS_PATH);
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
            addIngredient(Util.getIngredient(lines.get(i)));
        }
    }
}
