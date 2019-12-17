package com.baldino.myapp003.singletons;

import android.content.Context;

import com.baldino.myapp003.Ingredient;
import com.baldino.myapp003.IngredientListAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.baldino.myapp003.Util.*;

public class IngredientManagerSingleton {

    private static IngredientManagerSingleton singleton_instance = null;

    private Context context;

    private static final String FILE_PATH = "ingredients.txt";

    public List<Ingredient> ingredients;
    public IngredientListAdapter listAdapter = null;

    public int expandedVal = -1;

    private IngredientManagerSingleton()
    {
        ingredients = new ArrayList<>();
        listAdapter = new IngredientListAdapter();
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
        int pos = ingredients.size();
        for(int i = 0; i < ingredients.size() && !exists && !found_place; i++)
        {
            if(Ingredient.areEqual(ingredient, ingredients.get(i)))
                exists = true;
            else if(Ingredient.alphabFirst(ingredient, ingredients.get(i)))
            {
                found_place = true;
                pos = i;
            }
        }
        if(exists) return -1;
        else
        {
            ingredients.add(pos, ingredient);
            listAdapter.notifyItemInserted(pos);

            return 1;
        }
    }
    public int removeIngredient(int pos)
    {
        if(pos < 0 || pos >= ingredients.size()) return -1;
        ingredients.remove(pos);
        listAdapter.notifyItemRemoved(pos);

        int last_expanded = expandedVal;
        expandedVal = -1;
        if(last_expanded != -1) listAdapter.notifyItemChanged(last_expanded);

        return 0;
    }

    public Ingredient binaryFind(String name) { return binaryFind(name, 0, ingredients.size()-1); }
    public Ingredient binaryFind(String name, int left, int right)
    {
        if(left  > right) return null;

        int mid = left + ((right - left)/2);
        if(name.equals(ingredients.get(mid).getName())) return ingredients.get(mid);
        else if(name.compareTo(ingredients.get(mid).getName()) < 0) return binaryFind(name, left, mid-1);
        else return binaryFind(name, mid+1, right);
    }

    public void createFakeData()
    {
        ingredients.add(new Ingredient("Pasta", 0.5f, "kg", 0.55f));
        ingredients.add(new Ingredient("Tonno", 12f, "pz", 8.49f));
        ingredients.add(new Ingredient("Uova", 6f, "pz", 1.75f));
    }

    public int saveData()
    {
        StringBuilder output_string = new StringBuilder("");
        for(int i = 0; i < ingredients.size(); i++)
        {
            output_string.append('[');
            output_string.append(ingredients.get(i).getName());
            output_string.append("][");
            output_string.append(ingredients.get(i).getAmount());
            output_string.append("][");
            output_string.append(ingredients.get(i).getUnit());
            output_string.append("][");
            output_string.append(ingredients.get(i).getPrice());
            output_string.append("]\n");
        }

        try
        {
            FileOutputStream fos = context.openFileOutput(FILE_PATH, Context.MODE_PRIVATE);
            fos.write(output_string.toString().getBytes(STD_CHARSET));
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

    public int loadData()
    {
        ingredients = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = context.openFileInput(FILE_PATH);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, STD_CHARSET));
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
            ingredients.add(getIngredient(lines.get(i)));
        }

        return 0;
    }
}