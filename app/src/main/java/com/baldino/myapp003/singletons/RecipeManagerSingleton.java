package com.baldino.myapp003.singletons;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.baldino.myapp003.RecIngredient;
import com.baldino.myapp003.Recipe;
import com.baldino.myapp003.RecipeListAdapter;
import com.baldino.myapp003.RecipeType;

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

import static com.baldino.myapp003.Util.*;

public class RecipeManagerSingleton {

    private static RecipeManagerSingleton singleton_instance = null;

    private Context context;

    private static final String REC_TYPES_PATH = "recipe_types.txt";

    private static final String FIRST_C_PATH = "first_courses.txt";
    private static final String SECOND_C_PATH = "second_courses.txt";
    private static final String SIDE_D_PATH = "side_dishes.txt";

    public List<RecipeType> recipe_types;
    public List<String> type_names;
    public ArrayAdapter<String> type_names_adapter = null;

    private List<Recipe> first_courses;
    public RecipeListAdapter fc_adapter;
    public List<String> fc_names;
    public ArrayAdapter<String> fc_names_adapter = null;

    private List<Recipe> second_courses;
    public RecipeListAdapter sc_adapter;
    public List<String> sc_names;
    public ArrayAdapter<String> sc_names_adapter = null;

    private List<Recipe> side_dishes;
    public RecipeListAdapter sd_adapter;
    public List<String> sd_names;
    public ArrayAdapter<String> sd_names_adapter = null;

    public int expanded_value = -1, expanded_type = 0;

    private RecipeManagerSingleton()
    {
        first_courses = new ArrayList<>();
        fc_names = new ArrayList<>();
        fc_adapter = new RecipeListAdapter(0);

        second_courses = new ArrayList<>();
        sc_names = new ArrayList<>();
        sc_adapter = new RecipeListAdapter(1);

        side_dishes = new ArrayList<>();
        sd_names = new ArrayList<>();
        sd_adapter = new RecipeListAdapter(2);

        recipe_types = new ArrayList<>();
        type_names = new ArrayList<>();
    }

    public static RecipeManagerSingleton getInstance()
    {
        if (singleton_instance == null)
            singleton_instance = new RecipeManagerSingleton();

        return singleton_instance;
    }

    public void setContext(Context context) { this.context = context; }

