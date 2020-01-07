package com.baldino.myapp003.singletons;

import android.content.Context;
import android.util.Log;

import com.baldino.myapp003.IngredientListAdapter;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.data_classes.Ingredient;
import com.baldino.myapp003.main_fragments.IngredientsFragment;

public class Database
{
    private static Database instance = null;
    private Context context = null;

    private IngredientManager std_ingredients, mnr_ingredients;
    private RecipeManager recipes;
    private WeekManager week_manager;

    private Database()
    {
        std_ingredients = new IngredientManager(true);
        mnr_ingredients = new IngredientManager(false);

        recipes = new RecipeManager();

        week_manager = new WeekManager();
    }

    public synchronized static Database getInstance()
    {
        if(instance == null) {
            instance = new Database();
        }

        return instance;
    }

    public void setContext(Context context)
    {
        this.context = context.getApplicationContext();
    }

    public void loadAll()
    {
        if(context != null)
        {
            std_ingredients.loadIngr(context);
            mnr_ingredients.loadIngr(context);

            recipes.loadTypeNames(context);

            week_manager.loadWeeks(context);
            week_manager.loadDailyMeals(context);
            week_manager.loadData(context);
        }
    }

    public void setStdFragment(IngredientsFragment fragment) { std_ingredients.setFragment(fragment); }
    public void setMnrFragment(IngredientsFragment fragment) { mnr_ingredients.setFragment(fragment); }
    public IngredientListAdapter getStdAdapter() { return std_ingredients.getAdapter(); }
    public IngredientListAdapter getMnrAdapter() { return mnr_ingredients.getAdapter(); }
    public Ingredient getStdIngr(int pos) { return std_ingredients.getIngr(pos); }
    public Ingredient getMnrIngr(int pos) { return mnr_ingredients.getIngr(pos); }
    public Ingredient binaryFindStdIngr(String name) { return std_ingredients.binaryFindIngr(name); }
    public Ingredient binaryFindMnrIngr(String name) { return mnr_ingredients.binaryFindIngr(name); }
    public int binaryFindStdIndex(String name) { return std_ingredients.binaryFindIndex(name); }
    public int binaryFindMnrIndex(String name) { return mnr_ingredients.binaryFindIndex(name); }
    public int getStdSize() { return std_ingredients.getSize(); }
    public int getMnrSize() { return mnr_ingredients.getSize(); }
    public void removeStdIngr(int pos)
    {
        int result = std_ingredients.removeIngr(pos);
        if(result == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            std_ingredients.saveIngr(context);
        }
    }
    public void removeMnrIngr(int pos)
    {
        int result = mnr_ingredients.removeIngr(pos);
        if(result == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            mnr_ingredients.saveIngr(context);
        }
    }
    public void updateStdIngr(int pos, Ingredient new_ingredient)
    {
        int remove = std_ingredients.removeIngr(pos);
        int add = std_ingredients.addIngr(new_ingredient);
        if(remove == 0 || add == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            std_ingredients.saveIngr(context);
        }
    }
    public void updateMnrIngr(int pos, Ingredient new_ingredient)
    {
        int remove = mnr_ingredients.removeIngr(pos);
        int add = mnr_ingredients.addIngr(new_ingredient);
        if(remove == 0 || add == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            mnr_ingredients.saveIngr(context);
        }
    }
    public void addStdIngr(Ingredient new_ingredient)
    {
        int result = std_ingredients.addIngr(new_ingredient);
        if(result == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            std_ingredients.saveIngr(context);
        }
    }
    public void addMnrIngr(Ingredient new_ingredient)
    {
        int result = mnr_ingredients.addIngr(new_ingredient);
        if(result == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            mnr_ingredients.saveIngr(context);
        }
    }
}
