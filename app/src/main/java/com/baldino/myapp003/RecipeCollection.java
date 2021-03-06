package com.baldino.myapp003;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.baldino.myapp003.data_classes.RecIngredient;
import com.baldino.myapp003.data_classes.Recipe;

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

public class RecipeCollection
{
    private String name = "";

    private List<Recipe> recipes;
    private RecipeListAdapter recipe_list_adapter;
    private List<String> recipe_names = null;
    private ArrayAdapter<String> names_adapter = null;

    public RecipeCollection(String name, int index)
    {
        setName(name);

        recipes = new ArrayList<>();
        recipe_names = new ArrayList<>();
        recipe_names.add(Util.NULL_RECIPE);

        recipe_list_adapter = new RecipeListAdapter(index);
    }

    public void resetIndex(int index)
    {
        recipe_list_adapter.collection = index;
    }

    public void loadRecipes(Context context)
    {
        //TODO: ma ci va davvero questo?
        if(Util.compareStrings(Util.nameToFileName(name), "") == 0) return;

        recipes = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        File folder = new File(context.getFilesDir(), Util.COLLECTIONS_FOLDER);
        folder.mkdirs();

        try
        {
            FileInputStream fis = new FileInputStream(new File(folder, Util.nameToFileName(name) + ".txt"));
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
            Recipe new_recipe = new Recipe();
            new_recipe.setName(Util.getStringFromLine(lines.get(i)));
            i++;

            int ingredients_amount = -1;
            if(i < lines.size()) ingredients_amount = Util.getIntFromLine(lines.get(i));
            for(int iter = 0; iter < ingredients_amount; iter++)
            {
                i++;
                if(i < lines.size())
                {
                    RecIngredient rec_ingredient = Util.getRecipeIngredient(lines.get(i));
                    new_recipe.addIngredient(rec_ingredient);
                }
            }
            addRecipe(new_recipe);
        }
    }
    public void saveRecipes(Context context)
    {
        StringBuilder output_string = new StringBuilder("");
        for(int i = 0; i < recipes.size(); i++)
        {
            List<RecIngredient> ingredients = recipes.get(i).getCopyOfIngredients();
            output_string.append("[");
            output_string.append(recipes.get(i).getName());
            output_string.append("]\n[");
            output_string.append(ingredients.size());
            output_string.append("]\n");
            for(int j = 0; j < ingredients.size(); j++)
            {
                output_string.append('[');
                output_string.append(ingredients.get(j).getName());
                output_string.append("][");
                output_string.append(ingredients.get(j).getAmount());
                output_string.append("]\n");
            }
        }

        File folder = new File(context.getFilesDir(), Util.COLLECTIONS_FOLDER);
        folder.mkdirs();

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(folder, Util.nameToFileName(name) + ".txt"));
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

    public int addRecipe(Recipe recipe)
    {
        boolean exists = false, found_place = false;
        int pos = recipes.size();
        for(int i = 0; i < recipes.size() && !exists && !found_place; i++)
        {
            if(Recipe.areEqual(recipe, recipes.get(i)))
                exists = true;
            else if(Recipe.alphabFirst(recipe, recipes.get(i)))
            {
                found_place = true;
                pos = i;
            }
        }
        if(!exists)
        {
            recipes.add(pos, recipe);
            recipe_names.add(pos+1, recipe.getName());
            recipe_list_adapter.notifyItemInserted(pos);
            return pos;
        }
        else return -1;
    }
    public int removeRecipe(int pos)
    {
        if(pos < 0 || pos >= recipes.size()) return -1;
        recipes.remove(pos);
        recipe_names.remove(pos+1);
        recipe_list_adapter.notifyItemRemoved(pos);

        int last_expanded = recipe_list_adapter.expanded_value;
        recipe_list_adapter.expanded_value = -1;
        if(last_expanded != -1) recipe_list_adapter.notifyItemChanged(last_expanded);
        return 0;
    }

    public Recipe getRecipe(int pos)
    {
        return recipes.get(pos);
    }

    public Recipe binaryFind(String name) { return binaryFind(name, 0, recipes.size()-1); }
    public Recipe binaryFind(String name, int left, int right)
    {
        if(left  > right) return null;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, recipes.get(mid).getName()) == 0) return recipes.get(mid);
        else if(Util.compareStrings(name, recipes.get(mid).getName()) < 0) return binaryFind(name, left, mid-1);
        else return binaryFind(name, mid+1, right);
    }

    public int binaryFindIndex(String name) { return binaryFindIndex(name, 0, recipes.size()-1); }
    public int binaryFindIndex(String name, int left, int right)
    {
        if(left  > right) return -1;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, recipes.get(mid).getName()) == 0) return mid;
        else if(Util.compareStrings(name, recipes.get(mid).getName()) < 0) return binaryFindIndex(name, left, mid-1);
        else return binaryFindIndex(name, mid+1, right);
    }

    public RecipeListAdapter getListAdapter() { return recipe_list_adapter; }

    public ArrayAdapter<String> getNamesAdapter(Context context)
    {
        names_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, recipe_names);
        names_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return names_adapter;
    }

    public int getSize()
    {
        return (recipes == null ? 0 : recipes.size());
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = Util.normalizeString(name); }
}