    public void loadTypeNames()
    {
        recipe_types = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), REC_TYPES_PATH));
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
            fis.close();
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
            RecipeType new_recipe_type;
            String name = getRecTypeName(lines.get(i));

            new_recipe_type = new RecipeType(name, context);
            //new_recipe_type.createFakeData();
            new_recipe_type.loadRecipes();
            recipe_types.add(new_recipe_type);
        }

        type_names = new ArrayList<>();
        //type_names.add("-");
        for(RecipeType rec_type : recipe_types)
        {
            type_names.add(rec_type.getName());
        }

        type_names_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, type_names);
        type_names_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
    public void saveTypeNames()
    {
        StringBuilder output_string = new StringBuilder();
        for(int i = 0; i < recipe_types.size(); i++)
        {
            output_string.append("[");
            output_string.append(recipe_types.get(i).getName());
            output_string.append("]\n");
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), REC_TYPES_PATH));
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
    public void createFakeTypeNames()
    {
        recipe_types = new ArrayList<>();
        recipe_types.add(new RecipeType("Primi", context));
        recipe_types.add(new RecipeType("Secondi", context));
        recipe_types.add(new RecipeType("Contorni", context));
    }

    public void createFakeData()
    {
        Recipe recipeOne = new Recipe();
        recipeOne.setName("Paninazzo");
        recipeOne.ingredients.add(new RecIngredient("Pasta", 0.125f));
        recipeOne.ingredients.add(new RecIngredient("Passata Di Pomodoro", 0.240f));
        recipeOne.ingredients.add(new RecIngredient("Pancetta", 0.200f));
        recipeOne.ingredients.add(new RecIngredient("Cipolla", 0.5f));
        addSecondCourse(recipeOne);

        Recipe recipeTwo = new Recipe();
        recipeTwo.setName("Fagiolini");
        recipeTwo.ingredients.add(new RecIngredient("Pasta", 0.125f));
        recipeTwo.ingredients.add(new RecIngredient("Passata Di Pomodoro", 0.240f));
        recipeTwo.ingredients.add(new RecIngredient("Soffritto", 0.150f));
        addSideDish(recipeTwo);
    }

    public int addRecipe(Recipe recipe, int meal_type)
    {
        if(meal_type == 0) return addFirstCourse(recipe);
        else if(meal_type == 1) return  addSecondCourse(recipe);
        else return addSideDish(recipe);
    }
    public int addFirstCourse(Recipe recipe)
    {
        boolean exists = false, found_place = false;
        int pos = first_courses.size();
        for(int i = 0; i < first_courses.size() && !exists && !found_place; i++)
        {
            if(Recipe.areEqual(recipe, first_courses.get(i)))     //Only checks if fc_names are the same
                exists = true;
            else if(Recipe.alphabFirst(recipe, first_courses.get(i)))
            {
                found_place = true;
                pos = i;
            }
        }
        if(exists) return -1;
        else
        {
            first_courses.add(pos, recipe);
            fc_adapter.notifyItemInserted(pos);

            return 1;
        }
    }
    public int addSecondCourse(Recipe recipe)
    {
        boolean exists = false, found_place = false;
        int pos = second_courses.size();
        for(int i = 0; i < second_courses.size() && !exists && !found_place; i++)
        {
            if(Recipe.areEqual(recipe, second_courses.get(i)))     //Only checks if fc_names are the same
                exists = true;
            else if(Recipe.alphabFirst(recipe, second_courses.get(i)))
            {
                found_place = true;
                pos = i;
            }
        }
        if(exists) return -1;
        else
        {
            second_courses.add(pos, recipe);
            sc_adapter.notifyItemInserted(pos);

            return 1;
        }
    }
    public int addSideDish(Recipe recipe)
    {
        boolean exists = false, found_place = false;
        int pos = side_dishes.size();
        for(int i = 0; i < side_dishes.size() && !exists && !found_place; i++)
        {
            if(Recipe.areEqual(recipe, side_dishes.get(i)))     //Only checks if fc_names are the same
                exists = true;
            else if(Recipe.alphabFirst(recipe, side_dishes.get(i)))
            {
                found_place = true;
                pos = i;
            }
        }
        if(exists) return -1;
        else
        {
            side_dishes.add(pos, recipe);
            sd_adapter.notifyItemInserted(pos);

            return 1;
        }
    }

    public int removeRecipe(int pos, int meal_type)
    {
        if(meal_type == 0) return removeFirstCourse(pos);
        else if(meal_type == 1) return removeSecondCourse(pos);
        else return removeSideDish(pos);
    }

    public int removeFirstCourse(int pos)
    {
        if(pos < 0 || pos >= first_courses.size()) return -1;
        first_courses.remove(pos);
        fc_adapter.notifyItemRemoved(pos);

        int last_expanded = expanded_value;
        expanded_value = -1;
        if(last_expanded != -1) fc_adapter.notifyItemChanged(last_expanded);

        return 0;
    }

    public int removeSecondCourse(int pos)
    {
        if(pos < 0 || pos >= second_courses.size()) return -1;
        second_courses.remove(pos);
        sc_adapter.notifyItemRemoved(pos);

        int last_expanded = expanded_value;
        expanded_value = -1;
        if(last_expanded != -1) sc_adapter.notifyItemChanged(last_expanded);

        return 0;
    }

    public int removeSideDish(int pos)
    {
        if(pos < 0 || pos >= side_dishes.size()) return -1;
        side_dishes.remove(pos);
        sd_adapter.notifyItemRemoved(pos);

        int last_expanded = expanded_value;
        expanded_value = -1;
        if(last_expanded != -1) sd_adapter.notifyItemChanged(last_expanded);

        return 0;
    }

    public Recipe getRecipe(int i, int meal_type)
    {
        if(meal_type == 0) return first_courses.get(i);
        else if(meal_type == 1) return second_courses.get(i);
        else return side_dishes.get(i);
    }
    public int getSize(int meal_type)
    {
        if(meal_type == 0) return (first_courses == null ? 0 : first_courses.size());
        else if(meal_type == 1) return (second_courses == null ? 0 : second_courses.size());
        else return (side_dishes == null ? 0 : side_dishes.size());
    }

    public void update()
    {
        fc_names = new ArrayList<>();
        fc_names.add("-");
        for(Recipe rec : first_courses)
        {
            fc_names.add(rec.getName());
        }

        fc_names_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, fc_names);
        fc_names_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sc_names = new ArrayList<>();
        sc_names.add("-");
        for(Recipe rec : second_courses)
        {
            sc_names.add(rec.getName());
        }

        sc_names_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, sc_names);
        sc_names_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sd_names = new ArrayList<>();
        sd_names.add("-");
        for(Recipe rec : side_dishes)
        {
            sd_names.add(rec.getName());
        }

        sd_names_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, sd_names);
        sd_names_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public Recipe binaryFind(String name, int meal_type)
    {
        if(meal_type == 0) return binaryFindFC(name, 0, first_courses.size()-1);
        else if(meal_type == 1) return binaryFindSC(name, 0, second_courses.size()-1);
        else return binaryFindSD(name, 0, side_dishes.size()-1);
    }
    public int binaryFindIndex(Recipe recipe, int meal_type) { return binaryFindIndex(recipe.getName(), meal_type); }
    public int binaryFindIndex(String name, int meal_type)
    {
        if(meal_type == 0) return binaryFindIndexFC(name, 0, first_courses.size()-1);
        else if(meal_type == 1) return binaryFindIndexSC(name, 0, second_courses.size()-1);
        else return binaryFindIndexSD(name, 0, side_dishes.size()-1);
    }

    public Recipe binaryFindFC(String name, int left, int right)
    {
        if(left  > right) return null;

        int mid = left + ((right - left)/2);
        if(name.equals(first_courses.get(mid).getName())) return first_courses.get(mid);
        else if(name.compareTo(first_courses.get(mid).getName()) < 0) return binaryFindFC(name, left, mid-1);
        else return binaryFindFC(name, mid+1, right);
    }
    public int binaryFindIndexFC(String name, int left, int right)
    {
        if(left  > right) return -1;

        int mid = left + ((right - left)/2);
        if(name.equals(first_courses.get(mid).getName())) return mid;
        else if(name.compareTo(first_courses.get(mid).getName()) < 0) return binaryFindIndexFC(name, left, mid-1);
        else return binaryFindIndexFC(name, mid+1, right);
    }

    public Recipe binaryFindSC(String name, int left, int right)
    {
        if(left  > right) return null;

        int mid = left + ((right - left)/2);
        if(name.equals(second_courses.get(mid).getName())) return second_courses.get(mid);
        else if(name.compareTo(second_courses.get(mid).getName()) < 0) return binaryFindSC(name, left, mid-1);
        else return binaryFindSC(name, mid+1, right);
    }
    public int binaryFindIndexSC(String name, int left, int right)
    {
        if(left  > right) return -1;

        int mid = left + ((right - left)/2);
        if(name.equals(second_courses.get(mid).getName())) return mid;
        else if(name.compareTo(second_courses.get(mid).getName()) < 0) return binaryFindIndexSC(name, left, mid-1);
        else return binaryFindIndexSC(name, mid+1, right);
    }

    public Recipe binaryFindSD(String name, int left, int right)
    {
        if(left  > right) return null;

        int mid = left + ((right - left)/2);
        if(name.equals(side_dishes.get(mid).getName())) return side_dishes.get(mid);
        else if(name.compareTo(side_dishes.get(mid).getName()) < 0) return binaryFindSD(name, left, mid-1);
        else return binaryFindSD(name, mid+1, right);
    }
    public int binaryFindIndexSD(String name, int left, int right)
    {
        if(left  > right) return -1;

        int mid = left + ((right - left)/2);
        if(name.equals(side_dishes.get(mid).getName())) return mid;
        else if(name.compareTo(side_dishes.get(mid).getName()) < 0) return binaryFindIndexSD(name, left, mid-1);
        else return binaryFindIndexSD(name, mid+1, right);
    }

    public int saveData(int meal_type)
    {
        if(meal_type == 0) return saveFirstCourses();
        else if(meal_type == 1) return saveSecondCourses();
        else return saveSideDishes();
    }

    public int saveFirstCourses()
    {
        StringBuilder output_string = new StringBuilder("");
        for(int i = 0; i < first_courses.size(); i++)
        {
            output_string.append("[");
            output_string.append(first_courses.get(i).getName());
            output_string.append("]\n[");
            output_string.append(first_courses.get(i).ingredients.size());
            output_string.append("]\n");
            for(int j = 0; j < first_courses.get(i).ingredients.size(); j++)
            {
                output_string.append('[');
                output_string.append(first_courses.get(i).ingredients.get(j).getName());
                output_string.append("][");
                output_string.append(first_courses.get(i).ingredients.get(j).getAmount());
                output_string.append("]\n");
            }
        }

        //File folder = new File(context.getFilesDir(), "recipes_data");
        //folder.mkdirs();

        try
        {
            //FileOutputStream fos = new FileOutputStream(new File(folder, "secondi.txt"));
            FileOutputStream fos = context.openFileOutput(FIRST_C_PATH, Context.MODE_PRIVATE);
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
    public int loadFirstCourses()
    {
        first_courses = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = context.openFileInput(FIRST_C_PATH);
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
            addFirstCourse(new_recipe);
        }

        update();

        return 0;
    }

    public int saveSecondCourses()
    {
        StringBuilder output_string = new StringBuilder("");
        for(int i = 0; i < second_courses.size(); i++)
        {
            output_string.append("[");
            output_string.append(second_courses.get(i).getName());
            output_string.append("]\n[");
            output_string.append(second_courses.get(i).ingredients.size());
            output_string.append("]\n");
            for(int j = 0; j < second_courses.get(i).ingredients.size(); j++)
            {
                output_string.append('[');
                output_string.append(second_courses.get(i).ingredients.get(j).getName());
                output_string.append("][");
                output_string.append(second_courses.get(i).ingredients.get(j).getAmount());
                output_string.append("]\n");
            }
        }

        try
        {
            FileOutputStream fos = context.openFileOutput(SECOND_C_PATH, Context.MODE_PRIVATE);
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
    public int loadSecondCourses()
    {
        second_courses = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = context.openFileInput(SECOND_C_PATH);
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
            addSecondCourse(new_recipe);
        }

        update();

        return 0;
    }

    public int saveSideDishes()
    {
        StringBuilder output_string = new StringBuilder("");
        for(int i = 0; i < side_dishes.size(); i++)
        {
            output_string.append("[");
            output_string.append(side_dishes.get(i).getName());
            output_string.append("]\n[");
            output_string.append(side_dishes.get(i).ingredients.size());
            output_string.append("]\n");
            for(int j = 0; j < side_dishes.get(i).ingredients.size(); j++)
            {
                output_string.append('[');
                output_string.append(side_dishes.get(i).ingredients.get(j).getName());
                output_string.append("][");
                output_string.append(side_dishes.get(i).ingredients.get(j).getAmount());
                output_string.append("]\n");
            }
        }

        try
        {
            FileOutputStream fos = context.openFileOutput(SIDE_D_PATH, Context.MODE_PRIVATE);
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
    public int loadSideDishes()
    {
        side_dishes = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = context.openFileInput(SIDE_D_PATH);
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
            addSideDish(new_recipe);
        }

        update();

        return 0;
    }

    public int notifyItemChanged(int pos, int meal_type)
    {
        if(meal_type == 0) fc_adapter.notifyItemChanged(pos);
        else if(meal_type == 1) sc_adapter.notifyItemChanged(pos);
        else sd_adapter.notifyItemChanged(pos);

        return 0;
    }
}
