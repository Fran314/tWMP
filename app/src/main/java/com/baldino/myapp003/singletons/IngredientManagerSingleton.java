package com.baldino.myapp003.singletons;

import android.content.Context;

import com.baldino.myapp003.Ingredient;
import com.baldino.myapp003.IngredientListAdapter;
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

public class IngredientManagerSingleton
{
    private static IngredientManagerSingleton singleton_instance = null;

    private Context context;

    public List<Ingredient> standard_ingredients;
    public IngredientListAdapter standard_ingr_list_adapter;

    public List<Ingredient> minor_ingredients;
    public IngredientListAdapter minor_ingr_list_adapter;

    private IngredientManagerSingleton()
    {
        standard_ingredients = new ArrayList<>();
        standard_ingr_list_adapter = new IngredientListAdapter(true);

        minor_ingredients = new ArrayList<>();
        minor_ingr_list_adapter = new IngredientListAdapter(false);
    }

    public synchronized static IngredientManagerSingleton getInstance()
    {
        if (singleton_instance == null)
            singleton_instance = new IngredientManagerSingleton();

        return singleton_instance;
    }

    public void setContext(Context context) { this.context = context; }

    public void addStdIngr(Ingredient ingredient)
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
        if(!exists)
        {
            standard_ingredients.add(pos, ingredient);
            standard_ingr_list_adapter.notifyItemInserted(pos);
        }
    }
    public void removeStdIngr(int pos)
    {
        if(pos < 0 || pos >= standard_ingredients.size()) return;
        standard_ingredients.remove(pos);
        standard_ingr_list_adapter.notifyItemRemoved(pos);

        int last_expanded = standard_ingr_list_adapter.expanded_val;
        standard_ingr_list_adapter.expanded_val = -1;
        if(last_expanded != -1) standard_ingr_list_adapter.notifyItemChanged(last_expanded);
    }

    public void addMnrIngr(Ingredient ingredient)
    {
        boolean exists = false, found_place = false;
        int pos = minor_ingredients.size();
        for(int i = 0; i < minor_ingredients.size() && !exists && !found_place; i++)
        {
            if(Ingredient.areEqual(ingredient, minor_ingredients.get(i)))
                exists = true;
            else if(Ingredient.alphabFirst(ingredient, minor_ingredients.get(i)))
            {
                found_place = true;
                pos = i;
            }
        }
        if(!exists)
        {
            int last_expanded = minor_ingr_list_adapter.expanded_val;
            minor_ingr_list_adapter.expanded_val = -1;
            if(last_expanded != -1) minor_ingr_list_adapter.notifyItemChanged(last_expanded);

            minor_ingredients.add(pos, ingredient);
            minor_ingr_list_adapter.notifyItemInserted(pos);
        }
    }
    public void removeMnrIngr(int pos)
    {
        if(pos < 0 || pos >= minor_ingredients.size()) return;

        int last_expanded = minor_ingr_list_adapter.expanded_val;
        minor_ingr_list_adapter.expanded_val = -1;
        if(last_expanded != -1) minor_ingr_list_adapter.notifyItemChanged(last_expanded);

        minor_ingredients.remove(pos);
        minor_ingr_list_adapter.notifyItemRemoved(pos);
    }

    public Ingredient binaryFindStdIngr(String name) { return binaryFindStdIngr(name, 0, standard_ingredients.size()-1); }
    public Ingredient binaryFindStdIngr(String name, int left, int right)
    {
        if(left  > right) return null;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, standard_ingredients.get(mid).getName()) == 0) return standard_ingredients.get(mid);
        else if(Util.compareStrings(name, standard_ingredients.get(mid).getName()) < 0) return binaryFindStdIngr(name, left, mid-1);
        else return binaryFindStdIngr(name, mid+1, right);
    }
    public int binaryFindStdIndex(String name) { return binaryFindStdIndex(name, 0, standard_ingredients.size()-1); }
    public int binaryFindStdIndex(String name, int left, int right)
    {
        if(left  > right) return -1;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, standard_ingredients.get(mid).getName()) == 0) return mid;
        else if(Util.compareStrings(name, standard_ingredients.get(mid).getName()) < 0) return binaryFindStdIndex(name, left, mid-1);
        else return binaryFindStdIndex(name, mid+1, right);
    }

    public Ingredient binaryFindMnrIngr(String name) { return binaryFindMnrIngr(name, 0, minor_ingredients.size()-1); }
    public Ingredient binaryFindMnrIngr(String name, int left, int right)
    {
        if(left  > right) return null;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, minor_ingredients.get(mid).getName()) == 0) return minor_ingredients.get(mid);
        else if(Util.compareStrings(name, minor_ingredients.get(mid).getName()) < 0) return binaryFindMnrIngr(name, left, mid-1);
        else return binaryFindMnrIngr(name, mid+1, right);
    }
    public int binaryFindMnrIndex(String name) { return binaryFindMnrIndex(name, 0, minor_ingredients.size()-1); }
    public int binaryFindMnrIndex(String name, int left, int right)
    {
        if(left  > right) return -1;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, minor_ingredients.get(mid).getName()) == 0) return mid;
        else if(Util.compareStrings(name, minor_ingredients.get(mid).getName()) < 0) return binaryFindMnrIndex(name, left, mid-1);
        else return binaryFindMnrIndex(name, mid+1, right);
    }

    public void saveStdIngr()
    {
        StringBuilder output_string = new StringBuilder();
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
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), Util.STANDARD_INGR_PATH));
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
    public void loadStdIngr()
    {
        standard_ingredients = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), Util.STANDARD_INGR_PATH));
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
            addStdIngr(Util.getIngredient(lines.get(i)));
        }
    }

    public void saveMnrIngr()
    {
        StringBuilder output_string = new StringBuilder();
        for(int i = 0; i < minor_ingredients.size(); i++)
        {
            output_string.append('[');
            output_string.append(minor_ingredients.get(i).getName());
            output_string.append("][");
            output_string.append(minor_ingredients.get(i).getAmount());
            output_string.append("][");
            output_string.append(minor_ingredients.get(i).getUnit());
            output_string.append("][");
            output_string.append(minor_ingredients.get(i).getPrice());
            output_string.append("]\n");
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), Util.MINOR_INGR_PATH));
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
    public void loadMnrIngr()
    {
        minor_ingredients = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), Util.MINOR_INGR_PATH));
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
            addMnrIngr(Util.getIngredient(lines.get(i)));
        }
    }
}
