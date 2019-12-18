package com.baldino.myapp003;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.baldino.myapp003.Util.STD_CHARSET;
import static com.baldino.myapp003.Util.getRecipeIngredient;
import static com.baldino.myapp003.Util.getRecipeIngredientsAmount;
import static com.baldino.myapp003.Util.getRecipeName;
import static com.baldino.myapp003.Util.nameToFileName;
import static com.baldino.myapp003.Util.normalizeString;

public class RecipeType
{
    private static final String TYPES_FOLDER = "recipes_data";
    private Context context;

    private String name = "";

    private List<Recipe> recipes;
    private NewRecipeListAdapter recipe_list_adapter;
    private ArrayAdapter<String> names_adapter = null;

    public RecipeType(String name, Context context)
    {
        setName(name);
        this.context = context;

        recipes = new ArrayList<>();

        recipe_list_adapter = new NewRecipeListAdapter(this);
    }

    public void createFakeData()
    {
        recipes = new ArrayList<>();
        for(int i = 0; i < 10; i++)
        {
            Recipe recipeOne = new Recipe();
            recipeOne.setName("Paninazzo " + Integer.toString(i));
            recipeOne.ingredients.add(new RecIngredient("Pasta", 0.125f));
            recipeOne.ingredients.add(new RecIngredient("Passata Di Pomodoro", 0.240f));
            recipeOne.ingredients.add(new RecIngredient("Pancetta", 0.200f));
            recipeOne.ingredients.add(new RecIngredient("Cipolla", 0.5f));
            addRecipe(recipeOne);
        }
    }

    public void loadRecipes()
    {
        if(nameToFileName(name).equals("")) return;

        recipes = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        File folder = new File(context.getFilesDir(), TYPES_FOLDER);
        folder.mkdirs();

        try
        {
            FileInputStream fis = new FileInputStream(new File(folder, nameToFileName(name) + ".txt"));
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
            new_recipe.setName(getRecipeName(lines.get(i)));
            i++;

            int ingredients_amount = -1;
            if(i < lines.size()) ingredients_amount = getRecipeIngredientsAmount(lines.get(i));
            for(int iter = 0; iter < ingredients_amount; iter++)
            {
                i++;
                if(i < lines.size())
                {
                    RecIngredient rec_ingredient = getRecipeIngredient(lines.get(i));
                    new_recipe.ingredients.add(rec_ingredient);
                }
            }
            addRecipe(new_recipe);
        }
    }
    public void saveRecipes()
    {
        StringBuilder output_string = new StringBuilder("");
        for(int i = 0; i < recipes.size(); i++)
        {
            output_string.append("[");
            output_string.append(recipes.get(i).getName());
            output_string.append("]\n[");
            output_string.append(recipes.get(i).ingredients.size());
            output_string.append("]\n");
            for(int j = 0; j < recipes.get(i).ingredients.size(); j++)
            {
                output_string.append('[');
                output_string.append(recipes.get(i).ingredients.get(j).getName());
                output_string.append("][");
                output_string.append(recipes.get(i).ingredients.get(j).getAmount());
                output_string.append("]\n");
            }
        }

        File folder = new File(context.getFilesDir(), TYPES_FOLDER);
        folder.mkdirs();

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(folder, nameToFileName(name) + ".txt"));
            fos.write(output_string.toString().getBytes(STD_CHARSET));
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

    public void addRecipe(Recipe recipe)
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
            recipe_list_adapter.notifyItemInserted(pos);
        }
    }
    public void removeRecipe(int pos)
    {
        if(pos < 0 || pos >= recipes.size()) return;
        recipes.remove(pos);
        recipe_list_adapter.notifyItemRemoved(pos);

        int last_expanded = recipe_list_adapter.expanded_value;
        recipe_list_adapter.expanded_value = -1;
        if(last_expanded != -1) recipe_list_adapter.notifyItemChanged(last_expanded);
    }

    public Recipe getRecipe(int pos)
    {
        return recipes.get(pos);
    }

    public int binaryFindIndex(String name) { return binaryFindIndex(name, 0, recipes.size()-1); }
    public int binaryFindIndex(String name, int left, int right)
    {
        if(left  > right) return -1;

        int mid = left + ((right - left)/2);
        if(name.equals(recipes.get(mid).getName())) return mid;
        else if(name.compareTo(recipes.get(mid).getName()) < 0) return binaryFindIndex(name, left, mid-1);
        else return binaryFindIndex(name, mid+1, right);
    }

    public NewRecipeListAdapter getListAdapter() { return recipe_list_adapter; }

    public ArrayAdapter<String> getNamesAdapter()
    {

        List<String> recipe_names = new ArrayList<>();
        recipe_names.add("-");
        for(Recipe rec : recipes)
        {
            recipe_names.add(rec.getName());
        }

        names_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, recipe_names);
        names_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return names_adapter;
    }

    public int getSize()
    {
        return (recipes == null ? 0 : recipes.size());
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = normalizeString(name); }
}
